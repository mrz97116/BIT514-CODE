package com.dongxin.scm.sm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.sm.entity.Stock;
import com.dongxin.scm.sm.vo.OptionVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 仓库信息表
 * @Author: jeecg-boot
 * @Date: 2020-09-17
 * @Version: V1.0
 */
public interface StockMapper extends BaseMapper<Stock> {

    int enableById(@Param("ids") String ids, @Param("enable") String enable);

    List<OptionVO> queryWarehouse();
}
