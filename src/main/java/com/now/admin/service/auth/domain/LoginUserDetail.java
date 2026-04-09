package com.now.admin.service.auth.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录用户，认证后存储在SecurityContextHolder中的对象
 */
@Data
public class LoginUserDetail {

    /**
     * 用户主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * IP
     */
    private String ip;

    /**
     * 浏览器
     */
    private String userAgent;

    /**
     * 设备
     */
    private String device;

    private SysUserAuth sysUserAuth;
}
