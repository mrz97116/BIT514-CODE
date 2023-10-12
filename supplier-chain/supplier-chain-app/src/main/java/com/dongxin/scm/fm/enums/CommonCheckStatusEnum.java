package com.dongxin.scm.fm.enums;

public enum CommonCheckStatusEnum {
    PENDING_VERIFY("pending_verify", "待审核"),

    APPROVE("approve", "已通过");

    private String code;

    private String desc;

    CommonCheckStatusEnum(String code, String name) {
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
