package com.dongxin.scm.enums;

/**
 * @author ：melon
 * @date ：Created in 2021-11-1 18:42
 */
public enum WmsOperateEnum {

    I("I", "插入"),
    D("D", "删除");

    private String value;
    private String desc;

    WmsOperateEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }


}
