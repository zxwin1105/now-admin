package com.now.admin.service.auth.common.security;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collection;

public class CustomPhoneCodeAuthenticationToken extends AbstractAuthenticationToken {
    private final String phone;
    private final String code;
    

    public CustomPhoneCodeAuthenticationToken(@Nullable String phone, @Nullable String code) {
        super((Collection)null);
        this.phone = phone;
        this.code = code;
        setAuthenticated(false);
    }


    @Override
    public @Nullable Object getCredentials() {
        return code;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return phone;
    }
}
