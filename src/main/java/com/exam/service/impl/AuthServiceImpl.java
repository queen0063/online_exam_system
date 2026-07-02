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
import com.exam.security.jwt.JwtProperties;
import com.exam.security.jwt.JwtTokenProvider;
import com.exam.service.AuthService;
import com.exam.service.LoginLogService;
import com.exam.vo.auth.LoginVO;
import com.exam.vo.auth.RegisterInviteVO;
import com.exam.vo.classinfo.ClassInfoVO;
import com.exam.vo.user.CurrentUserVO;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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
    private final JwtProperties jwtProperties;
    private final LoginLogService loginLogService;

    public AuthServiceImpl(
            SysUserMapper sysUserMapper,
            SysRoleMapper sysRoleMapper,
            SysUserRoleMapper sysUserRoleMapper,
            ClassInfoMapper classInfoMapper,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            JwtProperties jwtProperties,
            LoginLogService loginLogService) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.classInfoMapper = classInfoMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
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
            RegisterInviteVO invite = getRegisterInvite(registerDTO.getInviteCode());
            if (sysUserMapper.selectByStudentNo(registerDTO.getStudentNo().trim()) != null) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "学号已存在");
            }
            registerDTO.setClassId(invite.getClassId());
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
        user.setStatus(1);
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
    public RegisterInviteVO getRegisterInvite(String inviteCode) {
        if (!StringUtils.hasText(inviteCode)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "学生注册必须使用教师专属注册链接");
        }
        String payload = parseInviteCode(inviteCode.trim());
        String[] parts = payload.split(":");
        if (parts.length != 2) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "注册链接无效");
        }
        Long classId = parseLong(parts[0], "注册链接无效");
        Long teacherId = parseLong(parts[1], "注册链接无效");
        ClassInfo classInfo = classInfoMapper.selectById(classId);
        if (classInfo == null || classInfo.getStatus() == null || classInfo.getStatus() != 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "班级不存在或已停用");
        }
        if (classInfo.getTeacherId() == null || !classInfo.getTeacherId().equals(teacherId)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "注册链接已失效，请联系教师重新获取");
        }
        SysUser teacher = sysUserMapper.selectById(teacherId);
        if (teacher == null || teacher.getStatus() == null || teacher.getStatus() != 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "负责教师不存在或账号未启用");
        }
        return buildRegisterInviteVO(inviteCode.trim(), classInfo, teacher);
    }

    @Override
    public RegisterInviteVO generateRegisterInvite(Long classId) {
        ClassInfo classInfo = classInfoMapper.selectById(classId);
        if (classInfo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "班级不存在");
        }
        if (classInfo.getStatus() == null || classInfo.getStatus() != 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "停用班级不能生成注册链接");
        }
        if (classInfo.getTeacherId() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "班级未设置负责教师");
        }
        boolean admin = SecurityContextUtils.hasAnyRole(RoleCode.ADMIN.name());
        Long currentUserId = SecurityContextUtils.getUserId();
        if (!admin && !classInfo.getTeacherId().equals(currentUserId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只能生成自己负责班级的注册链接");
        }
        SysUser teacher = sysUserMapper.selectById(classInfo.getTeacherId());
        if (teacher == null || teacher.getStatus() == null || teacher.getStatus() != 1) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "负责教师不存在或账号未启用");
        }
        String inviteCode = createInviteCode(classInfo.getId(), classInfo.getTeacherId());
        return buildRegisterInviteVO(inviteCode, classInfo, teacher);
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

    private RegisterInviteVO buildRegisterInviteVO(String inviteCode, ClassInfo classInfo, SysUser teacher) {
        return RegisterInviteVO.builder()
                .inviteCode(inviteCode)
                .classId(classInfo.getId())
                .className(classInfo.getClassName())
                .gradeName(classInfo.getGradeName())
                .teacherId(teacher.getId())
                .teacherName(teacher.getRealName())
                .registerUrl("/register?invite=" + inviteCode)
                .build();
    }

    private String createInviteCode(Long classId, Long teacherId) {
        String payload = classId + ":" + teacherId;
        String payloadPart = base64Url(payload);
        return payloadPart + "." + sign(payloadPart);
    }

    private String parseInviteCode(String inviteCode) {
        String[] parts = inviteCode.split("\\.");
        if (parts.length != 2 || !sign(parts[0]).equals(parts[1])) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "注册链接无效");
        }
        try {
            return new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "注册链接无效");
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "注册链接生成失败");
        }
    }

    private String base64Url(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private Long parseLong(String value, String message) {
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException ex) {
            throw new BusinessException(ResultCode.BAD_REQUEST, message);
        }
    }

    private String normalizeBlank(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
