package com.dongxin.scm.fm.vo;

import javafx.scene.layout.BackgroundImage;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettleAccountVO {
    private BigDecimal totalSettleAccount;

    private BigDecimal totalDisAccount;

    private BigDecimal incomePreUseAccount;

    private BigDecimal creditPreUseAccount;

    private BigDecimal incomeTotalSettleAccount;

    private BigDecimal creditTotalSettleAccount;

    private String customerId;

    private String settleId;
}
