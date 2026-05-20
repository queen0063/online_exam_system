package com.exam.service.impl;

import com.exam.common.api.ResultCode;
import com.exam.common.constant.SecurityConstants;
import com.exam.common.exception.BusinessException;
import com.exam.common.result.PageResult;
import com.exam.convert.UserConvert;
import com.exam.dto.user.ChangePasswordDTO;
import com.exam.dto.user.ResetPasswordDTO;
import com.exam.dto.user.UserQueryDTO;
import com.exam.dto.user.UserSaveDTO;
import com.exam.entity.SysRole;
import com.exam.entity.SysUser;
import com.exam.entity.SysUserRole;
import com.exam.mapper.SysRoleMapper;
import com.exam.mapper.SysUserMapper;
import com.exam.mapper.SysUserRoleMapper;
import com.exam.security.context.SecurityContextUtils;
import com.exam.service.UserService;
import com.exam.vo.user.UserVO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现。
 */
@Service
public class UserServiceImpl implements UserService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            SysUserMapper sysUserMapper,
            SysRoleMapper sysRoleMapper,
            SysUserRoleMapper sysUserRoleMapper,
            PasswordEncoder passwordEncoder) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PageResult<UserVO> page(UserQueryDTO queryDTO) {
        List<SysUser> users = sysUserMapper.selectPage(queryDTO);
        Long total = sysUserMapper.selectCount(queryDTO);
        List<UserVO> records = users.stream().map(this::buildUserVO).toList();
        return new PageResult<>(records, total, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public UserVO detail(Long id) {
        SysUser user = getUser(id);
        return buildUserVO(user);
    }

    @Override
    public List<UserVO> students(UserQueryDTO queryDTO) {
        boolean admin = SecurityContextUtils.hasAnyRole("ADMIN");
        return sysUserMapper.selectStudents(queryDTO, SecurityContextUtils.getUserId(), admin).stream()
                .map(this::buildUserVO)
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(UserSaveDTO userSaveDTO) {
        LocalDateTime now = LocalDateTime.now();
        if (userSaveDTO.getId() == null) {
            if (sysUserMapper.selectByUsername(userSaveDTO.getUsername()) != null) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "用户名已存在");
            }
            validateStudentNo(userSaveDTO.getStudentNo(), null);
            SysUser user = new SysUser();
            user.setUsername(userSaveDTO.getUsername());
            user.setPassword(passwordEncoder.encode(StringUtils.hasText(userSaveDTO.getPassword())
                    ? userSaveDTO.getPassword()
                    : SecurityConstants.DEFAULT_PASSWORD));
            user.setStudentNo(normalizeBlank(userSaveDTO.getStudentNo()));
            user.setRealName(userSaveDTO.getRealName());
            user.setPhone(userSaveDTO.getPhone());
            user.setEmail(userSaveDTO.getEmail());
            user.setClassId(userSaveDTO.getClassId());
            user.setStatus(userSaveDTO.getStatus());
            user.setCreateTime(now);
            user.setUpdateTime(now);
            user.setDeleted(0);
            sysUserMapper.insert(user);
            saveUserRoles(user.getId(), userSaveDTO.getRoleIds());
            return;
        }

        SysUser dbUser = getUser(userSaveDTO.getId());
        SysUser exists = sysUserMapper.selectByUsername(userSaveDTO.getUsername());
        if (exists != null && !exists.getId().equals(userSaveDTO.getId())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户名已存在");
        }
        validateStudentNo(userSaveDTO.getStudentNo(), userSaveDTO.getId());
        dbUser.setUsername(userSaveDTO.getUsername());
        dbUser.setStudentNo(normalizeBlank(userSaveDTO.getStudentNo()));
        dbUser.setRealName(userSaveDTO.getRealName());
        dbUser.setPhone(userSaveDTO.getPhone());
        dbUser.setEmail(userSaveDTO.getEmail());
        dbUser.setClassId(userSaveDTO.getClassId());
        dbUser.setStatus(userSaveDTO.getStatus());
        if (StringUtils.hasText(userSaveDTO.getPassword())) {
            dbUser.setPassword(passwordEncoder.encode(userSaveDTO.getPassword()));
        }
        dbUser.setUpdateTime(now);
        sysUserMapper.updateById(dbUser);
        sysUserRoleMapper.deleteByUserId(dbUser.getId());
        saveUserRoles(dbUser.getId(), userSaveDTO.getRoleIds());
    }

    @Override
    public void remove(Long id) {
        getUser(id);
        sysUserMapper.logicDeleteById(id);
        sysUserRoleMapper.deleteByUserId(id);
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        SysUser currentUser = getUser(SecurityContextUtils.getUserId());
        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), currentUser.getPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "旧密码错误");
        }
        currentUser.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        currentUser.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(currentUser);
    }

    @Override
    public void resetPassword(Long userId, ResetPasswordDTO resetPasswordDTO) {
        SysUser user = getUser(userId);
        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(user);
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        List<SysUserRole> list = new ArrayList<>();
        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setCreateTime(LocalDateTime.now());
            userRole.setUpdateTime(LocalDateTime.now());
            userRole.setDeleted(0);
            list.add(userRole);
        }
        if (!list.isEmpty()) {
            sysUserRoleMapper.batchInsert(list);
        }
    }

    private SysUser getUser(Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        return user;
    }

    private void validateStudentNo(String studentNo, Long currentUserId) {
        if (!StringUtils.hasText(studentNo)) {
            return;
        }
        SysUser exists = sysUserMapper.selectByStudentNo(studentNo.trim());
        if (exists != null && !exists.getId().equals(currentUserId)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "学号已存在");
        }
    }

    private String normalizeBlank(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private UserVO buildUserVO(SysUser user) {
        List<SysRole> roles = sysRoleMapper.selectRolesByUserId(user.getId());
        return UserConvert.toUserVO(user, roles);
    }
}
