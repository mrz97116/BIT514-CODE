package com.dongxin.scm.fm.enums;

public enum InitFundPoolTypeEnum {
    MONEY("money", "现金"),

    ACCEPTANCE("acceptance", "承兑");


    private String code;

    private String desc;

    InitFundPoolTypeEnum(String code, String name) {
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
