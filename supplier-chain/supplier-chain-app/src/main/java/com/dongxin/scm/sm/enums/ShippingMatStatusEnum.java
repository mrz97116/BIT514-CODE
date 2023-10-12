package com.dongxin.scm.sm.enums;

public enum ShippingMatStatusEnum {

    ON_BOARD("ON_BOARD", "即将入库"),

    PREPARE_ORDER("PREPARE_ORDER", "分货"),

    DELIVERY_COMMISSION("DELIVERY_COMMISSION", "提货委托"),

    IN_STOCK("IN_STOCK", "已入库");

    private String code;

    private String desc;

    ShippingMatStatusEnum(String code, String desc) {
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
