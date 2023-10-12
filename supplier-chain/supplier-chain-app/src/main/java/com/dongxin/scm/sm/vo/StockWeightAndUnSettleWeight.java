package com.dongxin.scm.sm.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockWeightAndUnSettleWeight {

    private BigDecimal stockWeight;

    private BigDecimal unSettleWeight;
}
