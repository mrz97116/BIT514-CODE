package com.dongxin.scm.bd.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dongxin.scm.bd.entity.CompanyTenant;
import com.dongxin.scm.bd.entity.CompanyTenantDet;
import com.dongxin.scm.bd.enums.TheoryAndNoTheoryEnum;
import com.dongxin.scm.bd.mapper.CompanyTenantDetMapper;
import com.dongxin.scm.bd.mapper.CompanyTenantMapper;
import com.dongxin.scm.bd.vo.CompanyTenantVo;
import com.dongxin.scm.sm.vo.OptionVO;
import com.google.common.collect.Lists;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 公司租户表
 * @Author: jeecg-boot
 * @Date: 2021-02-26
 * @Version: V1.0
 */
@Service
public class CompanyTenantService extends BaseService<CompanyTenantMapper, CompanyTenant> {

    @Resource
    private CompanyTenantMapper companyTenantMapper;
    @Resource
    private CompanyTenantDetMapper companyTenantDetMapper;

    @Transactional(rollbackFor = Exception.class)
    public void saveMain(CompanyTenant companyTenant, CompanyTenantVo companyTenantVo) {
        companyTenantMapper.insert(companyTenant);

        CompanyTenantDet companyTenantDet = new CompanyTenantDet();
        //外键设置
        companyTenantDet.setParentId(companyTenant.getId());
        companyTenantDet.setTenantCode(companyTenantVo.getTenantCode());
        companyTenantDetMapper.insert(companyTenantDet);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMain(CompanyTenant companyTenant, CompanyTenantVo companyTenantVo) {
        companyTenantMapper.updateById(companyTenant);

        //1.先删除子表数据
        companyTenantDetMapper.deleteByMainId(companyTenant.getId());
        CompanyTenantDet companyTenantDet = new CompanyTenantDet();
        companyTenantDet.setParentId(companyTenant.getId());
        companyTenantDet.setTenantCode(companyTenantVo.getTenantCode());
        companyTenantDetMapper.insert(companyTenantDet);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delMain(String id) {
        companyTenantDetMapper.deleteByMainId(id);
        companyTenantMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            companyTenantDetMapper.deleteByMainId(id.toString());
            companyTenantMapper.deleteById(id);
        }
    }

    public List<CompanyTenantVo> select() {
        return companyTenantMapper.querySys_companyInfo();
    }


    public List<OptionVO> selectAutomaticallyAdjustWeightAndQuantity() {

        List<OptionVO> typeOptionVO = Lists.newArrayList();
        TheoryAndNoTheoryEnum[] automaticallyAdjustWeightAndQuantityEnums = TheoryAndNoTheoryEnum.values();
        for (TheoryAndNoTheoryEnum automaticallyAdjustWeightAndQuantityEnum : automaticallyAdjustWeightAndQuantityEnums) {
            OptionVO optionVO = new OptionVO();
            optionVO.setText(automaticallyAdjustWeightAndQuantityEnum.getDesc());
            optionVO.setValue(automaticallyAdjustWeightAndQuantityEnum.getCode());

            typeOptionVO.add(optionVO);
        }

        return typeOptionVO;
    }

    public void updateCompanyTenant(IPage<CompanyTenantVo> pageList) {
        Map<String, String> theoryAndNoTheoryEnumMap = new HashMap<>();
        TheoryAndNoTheoryEnum[] theoryAndNoTheoryEnums = TheoryAndNoTheoryEnum.values();
        for (TheoryAndNoTheoryEnum automaticallyAdjustWeightAndQuantityEnum : theoryAndNoTheoryEnums) {
            theoryAndNoTheoryEnumMap.put(automaticallyAdjustWeightAndQuantityEnum.getCode(), automaticallyAdjustWeightAndQuantityEnum.getDesc());
        }

        for (CompanyTenantVo companyTenantVo : pageList.getRecords()) {
            companyTenantVo.setSalesDetTheoryWeightName(theoryAndNoTheoryEnumMap.get(companyTenantVo.getSalesDetTheoryWeight()));
            companyTenantVo.setSalesDetTheoryPriceName(theoryAndNoTheoryEnumMap.get(companyTenantVo.getSalesDetTheoryPrice()));
        }
    }
}
