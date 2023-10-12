package com.dongxin.scm.sm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.entity.SmMaterials;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 建筑材单重表
 * @Author: jeecg-boot
 * @Date: 2021-02-02
 * @Version: V1.0
 */
public interface SmMaterialsMapper extends BaseMapper<SmMaterials> {
    double queryMaterialsWeight(@Param("mat") Mat mat);
}
