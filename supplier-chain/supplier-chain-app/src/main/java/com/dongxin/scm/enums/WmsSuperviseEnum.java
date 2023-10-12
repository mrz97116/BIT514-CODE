package com.dongxin.scm.enums;


public enum WmsSuperviseEnum {

    SUPERVISE("supervise", "物流监管"),
    DISTRIBUTION("distribution", "配送并监管"),
    NO_SUP("no_sup", "不监管");

    private String value;
    private String desc;

    WmsSuperviseEnum(String value, String desc) {
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
