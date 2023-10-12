package com.dongxin.scm.sm.enums;

public enum StockActiveEnum {

    ENABLE("1", "启用"),

    DISABLE("0", "禁用");

    private String code;

    private String desc;

    StockActiveEnum(String code, String name) {
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
