package com.exam.convert;

import com.exam.entity.SysRole;
import com.exam.entity.SysUser;
import com.exam.vo.user.RoleVO;
import com.exam.vo.user.UserVO;
import java.util.List;

/**
 * 用户对象转换器。
 */
public final class UserConvert {

    private UserConvert() {
    }

    public static UserVO toUserVO(SysUser user, List<SysRole> roles) {
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .classId(user.getClassId())
                .status(user.getStatus())
                .roles(roles.stream()
                        .map(role -> RoleVO.builder()
                                .id(role.getId())
                                .roleCode(role.getRoleCode())
                                .roleName(role.getRoleName())
                                .build())
                        .toList())
                .createTime(user.getCreateTime())
                .build();
    }
}
