package com.dongxin.scm.om.enums;

/**
 * @Description:
 * @Autor: liujiazhi
 * Date:2021/3/27
 * @Version: V1.0
 */
public enum LogisticsEnum {

    LOGISTICS("logistics", "已推送"),

    LOGISTICS_TRA("logistics_tra", "已推送（过户）"),

    NO_LOGISTICS("no_logistics", "不推送"),

    WAIT_PUSH("wait_push", "待推送"),

    LOGISTICS_OK("logistics_ok", "已回推"),

    LOGISTICS_OK_CONFIRMED("logistics_ok_confirmed", "回推已确认"),

    LOGISTICS_ERR("logistics_err", "推送失败");

    private final String code;
    private final String name;

    LogisticsEnum(String code, String name) {
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
