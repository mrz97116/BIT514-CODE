package com.dongxin.scm.cm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.cm.entity.CustomerRate;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 顾客星级评分
 * @Author: jeecg-boot
 * @Date: 2020-12-11
 * @Version: V1.0
 */
public interface CustomerRateMapper extends BaseMapper<CustomerRate> {

    boolean deleteByMainId(@Param("mainId") String mainId);

    List<CustomerRate> selectByMainId(@Param("mainId") String mainId);
}
