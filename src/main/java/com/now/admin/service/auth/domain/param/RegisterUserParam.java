package com.now.admin.service.auth.domain.param;

import lombok.Data;

/**
 * 用户注册参数
 */
@Data
public class RegisterUserParam {

    private String username;
    private String password;
    private String confirmPassword;

}
