package com.exam.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

/**
 * 用户新增编辑参数。
 */
@Data
public class UserSaveDTO {

    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    @NotBlank(message = "姓名不能为空")
    private String realName;

    private String phone;

    private String email;

    private Long classId;

    @NotNull(message = "状态不能为空")
    private Integer status;

    @NotEmpty(message = "角色不能为空")
    private List<Long> roleIds;
}
