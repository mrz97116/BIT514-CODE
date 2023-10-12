package com.dongxin.scm.sm;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Console;
import com.dongxin.scm.Application;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.entity.SalesOrderDet;
import com.dongxin.scm.om.mapper.SalesOrderMapper;
import com.dongxin.scm.om.service.SalesOrderDetService;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.om.vo.BillDetVO;
import com.dongxin.scm.om.vo.BillVO;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.service.InventoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class SalesOrderTest {

    @Autowired
    private SalesOrderService salesOrderService;

    @Autowired
    private SalesOrderDetService salesOrderDetService;

    @Autowired
    private InventoryService inventoryService;

    @Resource
    private SalesOrderMapper salesOrderMapper;

    @Autowired
    private CustomerProfileService customerProfileService;

    private final Integer tenantID = 2;

    @Test
    public void bill() {
        //提单生成
        List<BillVO> list = salesOrderMapper.selectBill();
        for (BillVO billVO : list) {
            SalesOrder salesOrder = new SalesOrder();
            List<SalesOrderDet> salesOrderDetList = CollectionUtil.newArrayList();
            String customerId = customerProfileService.getIdByName(billVO.getCompanyName(), tenantID);
            billVO.setCustomerId(customerId);
            salesOrder.setTenantId(String.valueOf(tenantID));
            salesOrder.setId(billVO.getId());
            salesOrder.setSalesMan(billVO.getOperator());
            salesOrder.setRemark(billVO.getRemark());
            salesOrder.setCarNo(billVO.getCarNo());
            salesOrder.setDestination(billVO.getDestination());
            List<BillDetVO> billDetVOList = salesOrderMapper.selectBillDet(billVO.getId());
            for (BillDetVO billDetVO : billDetVOList) {
                SalesOrderDet salesOrderDet = new SalesOrderDet();
                salesOrderDet.setParentId(billVO.getId());
                salesOrderDet.setProdClassCode(prodClassCode(billDetVO.getProdClassCode()));
                salesOrderDet.setWeight(billDetVO.getWeight());
                salesOrderDet.setInventoryId(billDetVO.getStockId());
                salesOrderDet.setId(billDetVO.getId());
                salesOrderDet.setTenantId(tenantID);
                //库存
                Inventory inventory = inventoryService.getById(billDetVO.getInventoryId());
                salesOrderDet.setOrderWidth(inventory.getMatWidth());
                salesOrderDet.setOrderThick(inventory.getMatThick());
                salesOrderDet.setOrderLen(inventory.getMatLen());
                salesOrderDet.setSgSign(inventory.getSgSign());
                salesOrderDet.setOldProdCname(inventory.getOldProdCname());
                salesOrderDet.setProdCname(inventory.getOldProdCname());
                salesOrderDet.setTotalPrice(billDetVO.getTotalPrice());
                salesOrderDetList.add(salesOrderDet);
            }
            salesOrderService.saveMain(salesOrder, salesOrderDetList, newArrayList());
        }


    }

    @Test
    public void stack() {

    }


    private String prodClassCode(String code) {
        String str = "";
        switch (code) {
            case "3":
                str = "H";
                break;
            case "11":
                str = "F";
                break;
            case "12":
                str = "G";
                break;
            case "1":
                str = "C";
                break;
            case "7":
                str = "B";
                break;
            case "6":
                str = "D";
                break;
        }
        return str;
    }
}
