package com.exam.dto.marking;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 阅卷评分参数。
 */
@Data
public class MarkingScoreDTO {

    @NotNull(message = "得分不能为空")
    private Integer actualScore;

    private String teacherComment;
}
