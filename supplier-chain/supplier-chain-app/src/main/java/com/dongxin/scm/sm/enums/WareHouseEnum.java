package com.dongxin.scm.sm.enums;

public enum WareHouseEnum {
    STOCK_PENDING("0","待入库"),

    STOCK_DELETE("1","删除");

    private String code;

    private String desc;

    WareHouseEnum(String code, String desc) {
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
