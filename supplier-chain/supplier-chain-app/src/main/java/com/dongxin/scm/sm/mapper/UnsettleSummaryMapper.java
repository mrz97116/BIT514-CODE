package com.dongxin.scm.sm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.sm.entity.UnsettleSummary;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Description: 客户月度未结算量汇总统计
 * @Author: jeecg-boot
 * @Date: 2021-10-14
 * @Version: V1.0
 */
public interface UnsettleSummaryMapper extends BaseMapper<UnsettleSummary> {
    List<UnsettleSummary> queryUnsettleSummary(@Param("startTime")Date startTime, @Param("endTime")Date endTime, @Param("tenantId")int tenantId);
}
