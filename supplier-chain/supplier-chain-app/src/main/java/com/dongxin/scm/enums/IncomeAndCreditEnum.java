package com.dongxin.scm.enums;

public enum IncomeAndCreditEnum {
    CASH("cash", "现款"),
    ACCEPTANCE_BILL("acceptance_bill", "承兑汇票"),
    CREDIT("credit", "授信");

    private String code;

    private String desc;

    IncomeAndCreditEnum(String code, String name) {
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
