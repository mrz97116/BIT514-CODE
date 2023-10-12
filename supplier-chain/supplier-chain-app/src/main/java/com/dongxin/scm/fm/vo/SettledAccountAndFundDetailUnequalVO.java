package com.dongxin.scm.fm.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettledAccountAndFundDetailUnequalVO {
    private String stackId;
    private BigDecimal settledAccount;
    private BigDecimal fundDetailAccount;
}
