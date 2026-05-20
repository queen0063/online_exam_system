package com.exam.dto.classinfo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 班级保存参数。
 */
@Data
public class ClassInfoSaveDTO {

    private Long id;

    @NotBlank(message = "班级编码不能为空")
    private String classCode;

    @NotBlank(message = "班级名称不能为空")
    private String className;

    private String gradeName;

    private Long teacherId;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
