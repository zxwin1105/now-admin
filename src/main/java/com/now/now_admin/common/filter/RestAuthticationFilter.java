package com.now.now_admin.common.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 自定义的RESTful认证过滤器
 */
public class RestAuthticationFilter extends UsernamePasswordAuthenticationFilter {

    /**
     * 尝试认证用户
     * 
     * @param request  HTTP请求
     * @param response HTTP响应
     * @return 认证成功后的Authentication对象
     * @throws AuthenticationException 认证失败时抛出的异常
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        // 从请求中获取用户名和密码
        String username = null;
        String password = null;
        try {
            ServletInputStream inputStream = request.getInputStream();
            // 使用Jackson读取JSON

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

}
