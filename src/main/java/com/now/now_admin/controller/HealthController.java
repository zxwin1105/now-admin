package com.now.now_admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查控制器
 * 提供简单的健康检查端点，用于测试Spring Security配置
 */
@RestController
public class HealthController {

    /**
     * 健康检查端点
     * 可以公开访问，不需要认证
     */
    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    /**
     * 需要认证的健康检查端点
     * 用于测试认证是否生效
     */
    @GetMapping("/health/authenticated")
    public String authenticatedHealth() {
        return "Authenticated OK";
    }

    /**
     * 需要ADMIN角色的健康检查端点
     * 用于测试角色授权是否生效
     */
    @GetMapping("/admin/health")
    public String adminHealth() {
        return "Admin OK";
    }
}
