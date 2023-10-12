package com.dongxin.scm.sm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.sm.entity.DailyStatement;
import org.apache.ibatis.annotations.Update;

/**
 * @Description: 日报表
 * @Author: jeecg-boot
 * @Date: 2020-12-28
 * @Version: V1.0
 */
public interface DailyStatementMapper extends BaseMapper<DailyStatement> {

    @Update("truncate table sm_daily_statement")
    void deleteTableData();

}
