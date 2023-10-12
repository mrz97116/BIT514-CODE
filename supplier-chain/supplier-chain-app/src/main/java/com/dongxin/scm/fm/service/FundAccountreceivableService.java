package com.dongxin.scm.fm.service;

import com.dongxin.scm.enums.SerialNoEnum;
import com.dongxin.scm.fm.entity.FundAccountreceivable;
import com.dongxin.scm.fm.entity.SettleInfo;
import com.dongxin.scm.fm.mapper.FundAccountreceivableMapper;
import com.dongxin.scm.utils.SerialNoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @Description: 应收账款
 * @Author: jeecg-boot
 * @Date: 2021-06-09
 * @Version: V1.0
 */
@Service
public class FundAccountreceivableService extends BaseService<FundAccountreceivableMapper, FundAccountreceivable> {

    @Autowired
    private FundAccountService fundAccountService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(FundAccountreceivable fundAccountreceivable) {
        fundAccountreceivable.setSerialNo(SerialNoUtil.getSerialNo(SerialNoEnum.FUND_ACCOUNTRECEIVABLE_NO));
        return super.save(fundAccountreceivable);
    }
}
