package com.exam.service.impl;

import com.exam.common.api.ResultCode;
import com.exam.common.exception.BusinessException;
import com.exam.dto.role.RoleSaveDTO;
import com.exam.entity.SysRole;
import com.exam.mapper.SysRoleMapper;
import com.exam.mapper.SysUserRoleMapper;
import com.exam.service.RoleService;
import com.exam.vo.user.RoleVO;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色服务实现。
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    public RoleServiceImpl(SysRoleMapper sysRoleMapper, SysUserRoleMapper sysUserRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
    }

    @Override
    public List<RoleVO> listAll() {
        return sysRoleMapper.selectAll().stream().map(this::toRoleVO).toList();
    }

    @Override
    public RoleVO detail(Long id) {
        return toRoleVO(getRole(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(RoleSaveDTO roleSaveDTO) {
        SysRole existsRole = sysRoleMapper.selectByRoleCode(roleSaveDTO.getRoleCode());
        if (existsRole != null && !existsRole.getId().equals(roleSaveDTO.getId())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "角色编码已存在");
        }

        SysRole role = roleSaveDTO.getId() == null ? new SysRole() : getRole(roleSaveDTO.getId());
        role.setRoleCode(roleSaveDTO.getRoleCode());
        role.setRoleName(roleSaveDTO.getRoleName());
        role.setStatus(roleSaveDTO.getStatus());
        role.setUpdateTime(LocalDateTime.now());
        if (roleSaveDTO.getId() == null) {
            role.setCreateTime(LocalDateTime.now());
            role.setDeleted(0);
            sysRoleMapper.insert(role);
            return;
        }
        sysRoleMapper.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id) {
        getRole(id);
        sysRoleMapper.logicDeleteById(id);
        sysUserRoleMapper.deleteByRoleId(id);
    }

    private SysRole getRole(Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }
        return role;
    }

    private RoleVO toRoleVO(SysRole role) {
        return RoleVO.builder()
                .id(role.getId())
                .roleCode(role.getRoleCode())
                .roleName(role.getRoleName())
                .status(role.getStatus())
                .build();
    }
}
