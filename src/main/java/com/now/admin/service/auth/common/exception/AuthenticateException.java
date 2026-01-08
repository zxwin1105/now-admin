package com.now.admin.service.auth.common.exception;

import com.now.admin.common.constant.AppStatusEnum;
import com.now.admin.common.exception.CommonException;

public class AuthenticateException extends CommonException {

    public AuthenticateException(AppStatusEnum appStatusEnum) {
        super(appStatusEnum.getCode(),appStatusEnum.getMessage());
    }

    public AuthenticateException(String message) {
        super(AppStatusEnum.UNAUTHORIZED.getCode() ,message);
    }

}
