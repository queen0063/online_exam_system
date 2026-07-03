package com.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考试学生关联实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExamStudent extends BaseEntity {

    private Long examId;
    private Long studentId;
    private String answerStatus;
    private Integer switchCount;
}
