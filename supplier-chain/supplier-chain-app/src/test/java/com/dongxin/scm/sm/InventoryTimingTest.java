package com.dongxin.scm.sm;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dongxin.scm.Application;
import com.dongxin.scm.bd.entity.CompanyTenant;
import com.dongxin.scm.bd.entity.CompanyTenantDet;
import com.dongxin.scm.bd.mapper.CompanyTenantDetMapper;
import com.dongxin.scm.bd.service.CompanyTenantDetService;
import com.dongxin.scm.bd.service.CompanyTenantService;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.sm.entity.*;
import com.dongxin.scm.sm.entity.Stack;
import com.dongxin.scm.sm.service.*;
import com.dongxin.scm.sm.vo.StockWeightAndUnSettleWeight;
import com.dongxin.scm.utils.DateUtils;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.jeecg.config.mybatis.TenantContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class InventoryTimingTest {
    @Autowired
    InventoryService inventoryService;

    @Autowired
    InventoryTimingService inventoryTimingService;

    @Autowired
    StackDetService stackDetService;

    @Autowired
    MatService matService;

    @Autowired
    private CompanyTenantDetService companyTenantDetService;

    @Autowired
    private CompanyTenantService companyTenantService;

    @Resource
    private CompanyTenantDetMapper companyTenantDetMapper;

    @Autowired
    private StackService stackService;

    @Test
    public void addStockBalance() {

        List<Inventory> inventoryList = inventoryService.list();
        Map<String, StockWeightAndUnSettleWeight> stockWeightAndUnSettleWeightMap = new HashMap<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        Date twoDaysAgo = DateUtils.getDaysBeforeAndAfterTheCurrentTime(-2);
        //如将"20210130"转成20210130，为了通过bigdecimal的compareTo方法进行对比
        BigDecimal twoDaysAgoBigDecimal = new BigDecimal(sdf.format(twoDaysAgo));

        for (Inventory inventory : inventoryList) {
            BigDecimal unSettleWeight = BigDecimal.ZERO;

            //库存定时任务配置inventory_timing_configuration
            //inventory.getTenantId()自动拆箱为基础类型int
            Integer tenantId = Integer.valueOf(inventory.getTenantId());
            QueryWrapper<CompanyTenantDet> companyTenantDetQueryWrapper = new QueryWrapper<>();
            companyTenantDetQueryWrapper.lambda().eq(CompanyTenantDet::getTenantCode, tenantId);

            CompanyTenantDet companyTenantDet = companyTenantDetService.getOne(companyTenantDetQueryWrapper);
            String parentId = companyTenantDetMapper.selectOne(companyTenantDetQueryWrapper).getParentId();

            QueryWrapper<CompanyTenant> companyTenantQueryWrapper = new QueryWrapper<>();
            companyTenantQueryWrapper.lambda().eq(CompanyTenant::getId, parentId);

            CompanyTenant companyTenant = companyTenantService.getById(companyTenantDet.getParentId());
            //判断是否配置了库存定时任务
            if (ObjectUtil.isNotNull(companyTenantDet) && companyTenant.getInventoryTimingConfiguration().equals(YesNoEnum.YES.getCode())) {
                //未结算量：查询sm_stack_det表中优惠后总价discount_total_amount为空、del_flag为0的stackDet记录
                List<StackDet> stackDetList = stackDetService.selectUnSettleStackDet(inventory.getId());
                if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(stackDetList)) {
                    for (StackDet stackDet : stackDetList) {
                        Stack stack = stackService.getById(stackDet.getStackId());
                        BigDecimal shipDateBigDecimal = new BigDecimal(sdf.format(stack.getShipDate()));
                        //装车单发货时间在当前时间的前两天之后，则跳出并进入下一个循环
                        if (shipDateBigDecimal.compareTo(twoDaysAgoBigDecimal) > 0) {
                            continue;
                        }
                        //将double转换为BigDecimal类型
                        unSettleWeight = unSettleWeight.add(BigDecimal.valueOf(stackDet.getWeight()));
                    }
                }
                //柳钢装车时间为当前月最后两天
                BigDecimal lastCarLoadingWeight = BigDecimal.ZERO;
                List<Mat> matList = matService.selectLastCarLoadingTime(inventory.getId(), new Date());
                if (CollectionUtils.isNotEmpty(matList)) {
                    for (Mat mat : matList) {
                        lastCarLoadingWeight = lastCarLoadingWeight.add(BigDecimal.valueOf(mat.getMatNetWt()));
                    }
                }

                inventory.setWeight(inventory.getWeight() - lastCarLoadingWeight.doubleValue() + unSettleWeight.doubleValue());
            }

            StockWeightAndUnSettleWeight stockWeightAndUnSettleWeight = new StockWeightAndUnSettleWeight();
            if (ObjectUtil.isNotNull(inventory.getWeight())) {
                stockWeightAndUnSettleWeight.setStockWeight(BigDecimal.valueOf(inventory.getWeight()));
            } else {
                stockWeightAndUnSettleWeight.setStockWeight(BigDecimal.ZERO);
            }


            stockWeightAndUnSettleWeight.setUnSettleWeight(unSettleWeight);

            stockWeightAndUnSettleWeightMap.put(inventory.getId(), stockWeightAndUnSettleWeight);

        }

//读取库存表的数据往库存时间表的下个月的数据写入
        List<InventoryTiming> nextMonthInventoryTiming = CollectionHelper.newArrayList();
        for (Inventory inventory : inventoryList) {
            StockWeightAndUnSettleWeight stockWeightAndUnSettleWeight = stockWeightAndUnSettleWeightMap.get(inventory.getId());
            if (stockWeightAndUnSettleWeight.getStockWeight().compareTo(BigDecimal.ZERO) > 0 ||
                    stockWeightAndUnSettleWeight.getUnSettleWeight().compareTo(BigDecimal.ZERO) > 0) {
            }
            InventoryTiming inventoryTiming = new InventoryTiming();
            BeanUtils.copyProperties(inventory, inventoryTiming);

            inventoryTiming.setId(null);
            inventoryTiming.setCreateTime(null);
            inventoryTiming.setCreateBy(null);
            inventoryTiming.setUpdateTime(null);
            inventoryTiming.setUpdateBy(null);
            inventoryTiming.setSysOrgCode(null);


            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
            Calendar cale = Calendar.getInstance();
            cale = Calendar.getInstance();
            cale.add(Calendar.MONTH, 1);
            cale.set(Calendar.DAY_OF_MONTH, 1);
            String date = sDateFormat.format(cale.getTime());


            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ParsePosition pos = new ParsePosition(0);
            Date time = formatter.parse(date, pos);


            inventoryTiming.setInventoryId(inventory.getId());
            if (ObjectUtil.isNotNull(inventory.getWeight())) {
                inventoryTiming.setOpeningInventory(BigDecimal.valueOf(inventory.getWeight()));
            } else {
                inventoryTiming.setOpeningInventory(BigDecimal.ZERO);
            }

            inventoryTiming.setTime(time);
            inventoryTiming.setOpeningUnSettleWeight(stockWeightAndUnSettleWeight.getUnSettleWeight());
            inventoryTiming.setOpeningStockWeight(stockWeightAndUnSettleWeight.getStockWeight());
            if (ObjectUtil.isNull(inventoryTiming.getOpeningStockWeight())) {
                inventoryTiming.setOpeningStockWeight(BigDecimal.ZERO);
            }
            if ((inventoryTiming.getOpeningInventory() == null || inventoryTiming.getOpeningInventory().equals(BigDecimal.ZERO))
                    && (inventoryTiming.getOpeningStockWeight() == null || inventoryTiming.getOpeningStockWeight().equals(BigDecimal.ZERO))
                    && (inventoryTiming.getOpeningUnSettleWeight() == null || inventoryTiming.getOpeningUnSettleWeight().equals(BigDecimal.ZERO))
                    && (inventoryTiming.getEndingInventory() == null || inventoryTiming.getEndingInventory().equals(BigDecimal.ZERO))
                    && (inventoryTiming.getEndingStockWeight() == null || inventoryTiming.getEndingStockWeight().equals(BigDecimal.ZERO))
                    && (inventoryTiming.getEndingUnSettleWeight() == null || inventoryTiming.getEndingUnSettleWeight().equals(BigDecimal.ZERO))) {
                continue;
            } else {
                nextMonthInventoryTiming.add(inventoryTiming);
            }
        }


//        List<InventoryTiming> updateInventoryTimingList = CollectionHelper.newArrayList();
//        List<InventoryTiming> addInventoryTimingList = CollectionHelper.newArrayList();
//
//读取库存时间表的下个月的期初库存写入这个月期末库存
//        for (Inventory inventory : inventoryList) {
//
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            ParsePosition pos = new ParsePosition(0);
//            Date time = formatter.parse(DateUtils.getFirstDay(), pos);
//
//            QueryWrapper<InventoryTiming> inventoryTimingQueryWrapper = new QueryWrapper<>();
//            inventoryTimingQueryWrapper.lambda().eq(InventoryTiming::getInventoryId, inventory.getId())
//                    .eq(InventoryTiming::getTime, time);
//
//            InventoryTiming inventoryTiming = inventoryTimingService.getOne(inventoryTimingQueryWrapper);
////            if (ObjectUtil.isNull(inventoryTiming)){
////                continue;
////            }
//            StockWeightAndUnSettleWeight stockWeightAndUnSettleWeight = stockWeightAndUnSettleWeightMap.get(inventory.getId());
//            if (ObjectUtil.isNotEmpty(inventoryTiming)) {
//                if (ObjectUtil.isNotNull(inventory.getWeight())) {
//                    inventoryTiming.setEndingInventory(BigDecimal.valueOf(inventory.getWeight()));
//                } else {
//                    inventoryTiming.setEndingInventory(BigDecimal.ZERO);
//                }
//                inventoryTiming.setEndingStockWeight(stockWeightAndUnSettleWeight.getStockWeight());
//                inventoryTiming.setEndingUnSettleWeight(stockWeightAndUnSettleWeight.getUnSettleWeight());
//                updateInventoryTimingList.add(inventoryTiming);
//            }
//
//            if (ObjectUtil.isNull(inventoryTiming)) {
//                InventoryTiming addInventoryTiming = new InventoryTiming();
//                BeanUtils.copyProperties(inventory, addInventoryTiming);
//
//                addInventoryTiming.setId(null);
//                addInventoryTiming.setCreateTime(null);
//                addInventoryTiming.setCreateBy(null);
//                addInventoryTiming.setUpdateTime(null);
//                addInventoryTiming.setUpdateBy(null);
//                addInventoryTiming.setSysOrgCode(null);
//
//                addInventoryTiming.setInventoryId(inventory.getId());
//                addInventoryTiming.setOpeningInventory(BigDecimal.ZERO);
//
//                if (ObjectUtil.isNotNull(inventory.getWeight())) {
//                    addInventoryTiming.setEndingInventory(BigDecimal.valueOf(inventory.getWeight()));
//                } else {
//                    addInventoryTiming.setEndingInventory(BigDecimal.ZERO);
//                }
//                addInventoryTiming.setTime(time);
//                addInventoryTiming.setEndingStockWeight(stockWeightAndUnSettleWeight.getStockWeight());
//                if (ObjectUtil.isNull(addInventoryTiming.getEndingStockWeight())) {
//                    addInventoryTiming.setEndingStockWeight(BigDecimal.ZERO);
//                }
//
//                addInventoryTiming.setEndingUnSettleWeight(stockWeightAndUnSettleWeight.getUnSettleWeight());
//
//                if ((addInventoryTiming.getOpeningInventory() == null || addInventoryTiming.getOpeningInventory().equals(BigDecimal.ZERO))
//                        && (addInventoryTiming.getOpeningStockWeight() == null || addInventoryTiming.getOpeningStockWeight().equals(BigDecimal.ZERO))
//                        && (addInventoryTiming.getOpeningUnSettleWeight() == null || addInventoryTiming.getOpeningUnSettleWeight().equals(BigDecimal.ZERO))
//                        && (addInventoryTiming.getEndingInventory() == null || addInventoryTiming.getEndingInventory().equals(BigDecimal.ZERO))
//                        && (addInventoryTiming.getEndingStockWeight() == null || addInventoryTiming.getEndingStockWeight().equals(BigDecimal.ZERO))
//                        && (addInventoryTiming.getEndingUnSettleWeight() == null || addInventoryTiming.getEndingUnSettleWeight().equals(BigDecimal.ZERO))) {
//                    continue;
//                } else {
//                    addInventoryTimingList.add(addInventoryTiming);
//                }
//            }
//        }

        inventoryTimingService.saveBatch(nextMonthInventoryTiming);
//        inventoryTimingService.saveBatch(addInventoryTimingList);
//        inventoryTimingService.updateBatchById(updateInventoryTimingList);
    }
}
