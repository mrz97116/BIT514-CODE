package com.dongxin.scm.fm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.fm.entity.FundPool;
import com.dongxin.scm.fm.entity.InitMoeny;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description: 来款资金表
 * @Author: jeecg-boot
 * @Date: 2020-10-27
 * @Version: V1.0
 */
public interface FundPoolMapper extends BaseMapper<FundPool> {


    List<FundPool> selectThisMonthFundPoolList(@Param("thisMonthNumberOne") Date thisMonthNumberOne, @Param("approve") String status);

    List<FundPool> selectFundPoolSeven(Date sevenDaysAgoDate);

    String queryBankTypeByBankId(String bankId);

    List<InitMoeny> selectInitFundPool();


    //获取客户当月来款总金额
    BigDecimal sumInComeMoney(@Param("customerId") String customerId, @Param("startDay") String startDay, @Param("endDay") String endDay, @Param("tenant_id") String tenant_id);

    List<FundPool> selectFundPool(@Param("customerId") String customerId, @Param("paymentMethod") String paymentMethod);

    List<FundPool> getCustomerFundPool(@Param("customerId") String customerId);
}
