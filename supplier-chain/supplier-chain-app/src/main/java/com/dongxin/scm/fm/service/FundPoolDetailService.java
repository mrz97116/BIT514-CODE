package com.dongxin.scm.fm.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.entity.FundPool;
import com.dongxin.scm.fm.entity.FundPoolDetail;
import com.dongxin.scm.fm.enums.FundDetailTypeEnum;
import com.dongxin.scm.fm.enums.FundPoolDetailTypeEnum;
import com.dongxin.scm.fm.mapper.FundPoolDetailMapper;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * @Description: 记录来款资金使用明细
 * @Author: jeecg-boot
 * @Date: 2021-05-13
 * @Version: V1.0
 */
@Service
public class FundPoolDetailService extends BaseService<FundPoolDetailMapper, FundPoolDetail> {

    @Autowired
    private FundPoolService fundPoolService;


    //记录来款流水明细
    public void addFundPoolDetail(String customerId, String accountId, String fundPoolId, String outTradeNo, BigDecimal amount, String type) {
        FundPoolDetail fundPoolDetail = new FundPoolDetail();
        fundPoolDetail.setCustomerId(customerId);
        fundPoolDetail.setAccountId(accountId);
        fundPoolDetail.setFundPoolId(fundPoolId);
        fundPoolDetail.setOutTradeNo(outTradeNo);
        fundPoolDetail.setAmount(amount);
        fundPoolDetail.setType(type);
        save(fundPoolDetail);
    }

    //查找来款流水明细
    @Transactional(rollbackFor = Exception.class)
    public List<FundPoolDetail> listFundPoolDetailByOutTradeNo(String outTradeNo) {
        QueryWrapper<FundPoolDetail> fundPoolDetailQueryWrapper = new QueryWrapper<>();
        fundPoolDetailQueryWrapper.lambda().eq(FundPoolDetail::getOutTradeNo, outTradeNo)
                .eq(FundPoolDetail::getType, FundDetailTypeEnum.PRE_USE.getCode())
                .gt(FundPoolDetail::getAmount, BigDecimal.ZERO);
        return list(fundPoolDetailQueryWrapper);
    }

}
