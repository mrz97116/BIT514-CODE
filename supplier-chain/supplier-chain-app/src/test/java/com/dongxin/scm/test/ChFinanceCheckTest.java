package com.dongxin.scm.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.Application;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.fm.dto.FundAccountDTO;
import com.dongxin.scm.fm.entity.FinanceCheck;
import com.dongxin.scm.fm.entity.FundDetail;
import com.dongxin.scm.fm.entity.FundPool;
import com.dongxin.scm.fm.enums.CommonCheckStatusEnum;
import com.dongxin.scm.fm.enums.IncomeMethodTypeEnum;
import com.dongxin.scm.fm.mapper.FinanceCheckMapper;
import com.dongxin.scm.fm.service.FundAccountService;
import com.dongxin.scm.fm.service.FundDetailService;
import com.dongxin.scm.fm.service.FundPoolService;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.utils.ScmNumberUtils;
import org.jeecg.config.mybatis.TenantContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author ：melon
 * @date ：Created in 2021/4/6 10:14
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class ChFinanceCheckTest {


    @Resource
    private FinanceCheckMapper financeCheckMapper;

    @Autowired
    private FundAccountService fundAccountService;

    @Autowired
    private FundPoolService fundPoolService;
    @Autowired
    private FundDetailService fundDetailService;

    @Autowired
    private SalesOrderService salesOrderService;

    /**
     * 顾客资金核对
     */
    @Test
    public void balanceCheck() {

        QueryWrapper<FundPool> fundPoolQueryWrapper = new QueryWrapper<>();
        fundPoolQueryWrapper.lambda().ge(FundPool::getCreateTime, "2021-09-15 00:00:00")
                .eq(FundPool::getStatus, CommonCheckStatusEnum.APPROVE.getCode())
                .eq(FundPool::getIncomeMethod, IncomeMethodTypeEnum.NORMAL_PAYMENT.getCode());
        List<FundPool> fundPoolList = fundPoolService.list(fundPoolQueryWrapper);

        List<FundPool> unFundPool = newArrayList();
        for (FundPool fundPool : fundPoolList) {
            QueryWrapper<FundDetail> fundDetailQueryWrapper = new QueryWrapper<>();
            fundDetailQueryWrapper.lambda()
                    .eq(FundDetail::getFundId, fundPool.getId())
                    .in(FundDetail::getType, Arrays.asList("pre_use_stack", "refund", "settle"));
            fundDetailQueryWrapper.select("ifnull(sum(amount),0) as result");

            Map<String, Object> map = fundDetailService.getMap(fundDetailQueryWrapper);
            BigDecimal result = new BigDecimal(String.valueOf(map.get("result")));

            if (fundPool.getAvailAmount().compareTo(fundPool.getIncomingAmount().subtract(result)) != 0) {
                unFundPool.add(fundPool);
            }
        }
        System.out.println(unFundPool.toString());

    }

    @Test
    public void test_yrm_unpaid_order() {
        TenantContext.setTenant("5");
        salesOrderService.updateYRMCanLadingBill();
    }
}


