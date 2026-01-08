package com.now.admin.common.constant;

import lombok.Getter;

/**
 * 项目通用静态状态码
 */
public enum AppStatusEnum {

    SUCCESS(200, "成功"),
    CLIENT_ERROR(400, "客户端错误"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "未授权"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "方法不允许"),
    VALIDATE_TOKEN_FAIL(406, "Token验证失败"),
    TOKEN_EXPIRED(407, "Token已过期"),
    SERVER_ERROR(500, "服务器内部错误");


    @Getter
    private final Integer code;

    @Getter
    private final String message;



    private AppStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }



}
