package com.dongxin.scm.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.Application;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.common.enums.CommonCheckStatusEnum;
import com.dongxin.scm.fm.dto.FundAccountDTO;
import com.dongxin.scm.fm.entity.FundAccount;
import com.dongxin.scm.fm.entity.FundDetail;
import com.dongxin.scm.fm.entity.FundPool;
import com.dongxin.scm.fm.enums.FundDetailTypeEnum;
import com.dongxin.scm.fm.enums.FundPoolDetailTypeEnum;
import com.dongxin.scm.fm.enums.IncomeMethodTypeEnum;
import com.dongxin.scm.fm.enums.SettleStatusEnum;
import com.dongxin.scm.fm.service.FundAccountService;
import com.dongxin.scm.fm.service.FundDetailService;
import com.dongxin.scm.fm.service.FundPoolService;

import com.dongxin.scm.fm.service.FundService;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.sm.entity.Stack;
import com.dongxin.scm.sm.mapper.MatMapper;

import com.dongxin.scm.sm.service.StackService;
import io.micrometer.shaded.org.pcollections.PStack;
import jdk.nashorn.internal.runtime.regexp.joni.constants.StackPopLevel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;

import java.util.List;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;


/**
 * @author ：lhz
 * @date ：Created in 2021/05/25
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class FundPoolTest {
    @Autowired
    private FundPoolService fundPoolService;
    @Autowired
    private FundAccountService fundAccountService;
    @Autowired
    private CustomerProfileService customerProfileService;
    @Autowired
    private StackService stackService;
    @Autowired
    private SalesOrderService salesOrderService;
    @Autowired
    private FundDetailService fundDetailService;
    @Autowired
    private FundService fundService;

    //
    @Test
    public void fundPoolInit() {
        QueryWrapper<CustomerProfile> customerProfileQueryWrapper = new QueryWrapper<>();
        List<CustomerProfile> customerProfileList = customerProfileService.list(customerProfileQueryWrapper);
        for (CustomerProfile customerProfile : customerProfileList) {
            //获取客户来款主账户
            QueryWrapper<FundAccount> fundAccountQueryWrapper = new QueryWrapper<>();
            fundAccountQueryWrapper.lambda().eq(FundAccount::getCustomerId, customerProfile.getId());
            List<FundAccount> fundAccountList = fundAccountService.list(fundAccountQueryWrapper);
            BigDecimal poolPayCreditAmount = BigDecimal.ZERO;
            BigDecimal poolPayIncomeAmount = BigDecimal.ZERO;
            //来款总支出额=偿还授信支出+来款支出
            for (FundAccount fundAccount : fundAccountList) {
                if (fundAccount.getType().equals("credit")) {
                    poolPayCreditAmount = fundAccount.getAvailableAmount().add(fundAccount.getPreuseAmount())
                            .add(fundAccount.getSettleAmount()).subtract(fundAccount.getTotalIncome());
                }
                if (fundAccount.getType().equals("income")) {
                    poolPayIncomeAmount = fundAccount.getPreuseAmount().add(fundAccount.getSettleAmount());
                }
            }
            BigDecimal totalPoolPayAmount = poolPayCreditAmount.add(poolPayIncomeAmount);
            //获取客户来款
            QueryWrapper<FundPool> fundPoolQueryWrapper = new QueryWrapper<>();
            fundPoolQueryWrapper.lambda().eq(FundPool::getCustomerId, customerProfile.getId())
                    .eq(FundPool::getIncomeMethod, IncomeMethodTypeEnum.NORMAL_PAYMENT.getCode())
                    .eq(FundPool::getStatus, CommonCheckStatusEnum.APPROVE.getCode())
                    .orderByAsc(FundPool::getCreateTime);
            List<FundPool> fundPoolList = fundPoolService.list(fundPoolQueryWrapper);
            //扣款（初始化来款）
            for (FundPool fundPool : fundPoolList) {
                BigDecimal fundPoolAmount = fundPool.getIncomingAmount();
                if (totalPoolPayAmount.subtract(fundPoolAmount).compareTo(BigDecimal.ZERO) > 0) {
                    fundPool.setAvailAmount(BigDecimal.ZERO);
                    fundPoolService.updateById(fundPool);
                    totalPoolPayAmount = totalPoolPayAmount.subtract(fundPoolAmount);
                } else {
                    fundPool.setAvailAmount(fundPoolAmount.subtract(totalPoolPayAmount));
                    fundPoolService.updateById(fundPool);
                    totalPoolPayAmount = BigDecimal.ZERO;
                }
            }

        }
    }


    @Test
    public void selectUpdateCustomError() {
        QueryWrapper<Stack> stackQueryWrapper = new QueryWrapper<>();
        stackQueryWrapper.lambda().eq(Stack::getSettled, SettleStatusEnum.UNSETTLE.getCode()).ge(Stack::getCreateTime, "2021-04-20 00:00:00");
        List<Stack> stackList = stackService.list(stackQueryWrapper);

        List<Stack> unStackList = newArrayList();

        for (Stack stack : stackList) {
            QueryWrapper<SalesOrder> salesOrderQueryWrapper = new QueryWrapper<>();
            salesOrderQueryWrapper.lambda().eq(SalesOrder::getSaleBillNo, stack.getSaleBillNo())
                    .eq(SalesOrder::getTenantId, stack.getTenantId());
            SalesOrder salesOrder = salesOrderService.getOne(salesOrderQueryWrapper);


            QueryWrapper<FundDetail> fundDetailQueryWrapper = new QueryWrapper<>();
            fundDetailQueryWrapper.lambda().eq(FundDetail::getOutTradeNo, salesOrder.getId())
                    .eq(FundDetail::getType, FundDetailTypeEnum.PRE_USE.getCode())
                    .eq(FundDetail::getCustomerId, salesOrder.getCustomerId());
            List<FundDetail> fundDetailList = fundDetailService.list(fundDetailQueryWrapper);

            BigDecimal preUseAccount = BigDecimal.ZERO;
            for (FundDetail fundDetail : fundDetailList) {
                preUseAccount = preUseAccount.add(fundDetail.getAmount());
            }

            if (preUseAccount.compareTo(stack.getTotalAmount()) != 0) {
                unStackList.add(stack);
            }
        }


        System.out.println(unStackList);
    }


    @Test
    public void test_fund_account_query() {
        FundAccountDTO fundAccountDTO = fundService.getFundAccountDTO("1431138807675711489");

        System.out.println(fundAccountDTO);
    }
}
