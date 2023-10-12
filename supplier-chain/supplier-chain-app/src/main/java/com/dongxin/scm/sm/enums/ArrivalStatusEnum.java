package com.dongxin.scm.sm.enums;

public enum ArrivalStatusEnum {

    ARRIVED("arrived", "到货"),

    PENDING("pending", "待确定");

    private final String code;
    private final String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


    ArrivalStatusEnum(String value, String desc) {
        this.code = value;
        this.desc = desc;
    }


}
