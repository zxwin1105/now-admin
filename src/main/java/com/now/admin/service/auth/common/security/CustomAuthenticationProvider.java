package com.now.admin.service.auth.common.security;

import com.now.admin.common.constant.AppStatusEnum;
import com.now.admin.common.constant.SystemStatusConstant;
import com.now.admin.common.exception.InnerCommonException;
import com.now.admin.service.auth.domain.LoginUserDetail;
import com.now.admin.service.auth.domain.SysUserAuth;
import com.now.admin.service.auth.service.AuthService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * 自定义Provider ，用于SS做系统用户认证
 */
@Slf4j
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Lazy
    @Resource
    private AuthService authService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 账号认证
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return authenticateByPwd(authentication);
        } else if (authentication instanceof CustomPhoneCodeAuthenticationToken) {
            return authenticateByPhoneCode(authentication);
        }
        throw new UnsupportedOperationException("Unsupported authentication method");
    }

    private Authentication authenticateByPwd(Authentication authentication) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) authentication;
        String account = String.valueOf(authenticationToken.getPrincipal());
        String secret = String.valueOf(authenticationToken.getCredentials());
        Optional<LoginUserDetail> loginUserOptional = authService.loadUserByAccount(account);
        if (loginUserOptional.isPresent()) {
            LoginUserDetail loginUser = loginUserOptional.get();
            SysUserAuth sysUserAuth = loginUser.getSysUserAuth();

            // 判断账号状态是否异常
            if(Objects.isNull(loginUser.getStatus())){
                throw new InnerCommonException(AppStatusEnum.SERVER_ERROR.getCode(),
                        AppStatusEnum.SERVER_ERROR.getMessage());
            }
            SystemStatusConstant.validStatus(loginUser.getStatus());
            // 密码匹配
            if (passwordEncoder.matches(secret, sysUserAuth.getCredential())) {
                UsernamePasswordAuthenticationToken passwordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                passwordAuthenticationToken.setDetails(loginUser);
                return passwordAuthenticationToken;
            }
        }
        // 登录失败情况，直接抛出异常会被ss捕获并处理,mvc无法获取异常
        throw new BadCredentialsException("用户名或密码错误");
    }

    private Authentication authenticateByPhoneCode(Authentication authentication) {
        return null;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return CustomPhoneCodeAuthenticationToken.class.isAssignableFrom(authentication) ||
                UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
