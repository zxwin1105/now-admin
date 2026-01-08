package com.now.admin.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 认证类型枚举
 */
@Getter
@AllArgsConstructor
public enum AuthTypeEnum {
    
    /**
     * 用户名密码登录
     */
    USERNAME_PASSWORD("username_password", "用户名密码登录"),
    
    /**
     * 手机验证码登录
     */
    SMS_CODE("sms_code", "手机验证码登录"),
    
    /**
     * 微信登录
     */
    WECHAT("wechat", "微信登录"),
    
    /**
     * GitHub登录
     */
    GITHUB("github", "GitHub登录"),
    
    /**
     * Google登录
     */
    GOOGLE("google", "Google登录");
    
    private final String code;
    private final String description;
    
    /**
     * 根据code获取枚举
     */
    public static AuthTypeEnum fromCode(String code) {
        for (AuthTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的认证类型: " + code);
    }
}
