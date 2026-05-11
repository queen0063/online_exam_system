package com.exam.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

/**
 * 题目保存参数。
 */
@Data
public class QuestionSaveDTO {

    private Long id;

    @NotNull(message = "科目不能为空")
    private Long subjectId;

    @NotBlank(message = "题型不能为空")
    private String questionType;

    @NotBlank(message = "题干不能为空")
    private String title;

    private List<String> imageUrls;

    private List<String> options;

    @NotEmpty(message = "答案不能为空")
    private List<String> answers;

    private String analysis;

    @NotNull(message = "分值不能为空")
    private Integer score;

    @NotBlank(message = "难度不能为空")
    private String difficulty;

    @NotBlank(message = "知识点不能为空")
    private String knowledgePoint;
}
