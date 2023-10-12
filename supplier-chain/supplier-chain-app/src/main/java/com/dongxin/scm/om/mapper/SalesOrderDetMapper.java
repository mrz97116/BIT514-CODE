package com.dongxin.scm.om.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.om.entity.SalesOrderDet;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 销售单明细表
 * @Author: jeecg-boot
 * @Date: 2020-11-23
 * @Version: V1.0
 */
public interface SalesOrderDetMapper extends BaseMapper<SalesOrderDet> {

    boolean deleteByMainId(@Param("mainId") String mainId);

    List<SalesOrderDet> selectByMainId(@Param("mainId") String mainId);

    List<SalesOrderDet> selectByGoodEntrustmentLetterId(@Param("goodEntrustmentLetterId") String goodEntrustmentLetterId);
}
