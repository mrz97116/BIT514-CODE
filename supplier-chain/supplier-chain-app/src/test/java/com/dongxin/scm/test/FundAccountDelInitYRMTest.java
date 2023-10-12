package com.dongxin.scm.test;

import cn.hutool.core.collection.CollectionUtil;
import com.aliyun.oss.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.Application;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.enums.IncomeAndCreditEnum;
import com.dongxin.scm.fm.entity.Credit;
import com.dongxin.scm.fm.entity.FundAccount;
import com.dongxin.scm.fm.entity.FundDetail;
import com.dongxin.scm.fm.entity.FundPool;
import com.dongxin.scm.fm.enums.AccountTypeEnum;
import com.dongxin.scm.fm.enums.CommonCheckStatusEnum;
import com.dongxin.scm.fm.enums.FundDetailTypeEnum;
import com.dongxin.scm.fm.service.CreditService;
import com.dongxin.scm.fm.service.FundAccountService;
import com.dongxin.scm.fm.service.FundDetailService;
import com.dongxin.scm.fm.service.FundPoolService;
import com.dongxin.scm.sm.entity.Stack;
import com.dongxin.scm.sm.service.StackService;
import com.dongxin.scm.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.hutool.core.collection.CollUtil.newArrayList;
import static cn.hutool.core.collection.CollUtil.newHashMap;

/**
 * @author ：melon
 * 初始化阳蕊明数据
 * @date ：Created in 2021-9-9 14:24
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
@Slf4j
public class FundAccountDelInitYRMTest {

    public static List<String> yrmTenantIds = Stream.of("5", "6", "7", "8", "13").collect(Collectors.toList());

    @Autowired
    CustomerProfileService customerProfileService;

    @Autowired
    FundAccountService fundAccountService;

    @Autowired
    FundPoolService fundPoolService;

    @Autowired
    StackService stackService;

    @Autowired
    FundDetailService fundDetailService;

    @Autowired
    CreditService creditService;

    /**
     * 初始化来款数据，YRM客户金额初始化
     */
    @Test
    public void initYrmFundPool() throws Exception {

        QueryWrapper<CustomerProfile> customerProfileQueryWrapper = new QueryWrapper<>();

        customerProfileQueryWrapper.lambda().in(CustomerProfile::getTenantId, yrmTenantIds).eq(CustomerProfile::getDelFlag, 0);

        List<CustomerProfile> customerProfiles = customerProfileService.list(customerProfileQueryWrapper);

        List<String> customerIds = customerProfiles.stream().map(CustomerProfile::getId).collect(Collectors.toList());

        QueryWrapper<FundAccount> fundAccountQueryWrapper = new QueryWrapper<>();

        fundAccountQueryWrapper.lambda().in(FundAccount::getCustomerId, customerIds)
                .eq(FundAccount::getType, AccountTypeEnum.INCOME.getCode())
                .gt(FundAccount::getAvailableAmount, BigDecimal.ZERO)
                .in(FundAccount::getTenantId, yrmTenantIds);

        List<FundAccount> fundAccountList = fundAccountService.list(fundAccountQueryWrapper);

        for (FundAccount fundAccount : fundAccountList) {
            String customerId = fundAccount.getCustomerId();

            QueryWrapper<FundPool> fundPoolQueryWrapper = new QueryWrapper<>();

            fundPoolQueryWrapper.lambda().eq(FundPool::getCustomerId, customerId)
                    .eq(FundPool::getDelFlag, 0).eq(FundPool::getStatus, CommonCheckStatusEnum.APPROVE.getCode())
                    .orderByDesc(FundPool::getCreateTime);

            List<FundPool> fundPoolList = fundPoolService.list(fundPoolQueryWrapper);

            if (CollectionUtil.isEmpty(fundAccountList) && fundAccount.getAvailableAmount().compareTo(BigDecimal.ZERO) > 0) {
                log.error("来款数据异常:{}", fundAccount.getCustomerId());
                throw new Exception("来款数据异常");
            }
            BigDecimal shareAmount = fundAccount.getAvailableAmount();

            List<FundPool> updateFundPoolList = newArrayList();

            for (int i = 0; i < fundPoolList.size() && shareAmount.compareTo(BigDecimal.ZERO) > 0; i++) {
                FundPool fundPool = fundPoolList.get(i);
                if (fundPool.getIncomingAmount().compareTo(shareAmount) >= 0) {
                    fundPool.setAvailAmount(shareAmount);
                    shareAmount = BigDecimal.ZERO;
                } else {
                    fundPool.setAvailAmount(fundPool.getIncomingAmount());
                    shareAmount = shareAmount.subtract(fundPool.getIncomingAmount());
                }
                updateFundPoolList.add(fundPool);
            }

            BigDecimal fundPoolTotalAvailAmount = BigDecimal.ZERO;
            for (FundPool fundPool : updateFundPoolList) {
                if (fundPool.getAvailAmount().compareTo(BigDecimal.ZERO) < 0) {
                    throw new ServiceException("资金被扣减到0");
                }

                fundPoolTotalAvailAmount = fundPoolTotalAvailAmount.add(fundPool.getAvailAmount());
            }

            if (fundPoolTotalAvailAmount.compareTo(fundAccount.getAvailableAmount()) != 0) {
                throw new ServiceException("分摊总金额不对");
            }

            for (FundPool fundPool : updateFundPoolList) {
                fundPoolService.updateById(fundPool);
            }

        }


    }

    @Test
    public void init_yrm_credit() {
        QueryWrapper<FundAccount> fundAccountQueryWrapper = new QueryWrapper<>();
        fundAccountQueryWrapper.lambda().gt(FundAccount::getAvailableAmount, BigDecimal.ZERO)
                .eq(FundAccount::getType, AccountTypeEnum.CREDIT.getCode()).in(FundAccount::getTenantId, yrmTenantIds);

        List<FundAccount> fundAccountList = fundAccountService.list(fundAccountQueryWrapper);

        for (FundAccount fundAccount : fundAccountList) {
            QueryWrapper<Credit> creditQueryWrapper = new QueryWrapper<>();
            creditQueryWrapper.lambda().eq(Credit::getCustomerId, fundAccount.getCustomerId())
                    .eq(Credit::getStatus, com.dongxin.scm.common.enums.CommonCheckStatusEnum.APPROVE.getCode());

            List<Credit> creditList = creditService.list(creditQueryWrapper);
            Credit credit = creditList.get(0);

            credit.setAvailAmount(fundAccount.getAvailableAmount());

            creditService.updateById(credit);
        }
    }


    /***
     * 更新直销公司最近一个月装车单流水明细
     */
    @Test
    public void int_yrm_stack() {

        Date startTime = DateUtils.parseDate("2021-08-01 00:00:00");

        Date endTime = new Date();

        QueryWrapper<Stack> stackQueryWrapper = new QueryWrapper();

        stackQueryWrapper.lambda().in(Stack::getTenantId, yrmTenantIds).eq(Stack::getDelFlag, 0)
                .between(Stack::getCreateTime, startTime, endTime);

        List<Stack> stackList = stackService.list(stackQueryWrapper);

        for (Stack stack : stackList) {

            QueryWrapper<FundPool> fundPoolQueryWrapper = new QueryWrapper<>();

            fundPoolQueryWrapper.lambda().eq(FundPool::getCustomerId, stack.getCustomerId())
                    .eq(FundPool::getDelFlag, 0).eq(FundPool::getStatus, CommonCheckStatusEnum.APPROVE.getCode())
                    .orderByDesc(FundPool::getCreateTime);

            List<FundPool> fundPoolList = fundPoolService.list(fundPoolQueryWrapper);

            BigDecimal shareAmount = stack.getTotalAmount();

            Map<String, BigDecimal> fundPoolIdAndUseAmount = newHashMap();
            for (FundPool fundPool : fundPoolList) {

                QueryWrapper<FundDetail> fundDetailQueryWrapper = new QueryWrapper<>();
                fundDetailQueryWrapper.lambda().eq(FundDetail::getFundId, fundPool.getId())
                        .eq(FundDetail::getDelFlag, 0).eq(FundDetail::getType, FundDetailTypeEnum.PRE_USE_STACK);

                List<FundDetail> fundDetailList = fundDetailService.list(fundDetailQueryWrapper);

                BigDecimal totalUse = fundDetailList.stream().map(FundDetail::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal canUse = fundPool.getIncomingAmount().subtract(fundPool.getAvailAmount()).subtract(totalUse);

                if (canUse.compareTo(BigDecimal.ZERO) > 0) {
                    if (canUse.compareTo(shareAmount) >= 0) {
                        fundPoolIdAndUseAmount.put(fundPool.getId(), shareAmount);
                        shareAmount = BigDecimal.ZERO;
                    } else {
                        fundPoolIdAndUseAmount.put(fundPool.getId(), canUse);
                        shareAmount = shareAmount.subtract(canUse);
                    }
                }

                if (shareAmount.compareTo(BigDecimal.ZERO) == 0) {
                    break;
                }

            }

            List<FundDetail> insertFundDetailList = newArrayList();

            fundPoolIdAndUseAmount.forEach((k, v) -> {
                if (v.compareTo(BigDecimal.ZERO) != 0) {
                    FundDetail fundDetail = new FundDetail();
                    fundDetail.setCustomerId(stack.getCustomerId());
                    fundDetail.setAmount(v);
                    fundDetail.setType(FundDetailTypeEnum.PRE_USE_STACK.getCode());
                    fundDetail.setFundId(k);
                    fundDetail.setFundType(IncomeAndCreditEnum.CASH.getCode());
                    fundDetail.setOutTradeNo(stack.getSalesOrderId());
                    fundDetail.setTenantId(Integer.valueOf(stack.getTenantId()));
                    insertFundDetailList.add(fundDetail);
                }
            });

            BigDecimal totalDetailAmount = BigDecimal.ZERO;
            for (FundDetail fundDetail : insertFundDetailList) {
                totalDetailAmount = totalDetailAmount.add(fundDetail.getAmount());
            }

            if (totalDetailAmount.compareTo(stack.getTotalAmount()) != 0) {
                throw new ServiceException("总金额不对");
            }

            fundDetailService.saveBatch(insertFundDetailList);

        }


    }

}
