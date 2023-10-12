package com.dongxin.scm.om.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.om.entity.Order;

/**
 * @Description: 订单主表
 * @Author: jeecg-boot
 * @Date: 2020-11-13
 * @Version: V1.0
 */
public interface OrderMapper extends BaseMapper<Order> {

    Long orderMaxNum(String numSeq);

}
