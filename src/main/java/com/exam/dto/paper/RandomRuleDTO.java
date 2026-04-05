package com.exam.dto.paper;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 随机组卷规则参数。
 */
@Data
public class RandomRuleDTO {

    @NotNull(message = "科目不能为空")
    private Long subjectId;

    @NotBlank(message = "题型不能为空")
    private String questionType;

    @NotBlank(message = "难度不能为空")
    private String difficulty;

    private String knowledgePoint;

    @NotNull(message = "抽题数量不能为空")
    private Integer questionCount;

    @NotNull(message = "单题分值不能为空")
    private Integer questionScore;
}
