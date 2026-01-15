package com.now.admin.service.auth.common.util;

import com.now.admin.service.auth.domain.LoginUserDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

/**
 * 认证上下文工具类
 * 用于获取当前登录用户信息和验证登录状态
 * 
 * @author system
 * @date 2026/01/06
 */
@Slf4j
public class AuthContextHolder {

    private AuthContextHolder() {
        // 工具类不允许实例化
    }

    /**
     * 获取当前登录用户信息
     * 
     * @return 登录用户信息，未登录返回null
     */
    public static LoginUserDetail getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() 
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUserDetail) {
            return (LoginUserDetail) principal;
        }
        
        // 尝试从details中获取
        Object details = authentication.getDetails();
        if (details instanceof LoginUserDetail) {
            return (LoginUserDetail) details;
        }
        
        return null;
    }

    /**
     * 判断当前用户是否已登录
     * 
     * @return true-已登录，false-未登录
     */
    public static boolean isAuthenticated() {
        return getCurrentUser() != null;
    }

    /**
     * 获取当前登录用户的ID
     * 
     * @return 用户ID，未登录返回null
     */
    public static Long getCurrentUserId() {
        LoginUserDetail user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    /**
     * 获取当前登录用户的userId
     * 
     * @return userId，未登录返回null
     */
    public static String getCurrentUserIdString() {
        LoginUserDetail user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * 获取当前登录用户的用户名
     * 
     * @return 用户名，未登录返回null
     */
    public static String getCurrentUsername() {
        LoginUserDetail user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    /**
     * 验证指定ID是否为当前登录用户
     * 
     * @param id 要验证的用户ID
     * @return true-是当前用户，false-不是当前用户或未登录
     */
    public static boolean isCurrentUser(Long id) {
        if (id == null) {
            return false;
        }
        Long currentUserId = getCurrentUserId();
        return Objects.equals(id, currentUserId);
    }

    /**
     * 验证指定userId是否为当前登录用户
     * 
     * @param userId 要验证的userId
     * @return true-是当前用户，false-不是当前用户或未登录
     */
    public static boolean isCurrentUser(String userId) {
        if (userId == null || userId.isBlank()) {
            return false;
        }
        String currentUserId = getCurrentUserIdString();
        return Objects.equals(userId, currentUserId);
    }

    /**
     * 要求当前用户必须已登录，否则抛出异常
     * 
     * @return 当前登录用户信息
     * @throws IllegalStateException 当前用户未登录
     */
    public static LoginUserDetail requireAuthenticated() {
        LoginUserDetail user = getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("当前用户未登录");
        }
        return user;
    }

    /**
     * 要求当前用户必须是指定用户，否则抛出异常
     * 
     * @param id 要求的用户ID
     * @throws IllegalStateException 当前用户未登录或不是指定用户
     */
    public static void requireCurrentUser(Long id) {
        if (!isCurrentUser(id)) {
            throw new IllegalStateException("无权限访问其他用户的数据");
        }
    }

    /**
     * 要求当前用户必须是指定用户，否则抛出异常
     * 
     * @param userId 要求的userId
     * @throws IllegalStateException 当前用户未登录或不是指定用户
     */
    public static void requireCurrentUser(String userId) {
        if (!isCurrentUser(userId)) {
            throw new IllegalStateException("无权限访问其他用户的数据");
        }
    }
}
