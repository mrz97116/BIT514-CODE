package com.dongxin.scm.om.enums;

public enum WmsTypeEnum {

    PUSH_ORDER("push_order", "推送提单"),

    CANCEL_PUSH_ORDER("cancel_push_order", "撤销推送提单"),

    PUSH_TRANSFER("push_transfer", "推送过户"),

    CANCEL_PUSH_TRANSFER("cancel_push_transfer", "撤销推送过户");

    private final String code;
    private final String name;

    WmsTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
