package com.exam.service.impl;

import com.exam.common.api.ResultCode;
import com.exam.common.enums.RoleCode;
import com.exam.common.exception.BusinessException;
import com.exam.dto.auth.LoginDTO;
import com.exam.dto.auth.RegisterDTO;
import com.exam.entity.ClassInfo;
import com.exam.entity.LoginLog;
import com.exam.entity.SysRole;
import com.exam.entity.SysUser;
import com.exam.entity.SysUserRole;
import com.exam.mapper.ClassInfoMapper;
import com.exam.mapper.SysRoleMapper;
import com.exam.mapper.SysUserMapper;
import com.exam.mapper.SysUserRoleMapper;
import com.exam.security.context.SecurityContextUtils;
import com.exam.security.context.SecurityUser;
import com.exam.security.jwt.JwtTokenProvider;
import com.exam.service.AuthService;
import com.exam.service.LoginLogService;
import com.exam.vo.auth.LoginVO;
import com.exam.vo.classinfo.ClassInfoVO;
import com.exam.vo.user.CurrentUserVO;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 认证服务实现。
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final ClassInfoMapper classInfoMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginLogService loginLogService;

    public AuthServiceImpl(
            SysUserMapper sysUserMapper,
            SysRoleMapper sysRoleMapper,
            SysUserRoleMapper sysUserRoleMapper,
            ClassInfoMapper classInfoMapper,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            LoginLogService loginLogService) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.classInfoMapper = classInfoMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.loginLogService = loginLogService;
    }

    @Override
    public LoginVO login(LoginDTO loginDTO, HttpServletRequest request) {
        SysUser user = sysUserMapper.selectByUsername(loginDTO.getUsername());
        if (user == null || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            recordLoginLog(null, loginDTO.getUsername(), 0, request, "用户名或密码错误");
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            recordLoginLog(user.getId(), user.getUsername(), 0, request, "账号未启用");
            throw new BusinessException(ResultCode.UNAUTHORIZED, "账号未启用，请联系管理员");
        }
        List<SysRole> roles = sysRoleMapper.selectRolesByUserId(user.getId());
        SecurityUser securityUser = new SecurityUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRealName(),
                roles.stream().map(SysRole::getRoleCode).toList(),
                user.getStatus());
        recordLoginLog(user.getId(), user.getUsername(), 1, request, "登录成功");
        return LoginVO.builder()
                .token(jwtTokenProvider.createToken(securityUser))
                .userInfo(buildCurrentUserVO(user, roles))
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(RegisterDTO registerDTO) {
        String roleCode = registerDTO.getRoleCode();
        if (!RoleCode.TEACHER.name().equals(roleCode) && !RoleCode.STUDENT.name().equals(roleCode)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "仅支持教师或学生注册");
        }
        if (sysUserMapper.selectByUsername(registerDTO.getUsername()) != null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "用户名已存在");
        }
        if (RoleCode.STUDENT.name().equals(roleCode)) {
            if (!StringUtils.hasText(registerDTO.getStudentNo())) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "学生注册必须填写学号");
            }
            if (registerDTO.getClassId() == null) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "学生注册必须选择班级");
            }
            if (sysUserMapper.selectByStudentNo(registerDTO.getStudentNo().trim()) != null) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "学号已存在");
            }
            boolean classExists = classInfoMapper.selectVisible(null, true).stream()
                    .anyMatch(classInfo -> classInfo.getId().equals(registerDTO.getClassId()));
            if (!classExists) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "班级不存在或已停用");
            }
        } else if (StringUtils.hasText(registerDTO.getStudentNo())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "教师注册不能填写学号");
        }

        SysRole role = sysRoleMapper.selectByRoleCode(roleCode);
        if (role == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "注册角色不存在");
        }

        LocalDateTime now = LocalDateTime.now();
        SysUser user = new SysUser();
        user.setUsername(registerDTO.getUsername().trim());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setStudentNo(RoleCode.STUDENT.name().equals(roleCode) ? registerDTO.getStudentNo().trim() : null);
        user.setRealName(registerDTO.getRealName().trim());
        user.setPhone(normalizeBlank(registerDTO.getPhone()));
        user.setEmail(normalizeBlank(registerDTO.getEmail()));
        user.setClassId(RoleCode.STUDENT.name().equals(roleCode) ? registerDTO.getClassId() : null);
        user.setStatus(RoleCode.STUDENT.name().equals(roleCode) ? 1 : 0);
        user.setCreateTime(now);
        user.setUpdateTime(now);
        user.setDeleted(0);
        sysUserMapper.insert(user);

        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        userRole.setCreateTime(now);
        userRole.setUpdateTime(now);
        userRole.setDeleted(0);
        sysUserRoleMapper.batchInsert(List.of(userRole));
    }

    @Override
    public List<ClassInfoVO> registerClasses() {
        return classInfoMapper.selectVisible(null, true).stream()
                .map(this::toClassInfoVO)
                .toList();
    }

    @Override
    public void logout() {
        // JWT 为无状态认证，前端自行清理 token，这里预留扩展黑名单逻辑。
    }

    @Override
    public CurrentUserVO getCurrentUser() {
        SecurityUser currentUser = SecurityContextUtils.getCurrentUser();
        SysUser user = sysUserMapper.selectById(currentUser.getUserId());
        List<SysRole> roles = sysRoleMapper.selectRolesByUserId(currentUser.getUserId());
        return buildCurrentUserVO(user, roles);
    }

    private CurrentUserVO buildCurrentUserVO(SysUser user, List<SysRole> roles) {
        List<String> roleCodes = roles.stream().map(SysRole::getRoleCode).toList();
        CurrentUserVO.CurrentUserVOBuilder builder = CurrentUserVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .roleCodes(roleCodes);

        if (roleCodes.contains(RoleCode.STUDENT.name())) {
            builder.studentNo(user.getStudentNo());
            builder.classId(user.getClassId());
            if (user.getClassId() != null) {
                ClassInfo classInfo = classInfoMapper.selectById(user.getClassId());
                if (classInfo != null) {
                    builder.className(classInfo.getClassName());
                    builder.gradeName(classInfo.getGradeName());
                }
            }
        }

        if (roleCodes.contains(RoleCode.TEACHER.name())) {
            List<ClassInfoVO> teacherClasses = classInfoMapper.selectVisible(user.getId(), false).stream()
                    .map(this::toClassInfoVO)
                    .toList();
            builder.teacherClasses(teacherClasses);
        }

        return builder.build();
    }

    private void recordLoginLog(Long userId, String username, Integer successFlag, HttpServletRequest request, String message) {
        LoginLog loginLog = new LoginLog();
        loginLog.setUserId(userId);
        loginLog.setUsername(username);
        loginLog.setSuccessFlag(successFlag);
        loginLog.setIpAddress(request.getRemoteAddr());
        loginLog.setUserAgent(request.getHeader("User-Agent"));
        loginLog.setMessage(message);
        loginLog.setLoginTime(LocalDateTime.now());
        loginLog.setCreateTime(LocalDateTime.now());
        loginLog.setUpdateTime(LocalDateTime.now());
        loginLog.setDeleted(0);
        loginLogService.record(loginLog);
    }

    private ClassInfoVO toClassInfoVO(ClassInfo classInfo) {
        return ClassInfoVO.builder()
                .id(classInfo.getId())
                .classCode(classInfo.getClassCode())
                .className(classInfo.getClassName())
                .gradeName(classInfo.getGradeName())
                .teacherId(classInfo.getTeacherId())
                .status(classInfo.getStatus())
                .createTime(classInfo.getCreateTime())
                .build();
    }

    private String normalizeBlank(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
