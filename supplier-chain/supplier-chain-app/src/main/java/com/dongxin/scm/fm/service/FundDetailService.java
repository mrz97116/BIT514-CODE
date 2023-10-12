package com.dongxin.scm.fm.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dongxin.scm.bd.enums.TheoryAndNoTheoryEnum;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.fm.entity.FundDetail;
import com.dongxin.scm.fm.enums.FundDetailTypeEnum;
import com.dongxin.scm.fm.mapper.FundDetailMapper;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * @Description: 资金明细
 * @Author: jeecg-boot
 * @Date: 2020-10-15
 * @Version: V1.0
 */
@Service
public class FundDetailService extends BaseService<FundDetailMapper, FundDetail> {

    @Autowired
    private CustomerProfileService customerProfileService;

    public void saveFundDetail(BigDecimal amount, String customerId,
                               String outTradeNo, FundDetailTypeEnum fundDetailTypeEnum, String fundId, String incomeAndCreditEnum) {
        if (amount.compareTo(BigDecimal.ZERO) != 0) {
            FundDetail fundDetail = new FundDetail();
            fundDetail.setAmount(amount);
            fundDetail.setType(fundDetailTypeEnum.getCode());
            fundDetail.setCustomerId(customerId);
            fundDetail.setOutTradeNo(outTradeNo);
            fundDetail.setFundId(fundId);
            fundDetail.setFundType(incomeAndCreditEnum);
            save(fundDetail);
        }
    }

    //判断是否存在资金流水
    @Transactional(rollbackFor = Exception.class)
    public boolean hasFundDetail(String customerId) {
        QueryWrapper<FundDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FundDetail::getCustomerId, customerId);
        int count = count(queryWrapper);
        return count > 0;
    }

    public BigDecimal getUnLoadAmount(String fundId) {
        return baseMapper.getAmount(FundDetailTypeEnum.PRE_USE.getCode(), fundId);
    }

    public BigDecimal getUnLoadAmountByCustomerId(String customerId) {
        return baseMapper.getAmountByCustomerId(FundDetailTypeEnum.PRE_USE.getCode(), customerId);
    }

    public BigDecimal getUnSettleStackAmountByCustomerId(String customerId) {
        return baseMapper.getAmountByCustomerId(FundDetailTypeEnum.PRE_USE_STACK.getCode(), customerId);
    }


    public void cleanUnloadRecord(String outTradeNo) {
        QueryWrapper<FundDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FundDetail::getType, FundDetailTypeEnum.PRE_USE.getCode())
                .eq(FundDetail::getOutTradeNo, outTradeNo);
        removeByWrapper(queryWrapper);
    }


    public FundDetail addFundDetail(BigDecimal amount, String customerId,
                                    String outTradeNo, FundDetailTypeEnum fundDetailTypeEnum, String fundId, String incomeAndCreditEnum) {

        FundDetail fundDetail = new FundDetail();
        fundDetail.setAmount(amount);
        fundDetail.setType(fundDetailTypeEnum.getCode());
        fundDetail.setCustomerId(customerId);
        fundDetail.setOutTradeNo(outTradeNo);
        fundDetail.setFundId(fundId);
        fundDetail.setFundType(incomeAndCreditEnum);
        return fundDetail;
    }


    public IPage<FundDetail> translate(IPage<FundDetail> pageList) {
        List<String> customerIds = newArrayList();
        for (FundDetail fundDetail : pageList.getRecords()) {
            customerIds.add(fundDetail.getCustomerId());
        }

        Map<String, String> fundDetailTypeEnumMap = new HashMap<>();
        FundDetailTypeEnum[] theoryAndNoTheoryEnums = FundDetailTypeEnum.values();
        for (FundDetailTypeEnum fundDetailTypeEnum : theoryAndNoTheoryEnums) {
            fundDetailTypeEnumMap.put(fundDetailTypeEnum.getCode(), fundDetailTypeEnum.getDesc());
        }



        Map<String, String> customerNameMap = customerProfileService.getNameMap(customerIds);
        for (FundDetail fundDetail : pageList.getRecords()) {
            fundDetail.setCustomerName(customerNameMap.get(fundDetail.getCustomerId()));
            fundDetail.setType(fundDetailTypeEnumMap.get(fundDetail.getType()));
        }
        return pageList;
    }
}
