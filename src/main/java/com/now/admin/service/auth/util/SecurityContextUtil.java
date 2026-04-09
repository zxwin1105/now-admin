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
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Object details = securityContext.getAuthentication().getDetails();
        return Optional.ofNullable((LoginUserDetail) details);
    }
}
