package com.dongxin.scm.bd.enums;

public enum TheoryAndNoTheoryEnum {


    DISPLAY("display", "显示"),

    NO_DISPLAY("no_display", "不显示");


    private String code;

    private String desc;

    TheoryAndNoTheoryEnum(String code, String name) {
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
