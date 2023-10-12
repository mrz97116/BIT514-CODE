package com.dongxin.scm.fm.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.common.enums.CommonCheckStatusEnum;
import com.dongxin.scm.common.service.LockService;
import com.dongxin.scm.enums.IncomeAndCreditEnum;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.dto.FundPoolDTO;
import com.dongxin.scm.fm.entity.*;
import com.dongxin.scm.fm.enums.FundDetailTypeEnum;
import com.dongxin.scm.fm.enums.InvoiceStatusEnum;
import com.dongxin.scm.fm.enums.SettleStatusEnum;
import com.dongxin.scm.fm.mapper.FundAccountMapper;
import com.dongxin.scm.fm.vo.CustomerBalanceVO;
import com.dongxin.scm.fm.vo.FundAccountVO;
import com.dongxin.scm.om.util.Constants;
import com.dongxin.scm.sm.entity.Stack;
import com.dongxin.scm.sm.entity.StackDet;
import com.dongxin.scm.sm.enums.StatusEnum;
import com.dongxin.scm.sm.service.StackDetService;
import com.dongxin.scm.sm.service.StackService;
import com.dongxin.scm.utils.ScmBeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.newHashMap;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;

/**
 * @Description: 资金账户表
 * @Author: jeecg-boot
 * @Date: 2020-11-23
 * @Version: V1.0
 */
@Service
public class FundAccountService extends BaseService<FundAccountMapper, FundAccount> {


    @Autowired
    private RefundRecordsService refundRecordsService;

    @Autowired
    private CustomerProfileService customerProfileService;

    @Autowired
    private FundPoolService fundPoolService;

    @Autowired
    private StackService stackService;

    @Autowired
    private FundDetailService fundDetailService;

    @Autowired
    private CreditService creditService;

    @Autowired
    private SettleInfoService settleInfoService;

    @Autowired
    private FundService fundService;

    @Autowired
    private StackDetService stackDetService;

    @Autowired
    private LockService lockService;


    /**
     * 退款
     *
     * @param customerId
     * @param refundAmount
     */
    @Transactional(rollbackFor = Exception.class)
    public void refundUpdate(String customerId, BigDecimal refundAmount, String receiptCode, String remarks) {

        LockInfo lockInfo = lockService.lock(Constants.CUSTOMER_LOCK_HEADER + customerId);
        try {

            RefundRecords refundRecords = new RefundRecords();  //退款记录
            refundRecords.setRefundAmount(refundAmount);
            refundRecords.setCustomerId(customerId);
            refundRecords.setReceiptCode(receiptCode);
            refundRecords.setStatus(CommonCheckStatusEnum.PENDING_VERIFY.getCode());    //待审核
            refundRecords.setRefundDate(new Date());
            refundRecords.setRemarks(remarks);
            refundRecordsService.save(refundRecords);

            List<FundPool> updateFundPoolList = newArrayList();
            List<FundDetail> addFundDetailList = newArrayList();
            List<String> fundIds = newArrayList();
            Map<String, BigDecimal> fundIdAndAmountMap = newHashMap();

            if (StrUtil.isNotEmpty(receiptCode)) {
                QueryWrapper<FundPool> fundPoolQueryWrapper = new QueryWrapper<>();
                fundPoolQueryWrapper.lambda().eq(FundPool::getReceiptCode, receiptCode);
                FundPool fundPool = fundPoolService.getOne(fundPoolQueryWrapper);

                if (refundAmount.compareTo(fundPool.getAvailAmount()) != 0){
                    throw new ScmException("收据编号对应来款可用金额与输入的退款金额不相等");
                }

                fundIds.add(fundPool.getId());
                fundIdAndAmountMap.put(fundPool.getId(),fundPool.getAvailAmount());

            } else {
                List<FundPoolDTO> fundPoolDTOList = fundService.autoSelectFundPool(refundAmount, customerId, null);

                for (FundPoolDTO fundPoolDTO : fundPoolDTOList) {
                    fundIdAndAmountMap.put(fundPoolDTO.getFundId(), fundPoolDTO.getUseAmount());
                    fundIds.add(fundPoolDTO.getFundId());
                }
            }
            List<FundPool> fundPoolList = fundPoolService.getBaseMapper().selectBatchIds(fundIds);
            if (CollectionUtil.isEmpty(fundPoolList)) {
                throw new ScmException("未查询到来款");
            }
            for (FundPool fundPool : fundPoolList) {
                BigDecimal fundPoolAvailAmount = fundIdAndAmountMap.get(fundPool.getId());
                BigDecimal min = fundPoolAvailAmount.min(refundAmount);

                fundPool.setAvailAmount(fundPool.getAvailAmount().subtract(min));
                updateFundPoolList.add(fundPool);

                addFundDetailList.add(fundDetailService.addFundDetail(min, customerId, refundRecords.getId()
                        , FundDetailTypeEnum.REFUND, fundPool.getId(), IncomeAndCreditEnum.CASH.getCode()));

                refundAmount = refundAmount.subtract(min);
                if (refundAmount.compareTo(BigDecimal.ZERO) < 0) {
                    break;
                }
            }
            fundPoolService.updateBatchById(updateFundPoolList);
            fundDetailService.saveBatch(addFundDetailList);

        } finally {
            lockService.releaseLock(lockInfo);
        }
    }

    public List<Capital> getCapital(FundAccount fundAccount) {

        List<Capital> result = newArrayList();

        String customerId = fundAccount == null ? null : fundAccount.getCustomerId();
        List<CustomerProfile> customerProfileList = customerProfileService.getCustomerProfileList(customerId);
        if (CollectionUtils.isEmpty(customerProfileList)) {
            return result;
        }
        List<String> customerIds = newArrayList();
        customerProfileList.forEach(i -> customerIds.add(i.getId()));

        //来款可用金额
        QueryWrapper<FundPool> fundPoolQueryWrapper = new QueryWrapper<>();
        fundPoolQueryWrapper.lambda().eq(FundPool::getStatus, CommonCheckStatusEnum.APPROVE.getCode())
                .in(FundPool::getCustomerId, customerIds)
                .gt(FundPool::getAvailAmount, BigDecimal.ZERO);
        List<FundPool> fundPoolList = fundPoolService.list(fundPoolQueryWrapper);
        Map<String, List<FundPool>> fundPoolMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(fundPoolList)) {
            fundPoolMap = fundPoolList.stream().collect(
                    Collectors.groupingBy(
                            score -> score.getCustomerId()
                    ));
        }


        //装车预用金额
        QueryWrapper<FundDetail> preUseStackFundDetailQueryWrapper = new QueryWrapper<>();
        preUseStackFundDetailQueryWrapper.lambda().eq(FundDetail::getType, FundDetailTypeEnum.PRE_USE_STACK.getCode())
                .in(FundDetail::getCustomerId, customerIds);
        List<FundDetail> preUseStackFundDetailList = fundDetailService.list(preUseStackFundDetailQueryWrapper);
        Map<String, List<FundDetail>> preUseStackFundDetailMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(preUseStackFundDetailList)) {
            preUseStackFundDetailMap = preUseStackFundDetailList.stream().collect(
                    Collectors.groupingBy(
                            score -> score.getCustomerId()
                    )
            );
        }


        //提单预用
        QueryWrapper<FundDetail> preUseFundDetailQueryWrapper = new QueryWrapper<>();
        preUseFundDetailQueryWrapper.lambda().eq(FundDetail::getType, FundDetailTypeEnum.PRE_USE.getCode())
                .in(FundDetail::getCustomerId, customerIds);
        List<FundDetail> preUseFundDetailList = fundDetailService.list(preUseFundDetailQueryWrapper);
        Map<String, List<FundDetail>> preUseFundDetailMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(preUseFundDetailList)) {
            preUseFundDetailMap = preUseFundDetailList.stream().collect(
                    Collectors.groupingBy(
                            score -> score.getCustomerId()
                    )
            );
        }


        //退款冻结金额
        QueryWrapper<RefundRecords> refundRecordsQueryWrapper = new QueryWrapper<>();
        refundRecordsQueryWrapper.lambda().eq(RefundRecords::getStatus, CommonCheckStatusEnum.PENDING_VERIFY.getCode())
                .in(RefundRecords::getCustomerId, customerIds);
        List<RefundRecords> refundRecordsList = refundRecordsService.list(refundRecordsQueryWrapper);
        Map<String, List<RefundRecords>> refundRecordsMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(refundRecordsList)) {
            refundRecordsMap = refundRecordsList.stream().collect(
                    Collectors.groupingBy(
                            RefundRecords::getCustomerId
                    )
            );
        }

        //授信金额
        QueryWrapper<Credit> creditQueryWrapper = new QueryWrapper<>();
        creditQueryWrapper.lambda().eq(Credit::getStatus, CommonCheckStatusEnum.APPROVE.getCode())
                .in(Credit::getCustomerId, customerIds);
        List<Credit> creditList = creditService.list(creditQueryWrapper);
        Map<String, List<Credit>> creditMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(creditList)) {
            creditMap = creditList.stream().collect(
                    Collectors.groupingBy(
                            Credit::getCustomerId
                    )
            );
        }

        for (CustomerProfile customerProfile : customerProfileList) {
            String id = customerProfile.getId();

            BigDecimal incomeAvailableAmount = BigDecimal.ZERO;
            BigDecimal stackPreUseAmount = BigDecimal.ZERO;
            BigDecimal salesOrderPreUseAmount = BigDecimal.ZERO;
            BigDecimal refundFrozenAmount = BigDecimal.ZERO;
            BigDecimal creditAmount = BigDecimal.ZERO;

            if (CollectionUtils.isNotEmpty(fundPoolMap.get(id))) {
                for (FundPool fundPool : fundPoolMap.get(id)) {
                    incomeAvailableAmount = incomeAvailableAmount.add(fundPool.getAvailAmount());
                }
            }

            if (CollectionUtils.isNotEmpty(preUseStackFundDetailMap.get(id))) {
                for (FundDetail fundDetail : preUseStackFundDetailMap.get(id)) {
                    stackPreUseAmount = stackPreUseAmount.add(fundDetail.getAmount());
                }
            }

            if (CollectionUtils.isNotEmpty(preUseFundDetailMap.get(id))) {
                for (FundDetail fundDetail : preUseFundDetailMap.get(id)) {
                    salesOrderPreUseAmount = salesOrderPreUseAmount.add(fundDetail.getAmount());
                }
            }

            if (CollectionUtils.isNotEmpty(refundRecordsMap.get(id))) {
                for (RefundRecords refundRecords : refundRecordsMap.get(id)) {
                    refundFrozenAmount = refundFrozenAmount.add(refundRecords.getRefundAmount());
                }
            }

            if (CollectionUtils.isNotEmpty(creditMap.get(id))) {
                for (Credit credit : creditMap.get(id)) {
                    creditAmount = creditAmount.add(credit.getAvailAmount());
                }
            }

            Capital capital = new Capital();
            capital.setCustomerId(id);
            capital.setCustomerName(customerProfile.getCompanyName());
            capital.setIncomeAccount(incomeAvailableAmount.subtract(salesOrderPreUseAmount));
            capital.setCreditAccount(creditAmount);
            capital.setTotalAmount(capital.getIncomeAccount().add(capital.getCreditAccount()));
            capital.setFrozenAccount(refundFrozenAmount);
            capital.setIncomeAccountAndFrozenAccount(capital.getIncomeAccount().add(capital.getFrozenAccount()));
            capital.setPreUseAccount(salesOrderPreUseAmount.add(stackPreUseAmount));
            capital.setAvailableAndPreUseAccount(capital.getTotalAmount().add(capital.getPreUseAccount()).add(capital.getFrozenAccount()));

            result.add(capital);

        }

        return result;

    }


    /**
     * 物资设备网结算
     *
     * @param settleAccount
     * @param customerId
     * @param outTradeNo
     */
    @Transactional(rollbackFor = Exception.class)
    public void materialEquipmentNetworkSettle(BigDecimal settleAccount, String customerId, String outTradeNo, String id) {

        settleAccount = settleAccount.setScale(2, RoundingMode.HALF_UP);
        //校验提单
        if (StringUtils.isBlank(outTradeNo)) {
            log.error(StrUtil.format("{}获取提单失败", outTradeNo));
            throw new ScmException(StrUtil.format("{}获取提单失败", outTradeNo));
        }

        //校验结算金额不小于0
        BigDecimal zero = BigDecimal.ZERO;
        if (settleAccount.compareTo(zero) < 0) {
            log.error(StrUtil.format("{}结算金额小于0", outTradeNo));
            throw new ScmException(StrUtil.format("{}结算金额小于0", outTradeNo));
        }


        throw new ScmException(StrUtil.format("设备物资结算暂时不可用"));

        //插入用款财务明细


    }

    /**
     * 设备物资网结算红冲
     *
     * @param refundAccount
     * @param customerId
     * @param outTradeNo
     */
    @Transactional(rollbackFor = Exception.class)
    public void writeOff(BigDecimal refundAccount, String customerId, String outTradeNo, String id) {
        refundAccount = refundAccount.setScale(2, RoundingMode.HALF_UP);

        //检验使用到的数据
        if (refundAccount.compareTo(BigDecimal.ZERO) < 0) {
            log.error(StrUtil.format("{}客户,{}金额,{}外部订单号", customerId, refundAccount, outTradeNo));
            throw new ScmException(StrUtil.format("{}退款金额小于零", refundAccount));
        }
        if (StrUtil.isBlank(customerId)) {
            log.error(StrUtil.format("{}金额,{}外部订单号", refundAccount, outTradeNo));
            throw new ScmException(StrUtil.format("{}没有客户id", customerId));
        }
        if (StrUtil.isBlank(outTradeNo)) {
            log.error(StrUtil.format("{}客户,{}金额", customerId, refundAccount));
            throw new ScmException(StrUtil.format("{}没有外部订单号", outTradeNo));
        }

        //校验客户信息
        CustomerProfile customerProfile = customerProfileService.getById(customerId);
        if (ObjectUtil.isNull(customerProfile)) {
            log.error(StrUtil.format("{}客户,{}金额,{}外部订单号", customerId, refundAccount, outTradeNo));
            throw new ScmException(StrUtil.format("{}没有维护此客户", customerId));
        }

        //校验客户资金信息
        throw new ScmException(StrUtil.format("设备物资网冲红暂时不可用"));

        //退款


        //插入退款财务明细

    }


    /**
     * 删除结算单
     *
     * @param settleId
     */
    @Transactional(rollbackFor = Exception.class)
    public void settleDelete(String settleId) {

        SettleInfo settleInfo = settleInfoService.getById(settleId);

        LockInfo lockInfo = lockService.lock(Constants.CUSTOMER_LOCK_HEADER + settleInfo.getCustomer());

        try {
            if (ObjectUtil.isEmpty(settleId)) {
                throw new ScmException("查询不到结算单，" + settleId);
            }
            if (settleInfo.getStatus().equals(StatusEnum.WRITE_OFF.getCode())) {
                throw new ScmException("结算单已冲红," + settleId);
            }
            if (settleInfo.getInvoiceStatus().equals(InvoiceStatusEnum.YES.getCode())) {
                throw new ScmException("结算单以生成发票，" + settleId);
            }
            if (settleInfo.getStatus().equals(StatusEnum.WRITE_OFF_REVIEWER.getCode())) {
                throw new ScmException("结算单处于冲红复核状态," + settleId);
            }


            List<FundDetail> addFundDetailList = newArrayList();
            List<FundDetail> deleteFundDetailList = newArrayList();

            Stack stack = stackService.getById(settleInfo.getStackId());

            //获取结算流水
            QueryWrapper<FundDetail> settleFundDetailQueryWrapper = new QueryWrapper<>();
            settleFundDetailQueryWrapper.lambda().eq(FundDetail::getOutTradeNo, stack.getId())
                    .eq(FundDetail::getType, FundDetailTypeEnum.SETTLE.getCode());
            List<FundDetail> settleFundDetailList = fundDetailService.list(settleFundDetailQueryWrapper);

            if (CollectionUtil.isEmpty(settleFundDetailList)) {
                throw new ScmException("请联系技术人员进行删除");
            }

            //根据结算流水生成对应的装车单预用流水
            for (FundDetail fundDetail : settleFundDetailList) {
                FundDetail addFundDetail = new FundDetail();
                ScmBeanUtils.copySysOutsideProperties(fundDetail, addFundDetail);
                addFundDetail.setOutTradeNo(stack.getSalesOrderId());
                addFundDetail.setType(FundDetailTypeEnum.PRE_USE_STACK.getCode());
                addFundDetailList.add(addFundDetail);
                deleteFundDetailList.add(fundDetail);
            }

            //获取优惠流水
            QueryWrapper<FundDetail> discountFundDetailQueryWrapper = new QueryWrapper<>();
            discountFundDetailQueryWrapper.lambda().eq(FundDetail::getType, FundDetailTypeEnum.DISCOUNT.getCode())
                    .eq(FundDetail::getOutTradeNo, stack.getId());
            List<FundDetail> discountFundDetailList = fundDetailService.list(discountFundDetailQueryWrapper);

            if (CollectionUtils.isNotEmpty(discountFundDetailList)) {
                //累加优惠金额
                BigDecimal discountAmount = BigDecimal.ZERO;
                for (FundDetail fundDetail : discountFundDetailList) {
                    discountAmount = discountAmount.add(fundDetail.getAmount());
                    deleteFundDetailList.add(fundDetail);
                }
                //自动选款
                List<FundPoolDTO> fundPoolDTOList = fundService.autoSelectFundPool(discountAmount, stack.getCustomerId(), null);

                for (FundPoolDTO fundPoolDTO : fundPoolDTOList) {
                    //新增装车预用流水
                    FundDetail fundDetail = new FundDetail();
                    fundDetail.setAmount(fundPoolDTO.getUseAmount());
                    fundDetail.setType(FundDetailTypeEnum.PRE_USE_STACK.getCode());
                    fundDetail.setFundId(fundPoolDTO.getFundId());
                    fundDetail.setOutTradeNo(stack.getSalesOrderId());
                    fundDetail.setCustomerId(stack.getCustomerId());
                    fundDetail.setFundType(fundPoolDTO.getType());
                    addFundDetailList.add(fundDetail);

                    //修改来款可用金额
                    FundPool fundPool = fundPoolService.getById(fundPoolDTO.getFundId());
                    fundPool.setAvailAmount(fundPool.getAvailAmount().subtract(fundPoolDTO.getUseAmount()));
                    fundPoolService.updateById(fundPool);
                }

            }
            stack.setSettledId(null);
            stack.setSettledTotalPrice(null);
            stack.setDiscount(null);
            stack.setSettled(SettleStatusEnum.UNSETTLE.getCode());
            stack.setReducedAmount(null);

            QueryWrapper<StackDet> stackDetQueryWrapper = new QueryWrapper<>();
            stackDetQueryWrapper.lambda().eq(StackDet::getStackId, stack.getId());
            List<StackDet> stackDetList = stackDetService.list(stackDetQueryWrapper);
            for (StackDet stackDet : stackDetList) {
                stackDet.setDiscountTotalAmount(null);
                stackDet.setDiscountPrice(null);
            }

            stackService.updateById(stack);
            stackDetService.updateBatchById(stackDetList);
            settleInfoService.removeById(settleInfo);
            fundDetailService.removeByIds(deleteFundDetailList);
            fundDetailService.saveBatch(addFundDetailList);
        } finally {
            lockService.releaseLock(lockInfo);
        }

    }


    public void setExcelFundAccount(List<Capital> exportList) {

        List<String> customerIds = exportList.stream().map(Capital::getCustomerId).collect(Collectors.toList());

        Map<String, String> idAndNames = customerProfileService.getNameMap(customerIds);


        //查询出冻结金额
        QueryWrapper<RefundRecords> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(RefundRecords::getStatus, CommonCheckStatusEnum.PENDING_VERIFY)
                .in(RefundRecords::getCustomerId, customerIds);
        List<RefundRecords> refundRecordsList = refundRecordsService.list(queryWrapper);

        for (Capital capital : exportList) {

            //累加冻结金额
            capital.setFrozenAccount(BigDecimal.ZERO);
            BigDecimal refundAccount = BigDecimal.ZERO;
            for (RefundRecords refundRecords : refundRecordsList) {
                if (capital.getCustomerId().equals(refundRecords.getCustomerId())) {
                    refundAccount = refundAccount.add(refundRecords.getRefundAmount());
                    capital.setFrozenAccount(capital.getFrozenAccount().add(refundRecords.getRefundAmount()));
                }
            }

//            capital.setPreUseAccount(capital.getIncomePreUseAccount().add(capital.getCreditPreUseAccount()));
            capital.setAvailableAndPreUseAccount(refundAccount.add(capital.getIncomeAccount()));

            capital.setCustomerName(idAndNames.get(capital.getCustomerId()));
        }
    }

    public List<FundAccountVO> selectUnSettleAccountAndStackSettleAccount() {
        return baseMapper.selectUnSettleAccountAndStackSettleAccount();
    }


    public void stackWriteOff(BigDecimal writeOffAmount, String customerId, String oldStackId, String salesOrderId) {
//        throw new ScmException(StrUtil.format("装车单冲红暂时不可用"));

        QueryWrapper<FundDetail> queryWrapper = new QueryWrapper();

        List<String> queryTypeList = asList(FundDetailTypeEnum.DISCOUNT.getCode(), FundDetailTypeEnum.SETTLE.getCode());
        queryWrapper.lambda().in(FundDetail::getType, queryTypeList).eq(FundDetail::getOutTradeNo, oldStackId);

        List<FundDetail> historyFundDetailList = fundDetailService.list(queryWrapper);

        Map<String, BigDecimal> accountIdAndAmount = newHashMap();


        for (FundDetail fundDetail : historyFundDetailList) {
            if (StrUtil.isEmpty(fundDetail.getFundId())) {
                throw new ScmException("数据存在异常，请联系开发检查");
            }
            accountIdAndAmount.merge(fundDetail.getFundId(), fundDetail.getAmount(), BigDecimal::add);
        }

        accountIdAndAmount.forEach((k, v) -> {
            FundPool fundPool = fundPoolService.getById(k);
            fundPool.setAvailAmount(fundPool.getAvailAmount().add(v));

            fundPoolService.updateById(fundPool);

            fundDetailService.saveFundDetail(v, customerId, salesOrderId, FundDetailTypeEnum.WRITE_OFF, k, fundPool.geneFundType());
        });

        for (FundDetail fundDetail : historyFundDetailList) {
            fundDetailService.removeById(fundDetail.getId());
        }


    }

    /**
     * 获取未结算装车单总金额 （key:客户id，value:未结算装车单总金额）
     *
     * @param customerId（可空，默认查询所有客户）
     * @return
     */
    public Map<String, BigDecimal> getUnSettleTotalAmount(String customerId) {
        List<FundDetail> fundDetailList = baseMapper.getUnSettleTotalAmount(customerId);
        if (CollectionUtils.isEmpty(fundDetailList)) {
            return newHashMap();
        }

        Map<String, List<FundDetail>> groupingFundDetailMap = newHashMap();
        groupingFundDetailMap = fundDetailList.stream().collect(
                Collectors.groupingBy(
                        score -> score.getCustomerId()
                ));
        Map<String, BigDecimal> unSettleTotalMap = newHashMap();
        for (Map.Entry<String, List<FundDetail>> recode : groupingFundDetailMap.entrySet()) {
            List<FundDetail> fundDetailList1 = recode.getValue();
            BigDecimal unSettleTotalAmount = BigDecimal.ZERO;
            for (FundDetail fundDetail : fundDetailList1) {
                unSettleTotalAmount = unSettleTotalAmount.add(fundDetail.getAmount());
            }
            unSettleTotalMap.put(recode.getKey(), unSettleTotalAmount);
        }

        return unSettleTotalMap;
    }

    /**
     * 获取客户余额
     *
     * @param customerId
     * @return
     */
    public List<CustomerBalanceVO> getCustomerBalance(String customerId) {
        List<FundPool> fundPoolList = fundPoolService.getCustomerFundPool(customerId);

        Map<String, List<FundPool>> groupingFundPoolMap = newHashMap();
        groupingFundPoolMap = fundPoolList.stream().collect(
                Collectors.groupingBy(
                        score -> score.getCustomerId()
                ));
        Map<String, BigDecimal> unSettleTotalAmountMap = getUnSettleTotalAmount(customerId);

        List<CustomerProfile> customerProfileList = customerProfileService.getCustomerProfileList(customerId);

        List<CustomerBalanceVO> customerBalanceVOList = newArrayList();
        for (CustomerProfile recode : customerProfileList) {
            BigDecimal unSettleTotalAmount = unSettleTotalAmountMap.get(recode.getId()) == null ? BigDecimal.ZERO : unSettleTotalAmountMap.get(recode.getId());
            BigDecimal fundPoolAvailTotalAmount = BigDecimal.ZERO;
            if (CollectionUtils.isNotEmpty(groupingFundPoolMap.get(recode.getId()))) {
                for (FundPool fundPool : groupingFundPoolMap.get(recode.getId())) {
                    fundPoolAvailTotalAmount = fundPoolAvailTotalAmount.add(fundPool.getAvailAmount());
                }
            }
            CustomerBalanceVO customerBalanceVO = new CustomerBalanceVO();
            customerBalanceVO.setCustomerId(recode.getId());
            customerBalanceVO.setCustomerBalance(fundPoolAvailTotalAmount.add(unSettleTotalAmount));
            customerBalanceVO.setTenantId(recode.getTenantId().toString());
            customerBalanceVO.setCustomerName(recode.getCompanyName());
            customerBalanceVOList.add(customerBalanceVO);
        }

        return customerBalanceVOList;
    }

    public List<Capital> getCapitalBalance(String customerId) {

        List<CustomerBalanceVO> customerBalanceVOList = getCustomerBalance(customerId);

        List<Capital> capitalList = newArrayList();
        for (CustomerBalanceVO customerBalanceVO : customerBalanceVOList) {
            Capital capital = new Capital();
            capital.setCustomerId(customerBalanceVO.getCustomerId());
            capital.setIncomeAccount(customerBalanceVO.getCustomerBalance());
            capitalList.add(capital);
        }

        return capitalList;

    }
}
