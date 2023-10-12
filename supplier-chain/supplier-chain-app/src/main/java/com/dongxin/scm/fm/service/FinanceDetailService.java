package com.dongxin.scm.fm.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.enums.SerialNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.entity.FinanceDetail;
import com.dongxin.scm.fm.entity.FundAccount;
import com.dongxin.scm.fm.entity.FundPool;
import com.dongxin.scm.fm.enums.AccountTypeEnum;
import com.dongxin.scm.fm.enums.FinanceDetailEnum;
import com.dongxin.scm.fm.mapper.FinanceDetailMapper;
import com.dongxin.scm.sm.vo.OptionVO;
import com.dongxin.scm.utils.SerialNoUtil;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 财务明细
 * @Author: jeecg-boot
 * @Date: 2021-04-06
 * @Version: V1.0
 */
@Service
public class FinanceDetailService extends BaseService<FinanceDetailMapper, FinanceDetail> {
    @Autowired
    FundAccountService fundAccountService;

    @Autowired
    CustomerProfileService customerProfileService;

    @Autowired
    FundPoolService fundPoolService;

    @Autowired
    RefundRecordsService refundRecordsService;

    @Autowired
    SettleInfoService settleInfoService;

    /**
     * 来款
     *
     * @param customerId
     * @param fundPoolId
     */
    public void addPayment(String customerId, String fundPoolId) {
        if (StrUtil.isBlank(customerId)) {
            log.error(StrUtil.format("客户id为空:" + customerId + ",来款id:" + fundPoolId));
            throw new ScmException(StrUtil.format("客户id为空:" + customerId + ",来款id:" + fundPoolId));
        }
        if (StrUtil.isBlank(fundPoolId)) {
            log.error(StrUtil.format("来款id为空:" + fundPoolId + ",客户id:" + customerId));
            throw new ScmException(StrUtil.format("来款id为空:" + fundPoolId, "客户id:" + customerId));
        }

        FundPool fundPool = fundPoolService.getById(fundPoolId);

        BigDecimal accountBalance = selectAccountBalance(customerId);

        FinanceDetail financeDetail = new FinanceDetail();
        financeDetail.setTime(fundPool.getIncomingDate());
        financeDetail.setAccountBalance(accountBalance);
        financeDetail.setAccount(fundPool.getIncomingAmount());
        financeDetail.setType(FinanceDetailEnum.PAYMENT.getCode());
        financeDetail.setCustomerId(customerId);
        financeDetail.setOutTradeNumber(fundPool.getReceiptCode());
        financeDetail.setOutTradeNo(fundPoolId);
        financeDetail.setFinanceDetailNo(SerialNoUtil.getSerialNo(SerialNoEnum.FINANCE_DETAIL_NO));

        save(financeDetail);
    }


    public void useFund(BigDecimal useAccount, String customerId, String id, String no) {
        BigDecimal accountBalance = selectAccountBalance(customerId);

        addFinanceDetail(useAccount, id, no, customerId, FinanceDetailEnum.USE_FUND.getCode(), accountBalance);
    }


    public void refund(BigDecimal refundAccount, String customerId, String id, String no) {
        BigDecimal accountBalance = selectAccountBalance(customerId);

        addFinanceDetail(refundAccount, id, no, customerId, FinanceDetailEnum.REFUND.getCode(), accountBalance);
    }

    public void customerRefund(BigDecimal refundAccount, String customerId, String id) {
        BigDecimal accountBalance = selectAccountBalance(customerId);

        addFinanceDetail(refundAccount, id, null, customerId, FinanceDetailEnum.APPLY_REFUND.getCode(), accountBalance);
    }


    /**
     * 翻译名字
     *
     * @param pageList
     * @return
     */
    public IPage<FinanceDetail> translate(IPage<FinanceDetail> pageList) {
        Map<String, String> financeDetailMap = new HashMap<>();
        FinanceDetailEnum[] financeDetailEnums = FinanceDetailEnum.values();
        for (FinanceDetailEnum financeDetailEnum : financeDetailEnums) {
            financeDetailMap.put(financeDetailEnum.getCode(), financeDetailEnum.getDesc());
        }

        List<String> customerIdList = newArrayList();

        for (FinanceDetail financeDetail : pageList.getRecords()) {
            customerIdList.add(financeDetail.getCustomerId());
        }

        Map<String, String> customerIdMap = customerProfileService.getNameMap(customerIdList);
        for (FinanceDetail financeDetail : pageList.getRecords()) {
            financeDetail.setCustomerId(customerIdMap.get(financeDetail.getCustomerId()));
            financeDetail.setType(financeDetailMap.get(financeDetail.getType()));
        }

        return pageList;

    }

    /**
     * 获取类型名字
     *
     * @return
     */
    public List<OptionVO> selectType() {
        List<OptionVO> typeOptionVO = newArrayList();
        FinanceDetailEnum[] financeDetailEnums = FinanceDetailEnum.values();
        for (FinanceDetailEnum financeDetailEnum : financeDetailEnums) {
            OptionVO optionVO = new OptionVO();
            optionVO.setText(financeDetailEnum.getDesc());
            optionVO.setValue(financeDetailEnum.getCode());

            typeOptionVO.add(optionVO);
        }

        return typeOptionVO;
    }


    public BigDecimal selectAccountBalance(String customerId) {
        BigDecimal accountBalance = BigDecimal.ZERO;

        QueryWrapper<FundAccount> fundAccountQueryWrapper = new QueryWrapper<>();
        fundAccountQueryWrapper.lambda().eq(FundAccount::getCustomerId, customerId);
        List<FundAccount> fundAccountList = fundAccountService.list(fundAccountQueryWrapper);

        BigDecimal incomeAccount = BigDecimal.ZERO;
        BigDecimal creditAccount = BigDecimal.ZERO;
        for (FundAccount fundAccount : fundAccountList) {
            if (fundAccount.getType().equals(AccountTypeEnum.INCOME.getCode())) {
                incomeAccount = incomeAccount.add(fundAccount.getAvailableAmount());
            }
            if (fundAccount.getType().equals(AccountTypeEnum.CREDIT.getCode())) {
                creditAccount = creditAccount.add(fundAccount.generateToBePayCredit());
            }
        }
        accountBalance = accountBalance.add(incomeAccount.subtract(creditAccount));

        return accountBalance;

    }

    public void addFinanceDetail(BigDecimal account, String id, String no, String customerId, String type, BigDecimal accountBalance) {
        FinanceDetail financeDetail = new FinanceDetail();
        financeDetail.setAccount(account);
        financeDetail.setOutTradeNo(id);
        financeDetail.setOutTradeNumber(no);
        financeDetail.setCustomerId(customerId);
        financeDetail.setType(type);
        financeDetail.setAccountBalance(accountBalance);
        financeDetail.setTime(new Date());
//        financeDetail.setFinanceDetailNo(SerialNoUtil.getSerialNo(SerialNoEnum.FINANCE_DETAIL_NO));

        save(financeDetail);
    }

    public void deleteFundPool(FundPool fundPool) {
        BigDecimal accountBalance = selectAccountBalance(fundPool.getCustomerId());

        addFinanceDetail(fundPool.getIncomingAmount(), fundPool.getId(), fundPool.getReceiptCode(),
                fundPool.getCustomerId(), FinanceDetailEnum.DELETE_FUND_POOL.getCode(), accountBalance);
    }
}
