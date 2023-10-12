package com.dongxin.scm.sm.enums;

public enum StockTypeEnum {

    ON_ROUTE("on_route", "在途"),

    ON_BOARD("on_board","即将入库"),

    ON_READY("on_ready", "待确定"),

    ON_STOCK("on_stock", "已入库");

    private String code;

    private String desc;

    StockTypeEnum(String code, String desc) {
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
