package com.dongxin.scm.wms;

import lombok.Getter;

/**
 * 仓储系统响应状态码及描述
 */
@Getter
public enum WMSHttpStatus {
    NO_AUTH_PARAM(412, "未传入appId或者secretKey的值"),
    UNDEFINED_AUTH_PARAM(401, "找不到对应的appId或者secretKey"),
    NOT_WHITELIST_IP(416, "识别不出来或者匹配不上对应的IP地址"),
    REQUEST_TIMEOUT(408, "请求超时或者secretKey加上的时间拼写未在三分钟之内"),
    BAD_REQUEST(400, "在处理appid和secretkey验证发生意外的错误"),
    INTERNAL_SERVER_ERROR(500, "服务在处理请求的过程中，发生错误");

    private final int code;
    private final String message;

    WMSHttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据传入状态码解析获取对应的WMSHttpStatus对象
     * @param statusCode Http响应状态码
     */
    public static WMSHttpStatus resolve(int statusCode) {
        for (WMSHttpStatus status : WMSHttpStatus.values()) {
            if (status.code == statusCode) {
                return status;
            }
        }
        throw new IllegalArgumentException("无此状态码对应说明: " + statusCode);
    }

    @Override
    public String toString() {
        return "响应码: " + code + ", 错误信息: " + message;
    }
}
