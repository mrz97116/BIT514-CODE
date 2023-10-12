package com.dongxin.scm.sm.enums;

public enum MaterialSummaryEnum {

//    TOTAL("total", "钢材"),

    B("B", "螺纹钢"),

//    CE("CE", "中型"),

    D("D", "线材"),
//    E("E", "圆钢"),
    F("F", "热卷"),
//    G("G", "冷轧板卷材"),
    H("H", "板材");
//    I("I","管钢"),
//    J("J","不锈钢");
//    PURCHASED_MATERIALS("purchased_materials","外购材"),
//
//    PURCHASED_STAINLESS_STEEL("purchased_stainless_steel","外购不锈钢"),
//
//    COLD_ROLL("cold_roll","冷卷"),
//
//    COLD_ROLLED_SUBSTRATE("cold_rolled_substrate","冷轧基板");

    private String code;

    private String desc;

    MaterialSummaryEnum(String code, String name) {
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
