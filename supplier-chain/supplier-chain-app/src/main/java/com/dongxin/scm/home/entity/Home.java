package com.dongxin.scm.home.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Home implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> salesRanking;

    private List<SettleInfoVO> sevenDaySalesStatistics;

    private List<FundPoolSevenInfo> sevenDayFundPool;

    private FundPoolVO fundPoolVO;

    private SalesShow salesShow;

    private CountBill countBill;

    private MonthPayment monthPayment;

    private List<SalesOrderSevenInfo> sevenDaySalesOrder;

    private List<FundPoolAccountSevenInfo> sevenDayFundPoolAccount;

}
