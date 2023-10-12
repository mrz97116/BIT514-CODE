package com.dongxin.scm.fm.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Autor: liujiazhi
 * Date:2021/4/6
 * @Version: V1.0
 */
@Data
public class FundAccountVO {
    private String tenantId;//租户id
    private String customerId;//客户id
    private BigDecimal openingBanlance;//期初金额
    private BigDecimal endingBanlance;//期末金额
    private String companyName;//公司名称
}
