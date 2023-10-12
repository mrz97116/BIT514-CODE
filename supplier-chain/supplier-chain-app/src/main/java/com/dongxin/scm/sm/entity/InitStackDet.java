package com.dongxin.scm.sm.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InitStackDet {
    private Integer id;
    private String prodCode;
    private String prodCname;
    private String matKind;
    private String sgSign;
    private String size;
    private String specs;
    private BigDecimal price;
    private Double qty;
    private Double weight;
    private String remarks;
    private BigDecimal subtotal;
    private String stackId;
    private String prodInventory;
    private String wtMode;
    private BigDecimal addPrice;
    private String stack;
}
