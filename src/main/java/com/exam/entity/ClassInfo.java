package com.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 班级实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClassInfo extends BaseEntity {

    private String classCode;
    private String className;
    private String gradeName;
    private Long teacherId;
    private Integer status;
}
