package com.now.admin.service.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.now.admin.common.domain.vo.LoginRsp;
import com.now.admin.service.auth.common.security.CustomPhoneCodeAuthenticationToken;
import com.now.admin.service.auth.domain.LoginUserDetail;
import com.now.admin.service.auth.domain.SysUserAuth;
import com.now.admin.service.auth.domain.param.LoginUserParam;
import com.now.admin.service.auth.service.AuthService;
import com.now.admin.service.auth.service.SysUserAuthService;
import com.now.admin.service.sys.domain.SysUser;
import com.now.admin.service.sys.service.SysUserService;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private SysUserAuthService sysUserAuthService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private TokenService tokenService;

    @Override
    public LoginRsp authenticate(LoginUserParam loginUserParam) {
        LoginUserParam.LoginTypeEnum loginType = loginUserParam.getType();

        // 根据登录类型进行不同认证，获取认证结果authentication
        Authentication authentication = switch (loginType) {
            case LoginUserParam.LoginTypeEnum.PASSWORD -> {
                Authentication usernameToken = new UsernamePasswordAuthenticationToken(loginUserParam.getAccount(),
                        loginUserParam.getSecret());
                yield authenticationManager.authenticate(usernameToken);
            }
            case LoginUserParam.LoginTypeEnum.PHONE_CODE -> {
                Authentication PhoneCodeToken = new CustomPhoneCodeAuthenticationToken(loginUserParam.getAccount(),
                        loginUserParam.getSecret());
                yield authenticationManager.authenticate(PhoneCodeToken);
            }
        };

        if (authentication.isAuthenticated()) {
            // 认证成功，返回登录结果
            LoginUserDetail details = (LoginUserDetail) authentication.getDetails();
            if (details == null) {
                throw new BadCredentialsException("Invalid credentials");
            }

            return LoginRsp.builder()
                    .id(details.getId())
                    .userId(details.getUserId())
                    .token(tokenService.generateToken(details.getId()))
                    .refreshToken(tokenService.generateRefreshToken(details.getId()))
                    .build();
        }
        throw new BadCredentialsException("Invalid credentials");
    }

    @Override
    public Optional<LoginUserDetail> loadUserByAccount(String account) {
        if (account.isBlank()) {
            return Optional.empty();
        }
        LoginUserDetail loginUser = new LoginUserDetail();
        // 查询认证信息
        SysUserAuth sysUserAuth = sysUserAuthService.getOne(
                new LambdaQueryWrapper<SysUserAuth>().eq(SysUserAuth::getIdentifier, account));
        if (sysUserAuth == null) {
            return Optional.empty();
        }
        // 查询用户信息

        Long userId = sysUserAuth.getUserId();
        SysUser sysUser = sysUserService.getById(userId);
        BeanUtils.copyProperties(sysUser, loginUser);
        loginUser.setSysUserAuth(sysUserAuth);

        return Optional.of(loginUser);
    }

    @Override
    public Optional<LoginUserDetail> loadUserById(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }

        // 查询用户信息
        SysUser sysUser = sysUserService.getById(userId);
        if (sysUser == null) {
            return Optional.empty();
        }

        LoginUserDetail loginUser = new LoginUserDetail();
        BeanUtils.copyProperties(sysUser, loginUser);

        // 查询认证信息
        SysUserAuth sysUserAuth = sysUserAuthService.getOne(
                new LambdaQueryWrapper<SysUserAuth>().eq(SysUserAuth::getUserId, userId));
        loginUser.setSysUserAuth(sysUserAuth);

        return Optional.of(loginUser);
    }
}
