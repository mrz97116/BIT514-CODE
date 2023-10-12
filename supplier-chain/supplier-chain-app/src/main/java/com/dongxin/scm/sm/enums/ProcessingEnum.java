package com.dongxin.scm.sm.enums;

public enum ProcessingEnum {


        PROCESSING("processing", "加工"),

        NO_PROCESSING("no_processing","未加工");


    private String code;

    private String desc;

    ProcessingEnum(String code, String name) {
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
