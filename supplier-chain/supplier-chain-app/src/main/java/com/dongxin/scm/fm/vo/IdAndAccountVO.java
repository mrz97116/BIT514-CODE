package com.dongxin.scm.fm.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IdAndAccountVO {

    private String id;

    private BigDecimal account;

    private BigDecimal stackDetAccount;

    private BigDecimal fundDetSettleAccount;
}
