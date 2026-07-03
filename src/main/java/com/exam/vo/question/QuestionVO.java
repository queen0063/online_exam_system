package com.exam.vo.question;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * 题目返回对象。
 */
@Data
@Builder
public class QuestionVO {

    private Long id;
    private Long subjectId;
    private String questionType;
    private String title;
    private List<String> imageUrls;
    private List<String> options;
    private List<String> answers;
    private String analysis;
    private Integer score;
    private List<String> studentAnswers;
    private Integer questionScore;
    private Integer actualScore;
    private String difficulty;
    private String knowledgePoint;
    private Long creatorId;
    private LocalDateTime createTime;
    private LocalDateTime submitTime;
}
