package com.dongxin.scm.fm.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class InitMoeny {
    private BigDecimal account;

    private String companyName;

    private String type;

    private Date ticketDate;
}
