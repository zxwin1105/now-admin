package com.now.admin.service.auth.domain;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录用户，认证后存储在SecurityContextHolder中的对象
 */
@Data
public class LoginUserDetail {

    private Long id;

    private String userId;

    private String username;

    private String avatar;

    private Integer status;

    private LocalDateTime loginTime;

    private SysUserAuth sysUserAuth;
}
