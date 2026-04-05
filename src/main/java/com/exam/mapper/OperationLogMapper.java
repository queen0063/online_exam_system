package com.exam.mapper;

import com.exam.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper {

    int insert(OperationLog operationLog);
}
