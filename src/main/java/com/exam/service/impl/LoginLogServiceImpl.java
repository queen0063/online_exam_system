package com.exam.service.impl;

import com.exam.entity.LoginLog;
import com.exam.mapper.LoginLogMapper;
import com.exam.service.LoginLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 登录日志服务实现。
 */
@Service
public class LoginLogServiceImpl implements LoginLogService {

    private final LoginLogMapper loginLogMapper;

    public LoginLogServiceImpl(LoginLogMapper loginLogMapper) {
        this.loginLogMapper = loginLogMapper;
    }

    @Async("auditExecutor")
    @Override
    public void record(LoginLog loginLog) {
        loginLogMapper.insert(loginLog);
    }
}
