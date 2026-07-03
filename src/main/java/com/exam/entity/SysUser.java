package com.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户实体。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUser extends BaseEntity {

    private String username;
    private String password;
    private String studentNo;
    private String realName;
    private String phone;
    private String email;
    private Long classId;
    private Integer status;
}
