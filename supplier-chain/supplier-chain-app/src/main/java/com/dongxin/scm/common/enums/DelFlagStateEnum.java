package com.dongxin.scm.common.enums;

public enum DelFlagStateEnum {
    DELETED("1","删除"),
    UNDELETE("0","正常");

    private String name;
    private String code;

    DelFlagStateEnum(String code, String name){
        this.code=code;
        this.name=name;
    }
    public String getCode(){
        return code;
    }
    public String getName(){
        return name;
    }

}
