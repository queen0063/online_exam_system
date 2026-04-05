package com.exam.service.impl;

import com.exam.common.api.ResultCode;
import com.exam.common.exception.BusinessException;
import com.exam.dto.auth.LoginDTO;
import com.exam.entity.LoginLog;
import com.exam.entity.SysRole;
import com.exam.entity.SysUser;
import com.exam.mapper.SysRoleMapper;
import com.exam.mapper.SysUserMapper;
import com.exam.security.context.SecurityContextUtils;
import com.exam.security.context.SecurityUser;
import com.exam.security.jwt.JwtTokenProvider;
import com.exam.service.AuthService;
import com.exam.service.LoginLogService;
import com.exam.vo.auth.LoginVO;
import com.exam.vo.user.CurrentUserVO;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现。
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginLogService loginLogService;

    public AuthServiceImpl(
            SysUserMapper sysUserMapper,
            SysRoleMapper sysRoleMapper,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            LoginLogService loginLogService) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
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
                .userInfo(CurrentUserVO.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .realName(user.getRealName())
                        .roleCodes(securityUser.getRoleCodes())
                        .build())
                .build();
    }

    @Override
    public void logout() {
        // JWT 为无状态认证，前端自行清理 token，这里预留扩展黑名单逻辑。
    }

    @Override
    public CurrentUserVO getCurrentUser() {
        SecurityUser currentUser = SecurityContextUtils.getCurrentUser();
        return CurrentUserVO.builder()
                .userId(currentUser.getUserId())
                .username(currentUser.getUsername())
                .realName(currentUser.getRealName())
                .roleCodes(currentUser.getRoleCodes())
                .build();
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
}
