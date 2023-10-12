package com.dongxin.scm.fm.enums;

public enum AccountTypeEnum {


    CREDIT("credit", "授信"),

    INCOME("income", "来款");


    private String code;

    private String desc;

    AccountTypeEnum(String code, String name) {
        this.code = code;
        this.desc = name;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
