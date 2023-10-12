package com.dongxin.scm.home.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class FundPoolVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String payment;

    private String averageDailyPayment;
}
