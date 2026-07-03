package com.exam.vo.answer;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * 答题详情返回对象。
 */
@Data
@Builder
public class AnswerVO {

    private Long id;
    private Long questionId;
    private String questionType;
    private String title;
    private List<String> imageUrls;
    private List<String> options;
    private List<String> standardAnswers;
    private List<String> studentAnswers;
    private Integer questionScore;
    private Integer actualScore;
    private String teacherComment;
    private String markingStatus;
    private LocalDateTime submitTime;
}
