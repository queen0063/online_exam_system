package com.exam.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 题目实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Question extends BaseEntity {

    private Long subjectId;
    private String questionType;
    private String title;
    private String imageJson;
    private String optionsJson;
    private String answerJson;
    private String analysis;
    private Integer score;
    private String difficulty;
    private String knowledgePoint;
    private Long creatorId;
    private String studentAnswerContent;
    private Integer questionScore;
    private Integer actualScore;
    private LocalDateTime submitTime;
}
