package com.dongxin.scm.trustedthird.utils;

import lombok.Data;

/**
 * @author Administrator
 */
@Data
public class Result<T> {

    private static final long serialVersionUID = -8713837118340960775L;

    /** 成功*/
    private static final Integer SUCCESS = 0;
    /** 警告*/
    private static final Integer WARN = 1;
    /** 异常 失败*/
    private static final Integer FAIL = 500;

    private Integer code;

    private String message;

    private Object result;

    private boolean success;

    public Result() {
    }

    public Result(Integer code) {
        this.code = code;
    }

    public Result(Object data) {
        this.result = data;
    }

    public Result(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public Result(Integer code, Object data) {
        this.code = code;
        this.result = data;
    }

    public Result(Integer code, String msg, Object data) {
        this.code = code;
        this.message = msg;
        this.result = data;
    }

    public Result(Integer code, String msg, Object data,boolean success) {
        this.code = code;
        this.message = msg;
        this.result = data;
        this.success = success;
    }

    /**
     * 失败并返回数据
     * @param data
     * @return
     */
    public static Result error(Object data) {
        return new Result(FAIL, data);
    }

    /**
     * 警告并返回数据
     * @param data
     * @return
     */
    public static Result warn(Object data) {
        return new Result(WARN, data);
    }

    /**
     * 成功并返回数据
     * @param msg
     * @param data
     * @return
     */
    public static Result ok(String msg, Object data) {
        return new Result(SUCCESS, msg, data);
    }

    /**
     * 带数据
     * @param data
     * @return
     */
    public static Result ok(Object data) {
        return new Result(SUCCESS,data);
    }

    /**
     * 成功返回通用数据
     * @return
     */
    public static Result ok() {
        return new Result(SUCCESS, "操作成功");
    }

    public static Result error() {
        return Result.error("");
    }
}
