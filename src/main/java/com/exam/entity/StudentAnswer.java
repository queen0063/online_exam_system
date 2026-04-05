package com.exam.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生答题实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StudentAnswer extends BaseEntity {

    private Long examId;
    private Long paperId;
    private Long questionId;
    private Long studentId;
    private String answerContent;
    private Integer questionScore;
    private Integer actualScore;
    private Integer objectiveFlag;
    private Integer correctFlag;
    private String markingStatus;
    private String teacherComment;
    private LocalDateTime submitTime;
}
