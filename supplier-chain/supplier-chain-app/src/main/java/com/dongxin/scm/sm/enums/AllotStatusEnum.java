package com.dongxin.scm.sm.enums;

public enum AllotStatusEnum {
    ALLOTED("alloted", "完全分货"),

    NONALLOT("nonAllot", "未分货"),

    ALLOTPART("nonAllot", "未分货");

    private final String code;
    private final String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


    AllotStatusEnum(String value, String desc) {
        this.code = value;
        this.desc = desc;
    }

}
