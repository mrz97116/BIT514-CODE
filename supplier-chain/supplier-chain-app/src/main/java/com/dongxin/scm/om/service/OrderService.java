package com.dongxin.scm.om.service;

import cn.hutool.core.collection.CollectionUtil;
import com.dongxin.scm.om.entity.Order;
import com.dongxin.scm.om.entity.OrderDet;
import com.dongxin.scm.om.entity.Provision;
import com.dongxin.scm.om.mapper.OrderDetMapper;
import com.dongxin.scm.om.mapper.OrderMapper;
import com.dongxin.scm.om.mapper.ProvisionMapper;
import com.dongxin.scm.utils.BigDecimalUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 订单主表
 * @Author: jeecg-boot
 * @Date: 2020-11-13
 * @Version: V1.0
 */
@Service
public class OrderService extends BaseService<OrderMapper, Order> {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderDetMapper orderDetMapper;
    @Resource
    private ProvisionMapper provisionMapper;

    @Transactional(rollbackFor = Exception.class)
    public void saveMain(Order order, List<OrderDet> orderDetList, List<Provision> provisionList) {
        String orderNo = "";
        order.setOrderNo(orderNo);
        orderMapper.insert(order);
        BigDecimal totalPrice = BigDecimal.ZERO;
        double weight = 0.0;
        if (orderDetList != null && orderDetList.size() > 0) {
            for (OrderDet entity : orderDetList) {
                //外键设置
                entity.setOrderMainId(order.getId());
                weight += entity.getWeight();
                totalPrice = BigDecimalUtils.multiply(entity.getPrice(), entity.getWeight());
                orderDetMapper.insert(entity);
            }
        }
        if (CollectionUtils.isNotEmpty(provisionList)) {
            for (Provision entity : provisionList) {
                //外键设置
                entity.setParentId(order.getId());
                provisionMapper.insert(entity);
            }
        }
        order.setOrderWt(weight);
        order.setTotalPrice(totalPrice);
        updateById(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMain(Order order, List<OrderDet> orderDetList, List<Provision> provisionList) {

        BigDecimal totalPrice = BigDecimal.ZERO;
        double weight = 0.0;

        //1.先删除子表数据
        orderDetMapper.deleteByMainId(order.getId());
        provisionMapper.deleteByMainId(order.getId());

        //2.子表数据重新插入
        if (CollectionUtil.isNotEmpty(orderDetList)) {
            for (OrderDet entity : orderDetList) {
                //外键设置
                entity.setOrderMainId(order.getId());
                weight += entity.getWeight();
                totalPrice = BigDecimalUtils.multiply(entity.getPrice(), entity.getWeight());
                orderDetMapper.insert(entity);
            }
        }
        if (provisionList != null && provisionList.size() > 0) {
            for (Provision entity : provisionList) {
                //外键设置
                entity.setParentId(order.getId());
                provisionMapper.insert(entity);
            }
        }
        order.setOrderWt(weight);
        order.setTotalPrice(totalPrice);
        orderMapper.updateById(order);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delMain(String id) {
        orderDetMapper.deleteByMainId(id);
        provisionMapper.deleteByMainId(id);
        logicDeleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            orderDetMapper.deleteByMainId(id.toString());
            provisionMapper.deleteByMainId(id.toString());
            logicDeleteById(id);
        }
    }

}
