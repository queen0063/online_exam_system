package com.exam.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 角色保存参数。
 */
@Data
public class RoleSaveDTO {

    private Long id;

    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @NotNull(message = "状态不能为空")
    private Integer status;
}
