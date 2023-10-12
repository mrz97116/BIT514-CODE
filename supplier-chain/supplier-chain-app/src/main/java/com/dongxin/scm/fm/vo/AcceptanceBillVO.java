package com.dongxin.scm.fm.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
public class AcceptanceBillVO {
    @Excel(name = "背书人", width = 15)
    private String endorser;

    @Excel(name = "出票人", width = 15)
    private String drawer;

    @Excel(name = "票据金额", width = 15)
    private String billAccount;

    @Excel(name = "承兑人", width = 15)
    private String acceptor;

    @Excel(name = "票据号码", width = 15)
    private String billNumber;

    @Excel(name = "提交日期", width = 15)
    private String submitDate;

    @Excel(name = "票据到期日", width = 15)
    private String maturityDateOfBill;

    @Excel(name = "出票日期", width = 15)
    private String issueTicketsDate;

    @Excel(name = "备注", width = 15)
    private String remarks;
}
