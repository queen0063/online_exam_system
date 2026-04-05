package com.exam.security.context;

import com.exam.common.api.ResultCode;
import com.exam.common.exception.BusinessException;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全上下文工具。
 */
public final class SecurityContextUtils {

    private SecurityContextUtils() {
    }

    public static SecurityUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser securityUser)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "当前登录状态无效");
        }
        return securityUser;
    }

    public static Long getUserId() {
        return getCurrentUser().getUserId();
    }

    public static String getUsername() {
        return getCurrentUser().getUsername();
    }

    public static boolean hasAnyRole(String... roleCodes) {
        List<String> currentRoles = getCurrentUser().getRoleCodes();
        for (String roleCode : roleCodes) {
            if (currentRoles.contains(roleCode)) {
                return true;
            }
        }
        return false;
    }
}
