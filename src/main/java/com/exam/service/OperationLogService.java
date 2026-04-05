package com.exam.service;

import com.exam.entity.OperationLog;

public interface OperationLogService {

    void record(OperationLog operationLog);
}
