package com.dongxin.scm.fm.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.enums.IncomeAndCreditEnum;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.dto.CreditDTO;
import com.dongxin.scm.fm.dto.FundAccountDTO;
import com.dongxin.scm.fm.dto.FundPoolDTO;
import com.dongxin.scm.fm.entity.Credit;
import com.dongxin.scm.fm.entity.FundDetail;
import com.dongxin.scm.fm.entity.FundPool;
import com.dongxin.scm.fm.enums.CommonCheckStatusEnum;
import com.dongxin.scm.fm.enums.FundDetailTypeEnum;
import com.dongxin.scm.fm.enums.IncomeMethodTypeEnum;
import com.dongxin.scm.fm.enums.PaymentMethodEnum;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.enums.PaymentFlagEnum;
import com.dongxin.scm.sm.entity.Stack;
import com.dongxin.scm.sm.entity.StackDet;
import com.dongxin.scm.sm.service.StackDetService;
import com.dongxin.scm.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.config.mybatis.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * @author ：melon
 * @date ：Created in 2021-8-17 19:00
 */
@Service
@Slf4j
public class FundService {

    @Autowired
    private FundPoolService fundPoolService;

    @Autowired
    private CreditService creditService;

    @Autowired
    private StackDetService stackDetService;

    @Autowired
    private FundDetailService fundDetailService;

    public FundAccountDTO getFundAccountDTO(String customerId) {

        FundAccountDTO result = new FundAccountDTO();

        CreditDTO creditDTO = creditService.getCreditAvailAmountAndTotalCreditAmount(customerId);

        result.setCreditAvailAmount(creditDTO.getTotalAvailAmount());
        result.setTotalCreditAmount(creditDTO.getTotalCreditAmount());

        result.setUnLoadSalesOrderAmount(fundDetailService.getUnLoadAmountByCustomerId(customerId));
        result.setUnSettleStackAmount(fundDetailService.getUnSettleStackAmountByCustomerId(customerId));
        result.setIncomeAvailAmount(getIncomeAvailAmount(customerId));

        return result;

    }

    /**
     * 获取客户当前来款可用金额
     *
     * @param customerId
     * @return
     */
    private BigDecimal getIncomeAvailAmount(String customerId) {
        QueryWrapper<FundPool> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FundPool::getCustomerId, customerId)
                .eq(FundPool::getStatus, CommonCheckStatusEnum.APPROVE.getCode())
                .eq(FundPool::getIncomeMethod, IncomeMethodTypeEnum.NORMAL_PAYMENT.getCode())
                .gt(FundPool::getAvailAmount, BigDecimal.ZERO);

        queryWrapper.select("ifnull(sum(avail_amount),0) as total");

        Map<String, Object> availAmountMap = fundPoolService.getMap(queryWrapper);

        return new BigDecimal(String.valueOf(availAmountMap.get("total")));

    }


    //根据预用记录 查询资金使用情况
    public List<FundPoolDTO> getFundDTOByPreUseDetail(List<FundDetail> preUseFundDetailList) {
        List<FundPoolDTO> result = newArrayList();

        for (FundDetail fundDetail : preUseFundDetailList) {
            FundPoolDTO fundPoolDTO = new FundPoolDTO();
            fundPoolDTO.setFundId(fundDetail.getFundId());

            if (IncomeAndCreditEnum.CREDIT.getCode().equalsIgnoreCase(fundDetail.getFundType())) {
                Credit credit = creditService.getById(fundDetail.getFundId());

                fundPoolDTO.setAvailAmount(
                        credit.getAvailAmount().subtract(fundDetailService.getUnLoadAmount(fundDetail.getFundId())));

            } else {

                FundPool fundPool = fundPoolService.getById(fundDetail.getFundId());

                fundPoolDTO.setAvailAmount(
                        fundPool.getAvailAmount().subtract(fundDetailService.getUnLoadAmount(fundDetail.getFundId())));

            }

            fundPoolDTO.setUseAmount(fundDetail.getAmount());

            fundPoolDTO.setSalesOrderPreUseAmount(fundDetail.getAmount());

            fundPoolDTO.setType(fundDetail.getFundType());

            result.add(fundPoolDTO);
        }

        return result;

    }

    public List<FundPoolDTO> getFundPoolDTOByFundIds(List<FundDetail> fundDetailList, boolean autoSelect,
                                                     List<String> fundIds, String customerId) {
        List<String> fundPoolIds = newArrayList();

        List<String> creditFundIds = newArrayList();

        List<FundPoolDTO> result = newArrayList();


        for (FundDetail fundDetail : fundDetailList) {
            if (IncomeAndCreditEnum.CREDIT.getCode().equalsIgnoreCase(fundDetail.getFundType())) {
                creditFundIds.add(fundDetail.getFundId());
            } else {
                fundPoolIds.add(fundDetail.getFundId());
            }
        }

        QueryWrapper<FundPool> fundPoolQueryWrapper = new QueryWrapper<>();

        fundPoolQueryWrapper.lambda().eq(FundPool::getCustomerId, customerId)
                .gt(FundPool::getAvailAmount, BigDecimal.ZERO);

        if (CollectionUtil.isNotEmpty(fundPoolIds)) {
            fundPoolQueryWrapper.lambda().notIn(FundPool::getId, fundPoolIds);
        }

        if (!autoSelect) {
            //手动选款，只查已选的款
            fundPoolQueryWrapper.lambda().in(FundPool::getId, fundIds);
        }


        List<FundPool> fundPoolList = fundPoolService.list(fundPoolQueryWrapper);

        for (FundPool fundPool : fundPoolList) {
            FundPoolDTO fundPoolDTO = new FundPoolDTO();

            fundPoolDTO.setType(fundPool.getPaymentMethod());
            fundPoolDTO.setFundId(fundPool.getId());
            fundPoolDTO.setAvailAmount(fundPool.getAvailAmount().subtract(fundDetailService.getUnLoadAmount(fundPool.getId())));
            if (fundPoolDTO.getAvailAmount().compareTo(BigDecimal.ZERO) > 0) {
                result.add(fundPoolDTO);
            }
        }

        QueryWrapper<Credit> creditQueryWrapper = new QueryWrapper<>();

        creditQueryWrapper.lambda().eq(Credit::getCustomerId, customerId)
                .gt(Credit::getAvailAmount, BigDecimal.ZERO);

        if (CollectionUtil.isNotEmpty(creditFundIds)) {
            fundPoolQueryWrapper.lambda().notIn(FundPool::getId, creditFundIds);
        }

        if (!autoSelect) {
            //手动选款，只查已选的款
            creditQueryWrapper.lambda().in(Credit::getId, fundIds);
        }

        List<Credit> creditList = creditService.list(creditQueryWrapper);

        for (Credit credit : creditList) {
            FundPoolDTO fundPoolDTO = new FundPoolDTO();
            fundPoolDTO.setType(IncomeAndCreditEnum.CREDIT.getCode());
            fundPoolDTO.setFundId(credit.getId());
            fundPoolDTO.setAvailAmount(credit.getAvailAmount().subtract(fundDetailService.getUnLoadAmount(credit.getId())));
            if (fundPoolDTO.getAvailAmount().compareTo(BigDecimal.ZERO) > 0) {
                result.add(fundPoolDTO);
            }
        }

        return result;

    }

    //生成装车单时如果需要退款或者补款，则重新做一次选择
    public List<FundPoolDTO> reSelectFundAmount(BigDecimal needAddAmount, List<FundPoolDTO> fundPoolDTOList) {

        BigDecimal availNeedAddAmount = needAddAmount;
        for (int i = 0; i < fundPoolDTOList.size() && availNeedAddAmount.compareTo(BigDecimal.ZERO) != 0; i++) {
            FundPoolDTO fundPoolDTO = fundPoolDTOList.get(i);

            if (availNeedAddAmount.compareTo(BigDecimal.ZERO) > 0) {
                //补款
                if (fundPoolDTO.getAvailAmount().compareTo(availNeedAddAmount) > 0) {
                    //此来款可用大于需要补的款项
                    fundPoolDTO.setUseAmount(fundPoolDTO.getSalesOrderPreUseAmount().add(availNeedAddAmount));
                    availNeedAddAmount = BigDecimal.ZERO;
                } else {
                    fundPoolDTO.setUseAmount(fundPoolDTO.getSalesOrderPreUseAmount().add(fundPoolDTO.getAvailAmount()));
                    availNeedAddAmount = availNeedAddAmount.subtract(fundPoolDTO.getAvailAmount());
                }
            } else {
                //退款
                if (fundPoolDTO.getSalesOrderPreUseAmount().compareTo(availNeedAddAmount.abs()) > 0) {
                    //此来款预用足够退
                    fundPoolDTO.setUseAmount(fundPoolDTO.getSalesOrderPreUseAmount().subtract(availNeedAddAmount.abs()));
                    availNeedAddAmount = BigDecimal.ZERO;
                } else {
                    fundPoolDTO.setUseAmount(BigDecimal.ZERO);
                    availNeedAddAmount = fundPoolDTO.getSalesOrderPreUseAmount().add(availNeedAddAmount);
                }
            }
        }

        return fundPoolDTOList.stream().filter(f -> f.getUseAmount().compareTo(BigDecimal.ZERO) != 0).collect(Collectors.toList());

    }

    private void updateFundPool(List<FundPoolDTO> fundPoolDTOS) {
        for (FundPoolDTO fundPoolDTO : fundPoolDTOS) {
            if (fundPoolDTO.getType().equalsIgnoreCase(IncomeAndCreditEnum.CREDIT.getCode())) {
                Credit credit = creditService.getById(fundPoolDTO.getFundId());
                credit.setAvailAmount(credit.getAvailAmount().subtract(fundPoolDTO.getUseAmount()));
                creditService.updateById(credit);
            } else {
                FundPool fundPool = fundPoolService.getById(fundPoolDTO.getFundId());
                fundPool.setAvailAmount(fundPool.getAvailAmount().subtract(fundPoolDTO.getUseAmount()));
                fundPoolService.updateById(fundPool);
            }
        }
    }


    /**
     * 装车生成提单修改对应资金并产生流水
     */
    @Transactional(rollbackFor = Exception.class)
    public void geneStackFundDetail(List<FundPoolDTO> fundPoolDTOS, String customerId, String outTradeNo) {
        updateFundPool(fundPoolDTOS);

        List<FundDetail> fundDetailList = CollUtil.newArrayList();
        for (FundPoolDTO fundPoolDTO : fundPoolDTOS) {
            fundDetailList.add(new FundDetail(customerId, fundPoolDTO.getUseAmount(),
                    FundDetailTypeEnum.PRE_USE_STACK.getCode(), outTradeNo, fundPoolDTO.getFundId(), fundPoolDTO.getType()));
        }

        //保存流水
        fundDetailService.saveBatch(fundDetailList);
    }


    //修改装车单
    @Transactional(rollbackFor = Exception.class)
    public void stackDeductAndRefund(BigDecimal amount, String customerId, SalesOrder salesOrder) {
        QueryWrapper<FundDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FundDetail::getOutTradeNo, salesOrder.getId())
                .eq(FundDetail::getType, FundDetailTypeEnum.PRE_USE_STACK.getCode());
        List<FundDetail> fundDetailList = fundDetailService.list(queryWrapper);

        List<FundPoolDTO> fundPoolDTOList = getFundDTOByPreUseDetail(fundDetailList);

        //添加其他款项
        fundPoolDTOList.addAll(getFundPoolDTOByFundIds(fundDetailList,
                salesOrder.getAutoSelectFund().equalsIgnoreCase(YesNoEnum.YES.getCode()), salesOrder.generateFundIdList(), customerId));
        fundPoolDTOList = reSelectFundAmount(amount, fundPoolDTOList);

        //删除原有资金流水并退款
        deleteStackAndRefund(salesOrder.getId());

        geneStackFundDetail(fundPoolDTOList, customerId, salesOrder.getId());
    }

    private List<FundPoolDTO> transferToFundPoolDTO(BigDecimal salesOrderAmount, List<FundPool> incomeFundPoolList,
                                                    List<Credit> creditList) {
        List<FundPoolDTO> result = newArrayList();


        for (FundPool fundPool : incomeFundPoolList) {
            FundPoolDTO fundPoolDTO = new FundPoolDTO();
            fundPoolDTO.setFundId(fundPool.getId());

            if (fundPool.getPaymentMethod().equalsIgnoreCase(PaymentMethodEnum.ACCEPTANCE_BILL.getCode())) {
                fundPoolDTO.setSortLevel(1);
                fundPoolDTO.setType(IncomeAndCreditEnum.ACCEPTANCE_BILL.getCode());
                fundPoolDTO.setBillDueDate((int) DateUtil.betweenDay(fundPool.getIncomingDate(), fundPool.getTicketDate(), true));
            } else {
                fundPoolDTO.setSortLevel(2);
                fundPoolDTO.setType(IncomeAndCreditEnum.CASH.getCode());
                fundPoolDTO.setBillDueDate(10000);
            }

            fundPoolDTO.setAvailAmount(
                    fundPool.getAvailAmount().subtract(fundDetailService.getUnLoadAmount(fundPool.getId())));

            result.add(fundPoolDTO);
        }

        for (Credit credit : creditList) {
            FundPoolDTO fundPoolDTO = new FundPoolDTO();
            fundPoolDTO.setFundId(credit.getId());
            fundPoolDTO.setType(IncomeAndCreditEnum.CREDIT.getCode());
            fundPoolDTO.setAvailAmount(credit.getAvailAmount().subtract(fundDetailService.getUnLoadAmount(credit.getId())));
            fundPoolDTO.setSortLevel(3);
            fundPoolDTO.setBillDueDate(10000);
            result.add(fundPoolDTO);
        }

        //过滤可用金额大于0
        result = result.stream().filter(i -> i.getAvailAmount().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());


        //排序 现款 > 近期到期汇票 > 远期到期汇票 > 授信 sort 默认升序
        //第二排序为 汇票先用即将到期得
        //第三排序为 先使用余额少的
        result = result.stream()
                .sorted(Comparator.comparing(FundPoolDTO::getSortLevel)
                        .thenComparing(FundPoolDTO::getBillDueDate, Comparator.reverseOrder())
                        .thenComparing(FundPoolDTO::getAvailAmount))
                .collect(Collectors.toList());

        BigDecimal totalAvailAmount = result.stream().map(FundPoolDTO::getAvailAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        if (salesOrderAmount.compareTo(totalAvailAmount) > 0) {
            log.info("可用金额不足，请联系客户打款, salesOrderAmount:{}, totalAvailAmount:{}",
                    salesOrderAmount, totalAvailAmount);
            throw new ScmException("可用金额不足，请联系客户打款或重新选款");
        }

        BigDecimal needShareAmount = salesOrderAmount;
        for (int i = 0; i < result.size() && needShareAmount.compareTo(BigDecimal.ZERO) > 0; i++) {
            FundPoolDTO fundPoolDTO = result.get(i);

            if (fundPoolDTO.getAvailAmount().compareTo(needShareAmount) >= 0) {
                fundPoolDTO.setUseAmount(needShareAmount);
                needShareAmount = BigDecimal.ZERO;

            } else {
                needShareAmount = needShareAmount.subtract(fundPoolDTO.getAvailAmount());
                fundPoolDTO.setUseAmount(fundPoolDTO.getAvailAmount());
            }
        }
        return result.stream().filter(t -> t.getUseAmount().compareTo(BigDecimal.ZERO) != 0).collect(Collectors.toList());
    }


    public String generateFundDesc(List<FundPoolDTO> fundPoolDTOList) {
        if (CollectionUtil.isEmpty(fundPoolDTOList)) {
            return "";
        }

        if ("12".equalsIgnoreCase(TenantContext.getTenant())) {
            BigDecimal maxAmount = fundPoolDTOList.get(0).getUseAmount();
            String maxAmountFundId = fundPoolDTOList.get(0).getFundId();
            for (FundPoolDTO fundPoolDTO : fundPoolDTOList) {
                if (fundPoolDTO.getUseAmount().compareTo(maxAmount) > 0) {
                    maxAmount = fundPoolDTO.getUseAmount();
                    maxAmountFundId = fundPoolDTO.getFundId();
                }
            }

            FundPool fundPool = fundPoolService.getById(maxAmountFundId);

            fundPool.setGapDays(DateUtils.gapDays(fundPool.getIncomingDate(), fundPool.getTicketDate()));

            return fundPool.geneFundDesc();
        }

        return "";
    }

    /**
     * 自动选款：使用优先级：现款 > 其他来款方式（预录款、截转） > 银行承兑汇票（优先使用临近到期汇票）
     *
     * @param salesOrderAmount
     * @param customerId
     * @return
     */
    public List<FundPoolDTO> autoSelectFundPool(BigDecimal salesOrderAmount, String customerId, List<String> fundIds) {

        QueryWrapper<FundPool> incomeFundPoolQueryWrapper = new QueryWrapper<>();
        incomeFundPoolQueryWrapper.lambda().eq(FundPool::getCustomerId, customerId)
                .eq(FundPool::getStatus, CommonCheckStatusEnum.APPROVE.getCode())
                .gt(FundPool::getAvailAmount, BigDecimal.ZERO);

        //如果客户有选款 则查询客户的选款
        if (CollectionUtil.isNotEmpty(fundIds)) {
            incomeFundPoolQueryWrapper.lambda().in(FundPool::getId, fundIds);
        }


        //查询来款（包含现金，汇票）
        List<FundPool> incomeFundPoolList = fundPoolService.list(incomeFundPoolQueryWrapper);

        QueryWrapper<Credit> creditQueryWrapper = new QueryWrapper<>();
        creditQueryWrapper.lambda().eq(Credit::getCustomerId, customerId)
                .gt(Credit::getAvailAmount, BigDecimal.ZERO);

        //所有的授信
        List<Credit> creditList = creditService.list(creditQueryWrapper);

        return transferToFundPoolDTO(salesOrderAmount, incomeFundPoolList, creditList);
    }


    /**
     * 自动选款：使用优先级：现款 > 其他来款方式（预录款、截转） > 银行承兑汇票（优先使用临近到期汇票）
     *
     * @param salesOrderAmount 选款金额
     * @param customerId       客户id
     * @param fundIds          选款id
     * @param noSettleFundIds  不使用的来款id
     * @return
     */
    public List<FundPoolDTO> autoSelectFundPool(BigDecimal salesOrderAmount, String customerId, List<String> fundIds, List<String> noSettleFundIds) {
        if (CollectionUtil.isEmpty(noSettleFundIds)) {
            throw new ScmException(StrUtil.format("麻烦联系一下技术人员，他调错方法啦！"));
        }

        QueryWrapper<FundPool> incomeFundPoolQueryWrapper = new QueryWrapper<>();
        incomeFundPoolQueryWrapper.lambda().eq(FundPool::getCustomerId, customerId)
                .eq(FundPool::getStatus, CommonCheckStatusEnum.APPROVE.getCode())
                .gt(FundPool::getAvailAmount, BigDecimal.ZERO)
                .notIn(FundPool::getId, noSettleFundIds);

        //如果客户有选款 则查询客户的选款
        if (CollectionUtil.isNotEmpty(fundIds)) {
            incomeFundPoolQueryWrapper.lambda().in(FundPool::getId, fundIds);
        }


        //查询来款（包含现金，汇票）
        List<FundPool> incomeFundPoolList = fundPoolService.list(incomeFundPoolQueryWrapper);

        QueryWrapper<Credit> creditQueryWrapper = new QueryWrapper<>();
        creditQueryWrapper.lambda().eq(Credit::getCustomerId, customerId)
                .gt(Credit::getAvailAmount, BigDecimal.ZERO);

        //所有的授信
        List<Credit> creditList = creditService.list(creditQueryWrapper);

        return transferToFundPoolDTO(salesOrderAmount, incomeFundPoolList, creditList);
    }


    public void deleteStackAndRefund(String orderId) {
        QueryWrapper<FundDetail> fundDetailQueryWrapper = new QueryWrapper<>();
        fundDetailQueryWrapper.lambda().eq(FundDetail::getOutTradeNo, orderId)
                .eq(FundDetail::getType, FundDetailTypeEnum.PRE_USE_STACK.getCode());

        List<FundDetail> fundDetailList = fundDetailService.list(fundDetailQueryWrapper);

        if (CollectionUtil.isEmpty(fundDetailList)) {
            throw new ScmException(StrUtil.format("查询不到对应的资金流水，请联系技术人员删除"));
        }

        for (FundDetail fundDetail : fundDetailList) {

            if (fundDetail.getFundType().equalsIgnoreCase(IncomeAndCreditEnum.CREDIT.getCode())) {
                Credit credit = creditService.getById(fundDetail.getFundId());

                credit.setAvailAmount(credit.getAvailAmount().add(fundDetail.getAmount()));

                creditService.updateById(credit);
            } else {
                FundPool fundPool = fundPoolService.getById(fundDetail.getFundId());

                fundPool.setAvailAmount(fundPool.getAvailAmount().add(fundDetail.getAmount()));

                fundPoolService.updateById(fundPool);
            }

            fundDetailService.removeById(fundDetail.getId());
        }


    }


    public void generateSalesOrder(String customerId, String orderId, List<FundPoolDTO> fundPoolDTOList, String paymentFlag) {

        if (PaymentFlagEnum.PAID.getCode().equalsIgnoreCase(paymentFlag)) {
            List<FundDetail> fundDetailList = CollUtil.newArrayList();

            for (FundPoolDTO fundPoolDTO : fundPoolDTOList) {
                fundDetailList.add(new FundDetail(customerId, fundPoolDTO.getUseAmount(),
                        FundDetailTypeEnum.PRE_USE.getCode(), orderId, fundPoolDTO.getFundId(), fundPoolDTO.getType()));
            }
            fundDetailService.saveBatch(fundDetailList);
        }
    }

    /**
     * 计算来款使用金额
     *
     * @param fundPoolIdList
     * @param account
     * @return
     */
    public List<FundPoolDTO> getFundPoolUs(List<String> fundPoolIdList, BigDecimal account) {
        //获取来款
        List<FundPool> fundPoolList = fundPoolService.listByIds(fundPoolIdList);

        List<FundPoolDTO> fundPoolDTOList = newArrayList();
        for (FundPool fundPool : fundPoolList) {
            BigDecimal min = fundPool.getAvailAmount().min(account);
            account = account.subtract(min);
            FundPoolDTO fundPoolDTO = new FundPoolDTO();
            fundPoolDTO.setFundId(fundPool.getId());
            fundPoolDTO.setUseAmount(min);
            fundPoolDTOList.add(fundPoolDTO);
            if (account.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
        }

        return fundPoolDTOList;
    }


    public void settle(Stack stack) {
        //获取装车单明细
        QueryWrapper<StackDet> stackDetQueryWrapper = new QueryWrapper<>();
        stackDetQueryWrapper.lambda().eq(StackDet::getStackId, stack.getId());
        List<StackDet> stackDetList = stackDetService.list(stackDetQueryWrapper);

        //累加结算金额与优惠金额
        BigDecimal settleTotalAccount = BigDecimal.ZERO;
        BigDecimal discountTotalAccount = BigDecimal.ZERO;
        for (StackDet stackDet : stackDetList) {
            settleTotalAccount = settleTotalAccount.add(stackDet.getDiscountTotalAmount());
            discountTotalAccount = discountTotalAccount.add(stackDet.getTotalAmount().subtract(stackDet.getDiscountTotalAmount()));
        }


        QueryWrapper<FundDetail> fundDetailQueryWrapper = new QueryWrapper<>();
        fundDetailQueryWrapper.lambda().eq(FundDetail::getOutTradeNo, stack.getSalesOrderId())
                .eq(FundDetail::getType, FundDetailTypeEnum.PRE_USE_STACK.getCode());
        List<FundDetail> fundDetailList = fundDetailService.list(fundDetailQueryWrapper);

        List<FundPool> updateFundPool = newArrayList();
        //有优惠则需要操作来款
        if (discountTotalAccount.compareTo(BigDecimal.ZERO) > 0) {

            for (FundDetail fundDetail : fundDetailList) {
                BigDecimal min = discountTotalAccount.min(fundDetail.getAmount());
                //修改流水金额，后续结算流水用
                fundDetail.setAmount(fundDetail.getAmount().subtract(min));

                //修改来款可用金额
                FundPool fundPool = fundPoolService.getById(fundDetail.getFundId());
                fundPool.setAvailAmount(fundPool.getAvailAmount().add(min));
                updateFundPool.add(fundPool);

                //插入优惠流水
                fundDetailService.saveFundDetail(min, stack.getCustomerId(), stack.getId()
                        , FundDetailTypeEnum.DISCOUNT, fundDetail.getFundId(), fundDetail.getFundType());
                discountTotalAccount = discountTotalAccount.subtract(min);
                if (discountTotalAccount.compareTo(BigDecimal.ZERO) == 0) {
                    break;
                }

            }

        }

        List<FundDetail> settleFundDetailList = newArrayList();
        for (FundDetail fundDetail : fundDetailList) {
            BigDecimal fundDetailAmount = fundDetail.getAmount();
            if (fundDetailAmount.compareTo(BigDecimal.ZERO) > 0) {
                FundDetail settleFundDetail = new FundDetail();
                settleFundDetail.setAmount(fundDetailAmount);
                settleFundDetail.setCustomerId(stack.getCustomerId());
                settleFundDetail.setOutTradeNo(stack.getId());
                settleFundDetail.setType(FundDetailTypeEnum.SETTLE.getCode());
                settleFundDetail.setFundId(fundDetail.getFundId());
                settleFundDetail.setFundType(fundDetail.getFundType());
                settleFundDetailList.add(settleFundDetail);
            }
        }

        fundDetailService.saveBatch(settleFundDetailList);
        fundDetailService.removeByIds(fundDetailList);
        fundPoolService.updateBatchById(updateFundPool);
    }
}
