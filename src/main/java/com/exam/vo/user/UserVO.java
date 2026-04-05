package com.exam.vo.user;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * 用户返回对象。
 */
@Data
@Builder
public class UserVO {

    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private Long classId;
    private Integer status;
    private List<RoleVO> roles;
    private LocalDateTime createTime;
}
