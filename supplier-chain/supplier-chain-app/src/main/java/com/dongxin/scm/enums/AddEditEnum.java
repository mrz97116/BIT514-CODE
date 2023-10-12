package com.dongxin.scm.enums;

public enum AddEditEnum {

    ADD("add", "添加"),

    EDIT("edit", "编辑");

    private String code;

    private String name;

    AddEditEnum(String code, String name) {
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
