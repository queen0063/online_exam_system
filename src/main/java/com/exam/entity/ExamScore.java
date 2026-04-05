package com.exam.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考试成绩实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExamScore extends BaseEntity {

    private Long examId;
    private Long studentId;
    private Integer objectiveScore;
    private Integer subjectiveScore;
    private Integer totalScore;
    private Integer passFlag;
    private Integer excellentFlag;
    private Integer rankNo;
    private String scoreStatus;
    private LocalDateTime submitTime;
    private LocalDateTime publishTime;
}
