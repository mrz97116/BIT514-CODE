package com.dongxin.scm.fm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.fm.entity.CustomerFoundSummary;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 客户资金情况汇总表
 * @Author: jeecg-boot
 * @Date:   2021-04-06
 * @Version: V1.0
 */
public interface CustomerFoundSummaryMapper extends BaseMapper<CustomerFoundSummary> {

    public int updateCustomerFoundSummary(@Param("customerFoundSummary") CustomerFoundSummary customerFoundSummary);

}
