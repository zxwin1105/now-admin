package com.now.admin.common.exception;


public class InnerCommonException extends CommonException {

    public InnerCommonException(String message) {
        super(message);
    }

    public InnerCommonException(Integer code, String message) {
        super(code, message);
    }
}
