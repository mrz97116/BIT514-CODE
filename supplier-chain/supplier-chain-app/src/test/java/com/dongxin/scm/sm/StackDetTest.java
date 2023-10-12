package com.dongxin.scm.sm;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.Application;
import com.dongxin.scm.om.controller.SalesOrderController;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.entity.SalesOrderDet;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.entity.Stack;
import com.dongxin.scm.om.service.SalesOrderDetService;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.sm.entity.StackDet;
import com.dongxin.scm.sm.service.InventoryService;
import com.dongxin.scm.sm.service.StackDetService;
import com.dongxin.scm.sm.service.StackService;
import org.jeecg.config.mybatis.TenantContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DecimalFormat;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class StackDetTest {

    @Autowired
    InventoryService inventoryService;

    @Autowired
    StackDetService stackDetService;

    @Autowired
    SalesOrderDetService salesOrderDetService;

    @Autowired
    SalesOrderService salesOrderService;

    @Autowired
    StackService stackService;

    @Autowired
    SalesOrderController salesOrderController;

    @Test
    public void setSalesOrderId() {
        QueryWrapper<SalesOrder> salesOrderQueryWrapper = new QueryWrapper<>();
        salesOrderQueryWrapper.lambda().eq(SalesOrder::getCreateStackStatus, "1")
                .eq(SalesOrder::getTenantId, 2);
        List<SalesOrder> salesOrderList = salesOrderService.list(salesOrderQueryWrapper);
        List<Stack> stackList = CollectionUtil.newArrayList();
        for (SalesOrder salesOrder : salesOrderList) {
            QueryWrapper<Stack> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(Stack::getTenantId, salesOrder.getTenantId())
                    .eq(Stack::getSaleBillNo, salesOrder.getSaleBillNo());
            Stack stack = stackService.getOne(queryWrapper);
            if (ObjectUtil.isNotNull(stack)) {
                stack.setSalesOrderId(salesOrder.getId());
                stackList.add(stack);
            }
        }
        stackService.updateBatchById(stackList);
    }

    @Test
    public void setSaleOrderDetId() {
        QueryWrapper<StackDet> stackDetQueryWrapper = new QueryWrapper<>();
        stackDetQueryWrapper.lambda().eq(StackDet::getTenantId, 2);
        List<StackDet> stackDetList = stackDetService.list(stackDetQueryWrapper);
        List<StackDet> stackDetList1 = CollectionUtil.newArrayList();
        for (StackDet stackDet : stackDetList) {
            QueryWrapper<Stack> queryWrapperStack = new QueryWrapper<>();
            queryWrapperStack.lambda().eq(Stack::getId, stackDet.getStackId())
                    .eq(Stack::getTenantId, 2);
            Stack stack = stackService.getOne(queryWrapperStack);
            if (ObjectUtil.isNotNull(stack)) {
                if (StrUtil.isNotBlank(stack.getSaleBillNo())) {
                    QueryWrapper<SalesOrder> salesOrderQueryWrapper = new QueryWrapper<>();
                    salesOrderQueryWrapper.lambda().eq(SalesOrder::getTenantId, stack.getTenantId())
                            .eq(SalesOrder::getSaleBillNo, stack.getSaleBillNo());
                    SalesOrder salesOrder = salesOrderService.getOne(salesOrderQueryWrapper);
//                    if (ObjectUtil.isNull(salesOrder)) {
//                        continue;
//                    }
//                    List<SalesOrderDet> salesOrderDetList = salesOrderDetService.selectByMainId(salesOrder.getId());
//                    if (CollectionUtil.isEmpty(salesOrderDetList)) {
//                        continue;
//                    }
//                    if (StrUtil.isBlank(salesOrder.getCreateStackStatus())) {
//                        continue;
//                    }
                    if (ObjectUtil.isNotNull(salesOrder)) {
                        QueryWrapper<SalesOrderDet> queryWrapper = new QueryWrapper<>();
                        queryWrapper.lambda().eq(SalesOrderDet::getTenantId, stackDet.getTenantId())
                                .eq(SalesOrderDet::getParentId, stack.getSalesOrderId())
                                .eq(SalesOrderDet::getInventoryId, stackDet.getInventoryId())
                                .eq(SalesOrderDet::getOrderThick, stackDet.getMatThick())
                                .eq(SalesOrderDet::getOrderLen, stackDet.getMatLen())
                                .eq(SalesOrderDet::getOrderWidth, stackDet.getMatWidth())
                                .eq(SalesOrderDet::getWeight, stackDet.getWeight())
                                .eq(SalesOrderDet::getQty, stackDet.getQty());
                        if (StrUtil.isNotBlank(stackDet.getStockId())) {
                            queryWrapper.lambda().eq(SalesOrderDet::getStockId, stackDet.getStockId());
                        }
                        if (StrUtil.isNotBlank(stackDet.getMatNo())) {
                            queryWrapper.lambda().eq(SalesOrderDet::getMatNo, stackDet.getMatNo());
                        }
                        SalesOrderDet salesOrderDet = salesOrderDetService.getOne(queryWrapper);
                        if (ObjectUtil.isNull(salesOrderDet)) {
                            queryWrapper.lambda().eq(SalesOrderDet::getSgSign, stackDet.getSgSign());
                            salesOrderDet = salesOrderDetService.getOne(queryWrapper);
                        }
                        stackDet.setSalesOrderDetId(salesOrderDet.getId());
                        stackDetList1.add(stackDet);
                    }
                }
            }

        }
        stackDetService.updateBatchById(stackDetList1);
    }

    //测试用例 明细开错仓库库存
    @Test
    public void update() {
        SalesOrderDet salesOrderDet = salesOrderDetService.getById("");
        inventoryService.deductionAvailableById(salesOrderDet.getInventoryId(),-salesOrderDet.getQty(),-salesOrderDet.getWeight());
        inventoryService.deductionPracticalById(salesOrderDet.getInventoryId(),-salesOrderDet.getQty(),-salesOrderDet.getWeight());

        QueryWrapper<StackDet> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(StackDet::getSalesOrderDetId,salesOrderDet.getId());
        StackDet stackDet = stackDetService.getOne(queryWrapper);

        //修改后的库存id
        Inventory inventory  = inventoryService.getById("");

        salesOrderDet.setStockId(inventory.getStockHouseId());
        salesOrderDet.setInventoryId(inventory.getId());
        salesOrderDet.setStockName("物流园库");

        salesOrderDetService.updateById(salesOrderDet);

        stackDet.setStockId(inventory.getId());
        stackDet.setInventoryId(inventory.getId());
        stackDet.setStockName("物流园库");


        inventoryService.deductionAvailableById(inventory.getId(),salesOrderDet.getQty(),salesOrderDet.getWeight());
        inventoryService.deductionPracticalById(inventory.getId(),salesOrderDet.getQty(),salesOrderDet.getWeight());

        stackDetService.updateById(stackDet);


    }

    @Test
    public void t1(){
        double d = 1.23;
        DecimalFormat myFormatter = new DecimalFormat("#,##0.00");
        String str = myFormatter.format(d);
        System.out.println(str);
    }

    @Test
    public void test_det_error() {
        TenantContext.setTenant("12");
        salesOrderController.querySalesOrderDetListByMainId("1479343811483566081");
    }

}
