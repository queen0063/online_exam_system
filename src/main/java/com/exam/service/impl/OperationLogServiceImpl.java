package com.exam.service.impl;

import com.exam.entity.OperationLog;
import com.exam.mapper.OperationLogMapper;
import com.exam.service.OperationLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 操作日志服务实现。
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    public OperationLogServiceImpl(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    @Async("auditExecutor")
    @Override
    public void record(OperationLog operationLog) {
        operationLogMapper.insert(operationLog);
    }
}
