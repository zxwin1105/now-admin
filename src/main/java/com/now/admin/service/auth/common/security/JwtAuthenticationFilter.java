package com.now.admin.service.auth.common.security;

import com.now.admin.common.constant.AppStatusEnum;
import com.now.admin.common.domain.Result;
import com.now.admin.common.util.JsonUtil;
import com.now.admin.service.auth.domain.LoginUserDetail;
import com.now.admin.service.auth.service.AuthService;
import com.now.admin.service.auth.service.impl.TokenService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
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
import java.util.Objects;
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

        try {
            // 1. 获取请求头
            String header = request.getHeader(AUTHORIZATION_HEADER);

            // 2. 无 token 直接放行
            if (!StringUtils.hasText(header) || !header.startsWith(TOKEN_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 3. 提取 token
            String token = header.replace(TOKEN_PREFIX, "").trim();
            if (!StringUtils.hasText(token)) {
                responseError(response, AppStatusEnum.UNAUTHORIZED.getCode(), "Token 不能为空");
                return;
            }

            // 4. 解析 userId（捕获所有 Token 异常）
            Long userId;
            try {
                userId = tokenService.getUserIdFromToken(token);
            } catch (Exception e) {
                log.warn("Token 解析失败: {}", e.getMessage());
                responseError(response, AppStatusEnum.UNAUTHORIZED.getCode(), "Token 无效或已过期");
                return;
            }

            // 5. 已认证 → 跳过
            if (userId == null || SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 6. 查询用户
            Optional<LoginUserDetail> userOptional = authService.loadUserById(userId);
            if (userOptional.isEmpty()) {
                responseError(response, AppStatusEnum.UNAUTHORIZED.getCode(), "用户不存在或已禁用");
                return;
            }

            // 7. 构造认证对象（✅ 放完整用户对象）
            LoginUserDetail loginUser = userOptional.get();
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            loginUser,        // ✅ 主体放完整用户
                            null,
                            loginUser.getAuthorities()
                    );

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // 8. 放行
            filterChain.doFilter(request, response);

        } finally {
            // ✅【关键】请求结束清空上下文
            SecurityContextHolder.clearContext();
        }
    }

    // 统一错误返回
    private void responseError(HttpServletResponse response, int code, String msg) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        Result<?> result = Result.fail(code, msg);
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(Objects.requireNonNull(JsonUtil.toJson(result)).getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();
    }
}