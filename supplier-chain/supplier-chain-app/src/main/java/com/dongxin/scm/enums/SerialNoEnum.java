package com.dongxin.scm.enums;

public enum SerialNoEnum {
    SALESORDER_NO("SALESORDER_NO", "提单编号"),

    INVENTORY_NO("INVENTORY_NO", "锁定单号"),

    STACK_NO("STACK_NO", "装车单编号"),

    STOCK_NO("STOCK_NO", "入库编号"),

    PAYMENT_RULE("PAYMENT_RULE", "付款编码"),

    EXAMPLE("EXAMPLE", "样例"),

    ATTORNEY_NO("ATTORNEY_NO", "委托书编号"),

    CREDIT_NO("CREDIT_NO", "授信编号"),

    SETTLE_NO("SETTLE_NO", "结算单编号"),

    CONTRACT_NO("CONTRACT_NO", "合同编号"),

    RECEIPT_NO("RECEIPT_NO", "收据编号"),

    MACHINING_NO("MACHINING_NO", "加工编号"),

    EQUIPMENT_SUPPLIES_NO("EQUIPMENT_SUPPLIES_NO", "设备物资单号"),

    WRITE_OFF_NO("WRITE_OFF_NO", "红冲编号"),

    FINANCE_DETAIL_NO("FINANCE_DETAIL_NO", "财务明细编号"),

    FUND_ACCOUNTRECEIVABLE_NO("FUND_ACCOUNTRECEIVABLE_NO", "应收账款流水号"),

    BATCH_SETTLE_NO("BATCH_SETTLE_NO", "批量结算编号"),

    JZ_SALESORDER_NO("JZ_SALESORDER_NO", "建筑类提单编号"),

    GY_SALESORDER_NO("GY_SALESORDER_NO", "工业类提单编号"),

    JZ_STACK_NO("JZ_STACK_NO", "建筑类装车单编号"),

    GY_STACK_NO("GY_STACK_NO", "工业类装车单编号"),

    JZ_SETTLE_NO("JZ_SETTLE_NO", "建筑类结算单编号"),

    GY_SETTLE_NO("GY_SETTLE_NO", "工业类结算单编号"),

    PREPARE_ORDER_NO("PREPARE_ORDER_NO", "预开单号"),

    GOOD_ENTRUSTMENT_LETTER_NO("GOOD_ENTRUSTMENT_LETTER_NO","提货委托单号"),

    GD_EXTEND_NO_ZB("GD_EXTEND_NO_ZB","广东现货单号中板"),

    GD_EXTEND_NO_RZ("GD_EXTEND_NO_RZ","广东现货单号热轧"),

    GD_EXTEND_NO_LZ("GD_EXTEND_NO_LZ","广东现货单号冷轧");

    private String bizCode;

    private String desc;

    SerialNoEnum(String bizCode, String name) {
        this.bizCode = bizCode;
        this.desc = name;
    }

    public String getBizCode() {
        return bizCode;
    }

    public String getDesc() {
        return desc;
    }
}
