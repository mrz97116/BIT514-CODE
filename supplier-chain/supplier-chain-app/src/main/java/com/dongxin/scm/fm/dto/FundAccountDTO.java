package com.dongxin.scm.fm.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author ：melon
 * @date ：Created in 2020/11/24 19:32
 */
@Data
public class FundAccountDTO {


    //未装车提单金额
    private BigDecimal unLoadSalesOrderAmount;

    private BigDecimal unSettleStackAmount;

    //来款可用余额
    private BigDecimal incomeAvailAmount;

    //授信可用余额
    private BigDecimal creditAvailAmount;

    //总授信额度
    private BigDecimal totalCreditAmount;


    //前端 可开单金额
    public BigDecimal getTotalAvailableAmount() {
        return incomeAvailAmount.add(creditAvailAmount).subtract(unLoadSalesOrderAmount);
    }

    //授信金额
    public BigDecimal getCreditAvailAbleAmount() {
        return creditAvailAmount;
    }

    //提单可用金额 老逻辑添加了冻结金额，新逻辑就是可用余额累加
    public BigDecimal getOrderAvailableAmount() {
        return incomeAvailAmount.add(creditAvailAmount).add(unSettleStackAmount);
    }

    //获取授信账户应还款额度
    public BigDecimal getNeedPayAmount() {
        return totalCreditAmount.subtract(creditAvailAmount);
    }
}
