package com.dongxin.scm.om.enums;

/**
 * Created by Lenovo on 2021/1/13.
 */
public enum ContractFlagEnum {

    NORMAL("normal", "正常"),

    TERMINATION("termination", "终止"),

    FINISH("finish", "完结");

    private final String code;
    private final String name;

    ContractFlagEnum(String code, String name) {
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
