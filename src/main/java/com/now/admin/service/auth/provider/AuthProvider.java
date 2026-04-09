package com.now.admin.service.auth.provider;

import com.now.admin.service.auth.domain.LoginUserDetail;
import com.now.admin.service.auth.util.SecurityContextUtil;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * auth服务对外提供auth相关接口
 */
@Service
public class AuthProvider {

    /**
     * 获取当前登录用户信息
     * @return Optional<LoginUserDetail>
     */
    public Optional<LoginUserDetail> getLoginUserDetail() {
        return SecurityContextUtil.getCurrentUser();
    }
}
