package com.dongxin.scm.sm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.sm.dto.DailySalesDateDTO;
import com.dongxin.scm.sm.entity.Inventory;

import java.util.List;

/**
 * @Description: 库存信息
 * @Author: jeecg-boot
 * @Date: 2020-12-03
 * @Version: V1.0
 */
public interface InventoryMapper extends BaseMapper<Inventory> {

    List<DailySalesDateDTO> priceMsg();

    List<DailySalesDateDTO> transactionWeight();

    List<DailySalesDateDTO> stockWeight();

    List<DailySalesDateDTO> inventoryWeight();

    void setDelFlagFalseByMatNo(String matNo);

    void setDelFlagFalseById(String id,String stockHouseId);

    Inventory getInventoryByMatNoIgnoreDelFlag(String matNo);

    List<Inventory> getInventoryDelFlagById(String id);

    Inventory getInventoryDataDelFlagById(String id);


}
