package com.dongxin.scm.cm.service;

import com.dongxin.scm.cm.entity.CustomerAddress;
import com.dongxin.scm.cm.mapper.CustomerAddressMapper;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 顾客收货地址信息
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */
@Service
public class CustomerAddressService extends BaseService<CustomerAddressMapper, CustomerAddress> {

    public List<CustomerAddress> selectByMainId(String mainId) {
        return baseMapper.selectByMainId(mainId);
    }

    public void deleteBatchByMainId(String id) {
        List<CustomerAddress> details = selectByMainId(id);
        for (CustomerAddress detail : details) {
            logicDelete(detail);
        }
    }


}
