package com.dongxin.scm.fm.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author ：melon
 * @date ：Created in 2021-8-19 10:03
 */
@Data
public class CreditDTO {

    private BigDecimal totalCreditAmount;

    private BigDecimal totalAvailAmount;
}
