package com.dongxin.scm.om.enums;

/**
 * @Description:
 * @Autor: liujiazhi
 * Date:2021/3/27
 * @Version: V1.0
 */
public enum PaymentFlagEnum {

    UNPAID("unpaid", "未支付"),

    PAID("paid", "已支付");

    private final String code;
    private final String name;

    PaymentFlagEnum(String code, String name) {
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
