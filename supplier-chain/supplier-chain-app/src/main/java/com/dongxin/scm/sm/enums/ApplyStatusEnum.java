package com.dongxin.scm.sm.enums;

public enum ApplyStatusEnum {

    INIT("init", "初始状态"),

    COMMITTED("committed", "已提交"),

    VERIFIED("verified", "已审核");

    private final String code;
    private final String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


    ApplyStatusEnum(String value, String desc) {
        this.code = value;
        this.desc = desc;
    }


}
