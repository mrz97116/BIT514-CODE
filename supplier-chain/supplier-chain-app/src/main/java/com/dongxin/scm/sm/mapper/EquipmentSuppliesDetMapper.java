package com.dongxin.scm.sm.mapper;

import java.math.BigDecimal;
import java.util.List;

import com.dongxin.scm.sm.dto.EquipmentSuppliesDTO;
import com.dongxin.scm.sm.entity.EquipmentSuppliesDet;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 设备物资明细表
 * @Author: jeecg-boot
 * @Date:   2021-03-02
 * @Version: V1.0
 */
public interface EquipmentSuppliesDetMapper extends BaseMapper<EquipmentSuppliesDet> {

    List<EquipmentSuppliesDTO> sumAmount();

}
