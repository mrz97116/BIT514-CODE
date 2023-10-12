package com.dongxin.scm.om.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.om.entity.OrderMat;
import com.dongxin.scm.sm.entity.Mat;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 订单物料信息表
 * @Author: jeecg-boot
 * @Date: 2020-12-01
 * @Version: V1.0
 */
public interface OrderMatMapper extends BaseMapper<OrderMat> {

    double queryWeight(@Param("mat") Mat mat);

}
