package com.dongxin.scm.fm.enums;

public enum IncomingTypeEnum {
    NORMAL("normal", "普通来款"),

    CREDIT("credit", "授信"),

    DISCOUNT("discount", "优惠");

    private final String code;
    private final String name;

    IncomingTypeEnum(String code, String name) {
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
