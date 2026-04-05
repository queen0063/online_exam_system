package com.exam.dto.answer;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

/**
 * 单题答题参数。
 */
@Data
public class AnswerItemDTO {

    @NotNull(message = "题目不能为空")
    private Long questionId;

    private List<String> answers;
}
