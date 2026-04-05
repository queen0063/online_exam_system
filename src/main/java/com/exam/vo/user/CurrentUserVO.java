package com.exam.vo.user;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * 当前登录用户返回对象。
 */
@Data
@Builder
public class CurrentUserVO {

    private Long userId;
    private String username;
    private String realName;
    private List<String> roleCodes;
}
