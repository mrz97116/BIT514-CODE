package com.dongxin.scm.fm.dto;

import lombok.Data;

import java.math.BigDecimal;


/**
 * @author ：melon
 * @date ：Created in 2020/11/4 16:27
 * @desc : 来款扣除金额和来款
 */
@Data
public class FundPoolDTO {

    //资金id
    private String fundId;

    //本次使用金额
    private BigDecimal useAmount = BigDecimal.ZERO;

    //资金可用金额 已减去未装车提单金额
    private BigDecimal availAmount;

    //提单预用金额 在生成装车单环节使用，提单环节不使用
    private BigDecimal salesOrderPreUseAmount = BigDecimal.ZERO;

    //现金，汇票，授信
    private String type;

    //汇票到期天数 来款和授信默认10000
    private int billDueDate;


    //现金1， 汇票2， 授信3 越小越靠前 即升序
    private int sortLevel;


}
