package com.dongxin.scm.sm.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dongxin.scm.common.service.DataLogService;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.sm.dto.InventoryAllotDTO;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.entity.InventoryAllot;
import com.dongxin.scm.sm.mapper.InventoryAllotMapper;
import net.sf.saxon.functions.ConstantFunction;
import org.jeecg.config.mybatis.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description: 货物调拨表
 * @Author: jeecg-boot
 * @Date:   2021-08-08
 * @Version: V1.0
 */
@Service
public class InventoryAllotService extends BaseService<InventoryAllotMapper, InventoryAllot> {
    @Autowired
    private DataLogService dataLogService;
    @Autowired
    private InventoryService inventoryService;


    @Transactional(rollbackFor = Exception.class)
    public boolean save(InventoryAllotDTO inventoryAllotDTO,String allot_id,String receive_id) {
        InventoryAllot inventoryAllot = new InventoryAllot();

        //调拨单号
        SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        inventoryAllot.setAllotNum("DB"+time.format(new Date()));
        inventoryAllot.setAllotStockHouseId(inventoryAllotDTO.getList().get(0).getStockHouseId());
        inventoryAllot.setReceiveStockHouseId(inventoryAllotDTO.getWarehouse());
        inventoryAllot.setStockLocation(inventoryAllotDTO.getStockLocation());
        inventoryAllot.setAllotNumberSum(inventoryAllotDTO.getAllotNumber());
        inventoryAllot.setAllotWeightSum(inventoryAllotDTO.getAllotWeight());
        inventoryAllot.setAllotCreateTime(inventoryAllotDTO.getWarehouseTime());
        inventoryAllot.setSgSign(inventoryAllotDTO.getList().get(0).getSgSign());
        inventoryAllot.setMatNo(inventoryAllotDTO.getList().get(0).getMatNo());
        inventoryAllot.setMatLen(inventoryAllotDTO.getList().get(0).getMatLen());
        inventoryAllot.setMatWidth(inventoryAllotDTO.getList().get(0).getMatWidth());
        inventoryAllot.setMatChick(inventoryAllotDTO.getList().get(0).getMatThick());
        inventoryAllot.setProdCname(inventoryAllotDTO.getList().get(0).getProdCname());
        inventoryAllot.setDischargerName(inventoryAllotDTO.getDischargerName());
        inventoryAllot.setRemarks(inventoryAllotDTO.getRemarks());
        inventoryAllot.setReceiveId(receive_id);
        inventoryAllot.setAllotId(allot_id);
        inventoryAllot.setTenantId(Integer.valueOf(TenantContext.getTenant()));
        inventoryAllot.setDelFlag(0);


        boolean signal = save(inventoryAllot);
        return signal;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean logicalDelete(String id){

        InventoryAllot inventoryAllot = getById(id);
        boolean res1 = inventoryService.recoverAllot(inventoryAllot);
        logicDeleteById(id);
        return res1;

    }



}
