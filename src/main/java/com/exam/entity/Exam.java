package com.exam.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 考试实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Exam extends BaseEntity {

    private String examName;
    private Long paperId;
    private Long subjectId;
    private Long creatorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private Integer passScore;
    private String status;
    private Integer resultPublished;
}
