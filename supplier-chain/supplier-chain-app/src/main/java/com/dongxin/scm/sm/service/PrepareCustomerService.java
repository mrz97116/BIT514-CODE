package com.dongxin.scm.sm.service;

import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.sm.entity.PrepareCustomer;
import com.dongxin.scm.sm.mapper.PrepareCustomerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.jeecg.common.system.base.service.BaseService;

/**
 * @Description: 分货客户
 * @Author: jeecg-boot
 * @Date:   2021-12-08
 * @Version: V1.0
 */
@Service
public class PrepareCustomerService extends BaseService<PrepareCustomerMapper, PrepareCustomer> {

    @Autowired
    private CustomerProfileService customerProfileService;
    @Override
    public boolean save(PrepareCustomer entity) {
        CustomerProfile customerProfile = customerProfileService.getCustomer(entity.getCustomerId());
        entity.setCompanyName(customerProfile.getCompanyName());
        return super.save(entity);
    }
}
