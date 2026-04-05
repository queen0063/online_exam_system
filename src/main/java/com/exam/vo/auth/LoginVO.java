package com.exam.vo.auth;

import com.exam.vo.user.CurrentUserVO;
import lombok.Builder;
import lombok.Data;

/**
 * 登录返回对象。
 */
@Data
@Builder
public class LoginVO {

    private String token;
    private CurrentUserVO userInfo;
}
