package com.dongxin.scm.fm.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
//@EqualsAndHashCode(callSuper = false)
public class AcceptanceImport {
    /**
     * 银行名称
     */
    @Excel(name = "到账银行", width = 15)
    private java.lang.String bankName;
    /**
     * 顾客名称
     */
    @Excel(name = "背书人名称", width = 15)
    private java.lang.String customerId;
    /**
     * 来款金额
     */
    @Excel(name = "票据金额", width = 15)
    private java.lang.String incomeAccount;
    /**
     * 承兑银行
     */
    @Excel(name = "承兑人名称", width = 15)
    private String acceptor;
    /**
     * 承兑汇票号
     */
    @Excel(name = "票据号码", width = 15)
    private java.lang.String acceptanceTicketNo;
    /**
     * 汇票到期日期
     */
    @Excel(name = "到期日期", width = 15)
    private String ticketDate;
    /**
     * 出票日期
     */
    @Excel(name = "出票日期", width = 15)
    private String issueTicketsDate;

    @Excel(name = "备注", width = 15)
    private String remarks;

}
