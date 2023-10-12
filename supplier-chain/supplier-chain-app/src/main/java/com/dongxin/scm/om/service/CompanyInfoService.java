package com.dongxin.scm.om.service;

import com.dongxin.scm.om.entity.CompanyInfo;
import com.dongxin.scm.om.entity.CompanyInfoBank;
import com.dongxin.scm.om.mapper.CompanyInfoBankMapper;
import com.dongxin.scm.om.mapper.CompanyInfoMapper;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 公司信息
 * @Author: jeecg-boot
 * @Date: 2020-12-14
 * @Version: V1.0
 */
@Service
public class CompanyInfoService extends BaseService<CompanyInfoMapper, CompanyInfo> {

    @Autowired
    private CompanyInfoMapper companyInfoMapper;
    @Autowired
    private CompanyInfoBankMapper companyInfoBankMapper;

    @Transactional(rollbackFor = Exception.class)
    public void saveMain(CompanyInfo companyInfo, List<CompanyInfoBank> companyInfoBankList) {
        companyInfoMapper.insert(companyInfo);
        if (companyInfoBankList != null && companyInfoBankList.size() > 0) {
            for (CompanyInfoBank entity : companyInfoBankList) {
                //外键设置
                entity.setCompanyId(companyInfo.getId());
                companyInfoBankMapper.insert(entity);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMain(CompanyInfo companyInfo, List<CompanyInfoBank> companyInfoBankList) {
        companyInfoMapper.updateById(companyInfo);

        //1.先删除子表数据
        companyInfoBankMapper.deleteByMainId(companyInfo.getId());

        //2.子表数据重新插入
        if (companyInfoBankList != null && companyInfoBankList.size() > 0) {
            for (CompanyInfoBank entity : companyInfoBankList) {
                //外键设置
                entity.setCompanyId(companyInfo.getId());
                companyInfoBankMapper.insert(entity);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delMain(String id) {
        companyInfoBankMapper.deleteByMainId(id);
        companyInfoMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            companyInfoBankMapper.deleteByMainId(id.toString());
            companyInfoMapper.deleteById(id);
        }
    }

}
