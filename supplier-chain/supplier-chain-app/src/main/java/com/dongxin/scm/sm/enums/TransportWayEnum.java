package com.dongxin.scm.sm.enums;

public enum TransportWayEnum {

    CARTAGE("cartage", "车运"),

    SHIPPING("shipping", "船运");

    private String code;

    private String desc;

    TransportWayEnum(String code, String name) {
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
