package com.now.admin.common.exception;

import com.now.admin.common.constant.AppStatusEnum;
import lombok.Getter;

public class CommonException extends RuntimeException{

    @Getter
    private final Integer code;

    @Getter
    private final String message;

    public CommonException(Integer code,String message) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public CommonException(AppStatusEnum appStatusEnum) {
        super(appStatusEnum.getMessage());
        this.message = appStatusEnum.getMessage();
        this.code = appStatusEnum.getCode();
    }

    public CommonException(String message) {
        super(message);
        this.message = message;
        this.code = 400;
    }
}
