package com.dongxin.scm.enums;

public enum AddTypeEnum {
    ADD("add", "新增"),
    MACHINING_ADD("machining_add", "加工新增");

    private String value;
    private String desc;

    AddTypeEnum(String value, String desc) {
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
