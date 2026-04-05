package com.exam.dto.paper;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

/**
 * 试卷保存参数。
 */
@Data
public class PaperSaveDTO {

    private Long id;

    @NotBlank(message = "试卷名称不能为空")
    private String paperName;

    @NotNull(message = "科目不能为空")
    private Long subjectId;

    @NotBlank(message = "组卷方式不能为空")
    private String generateType;

    @NotNull(message = "时长不能为空")
    private Integer durationMinutes;

    private String description;

    @Valid
    private List<PaperQuestionDTO> questions;

    @Valid
    private List<RandomRuleDTO> randomRules;
}
