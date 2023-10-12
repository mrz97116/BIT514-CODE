package com.dongxin.scm.sm.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.dongxin.scm.enums.ProdClassCodeEnum;
import com.dongxin.scm.fm.entity.SettleMatCoding;
import com.dongxin.scm.fm.mapper.SettleMatCodingMapper;
import com.dongxin.scm.fm.service.SettleMatCodingService;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.entity.InventoryTiming;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.entity.Processing;
import com.dongxin.scm.sm.service.InventoryService;
import com.dongxin.scm.sm.service.InventoryTimingService;
import com.dongxin.scm.sm.service.MatService;
import com.dongxin.scm.sm.service.ProcessingService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * 参照fm_settle_mat_coding中的mat_code
 * 定时为sm_mat和sm_inventory和sm_processing表中的mat_code赋值
 */

public class AutoMatCodeJob implements Job {
    @Autowired
    private MatService matService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private SettleMatCodingService settleMatCodingService;
    @Autowired
    private InventoryTimingService inventoryTimingService;
    @Autowired
    private ProcessingService processingService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //查材料表里物料编码为空的数据
        QueryWrapper<Mat> matQueryWrapper = new QueryWrapper<>();
        matQueryWrapper.lambda().eq(Mat::getTenantId, 2).isNull(Mat::getMatCode);
        List<Mat> matList = matService.list(matQueryWrapper);
        //查询库存表物料编码为空的数据
        QueryWrapper<Inventory> inventoryQueryWrapper = new QueryWrapper<>();
        inventoryQueryWrapper.lambda().eq(Inventory::getTenantId, 2).isNull(Inventory::getMatCode);
        List<Inventory> inventoryList = inventoryService.list(inventoryQueryWrapper);
        //查询库存定时表中物料编码为空的数据
        QueryWrapper<InventoryTiming> inventoryTimingQueryWrapper = new QueryWrapper<>();
        inventoryTimingQueryWrapper.lambda().eq(InventoryTiming::getTenantId, 2).isNull(InventoryTiming::getMatCode);
        List<InventoryTiming> inventoryTimingList = inventoryTimingService.list(inventoryTimingQueryWrapper);
        //查加工表里物料编码为空的数据
        QueryWrapper<Processing> processingQueryWrapper = new QueryWrapper<>();
        processingQueryWrapper.lambda().eq(Processing::getTenantId,"2").isNull(Processing::getMatCode);
        List<Processing> processingList = processingService.list(processingQueryWrapper);

        //创建对应空集合
        List<Mat> updateMatList = newArrayList();
        List<Inventory> updateInventoryList = newArrayList();
        List<InventoryTiming> updateInventoryTimingList = newArrayList();
        List<Processing> updateProcessingList = newArrayList();
        //查询和匹配结算报表物料信息维护中的物料编码，并为材料表中物料编码为空的字段赋值
        if (CollectionUtil.isNotEmpty(matList)) {
            for (Mat mat : matList) {
                if (mat.getProdClassCode().equals(ProdClassCodeEnum.D.getValue()) || mat.getProdClassCode().equals(ProdClassCodeEnum.B.getValue())) {
                    QueryWrapper<SettleMatCoding> matCodingQueryWrapperDB = new QueryWrapper<>();
                    matCodingQueryWrapperDB.lambda().eq(SettleMatCoding::getOldProdCname, mat.getOldProdCname())
                            .eq(SettleMatCoding::getSgSign, mat.getSgSign())
                            .eq(SettleMatCoding::getMatThick, mat.getMatThick());
                    SettleMatCoding settleMatCodingDB = settleMatCodingService.getOne(matCodingQueryWrapperDB);
                    if (ObjectUtil.isNotEmpty(settleMatCodingDB)) {
                        mat.setMatCode(settleMatCodingDB.getMatCode());
                        updateMatList.add(mat);
                    }
                } else {
                    QueryWrapper<SettleMatCoding> matCodingQueryWrapperDB = new QueryWrapper<>();
                    matCodingQueryWrapperDB.lambda().eq(SettleMatCoding::getOldProdCname, mat.getOldProdCname())
                            .eq(SettleMatCoding::getSgSign, mat.getSgSign());
                    SettleMatCoding settleMatCodingDB = settleMatCodingService.getOne(matCodingQueryWrapperDB);
                    if (ObjectUtil.isNotEmpty(settleMatCodingDB)) {
                        mat.setMatCode(settleMatCodingDB.getMatCode());
                        updateMatList.add(mat);
                    }
                }
                if (mat.getStockHouseId().equals("5")) {
                    mat.setMatCode("6.07.003");
                }
            }

        }
        //查询和匹配结算报表物料信息维护中的物料编码，并为库存表中物料编码为空的字段赋值
        if (CollectionUtil.isNotEmpty(inventoryList)) {
            for (Inventory inventory : inventoryList) {
                if (inventory.getProdClassCode().equals(ProdClassCodeEnum.D.getValue()) || inventory.getProdClassCode().equals(ProdClassCodeEnum.B.getValue())) {
                    QueryWrapper<SettleMatCoding> settleMatCodingQueryWrapper = new QueryWrapper<>();
                    settleMatCodingQueryWrapper.lambda().eq(SettleMatCoding::getOldProdCname, inventory.getOldProdCname())
                            .eq(SettleMatCoding::getSgSign, inventory.getSgSign())
                            .eq(SettleMatCoding::getMatThick, inventory.getMatThick());
                    SettleMatCoding settleMatCoding = settleMatCodingService.getOne(settleMatCodingQueryWrapper);
                    if (ObjectUtil.isNotEmpty(settleMatCoding)) {
                        inventory.setMatCode(settleMatCoding.getMatCode());
                        updateInventoryList.add(inventory);
                    }
                } else {
                    QueryWrapper<SettleMatCoding> settleMatCodingQueryWrapper = new QueryWrapper<>();
                    settleMatCodingQueryWrapper.lambda().eq(SettleMatCoding::getOldProdCname, inventory.getOldProdCname())
                            .eq(SettleMatCoding::getSgSign, inventory.getSgSign());
                    SettleMatCoding settleMatCoding = settleMatCodingService.getOne(settleMatCodingQueryWrapper);
                    if (ObjectUtil.isNotEmpty(settleMatCoding)) {
                        inventory.setMatCode(settleMatCoding.getMatCode());
                        updateInventoryList.add(inventory);
                    }
                }
                if (inventory.getStockHouseId().equals("5")) {
                    inventory.setMatCode("6.07.003");
                }
            }
        }
        //查询和匹配结算报表物料信息维护中的物料编码，并为库存定时表中物料编码为空的字段赋值
        if (CollectionUtil.isNotEmpty(inventoryTimingList)) {
            for (InventoryTiming inventoryTiming : inventoryTimingList) {
                if (inventoryTiming.getProdClassCode().equals(ProdClassCodeEnum.D.getValue()) || inventoryTiming.getProdClassCode().equals(ProdClassCodeEnum.B.getValue())) {
                    QueryWrapper<SettleMatCoding> settleMatCodingQueryWrapper = new QueryWrapper<>();
                    settleMatCodingQueryWrapper.lambda().eq(SettleMatCoding::getOldProdCname, inventoryTiming.getOldProdCname())
                            .eq(SettleMatCoding::getSgSign, inventoryTiming.getSgSign())
                            .eq(SettleMatCoding::getMatThick, inventoryTiming.getMatThick());
                    SettleMatCoding settleMatCoding = settleMatCodingService.getOne(settleMatCodingQueryWrapper);
                    if (ObjectUtil.isNotEmpty(settleMatCoding)) {
                        inventoryTiming.setMatCode(settleMatCoding.getMatCode());
                        updateInventoryTimingList.add(inventoryTiming);
                    }
                } else {
                    QueryWrapper<SettleMatCoding> settleMatCodingQueryWrapper = new QueryWrapper<>();
                    settleMatCodingQueryWrapper.lambda().eq(SettleMatCoding::getOldProdCname, inventoryTiming.getOldProdCname())
                            .eq(SettleMatCoding::getSgSign, inventoryTiming.getSgSign());
                    SettleMatCoding settleMatCoding = settleMatCodingService.getOne(settleMatCodingQueryWrapper);
                    if (ObjectUtil.isNotEmpty(settleMatCoding)) {
                        inventoryTiming.setMatCode(settleMatCoding.getMatCode());
                        updateInventoryTimingList.add(inventoryTiming);
                    }
                }
                if (inventoryTiming.getStockHouseId().equals("5")) {
                    inventoryTiming.setMatCode("6.07.003");
                }
            }
        }
        //查询和匹配结算报表物料信息维护中的物料编码，并为加工表中物料编码为空的字段赋值
        if(CollectionUtil.isNotEmpty(processingList)){
            for (Processing processing : processingList) {
                if (processing.getProdClassCode().equals(ProdClassCodeEnum.D.getValue()) || processing.getProdClassCode().equals(ProdClassCodeEnum.B.getValue())){
                    QueryWrapper<SettleMatCoding> settleMatCodingQueryWrapper = new QueryWrapper<>();
                    settleMatCodingQueryWrapper.lambda().eq(SettleMatCoding::getOldProdCname, processing.getProdCnameOther())
                            .eq(SettleMatCoding::getSgSign, processing.getSgSign())
                            .eq(SettleMatCoding::getMatThick, processing.getMatThick());
                    SettleMatCoding settleMatCoding = settleMatCodingService.getOne(settleMatCodingQueryWrapper);
                    if (ObjectUtil.isNotEmpty(settleMatCoding)) {
                        processing.setMatCode((settleMatCoding.getMatCode()));
                        updateProcessingList.add(processing);
                    }
                } else {
                    QueryWrapper<SettleMatCoding> settleMatCodingQueryWrapper = new QueryWrapper<>();
                    settleMatCodingQueryWrapper.lambda().eq(SettleMatCoding::getOldProdCname, processing.getProdCnameOther())
                            .eq(SettleMatCoding::getSgSign, processing.getSgSign());
                    SettleMatCoding settleMatCoding = settleMatCodingService.getOne(settleMatCodingQueryWrapper);
                    if (ObjectUtil.isNotEmpty(settleMatCoding)) {
                        processing.setMatCode(settleMatCoding.getMatCode());
                        updateProcessingList.add(processing);
                    }
                }
            }
        }
        //更新数据
        inventoryService.updateBatchById(updateInventoryList);
        matService.updateBatchById(updateMatList);
        inventoryTimingService.updateBatchById(updateInventoryTimingList);
        processingService.updateBatchById(processingList);
    }
}

