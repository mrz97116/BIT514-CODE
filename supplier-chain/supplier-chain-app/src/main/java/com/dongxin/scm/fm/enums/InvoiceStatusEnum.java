package com.dongxin.scm.fm.enums;

public enum InvoiceStatusEnum {
    YES("yes", "已生成"),

    NO("no", "未生成");

    private final String code;
    private final String name;

    InvoiceStatusEnum(String code, String name) {
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
