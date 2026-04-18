package com.exam.vo.user;

import lombok.Builder;
import lombok.Data;

/**
 * 角色返回对象。
 */
@Data
@Builder
public class RoleVO {

    private Long id;
    private String roleCode;
    private String roleName;
    private Integer status;
}
