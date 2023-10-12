package com.dongxin.scm.home.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalesOrderSevenInfo {
    private static final long serialVersionUID = 1L;

    private int date;

    private BigDecimal numberOfPayments;

    private String dateString;
}
