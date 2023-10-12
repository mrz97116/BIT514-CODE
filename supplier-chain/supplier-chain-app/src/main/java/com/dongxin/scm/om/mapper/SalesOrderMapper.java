package com.dongxin.scm.om.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.vo.BillDetVO;
import com.dongxin.scm.om.vo.BillVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 销售单
 * @Author: jeecg-boot
 * @Date: 2020-11-23
 * @Version: V1.0
 */
public interface SalesOrderMapper extends BaseMapper<SalesOrder> {

    List<BillVO> selectBill();

    List<BillDetVO> selectBillDet(String salesId);
    List<SalesOrder> selectByMainId(@Param("mainId") String mainId);


    BigDecimal getUnLoadSaleOrderAmount(String customerId);
}
