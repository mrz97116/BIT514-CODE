package com.dongxin.scm.sm.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dongxin.scm.bd.mapper.CompanyTenantMapper;
import com.dongxin.scm.bd.vo.CompanyTenantVo;
import com.dongxin.scm.common.enums.CommonCheckStatusEnum;
import com.dongxin.scm.common.service.LockService;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.om.util.Constants;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.entity.WarehouseWarrant;
import com.dongxin.scm.sm.enums.StockTypeEnum;
import com.dongxin.scm.sm.enums.WareHouseEnum;
import com.dongxin.scm.sm.mapper.WarehouseWarrantMapper;
import com.dongxin.scm.sm.vo.MatVo;
import lombok.val;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.base.service.BaseService;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.config.mybatis.TenantContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 入库单
 * @Author: jeecg-boot
 * @Date: 2021-04-06
 * @Version: V1.0
 */
@Service
public class WarehouseWarrantService extends BaseService<WarehouseWarrantMapper, WarehouseWarrant> {

    @Autowired
    private MatService matService;
    @Autowired
    private InventoryService inventoryService;
    @Resource
    private CompanyTenantMapper companyTenantMapper;
    @Autowired
    private LockService lockService;

    //入库单整单删除退库存
    @Transactional(rollbackFor = Exception.class)
    public void subduction(String id, boolean needRollbackMat) {
        CompanyTenantVo companyTenantVo = companyTenantMapper.queryPaymentConfiguration(Integer.valueOf(TenantContext.getTenant()));
        String wareHouseFlag = companyTenantVo.getStockConfiguration();
        List<Mat> list = matService.queryListByWarehouseWarrantId(id);
        for (Mat mat : list) {
            inventoryService.warehouseWarrantSubduction(mat);
            if (WareHouseEnum.STOCK_PENDING.getCode().equals(wareHouseFlag) && needRollbackMat) {
                beforeDeleteMat(mat);
            }
            matService.removeById(mat.getId());
        }
        removeById(id);
    }

    //入库单明细删除退库存
    @Transactional(rollbackFor = Exception.class)
    public void detailSubduction(List<String> matIds) {
        CompanyTenantVo companyTenantVo = companyTenantMapper.queryPaymentConfiguration(Integer.valueOf(TenantContext.getTenant()));
        String wareHouseFlag = companyTenantVo.getStockConfiguration();
        String warehouseWarrantId = matService.getById(matIds.get(0)).getWarehouseWarrantId();
        for (String id : matIds) {
            Mat mat = matService.getById(id);
            inventoryService.warehouseWarrantSubduction(mat);
            if (WareHouseEnum.STOCK_PENDING.getCode().equals(wareHouseFlag)) {
                beforeDeleteMat(mat);
            }
            matService.removeById(mat.getId());
        }
        compute(warehouseWarrantId);

    }

    public void compute(String warehouseWarrantId) {
        List<Mat> matList = matService.queryListByWarehouseWarrantId(warehouseWarrantId);
        WarehouseWarrant warehouseWarrant = getById(warehouseWarrantId);
        double sumWeight = 0d;
        int sumQty = 0;
        if (CollectionUtil.isEmpty(matList)) {
            warehouseWarrant.setWeight(sumWeight)
                    .setQty(sumQty);
        }

        if (CollectionUtil.isNotEmpty(matList)) {
            sumQty = matList.stream().map(e -> {
                if (ObjectUtil.isNull(e.getMatNum())) {
                    return 0;
                }
                return e.getMatNum();
            }).reduce(0, Integer::sum);

            //重量合计
            sumWeight = matList.stream().map(e -> {
                if (ObjectUtil.isNull(e.getMatNetWt())) {
                    return 0d;
                }
                return e.getMatNetWt();
            }).reduce(0d, Double::sum);

            warehouseWarrant.setQty(sumQty)
                    .setWeight(sumWeight);
        }

        updateById(warehouseWarrant);

    }


    private void beforeDeleteMat(Mat mat) {
        List<Mat> matList = newArrayList();
        if (StringUtils.isBlank(mat.getAddType())) {
            Mat addMat = new Mat();
            BeanUtils.copyProperties(mat, addMat);
            addMat.setId(null);
            addMat.setCreateTime(null);
            addMat.setUpdateBy(null);
            addMat.setUpdateTime(null);
            addMat.setTenantId(null);
            addMat.setDelFlag(null);

            addMat.setStockHouseId(null);
            addMat.setStockNo(null);
            addMat.setInventoryId(null);
            addMat.setWarehouseWarrantId(null);
            addMat.setStorageTime(null);
            addMat.setStockType(StockTypeEnum.ON_ROUTE.getCode());

            matList.add(addMat);
        }

        if (CollectionUtil.isNotEmpty(matList)) {
            matService.saveBatch(matList);
        }
    }

    //移库逻辑
    @Transactional(rollbackFor = Exception.class)
    public void subductionAsStockChanges(WarehouseWarrant warehouseWarrant, List<Mat> newMatList) {
        //查询原有材料数据
        QueryWrapper<Mat> matQueryWrapper = new QueryWrapper<>();
        matQueryWrapper.lambda().eq(Mat::getWarehouseWarrantId, warehouseWarrant.getId());
        List<Mat> matList = matService.list(matQueryWrapper);
        matList.forEach(item -> {
            for(Mat newMat:newMatList){
                if(item.getId().equals(newMat.getId())){
                    item.setStockLocation(newMat.getStockLocation());
                }
            }
            item.setStockHouseId(warehouseWarrant.getStockId());
        });
        //删除旧的入库单及库存
        subduction(warehouseWarrant.getId(), false);

        MatVo matVo = new MatVo();
        matVo.setRemarks(matList.get(0).getRemarks());
        matVo.setCarNo(matList.get(0).getCarNo());
        matVo.setStockHouseId(warehouseWarrant.getStockId());
        matVo.setMatDetList(matList);
        matVo.setStockLocation(matList.get(0).getStockLocation());
        matVo.setDischargerName(matList.get(0).getDischargerName());
        matService.saveMain(matVo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void newUpdateById(WarehouseWarrant warehouseWarrant, List<Mat> matList) {
        String tenantId = TenantContext.getTenant();
        LockInfo lockInfo = lockService.lock(Constants.WARAHOUSEWARRANT_LOCK_HEADER + tenantId);
        try {
            updateById(warehouseWarrant);
            for (Mat mat : matList) {
                Mat mat1 = matService.getById(mat.getId());
                String inventoryId = mat1.getInventoryId();
                Inventory inventory = inventoryService.getById(inventoryId);
                if (ObjectUtil.isNotNull(mat.getStockLocation())) {
                    inventory.setStockLocation(mat.getStockLocation());
                    inventoryService.updateById(inventory);
                }
            }
            matService.updateBatchById(matList);
        } finally {
            lockService.releaseLock(lockInfo);
        }
    }
}
