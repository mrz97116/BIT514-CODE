package com.dongxin.scm.fm.enums;

public enum StatementStateEnum {
    NORMAL("normal","正常"),

    WRITE_OFF("write","红冲");

    private String code;

    private String desc;

    StatementStateEnum(String code, String name) {
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
