package com.dongxin.scm.enums;

public enum YesNoEnum {

    YES("1", "是"),

    NO("0", "否");

    private String code;

    private String desc;

    YesNoEnum(String code, String name) {
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
