package com.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRole extends BaseEntity {

    private String roleCode;
    private String roleName;
    private Integer status;
}
