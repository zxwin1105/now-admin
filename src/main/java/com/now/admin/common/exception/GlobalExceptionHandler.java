package com.now.admin.common.exception;

import com.now.admin.common.domain.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    private Result<String> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return Result.failure(e.getMessage());
    }

    @ExceptionHandler(CommonException.class)
    private Result<String> handleAuthenticateException(CommonException e) {
        Integer code = e.getCode();
        String msg = e.getMessage();
        log.error("{}:{}", code, msg);
        return Result.fail(code, msg);
    }
}
