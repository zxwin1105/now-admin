package com.now.now_admin.constant;

/**
 * 项目通用静态状态码
 */
public interface AppCodeConstant {

    /**
     * 成功状态码
     */
    Integer SUCCESS = 200;

    /**
     * 服务器错误状态码
     */
    Integer SERVER_ERROR = 500;

    /**
     * 客户端错误状态码
     */
    Integer CLIENT_ERROR = 400;

    /**
     * 未认证状态码
     */
    Integer UNAUTHORIZED = 401;

    /**
     * 未授权状态码
     */
    Integer FORBIDDEN = 403;

    /**
     * 资源不存在状态码
     */
    Integer NOT_FOUND = 404;

    /**
     * 方法不允许状态码
     */
    Integer METHOD_NOT_ALLOWED = 405;

}
