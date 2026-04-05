package com.exam.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OperationLog extends BaseEntity {

    private Long operatorId;
    private String operatorName;
    private String operationType;
    private String moduleName;
    private String operationDesc;
    private String requestUri;
    private String requestMethod;
    private String requestParam;
    private Integer successFlag;
    private LocalDateTime operationTime;
}
