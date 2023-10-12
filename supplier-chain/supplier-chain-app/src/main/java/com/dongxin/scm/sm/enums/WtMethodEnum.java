package com.dongxin.scm.sm.enums;

public enum WtMethodEnum {
    ACTUAL("0","实重"),
    THEORY("1","理计");

    private String value;
    private String desc;

    WtMethodEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
