package com.now.now_admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Spring Security配置类
 * 配置安全规则和用户认证
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        // 从配置文件读取用户名和密码
        @Value("${swagger.username:admin}")
        private String adminUsername;

        @Value("${swagger.password:password}")
        private String adminPassword;

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
                                                .requestMatchers("/public/**", "/health")
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
                                // 禁用会话管理，使用JWT来管理状态
                                .sessionManagement(AbstractHttpConfigurer::disable);
                // 添加自定义的JWT认证过滤器
                // .addFilterBefore(jwtAuthenticationFilter(),
                // UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }

        /**
         * 配置用户详细信息服务
         * 提供用户认证所需的用户信息
         */
        @Bean
        public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
                // 创建管理员用户
                UserDetails adminUser = User.builder()
                                .username(adminUsername)
                                .password(passwordEncoder.encode(adminPassword))
                                .roles("ADMIN")
                                .build();

                // 创建普通用户（如果需要）
                UserDetails userUser = User.builder()
                                .username("user")
                                .password(passwordEncoder.encode("password"))
                                .roles("USER")
                                .build();

                // 使用内存存储用户信息（生产环境应该使用数据库或其他持久化存储）
                return new InMemoryUserDetailsManager(adminUser, userUser);
        }

        /**
         * 配置密码编码器
         * 使用BCrypt算法进行密码加密
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

}
