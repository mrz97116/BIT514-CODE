package com.dongxin.scm.om.service;

import com.dongxin.scm.om.entity.CompanyInfoBank;
import com.dongxin.scm.om.mapper.CompanyInfoBankMapper;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 公司银行卡信息
 * @Author: jeecg-boot
 * @Date: 2020-12-14
 * @Version: V1.0
 */
@Service
public class CompanyInfoBankService extends BaseService<CompanyInfoBankMapper, CompanyInfoBank> {

    @Autowired
    private CompanyInfoBankMapper companyInfoBankMapper;

    public List<CompanyInfoBank> selectByMainId(String mainId) {
        return companyInfoBankMapper.selectByMainId(mainId);
    }
}
