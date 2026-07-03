package com.exam.dto.user;

import com.exam.dto.common.PageQueryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryDTO extends PageQueryDTO {

    private String keyword;
    private Long roleId;
    private Long classId;
    private Integer status;
}
