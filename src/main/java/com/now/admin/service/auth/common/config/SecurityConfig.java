package com.now.admin.service.auth.common.config;

import com.now.admin.service.auth.common.security.JwtAuthenticationFilter;
import com.now.admin.service.auth.common.security.CustomAuthenticationProvider;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring Security配置类
 * 配置安全规则和用户认证
 */
@Configuration
@EnableWebSecurity(debug = false)
public class SecurityConfig {

    @Resource
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Lazy
    @Resource
    private CustomAuthenticationProvider customAuthenticationProvider;


    /**
     * 配置安全过滤链
     * 定义哪些路径需要认证，哪些可以公开访问
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 配置请求授权
                .authorizeHttpRequests(authorize -> authorize
                        // 配置可以公开访问的路径
                        .requestMatchers("/public/**","/auth/register",  "/auth/login", "/auth/sms/**")
                        .permitAll()
                        // 配置Swagger相关路径需要认证
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                "/swagger-ui.html")
                        .authenticated()
                        // 配置需要ADMIN角色的路径
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")
                        // 配置其他所有路径都需要认证
                        .anyRequest()
                        .authenticated())
                // 关闭基本认证
                .httpBasic(AbstractHttpConfigurer::disable)
                // 禁用生成的表单登录页面
                .formLogin(AbstractHttpConfigurer::disable)
                // 关闭CSRF保护，前后端分离模式下，
                .csrf(AbstractHttpConfigurer::disable)
                // 禁用生成默认的注销页面
                .logout(AbstractHttpConfigurer::disable)
                // 配置无状态会话（使用JWT）
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 添加JWT认证过滤器（在UsernamePasswordAuthenticationFilter之前）
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }



    /**
     * 配置认证管理器
     * 注册多个AuthenticationProvider支持多种认证方式
     */
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        // 创建ProviderManager，注册多个认证提供者
        return new ProviderManager(
            List.of(
                customAuthenticationProvider
            )
        );
    }


    /**
     * 配置密码编码器
     * 使用BCrypt算法进行密码加密
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        return new DelegatingPasswordEncoder("bcrypt", encoders);
    }
}
