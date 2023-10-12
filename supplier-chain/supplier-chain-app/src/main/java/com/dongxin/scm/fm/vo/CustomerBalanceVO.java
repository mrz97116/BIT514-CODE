package com.dongxin.scm.fm.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerBalanceVO {
    private String customerId;
    private BigDecimal customerBalance;
    private String tenantId;
    private String customerName;
}
