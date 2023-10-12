package com.dongxin.scm.sm.enums;

public enum InventoryLockEnum {

    LOCK("lock", "锁定"),

    UNLOCK("unlock", "解锁");

    private final String code;
    private final String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


    InventoryLockEnum(String value, String desc) {
        this.code = value;
        this.desc = desc;
    }
}
