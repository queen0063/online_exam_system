package com.exam.dto.paper;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 试卷题目参数。
 */
@Data
public class PaperQuestionDTO {

    @NotNull(message = "题目不能为空")
    private Long questionId;

    @NotNull(message = "题目分值不能为空")
    private Integer questionScore;

    @NotNull(message = "题目顺序不能为空")
    private Integer sortNo;
}
