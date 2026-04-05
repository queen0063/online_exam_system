package com.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 试卷实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Paper extends BaseEntity {

    private String paperName;
    private Long subjectId;
    private String generateType;
    private Integer totalScore;
    private Integer durationMinutes;
    private Long creatorId;
    private String description;
}
