package com.dongxin.scm.wms.exception;

import com.dongxin.scm.wms.WMSHttpStatus;
import lombok.Getter;

/**
 * 请求失败时抛出的异常信息
 */
public class WMSException extends Exception {

    @Getter
    private String api;

    @Getter
    private int statusCode;

    @Getter
    private String reason;

    public WMSException(String api, int statusCode) {
        this.api = api;
        this.statusCode = statusCode;
        this.reason = WMSHttpStatus.resolve(statusCode).getMessage();
    }

    public WMSException(String api, int statusCode, String reason) {
        this.api = api;
        this.statusCode = statusCode;
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        if (0 == statusCode) {
            return "请求API: " + this.api + " 失败! 未得到目标服务器响应, 错误信息: " + this.reason;
        }
        return "请求API: " + this.api + " 失败! 响应码: " + this.statusCode + ", 错误信息: " + this.reason;
    }
}
