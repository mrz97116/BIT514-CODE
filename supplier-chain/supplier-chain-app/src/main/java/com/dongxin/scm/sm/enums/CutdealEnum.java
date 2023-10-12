package com.dongxin.scm.sm.enums;

public enum CutdealEnum {
    YES("0","是"),

    NO("1","否");

    private String code;

    private String desc;

    CutdealEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }



}
