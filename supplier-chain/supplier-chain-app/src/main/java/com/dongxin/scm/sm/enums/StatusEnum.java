package com.dongxin.scm.sm.enums;

public enum StatusEnum {

    WRITE_OFF("write_off", "红冲"),

    //多次红冲涉及唯一索引暂时解决方案
    WRITE_OFF_TWO("write_off_two", "二次红冲"),

    WRITE_OFF_REVIEWER("write_off_reviewer","红冲复核"),

    All_CREATE_STACK("all_create", "已生成装车单"),

    NO_CREATE_STACK("no_create", "未生成装车单"),

    PART_CREATE_STACK("part_create", "部分生成装车单");

    private String code;

    private String desc;

    StatusEnum(String code, String name) {
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
