package com.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户角色关联实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserRole extends BaseEntity {

    private Long userId;
    private Long roleId;
}
