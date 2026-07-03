package com.exam.dto.subject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 科目保存参数。
 */
@Data
public class SubjectSaveDTO {

    private Long id;

    private String subjectCode;

    @NotBlank(message = "科目名称不能为空")
    private String subjectName;

    private String description;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
