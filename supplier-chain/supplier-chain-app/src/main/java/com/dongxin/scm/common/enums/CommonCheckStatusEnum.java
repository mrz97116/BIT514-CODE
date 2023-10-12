package com.dongxin.scm.common.enums;

/**
 * 通用审核
 */
public enum CommonCheckStatusEnum {
    PENDING_VERIFY("pending_verify", "待审核"),

    APPROVE("approve", "已通过"),

    REJECT("reject", "未通过");

    private String code;

    private String name;

    CommonCheckStatusEnum(String code, String name) {
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
