package com.dongxin.scm.fm.enums;

public enum FundPoolDetailTypeEnum {


    AVAILABLE_STATUS("available", "可用"),

    UNAVAILABLE_STATUS("unavailable", "不可用");

    private String code;

    private String desc;

    FundPoolDetailTypeEnum(String code, String name) {
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
