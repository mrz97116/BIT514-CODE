package com.dongxin.scm.sm.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class InitStack {
    private String id;
    private String companyCode;
    private String companyName;
    private String stackNo;
    private Date createTime;
    private String creator;
    private String carNo;
    private String receiver;
    private String remark;
    private BigDecimal total;
    private String billId;
    private String status;
    private String payMethod;
    private String receiverIdCard;
    private String dest;
    private Date carTime;
    private Integer rmaId;
    private String rmaNo;
}
