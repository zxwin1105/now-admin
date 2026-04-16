package com.now.admin.service.auth.common.security;

import com.now.admin.common.constant.AppStatusEnum;
import com.now.admin.common.domain.Result;
import com.now.admin.common.util.JsonUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 无权限访问处理
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json,charset=UTF-8");
        ServletOutputStream outputStream = response.getOutputStream();

        Result<String> result = Result.fail(AppStatusEnum.FORBIDDEN.getCode(),
                AppStatusEnum.FORBIDDEN.getMessage());
        outputStream.write(Objects.requireNonNull(JsonUtil.toJson(response)).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}
