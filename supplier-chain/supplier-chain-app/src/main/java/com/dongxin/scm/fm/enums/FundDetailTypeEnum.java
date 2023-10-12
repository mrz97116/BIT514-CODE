package com.dongxin.scm.fm.enums;

public enum FundDetailTypeEnum {


    CREDIT("credit", "授信"),//

    INCOME("income", "来款"),

    SETTLE("settle", "结算"),//

    PRE_USE("pre_use", "预用"),//

    PRE_USE_STACK("pre_use_stack", "装车单预用"),//

    REFUND("refund", "退款"),//

    CUSTOMER_REFUND("customer_refund","客户退款"),


//    REFUND_TO_AVAILABLE("refund_to_available", "退差价"),

//    PAY_CREDIT("pay_credit", "授信还款"),

    DISCOUNT("discount", "折扣"),//

//    THAW_REFUND("thaw_refund", "退款解冻"),

//    FROZEN_REFUND("frozen_refund", "退款冻结"),

//    MEN_SETTLE("material_equipment_network_settle", "设备物资结算"),//

//    OLD_PRE_USE("old_pre_ues","废弃预用"),

//    OLD_SETTLE("old_settle","废弃结算"),

//    OLD_DISCOUNT("old_discount","废弃折扣"),

    WRITE_OFF("write_off", "红冲");

//    FUND_POOL_DELETE_REFUND_CREDIT("fund_pool_delete_refund_credit","来款删除退款(授信)"),

//    FUND_POOL_DELETE_REFUND_INCOME("fund_pool_delete_refund_income","来款删除退款(来款)"),

//    WRITE_OFF_PRE_USE("write_off_pre_ues","红冲预用"),

//    REPAYMENT("repayment","还款");




    private String code;

    private String desc;

    FundDetailTypeEnum(String code, String name) {
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
