package com.dongxin.scm.om.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.om.entity.OrderDet;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 订单明细表
 * @Author: jeecg-boot
 * @Date: 2020-11-13
 * @Version: V1.0
 */
public interface OrderDetMapper extends BaseMapper<OrderDet> {

    boolean deleteByMainId(@Param("mainId") String mainId);

    List<OrderDet> selectByMainId(@Param("mainId") String mainId);
}
