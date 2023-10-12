package com.dongxin.scm.enums;

public enum ProdClassCodeEnum {

    A("A", "铸坯"),
    B("B", "棒材"),
    C("C", "型材"),
    D("D", "线材"),
    E("E", "圆钢"),
    F("F", "热轧板卷材"),
    G("G", "冷轧板卷材"),
    H("H", "中厚板材"),
    I("I","管钢"),
    J("J","不锈钢"),
    Z("Z","热轧开平板");

    private String value;
    private String desc;

    ProdClassCodeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
