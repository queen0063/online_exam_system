package com.exam.service;

import com.exam.dto.auth.LoginDTO;
import com.exam.dto.auth.RegisterDTO;
import com.exam.vo.auth.LoginVO;
import com.exam.vo.classinfo.ClassInfoVO;
import com.exam.vo.user.CurrentUserVO;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface AuthService {

    LoginVO login(LoginDTO loginDTO, HttpServletRequest request);

    void register(RegisterDTO registerDTO);

    List<ClassInfoVO> registerClasses();

    void logout();

    CurrentUserVO getCurrentUser();
}
