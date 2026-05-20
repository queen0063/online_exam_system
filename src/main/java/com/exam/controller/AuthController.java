package com.exam.controller;

import com.exam.aspect.OperationLog;
import com.exam.common.enums.OperationTypeEnum;
import com.exam.common.result.Result;
import com.exam.dto.auth.LoginDTO;
import com.exam.dto.auth.RegisterDTO;
import com.exam.service.AuthService;
import com.exam.vo.auth.LoginVO;
import com.exam.vo.classinfo.ClassInfoVO;
import com.exam.vo.user.CurrentUserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口。
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @OperationLog(module = "认证模块", description = "用户登录", operationType = OperationTypeEnum.LOGIN)
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        return Result.success("登录成功", authService.login(loginDTO, request));
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return Result.success("注册成功", null);
    }

    @GetMapping("/register/classes")
    public Result<List<ClassInfoVO>> registerClasses() {
        return Result.success(authService.registerClasses());
    }

    @PostMapping("/logout")
    @OperationLog(module = "认证模块", description = "用户退出", operationType = OperationTypeEnum.LOGOUT)
    public Result<Void> logout() {
        authService.logout();
        return Result.success("退出成功", null);
    }

    @GetMapping("/me")
    public Result<CurrentUserVO> me() {
        return Result.success(authService.getCurrentUser());
    }
}
