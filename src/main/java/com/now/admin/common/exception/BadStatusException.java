package com.now.admin.common.exception;

import com.now.admin.common.constant.AppStatusEnum;

public class BadStatusException extends CommonException{
    public BadStatusException(Integer code, String message) {
        super(code, message);
    }

    public BadStatusException(AppStatusEnum appStatusEnum) {
        super(appStatusEnum);
    }

    public BadStatusException(String message) {
        super(message);
    }
}
