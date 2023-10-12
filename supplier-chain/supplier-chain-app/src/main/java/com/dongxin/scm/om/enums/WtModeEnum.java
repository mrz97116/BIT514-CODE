package com.dongxin.scm.om.enums;

public enum WtModeEnum {

    REAL_WEIGHT("0", "实重"),

    WEIGHT_MANAGEMENT("1", "理重");

    private final String code;
    private final String name;

    WtModeEnum(String code, String name) {
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
