package com.dongxin.scm.om.service;

import com.dongxin.scm.om.entity.Provision;
import com.dongxin.scm.om.mapper.ProvisionMapper;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 商务条款
 * @Author: jeecg-boot
 * @Date: 2020-11-13
 * @Version: V1.0
 */
@Service
public class ProvisionService extends BaseService<ProvisionMapper, Provision> {

    @Autowired
    private ProvisionMapper provisionMapper;

    public List<Provision> selectByMainId(String mainId) {
        return provisionMapper.selectByMainId(mainId);
    }
}
