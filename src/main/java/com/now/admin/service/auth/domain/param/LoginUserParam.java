package com.now.admin.service.auth.domain.param;

import lombok.Data;

/**
 * 登录用户参数
 */
@Data
public class LoginUserParam {

    /**
     * 用户登录账号， 可以是手机号、邮箱、用户名
     */
    private String account;

    /**
     * 用户登录密码， 公钥加密处理的密码，验证码
     */
    private String secret;

    /**
     * 登录方式 1-用户名密码 2-手机验证码 3-邮箱验证码
     */
    private LoginTypeEnum type;

    /**
     * 登录平台
     */
    private PlatformEnum platform;

    public enum LoginTypeEnum {
        PASSWORD,
        PHONE_CODE,
    }

    public enum PlatformEnum {
        WEB,
        APP
    }

}
