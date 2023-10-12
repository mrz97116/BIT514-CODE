package com.dongxin.scm.om.enums;

/**
 * @Description:
 * @Autor: liujiazhi
 * Date:2021/5/28
 * @Version: V1.0
 */
public enum OrderStockEnum {

    SINGLE_STOCK("0", "同间仓库"),

    MORE_STOCK("1", "多间仓库");

    private final String code;
    private final String name;

    OrderStockEnum(String code, String name) {
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
