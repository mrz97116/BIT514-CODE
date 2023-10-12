package com.dongxin.scm.sm.enums;

public enum StorageStatusEnum {

    STORED("stored", "已实物入库"),

    NOT_STORED("not_stored", "未实物入库");

    private String code;

    private String desc;

    StorageStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
