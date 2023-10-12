package com.dongxin.scm.fm.enums;

public enum SettleStatusEnum {
    SETTLED("settled", "已结算"),

    UNSETTLE("unsettle", "未结算");

    private String code;

    private String desc;

    SettleStatusEnum(String code, String name) {
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
