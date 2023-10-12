package com.dongxin.scm.cm.service;

import com.dongxin.scm.cm.entity.CustomerBank;
import com.dongxin.scm.cm.mapper.CustomerBankMapper;
import com.dongxin.scm.fm.entity.Bank;
import com.dongxin.scm.fm.service.BankService;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 顾客银行卡信息
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */
@Service
public class CustomerBankService extends BaseService<CustomerBankMapper, CustomerBank> {

    @Autowired
    private BankService bankService;

    public List<CustomerBank> selectByMainId(String mainId) {
        return baseMapper.selectByMainId(mainId);
    }

    public void deleteBatchByMainId(String id) {
        List<CustomerBank> details = selectByMainId(id);
        for (CustomerBank detail : details) {
            logicDelete(detail);
        }
    }

}
