package com.dongxin.scm.fm.enums;

/**
 * 通用审核
 */
public enum CreditTypeStatusEnum {

    ADD("add", "增加"),

    REDUCE("reduce", "减少");

    private String code;

    private String name;

    CreditTypeStatusEnum(String code, String name) {
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
