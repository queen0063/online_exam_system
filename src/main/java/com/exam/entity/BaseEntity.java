package com.exam.entity;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 基础实体。
 */
@Data
public class BaseEntity {

    private Long id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}
