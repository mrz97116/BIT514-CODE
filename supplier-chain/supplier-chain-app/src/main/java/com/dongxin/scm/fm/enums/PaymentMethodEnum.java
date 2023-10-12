package com.dongxin.scm.fm.enums;

public enum PaymentMethodEnum {

    CASH("cash", "现金"),

    ACCEPTANCE_BILL("acceptance_bill", "承兑汇票"),

    ADVANCE_CHARGE("advance_charge", "预付款");

    private final String code;
    private final String name;

    PaymentMethodEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
