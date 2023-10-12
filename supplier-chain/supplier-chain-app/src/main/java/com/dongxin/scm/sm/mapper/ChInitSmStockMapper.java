package com.dongxin.scm.sm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.dongxin.scm.sm.entity.ChInitSmStock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 岑海库存表
 * @Author: jeecg-boot
 * @Date:   2021-03-09
 * @Version: V1.0
 */
public interface ChInitSmStockMapper extends BaseMapper<ChInitSmStock> {

     List<ChInitSmStock> selectAll();

     String selectProdName(@Param("chInitSmStock") ChInitSmStock chInitSmStock);

     String selectWeight(@Param("chInitSmStock") ChInitSmStock chInitSmStock);
}
