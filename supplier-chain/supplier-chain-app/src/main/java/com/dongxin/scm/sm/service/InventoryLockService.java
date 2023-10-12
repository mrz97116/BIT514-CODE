package com.dongxin.scm.sm.service;

import cn.hutool.core.util.ObjectUtil;
import com.dongxin.scm.enums.ProdClassCodeEnum;
import com.dongxin.scm.enums.SerialNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.entity.InventoryLock;
import com.dongxin.scm.sm.enums.InventoryLockEnum;
import com.dongxin.scm.sm.mapper.InventoryLockMapper;
import com.dongxin.scm.sm.vo.LockVo;
import com.dongxin.scm.utils.SerialNoUtil;
import org.jeecg.common.system.base.service.BaseService;
import org.jeecg.config.mybatis.TenantContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: 库存锁定
 * @Author: jeecg-boot
 * @Date: 2021-06-23
 * @Version: V1.0
 */
@Service
public class InventoryLockService extends BaseService<InventoryLockMapper, InventoryLock> {
    @Autowired
    private InventoryService inventoryService;

    /**
     * 解锁
     */
    @Transactional(rollbackFor = Exception.class)
    public void inventoryUnlocking(String ids) {
        List<String> inventoryLockIdList = Arrays.asList(ids.split(","));

        List<InventoryLock> inventoryLockListList = listByIds(inventoryLockIdList);
        for (InventoryLock inventoryLock : inventoryLockListList) {

            if (InventoryLockEnum.UNLOCK.getCode().equals(inventoryLock.getLockStatus())) {
                throw new ScmException("该条数据已解锁");
            }
            inventoryLock.setLockStatus(InventoryLockEnum.UNLOCK.getCode());

            //数量处理
            Double weight = inventoryLock.getLockWeight();
            Double qty = inventoryLock.getLockQty();

            inventoryLock.setUnlockQty(qty);
            inventoryLock.setUnlockWeight(weight);

            inventoryLock.setLockWeight(0.0);
            inventoryLock.setLockQty(0.0);

            updateById(inventoryLock);

            Inventory inventory = inventoryService.getById(inventoryLock.getInventoryId());

            //库存数量处理
            inventoryService.deductionAvailable(inventory, -qty, -weight);//可售
            inventoryService.deductionPractical(inventory, -qty, -weight);//库存

            //已锁定数量处理
            inventory.setLockQty(inventory.getLockQty() - qty);
            inventory.setLockWeight(inventory.getLockWeight() - weight);

            inventoryService.updateById(inventory);
        }
    }

    /**
     * 生成锁定单
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveLock(String ids , String remark) {
        List<String> inventoryIdList = Arrays.asList(ids.split(","));
        List<Inventory> inventoryList = inventoryService.listByIds(inventoryIdList);

        for (Inventory inventory : inventoryList) {
            //单号
            String lockNO = SerialNoUtil.getSerialNo(SerialNoEnum.INVENTORY_NO);

            if (inventory.getProdClassCode().equalsIgnoreCase(ProdClassCodeEnum.B.getValue() )
                    || inventory.getProdClassCode().equalsIgnoreCase(ProdClassCodeEnum.D.getValue() )){
                throw new ScmException("暂时不支持建筑材的锁定，如有建筑材锁定需求请联系技术人员");
            }
            if (inventory.getAvailableQty() == 0 ){
                throw new ScmException("库存可售为0，无法锁定");
            }
            InventoryLock inventoryLock = new InventoryLock();
            inventoryLock.setLockStatus(InventoryLockEnum.LOCK.getCode())
                    .setDelFlag(0)
                    .setLockNo(lockNO)
                    .setMatNo(inventory.getMatNo())
                    .setStockHouseId(inventory.getStockHouseId())
                    .setLockWeight(inventory.getAvailableWeight())
                    .setLockQty(inventory.getAvailableQty())
                    .setRemark(inventory.getRemark())
                    .setProdClassCode(inventory.getProdClassCode())
                    .setProdCname(inventory.getProdCname())
                    .setMatLen(inventory.getMatLen())
                    .setMatThick(inventory.getMatThick())
                    .setMatWidth(inventory.getMatWidth())
                    .setRemark(remark)
                    .setSgSign(inventory.getSgSign())
                    .setStockLocation(inventory.getStockLocation())
                    .setInventoryId(inventory.getId());
            save(inventoryLock);

            /**
             * 库存锁定
             * 处理数量
             */
            inventory.setLockWeight(ObjectUtil.isNotNull(inventory.getLockWeight()) ? inventory.getAvailableWeight() : 0 + inventory.getAvailableWeight());
            inventory.setLockQty(ObjectUtil.isNotNull(inventory.getLockQty()) ? inventory.getAvailableQty() : 0 + inventory.getAvailableQty());

            Double qty = inventory.getLockQty();
            Double weight = inventory.getLockWeight();

            inventoryService.deductionAvailable(inventory, qty, weight);//可售数、量
            inventoryService.deductionPractical(inventory, qty, weight);//库存数、量

        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void bulkEditing(String ids , String remark) {
        List<String> inventoryLockIdList = Arrays.asList(ids.split(","));
        List<InventoryLock> inventoryLockList = listByIds(inventoryLockIdList);

        for (InventoryLock inventoryLock : inventoryLockList) {
            inventoryLock.setRemark(remark);
            updateById(inventoryLock);
        }
    }
}
