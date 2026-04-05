package com.exam.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 重置密码参数。
 */
@Data
public class ResetPasswordDTO {

    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
