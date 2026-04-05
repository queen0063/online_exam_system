package com.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 科目实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Subject extends BaseEntity {

    private String subjectCode;
    private String subjectName;
    private String description;
    private Integer status;
}
