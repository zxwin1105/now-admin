package com.now.admin.service.auth.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * 登录用户，认证后存储在SecurityContextHolder中的对象
 */
@Data
public class LoginUserDetail implements UserDetails {

    /**
     * 用户主键ID
     */
    private Long id;

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

    /**
     * 用户认证信息
     */
    @JsonIgnore
    private SysUserAuth sysUserAuth;

    /**
     * 用户权限信息
     */
    private Set<String> perms;

    /**
     * 用户角色信息
     */
    private Set<String> roles;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (perms == null) return Collections.emptyList();
        // 把 "sys:user:list" 转成 SimpleGrantedAuthority
        return perms.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public @Nullable String getPassword() {
        return "";
    }
}
