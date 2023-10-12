package com.dongxin.scm.fm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.fm.entity.FundDetail;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 资金明细
 * @Author: jeecg-boot
 * @Date: 2020-10-15
 * @Version: V1.0
 */
public interface FundDetailMapper extends BaseMapper<FundDetail> {

    BigDecimal getAmount(@Param("type") String type, @Param("fundId") String fundId);

    BigDecimal getAmountByCustomerId(@Param("type") String type, @Param("customerId") String customerId);


}
