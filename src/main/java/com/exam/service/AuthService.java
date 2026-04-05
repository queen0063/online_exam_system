package com.exam.service;

import com.exam.dto.auth.LoginDTO;
import com.exam.vo.auth.LoginVO;
import com.exam.vo.user.CurrentUserVO;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    LoginVO login(LoginDTO loginDTO, HttpServletRequest request);

    void logout();

    CurrentUserVO getCurrentUser();
}
