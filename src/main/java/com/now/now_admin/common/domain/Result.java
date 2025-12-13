package com.now.now_admin.common.domain;

import com.now.now_admin.constant.AppCodeConstant;
import lombok.Data;
import java.io.Serializable;

/**
 * 通用返回结果类
 * 用于封装API返回的结果，包含状态码、消息和数据
 */
@Data
public class Result<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;

    /**
     * =
     * 通用构造方法
     * 初始化状态码、消息和数据
     * 
     * @param code 状态码
     * @param msg  消息
     * @param data 数据
     */
    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 空构造器
     */
    public Result() {
    }

    /**
     * 静态方法，用于快速创建成功结果
     * 
     * @param <T>  返回类
     * @param data 数据
     * @return Result<T> 成功结果
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(AppCodeConstant.SUCCESS, "success", data);
    }

    /**
     * 静态方法，用于快速创建成功结果
     * 
     * @param <T> 返回类
     * @return Result<T> 成功结果
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    /**
     * 静态方法，用于快速创建失败结果
     * 
     * @param <T>  返回类
     * @param code 状态码
     * @param msg  消息
     * @return Result<T> 失败结果
     */
    public static <T> Result<T> fail(Integer code, String msg) {
        return new Result<T>(code, msg, null);
    }

    /**
     * 静态方法，用于快速创建失败结果
     * 
     * @param <T> 返回类
     * @return Result<T> 失败结果
     */
    public static <T> Result<T> clientFail() {
        return new Result<>(AppCodeConstant.CLIENT_ERROR, "client_fail", null);
    }
}
