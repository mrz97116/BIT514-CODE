package com.dongxin.scm.fm.enums;

public enum FinanceDetailEnum {

    PAYMENT("payment","来款"),

    USE_FUND("use_fund","用款"),

    REFUND("refund","退款"),

    APPLY_REFUND("apply_refund","申请退款"),

    INIT_DATA("init_data","初始化数据"),

    DELETE_FUND_POOL("delete_fund_pool","删除来款");

    private String code;

    private String desc;

    FinanceDetailEnum(String code, String name) {
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
