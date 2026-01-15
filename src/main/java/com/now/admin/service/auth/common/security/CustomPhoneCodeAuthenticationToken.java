package com.now.admin.service.auth.common.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collection;

public class CustomPhoneCodeAuthenticationToken extends AbstractAuthenticationToken {
    private final String phone;
    private final String code;


    public CustomPhoneCodeAuthenticationToken(String phone, String code) {
        super((Collection) null);
        this.phone = phone;
        this.code = code;
        setAuthenticated(false);
    }


    @Override
    public Object getCredentials() {
        return code;
    }

    @Override
    public Object getPrincipal() {
        return phone;
    }
}
