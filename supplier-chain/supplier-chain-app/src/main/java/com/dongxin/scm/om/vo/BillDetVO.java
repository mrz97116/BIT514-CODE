package com.dongxin.scm.om.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillDetVO {

    private String id;
    private String prodCode;
    private String prod_name;
    private String prodClassCode;
    private String sgSign;
    private String custMatSpecs1;
    private String custMatSpecs;
    private BigDecimal price;
    private Integer qty;
    private double weight;
    private String remark;
    private BigDecimal totalPrice;
    private String salesId;
    private String inventoryId;
    private String matNo;
    private String wtMode;
    private BigDecimal addPrice;
    private String stockId;

}
