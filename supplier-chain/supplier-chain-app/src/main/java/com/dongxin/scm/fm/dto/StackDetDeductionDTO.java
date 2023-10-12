package com.dongxin.scm.fm.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author ：melon
 * @date ：Created in 2020/11/5 11:13
 */
@Data
public class StackDetDeductionDTO {

    //装车单明细总金额
    private BigDecimal stackDetTotalAmount;

    //扣款id
    private String deductionId;
}
