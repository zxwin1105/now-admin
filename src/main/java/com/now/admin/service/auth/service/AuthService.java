package com.now.admin.service.auth.service;

import com.now.admin.common.domain.vo.LoginRsp;
import com.now.admin.service.auth.domain.LoginUserDetail;
import com.now.admin.service.auth.domain.param.LoginUserParam;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

public interface AuthService {

    /**
     * 登录认证
     * 
     * @param loginUserParam 登录参数
     * @return 登录认证结果
     */
    LoginRsp authenticate(@Validated LoginUserParam loginUserParam);

    /**
     * 通过用户登录账户加载登录认证所需用户信息
     * 
     * @param account 登录账户
     * @return Optional<LoginUserDetail>
     */
    Optional<LoginUserDetail> loadUserByAccount(String account);

    /**
     * 通过用户ID加载用户信息
     * 
     * @param userId 用户ID
     * @return 登录用户
     */
    Optional<LoginUserDetail> loadUserById(Long userId);

}
