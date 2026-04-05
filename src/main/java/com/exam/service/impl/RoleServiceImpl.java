package com.exam.service.impl;

import com.exam.mapper.SysRoleMapper;
import com.exam.service.RoleService;
import com.exam.vo.user.RoleVO;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 角色服务实现。
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final SysRoleMapper sysRoleMapper;

    public RoleServiceImpl(SysRoleMapper sysRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
    }

    @Override
    public List<RoleVO> listAll() {
        return sysRoleMapper.selectAll().stream()
                .map(role -> RoleVO.builder()
                        .id(role.getId())
                        .roleCode(role.getRoleCode())
                        .roleName(role.getRoleName())
                        .build())
                .toList();
    }
}
