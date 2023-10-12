package com.dongxin.scm.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.Application;
import com.dongxin.scm.fm.entity.FundAccount;
import com.dongxin.scm.fm.entity.FundDetail;
import com.dongxin.scm.fm.enums.AccountTypeEnum;
import com.dongxin.scm.fm.enums.FundDetailTypeEnum;
import com.dongxin.scm.fm.service.FundAccountService;
import com.dongxin.scm.fm.service.FundDetailService;
import com.dongxin.scm.fm.service.SettleInfoService;
import com.dongxin.scm.fm.vo.SettledAccountAndFundDetailUnequalVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.querydsl.QuerydslUtils;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class RepairData {
    @Autowired
    private SettleInfoService settleInfoService;

    @Autowired
    private FundAccountService fundAccountService;

    @Autowired
    private FundDetailService fundDetailService;



}
