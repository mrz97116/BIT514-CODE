package com.dongxin.scm.fm.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.entity.BlackBank;
import com.dongxin.scm.fm.mapper.BlackBankMapper;
import com.dongxin.scm.om.util.Constants;
import org.jeecg.config.mybatis.TenantContext;
import org.springframework.stereotype.Service;
import org.jeecg.common.system.base.service.BaseService;

/**
 * @Description: 银行黑名单
 * @Author: jeecg-boot
 * @Date: 2021-12-31
 * @Version: V1.0
 */
@Service
public class BlackBankService extends BaseService<BlackBankMapper, BlackBank> {

    public boolean addBank(BlackBank blackBank) {

        QueryWrapper<BlackBank> blackBankQueryWrapper = new QueryWrapper<>();
        blackBankQueryWrapper.lambda().eq(BlackBank::getBank, blackBank.getBank());
        BlackBank blackBankInDb = getOne(blackBankQueryWrapper);
        if (ObjectUtil.isNotEmpty(blackBankInDb)) {
            throw new ScmException("该银行已在黑名单，请勿重复添加！");
        }

        boolean flag = super.save(blackBank);

        return flag;

    }

    public boolean blackBankChick(String id) {

        if (Constants.YRM_TENANT_IDS.contains(TenantContext.getTenant())){
            return false;
        }

        QueryWrapper<BlackBank> blackBankQueryWrapper = new QueryWrapper<>();
        blackBankQueryWrapper.lambda().eq(BlackBank::getBank, id);

        BlackBank blackBank = getOne(blackBankQueryWrapper);
        if (ObjectUtil.isNotEmpty(blackBank)) {
            return true;
        }

        return false;

    }

}
