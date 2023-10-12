package com.dongxin.scm.fm.enums;

public enum IncomeMethodTypeEnum {
    NORMAL_PAYMENT("normal_payment", "正常来款"),

    SALES_RETURN("sales_return","客户退货"),

    RED_SCOUR("red_scour", "红冲");


    private String code;

    private String desc;

    IncomeMethodTypeEnum(String code, String name) {
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
