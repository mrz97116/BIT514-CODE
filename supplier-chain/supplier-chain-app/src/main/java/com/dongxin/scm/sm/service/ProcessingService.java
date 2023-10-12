package com.dongxin.scm.sm.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.enums.SerialNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.sm.dto.ProcessYrmDTO;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.entity.Processing;
import com.dongxin.scm.sm.entity.ProcessingDet;
import com.dongxin.scm.sm.enums.ProcessingEnum;
import com.dongxin.scm.sm.mapper.ProcessingMapper;
import com.dongxin.scm.utils.ScmBeanUtils;
import com.dongxin.scm.utils.SerialNoUtil;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @Description: 加工管理
 * @Author: jeecg-boot
 * @Date: 2021-02-20
 * @Version: V1.0
 */
@Service
public class ProcessingService extends BaseService<ProcessingMapper, Processing> {

    @Autowired
    MatService matService;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    ProcessingDetService processingDetService;

    @Transactional(rollbackFor = Exception.class)
    public void processMat(List<String> inventoryIdList) {
        if (CollectionUtil.isEmpty(inventoryIdList)) {
            throw new ScmException("参数错误");
        }
        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(Inventory::getId, inventoryIdList);
        List<Inventory> existInventoryList = inventoryService.list(queryWrapper);
        if (CollectionUtil.isEmpty(inventoryIdList)) {
            log.error(String.format("未查询到对应物料信息：s%", JSON.toJSONString(inventoryIdList)));
            throw new ScmException("未查询到对应物料信息");
        }

        String processingNo = SerialNoUtil.getSerialNo(SerialNoEnum.MACHINING_NO);

        List<Processing> processingList = new ArrayList<>();

        List<String> matNoList = new ArrayList();

        for (Inventory inventory : existInventoryList) {
            matNoList.add(inventory.getMatNo());
            Processing processing = new Processing();

            ScmBeanUtils.copyProperties(inventory, processing);

            processing.setProcessingNo(processingNo);

            processingList.add(processing);

            inventoryService.removeById(inventory.getId());

        }
        saveBatch(processingList);

        QueryWrapper<Mat> matQueryWrapper = new QueryWrapper<>();
        matQueryWrapper.lambda().in(Mat::getMatNo, matNoList);
        List<Mat> matList = matService.list(matQueryWrapper);

        for (Mat mat : matList) {
            mat.setProcessingIdentification(ProcessingEnum.PROCESSING.getCode());
        }

        matService.updateBatchById(matList);

    }

    @Transactional(rollbackFor = Exception.class)
    public void processMatYrm(String inventoryId, List<ProcessYrmDTO> processDtoList, String customerId, String salesManId, String remarks) {
        Inventory existInventory = inventoryService.getById(inventoryId);
        if (CollectionUtil.isEmpty(Collections.singleton(existInventory))) {
            log.error(String.format("未查询到对应物料信息：s%", JSON.toJSONString(inventoryId)));
            throw new ScmException("未查询到对应物料信息");
        }

        String processingNo = SerialNoUtil.getSerialNo(SerialNoEnum.MACHINING_NO);

        List<Processing> processingList = new ArrayList<>();

        List<ProcessingDet> processingDetList = new ArrayList<>();

        Processing processing = new Processing();

        ScmBeanUtils.copyProperties(existInventory, processing);

        processing.setProcessingNo(processingNo);

        processing.setCustomerId(customerId);

        processing.setSalesManId(salesManId);

        processing.setRemarks(remarks);

        processingList.add(processing);
        saveBatch(processingList);

        //保存加工详情
        Double totalProcessingWeight = 0.0;
        for (ProcessYrmDTO processDTO : processDtoList) {
            ProcessingDet processingDet = new ProcessingDet();
            //判断加工信息是否合法
            totalProcessingWeight += processDTO.getWeight();
            if (totalProcessingWeight > processing.getWeight() * 1000) {
                throw new ScmException("加工长度、块数错误！材料重量不足！");
            }
            processingDet.setProcessNo(processing.getId());
            processingDet.setLen(processDTO.getLen());
            processingDet.setCakeNum(processDTO.getCakeNum());
            processingDet.setCakeWeight(processDTO.getCakeWeight());
            processingDet.setWeight(processDTO.getWeight());
            processingDet.setNum(processDTO.getNum());
            processingDetList.add(processingDet);
        }
        processingDetService.saveBatch(processingDetList);

        inventoryService.removeById(inventoryId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delProcessing(String processingId) {

        //回退物料信息
        Processing processing = getById(processingId);

        Inventory inventory = inventoryService.getByMatNoIgnoreDelFlag(processing.getMatNo());

        if (inventory == null) {
            throw new ScmException("未匹配到对应的材料号");
        }

        inventory.setDelFlag(0);
        inventoryService.setDelFlagFalseByMatNo(inventory.getMatNo());

        //删除加工单
        removeById(processingId);

        //删除加工详情单
        List<ProcessingDet> processingDetListDel = processingDetService.getDeleteListById(processingId);

        for (ProcessingDet processingDetDel : processingDetListDel) {
            processingDetService.removeById(processingDetDel.getId());
        }

        //回写Mat表加工标识
        QueryWrapper<Mat> matQueryWrapper = new QueryWrapper<>();
        matQueryWrapper.lambda().eq(Mat::getMatNo,inventory.getMatNo());
        Mat mat = matService.getOne(matQueryWrapper);

        mat.setProcessingIdentification(ProcessingEnum.NO_PROCESSING.getCode());

        matService.updateById(mat);

    }


}
