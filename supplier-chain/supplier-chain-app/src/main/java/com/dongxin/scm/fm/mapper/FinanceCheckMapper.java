package com.dongxin.scm.fm.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.dongxin.scm.fm.entity.FinanceCheck;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 可用余额核对
 * @Author: jeecg-boot
 * @Date:   2021-04-06
 * @Version: V1.0
 */
public interface FinanceCheckMapper extends BaseMapper<FinanceCheck> {

    String getCustomerId(@Param("companyName") String companyName, @Param("tenantId")int tenantId);

    BigDecimal sumIncome(String customerId);

}
