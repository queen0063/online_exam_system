package com.exam.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录日志实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginLog extends BaseEntity {

    private Long userId;
    private String username;
    private Integer successFlag;
    private String ipAddress;
    private String userAgent;
    private String message;
    private LocalDateTime loginTime;
}
