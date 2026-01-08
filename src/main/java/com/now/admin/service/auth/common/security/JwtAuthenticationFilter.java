package com.now.admin.service.auth.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.now.admin.common.constant.AppStatusEnum;
import com.now.admin.common.domain.Result;
import com.now.admin.service.auth.common.exception.AuthenticateException;
import com.now.admin.service.auth.domain.LoginUserDetail;
import com.now.admin.service.auth.service.AuthService;
import com.now.admin.service.auth.service.impl.TokenService;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * JWT认证过滤器
 * 拦截所有请求，验证Token的有效性
 * 如果Token有效，则将用户信息设置到SecurityContext中
 *
 * @author zhaixinwei
 * @date 2025/12/25
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private TokenService tokenService;

    @Lazy
    @Resource
    private AuthService authService;
    
    /**
     * Token请求头名称
     */
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * Token前缀
     */
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 获取token
            String header = request.getHeader(AUTHORIZATION_HEADER);
            
            // 如果没有Token或Token格式不正确，直接放行，由Spring Security的授权配置决定是否允许访问
            if(!StringUtils.hasText(header) || !header.startsWith(TOKEN_PREFIX)){
                filterChain.doFilter(request, response);
                return;
            }

            String token = header.substring(TOKEN_PREFIX.length());
            
            // 验证token
            boolean isValid = tokenService.validateToken(token);
            if (!isValid) {
                handleException(response, AppStatusEnum.UNAUTHORIZED.getCode(), "Token无效");
                return;
            }
            
            // 从token中获取用户ID
            Long userId = tokenService.getUserIdFromToken(token);
            if (userId == null) {
                handleException(response, AppStatusEnum.UNAUTHORIZED.getCode(), "无法获取用户信息");
                return;
            }
            
            // 加载用户信息
            Optional<LoginUserDetail> userOptional = authService.loadUserById(userId);
            if (userOptional.isEmpty()) {
                handleException(response, AppStatusEnum.UNAUTHORIZED.getCode(), "用户不存在");
                return;
            }
            
            LoginUserDetail user = userOptional.get();
            
            // 创建认证对象并设置到SecurityContext中
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(user, null, null);
            authentication.setDetails(user);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            log.debug("用户 {} 认证成功", user.getUsername());
            
            // 继续执行过滤链
            filterChain.doFilter(request, response);
        } catch (AuthenticateException e) {
            log.error("认证异常: {}", e.getMessage());
            handleException(response, e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage(), e);
            handleException(response, AppStatusEnum.UNAUTHORIZED.getCode(), "Token验证失败");
        } finally {
            // 清理SecurityContext，防止线程复用导致的安全问题
            SecurityContextHolder.clearContext();
        }
    }
    
    /**
     * 处理异常，返回JSON格式的错误信息
     */
    private void handleException(HttpServletResponse response, Integer code, String message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        
        Result<String> result = Result.fail(code, message);
        String jsonResult = objectMapper.writeValueAsString(result);
        response.getWriter().write(jsonResult);
    }


}
