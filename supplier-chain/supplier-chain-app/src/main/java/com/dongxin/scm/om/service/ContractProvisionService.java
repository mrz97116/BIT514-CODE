package com.dongxin.scm.om.service;

import com.dongxin.scm.om.entity.ContractProvision;
import com.dongxin.scm.om.mapper.ContractProvisionMapper;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 合同商务条款
 * @Author: jeecg-boot
 * @Date: 2020-12-14
 * @Version: V1.0
 */
@Service
public class ContractProvisionService extends BaseService<ContractProvisionMapper, ContractProvision> {

    @Autowired
    private ContractProvisionMapper contractProvisionMapper;

    public List<ContractProvision> selectByMainId(String mainId) {
        return contractProvisionMapper.selectByMainId(mainId);
    }
}
