package com.now.admin.service.auth.common.security;

import com.now.admin.common.config.SecretKeyConfig;
import com.now.admin.common.domain.Result;
import com.now.admin.common.domain.vo.LoginRsp;
import com.now.admin.common.util.RsaUtil;
import com.now.admin.common.util.SpringUtil;
import com.now.admin.service.auth.common.exception.AuthenticateException;
import com.now.admin.service.auth.domain.LoginUserDetail;
import com.now.admin.service.auth.domain.param.LoginUserParam;
import com.now.admin.service.auth.service.impl.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;


public class LoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private TokenService tokenService;

    private final static JsonMapper jsonMapper = SpringUtil.getBean(JsonMapper.class);

    private SecretKeyConfig secretKeyConfig = SpringUtil.getBean(SecretKeyConfig.class);

    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public LoginAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        setFilterProcessesUrl("/auth/login"); // 登录接口地址
    }

    // 1. 解析前端 JSON 登录参数
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (request.getMethod().equals("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return null;
        }
        try {
            // 读取 JSON
            LoginUserParam param = jsonMapper.readValue(request.getInputStream(), LoginUserParam.class);
            param.setSecret(RsaUtil.decrypt(param.getSecret(),secretKeyConfig.getPrivateKeyStr()));
            // 创建 Token（支持你的多类型登录）
            Authentication authToken;
            if (param.getType() == LoginUserParam.LoginTypeEnum.PASSWORD) {
                authToken = new UsernamePasswordAuthenticationToken(param.getAccount(), param.getSecret());
            } else {
                // 你的短信验证码 Token
                authToken = new CustomPhoneCodeAuthenticationToken(param.getAccount(), param.getSecret());
            }

            return getAuthenticationManager().authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException("登录参数解析失败");
        }
    }

    // 2. 登录成功（返回 token）
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {

        // 认证成功，返回登录结果
        LoginUserDetail details = (LoginUserDetail) authResult.getDetails();
        if (details == null) {
            throw new AuthenticateException("认证失败");
        }

        LoginRsp rsp = LoginRsp.builder()
                .userId(details.getId())
                .token(tokenService.generateToken(details.getId()))
                .refreshToken(tokenService.generateRefreshToken(details.getId()))
                .build();
        // 返回 JSON
        response.setContentType("application/json;charset=utf-8");
        try {
            Result result = Result.success(rsp);
            jsonMapper.writeValue(response.getOutputStream(), result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 3. 登录失败（密码错误、用户不存在）
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        try {
            Result result = Result.fail(401, "账号或密码不正确");
            jsonMapper.writeValue(response.getOutputStream(), result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}