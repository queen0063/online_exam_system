package com.exam.vo.paper;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * 试卷题目返回对象。
 */
@Data
@Builder
public class PaperQuestionVO {

    private Long questionId;
    private Integer questionScore;
    private Integer sortNo;
    private String questionType;
    private String title;
    private List<String> imageUrls;
    private List<String> options;
    private List<String> answers;
    private String analysis;
}
