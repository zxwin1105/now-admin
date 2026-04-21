package com.now.admin.service.auth.common.security;

import com.now.admin.common.constant.AppStatusEnum;
import com.now.admin.common.domain.Result;
import com.now.admin.common.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * JWT 认证失败处理接口
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error(authException.getCause().getMessage());
        response.setContentType("application/json,charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ServletOutputStream outputStream = response.getOutputStream();
        System.out.println(authException.getMessage());
        Result<String> result = Result.fail(AppStatusEnum.UNAUTHORIZED.getCode(),
                AppStatusEnum.UNAUTHORIZED.getMessage());
        outputStream.write(Objects.requireNonNull(JsonUtil.toJson(result)).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
