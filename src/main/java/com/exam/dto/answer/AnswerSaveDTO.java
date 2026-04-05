package com.exam.dto.answer;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

/**
 * 保存答题参数。
 */
@Data
public class AnswerSaveDTO {

    @NotEmpty(message = "答题内容不能为空")
    private List<AnswerItemDTO> answers;
}
