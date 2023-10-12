package com.dongxin.scm.om.service;

import com.dongxin.scm.om.entity.OrderDet;
import com.dongxin.scm.om.mapper.OrderDetMapper;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 订单明细表
 * @Author: jeecg-boot
 * @Date: 2020-11-13
 * @Version: V1.0
 */
@Service
public class OrderDetService extends BaseService<OrderDetMapper, OrderDet> {

    @Autowired
    private OrderDetMapper orderDetMapper;

    public List<OrderDet> selectByMainId(String mainId) {
        return orderDetMapper.selectByMainId(mainId);
    }
}
