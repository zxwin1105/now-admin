package com.now.admin.service.auth.util;

import com.now.admin.service.auth.domain.LoginUserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * SecurityContextUtil
 *
 * 简化SecurityContext操作
 */
public class SecurityContextUtil {


    /**
     * 获取当前登录用户详情信息
     * @return Optional<LoginUserDetail>
     */
    public static Optional<LoginUserDetail> getCurrentUser(){
        // 1. 先拿上下文
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext == null) {
            return Optional.empty();
        }

        // 2. 再拿认证信息（关键！未登录时这里是 null）
        Authentication authentication = securityContext.getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }

        // 3. 再拿详情，安全强转
        Object principal = authentication.getPrincipal();

        // 判断是否是你的用户对象
        if (!(principal instanceof LoginUserDetail)) {
            return Optional.empty();
        }

        return Optional.of((LoginUserDetail) principal);
    }
}
