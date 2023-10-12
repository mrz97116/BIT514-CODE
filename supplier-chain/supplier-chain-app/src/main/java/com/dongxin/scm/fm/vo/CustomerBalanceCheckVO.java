package com.dongxin.scm.fm.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerBalanceCheckVO {
    private String customerId;

    private String customerName;

    private BigDecimal customerAccount;

    private BigDecimal fundPoolAccount;

    private BigDecimal settleAccount;

    private BigDecimal equipmentSuppliesSettleAccount;

    private BigDecimal approveRefundRecordsAccount;

    private BigDecimal unApproveRefundRecordsAccount;

    private BigDecimal balance;

    private BigDecimal difference;
}
