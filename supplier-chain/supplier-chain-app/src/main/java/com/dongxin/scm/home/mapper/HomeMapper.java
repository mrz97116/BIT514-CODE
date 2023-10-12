package com.dongxin.scm.home.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.bd.entity.TrainStation;
import org.apache.ibatis.annotations.Param;

public interface HomeMapper extends BaseMapper<TrainStation> {
    Integer monthSaleBillNo(@Param("dateStr") String dateStr, @Param("delFlag") Integer delFlag);

    String queryCompanyHome(Integer id);
}
