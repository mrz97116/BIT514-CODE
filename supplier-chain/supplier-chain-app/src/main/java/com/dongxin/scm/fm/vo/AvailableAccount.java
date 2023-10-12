package com.dongxin.scm.fm.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AvailableAccount {
    private BigDecimal incomeAvailableAccount;

    private BigDecimal creditAvailableAccount;
}
