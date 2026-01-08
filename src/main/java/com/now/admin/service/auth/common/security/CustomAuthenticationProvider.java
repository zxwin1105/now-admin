package com.now.admin.service.auth.common.security;

import com.now.admin.service.auth.domain.LoginUserDetail;
import com.now.admin.service.auth.domain.SysUserAuth;
import com.now.admin.service.auth.service.AuthService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(authentication instanceof UsernamePasswordAuthenticationToken){
            return authenticateByPwd(authentication);
        }else  if(authentication instanceof CustomPhoneCodeAuthenticationToken){
            return authenticateByPhoneCode(authentication);
        }
        throw new UnsupportedOperationException("Unsupported authentication method");
    }

    private @Nullable Authentication authenticateByPwd(Authentication authentication) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) authentication;
        String account = String.valueOf(authenticationToken.getPrincipal());
        String secret = String.valueOf(authenticationToken.getCredentials());
        Optional<LoginUserDetail> loginUserOptional = authService.loadUserByAccount(account);
        if(loginUserOptional.isPresent()){
            LoginUserDetail loginUser = loginUserOptional.get();
            SysUserAuth sysUserAuth = loginUser.getSysUserAuth();
            if(passwordEncoder.matches(secret, sysUserAuth.getCredential())){
                // 密码匹配
                UsernamePasswordAuthenticationToken passwordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, null);
                passwordAuthenticationToken.setDetails(loginUser);
                return passwordAuthenticationToken;
            }
        }
        throw new BadCredentialsException("Invalid credentials");
    }

    private @Nullable Authentication authenticateByPhoneCode(Authentication authentication) {
        return null;
    }



    @Override
    public boolean supports(Class<?> authentication) {
        return CustomPhoneCodeAuthenticationToken.class.isAssignableFrom(authentication) ||
                UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
