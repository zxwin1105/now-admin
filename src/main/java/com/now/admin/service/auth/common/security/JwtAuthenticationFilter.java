package com.now.admin.service.auth.common.security;

import com.now.admin.common.constant.AppStatusEnum;
import com.now.admin.common.domain.Result;
import com.now.admin.service.auth.domain.LoginUserDetail;
import com.now.admin.service.auth.service.AuthService;
import com.now.admin.service.auth.service.impl.TokenService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private TokenService tokenService;

    @Lazy
    @Resource
    private AuthService authService;

    @Resource
    private JsonMapper jsonMapper;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // -------------- 1. 获取 Token --------------
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(header) || !header.startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // -------------- 2. 解析 Token --------------
        String token = header.substring(TOKEN_PREFIX.length());
        Long userId;
        try {
            userId = tokenService.getUserIdFromToken(token);
        } catch (Exception e) {
            responseError(response, AppStatusEnum.UNAUTHORIZED.getCode(), "Token 解析失败");
            return;
        }

        // -------------- 3. 已登录？直接放行 --------------
        if (userId == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // -------------- 4. 加载用户 --------------
        Optional<LoginUserDetail> userOptional = authService.loadUserById(userId);
        if (userOptional.isEmpty()) {
            responseError(response, AppStatusEnum.UNAUTHORIZED.getCode(), "用户不存在");
            return;
        }

        // -------------- 5. 存入 Spring 上下文 --------------
        LoginUserDetail user = userOptional.get();
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        Collections.emptyList()
                );
//        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // -------------- 6. 放行 --------------
        filterChain.doFilter(request, response);
    }

    // 统一错误返回（关键：必须 立即 return，不能让流程继续）
    private void responseError(HttpServletResponse response, int code, String msg) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Result<?> result = Result.fail(code, msg);
        jsonMapper.writeValue(response.getWriter(), result);
    }
}