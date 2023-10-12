package com.dongxin.scm.test;

import cn.hutool.core.collection.CollectionUtil;
import com.aliyun.oss.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.Application;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.enums.IncomeAndCreditEnum;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.fm.entity.FundAccount;
import com.dongxin.scm.fm.entity.FundDetail;
import com.dongxin.scm.fm.entity.FundPool;
import com.dongxin.scm.fm.enums.AccountTypeEnum;
import com.dongxin.scm.fm.enums.CommonCheckStatusEnum;
import com.dongxin.scm.fm.enums.FundDetailTypeEnum;
import com.dongxin.scm.fm.enums.SettleStatusEnum;
import com.dongxin.scm.fm.service.FundAccountService;
import com.dongxin.scm.fm.service.FundDetailService;
import com.dongxin.scm.fm.service.FundPoolService;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.sm.entity.Stack;
import com.dongxin.scm.sm.enums.StatusEnum;
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
 * 直销公司刷数据，除岑海外，岑海单独刷，因为数据量太大
 * @date ：Created in 2021-9-9 14:24
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
@Slf4j
public class FundAccountDelDirectSaleInitTest {

    public static List<String> directSaleTenantIds = Stream.of("9","10","11").collect(Collectors.toList());


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
    SalesOrderService salesOrderService;


    //初始化
    @Test
    public void initDirectSaleFundPool() throws Exception {

        QueryWrapper<CustomerProfile> customerProfileQueryWrapper = new QueryWrapper<>();

        customerProfileQueryWrapper.lambda().in(CustomerProfile::getTenantId, directSaleTenantIds).eq(CustomerProfile::getDelFlag, 0);

        List<CustomerProfile> customerProfiles = customerProfileService.list(customerProfileQueryWrapper);

        List<String> customerIds = customerProfiles.stream().map(CustomerProfile::getId).collect(Collectors.toList());

        QueryWrapper<FundAccount> fundAccountQueryWrapper = new QueryWrapper<>();

        //预用的金额也要加上
        fundAccountQueryWrapper.lambda().in(FundAccount::getCustomerId, customerIds)
                .eq(FundAccount::getType, AccountTypeEnum.INCOME.getCode())
                .in(FundAccount::getTenantId, directSaleTenantIds);

        List<FundAccount> fundAccountList = fundAccountService.list(fundAccountQueryWrapper);

        fundAccountList = fundAccountList.stream().filter(
                f -> f.getAvailableAmount().add(f.getPreuseAmount()).compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());

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
            BigDecimal preUseAndAvailAmount = fundAccount.getAvailableAmount().add(fundAccount.getPreuseAmount());

            BigDecimal unSettleAmount = stackService.sumUnSettleStackAmount(customerId);

            BigDecimal shareAmount = preUseAndAvailAmount.subtract(unSettleAmount);

            if (shareAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new ServiceException("账务信息出错");
            }
            if (shareAmount.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

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

            if (fundPoolTotalAvailAmount.compareTo(preUseAndAvailAmount.subtract(unSettleAmount)) != 0) {
                throw new ServiceException("分摊总金额不对");
            }


            for (FundPool fundPool : updateFundPoolList) {
                fundPoolService.updateById(fundPool);
            }

        }


    }

    /***
     * 更新直销公司未装车的提单数据
     */
    @Test
    public void int_directSale_salesOrder() {


        QueryWrapper<SalesOrder> saleOrderQueryWrapper = new QueryWrapper();

        saleOrderQueryWrapper.lambda().in(SalesOrder::getTenantId, directSaleTenantIds).eq(SalesOrder::getDelFlag, 0)
                .eq(SalesOrder::getCreateStackStatus, YesNoEnum.NO.getCode());

        List<SalesOrder> salesOrderList = salesOrderService.list(saleOrderQueryWrapper);

        for (SalesOrder salesOrder : salesOrderList) {

            QueryWrapper<FundPool> fundPoolQueryWrapper = new QueryWrapper<>();

            fundPoolQueryWrapper.lambda().eq(FundPool::getCustomerId, salesOrder.getCustomerId())
                    .eq(FundPool::getDelFlag, 0).eq(FundPool::getStatus, CommonCheckStatusEnum.APPROVE.getCode())
                    .gt(FundPool::getAvailAmount, BigDecimal.ZERO)
                    .orderByDesc(FundPool::getCreateTime);

            List<FundPool> fundPoolList = fundPoolService.list(fundPoolQueryWrapper);

            BigDecimal shareAmount = salesOrder.getTotalAmount();

            Map<String, BigDecimal> fundPoolIdAndUseAmount = newHashMap();
            for (FundPool fundPool : fundPoolList) {

                QueryWrapper<FundDetail> fundDetailQueryWrapper = new QueryWrapper<>();
                fundDetailQueryWrapper.lambda().eq(FundDetail::getFundId, fundPool.getId())
                        .eq(FundDetail::getDelFlag, 0).eq(FundDetail::getType, FundDetailTypeEnum.PRE_USE);

                List<FundDetail> fundDetailList = fundDetailService.list(fundDetailQueryWrapper);

                BigDecimal totalPreUse = fundDetailList.stream().map(FundDetail::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal canPreUse = fundPool.getAvailAmount().subtract(totalPreUse);

                //此if前需加一个canPreUse大于零的判断
                if (canPreUse.compareTo(BigDecimal.ZERO) > 0) {
                    if (canPreUse.compareTo(shareAmount) >= 0) {
                        fundPoolIdAndUseAmount.put(fundPool.getId(), shareAmount);
                        shareAmount = BigDecimal.ZERO;
                    } else {
                        fundPoolIdAndUseAmount.put(fundPool.getId(), canPreUse);
                        shareAmount = shareAmount.subtract(canPreUse);
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
                    fundDetail.setCustomerId(salesOrder.getCustomerId());
                    fundDetail.setAmount(v);
                    fundDetail.setType(FundDetailTypeEnum.PRE_USE.getCode());
                    fundDetail.setFundId(k);
                    fundDetail.setFundType(IncomeAndCreditEnum.CASH.getCode());
                    fundDetail.setOutTradeNo(salesOrder.getId());
                    fundDetail.setTenantId(Integer.valueOf(salesOrder.getTenantId()));
                    insertFundDetailList.add(fundDetail);
                }
            });

            BigDecimal totalDetailAmount = BigDecimal.ZERO;
            for (FundDetail fundDetail : insertFundDetailList) {
                totalDetailAmount = totalDetailAmount.add(fundDetail.getAmount());
            }

            if (totalDetailAmount.compareTo(salesOrder.getTotalAmount()) != 0) {
                throw new ServiceException("总金额不对");
            }

            fundDetailService.saveBatch(insertFundDetailList);

        }

    }

    @Test
    public void int_directSale_stack() {


        QueryWrapper<Stack> stackQueryWrapper = new QueryWrapper();

        stackQueryWrapper.lambda().in(Stack::getTenantId, directSaleTenantIds).eq(Stack::getDelFlag, 0)
                .eq(Stack::getSettled, SettleStatusEnum.UNSETTLE.getCode());


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
                        .eq(FundDetail::getDelFlag, 0);

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
