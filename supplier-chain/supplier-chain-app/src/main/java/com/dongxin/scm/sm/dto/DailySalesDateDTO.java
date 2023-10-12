package com.dongxin.scm.sm.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DailySalesDateDTO {

    //品种
    private String mat;

    //规格
    private String custMatSpecs;

    //计重方式
    private String wtMode;

    //基础价
    private BigDecimal basePrice;

    //成交价
    private BigDecimal transactionPrice;

    //成交量
    private Double transactionWeight;

    //进货量
    private Double stockWeight;

    //库存量
    private Double inventoryWeight;

    //备注
    private String remark;
}
