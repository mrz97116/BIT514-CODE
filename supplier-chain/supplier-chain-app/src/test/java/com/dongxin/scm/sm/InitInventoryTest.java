package com.dongxin.scm.sm;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.dongxin.scm.Application;
import com.dongxin.scm.sm.entity.ChInitSmStock;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.service.ChInitSmStockService;
import com.dongxin.scm.sm.service.InventoryService;
import com.dongxin.scm.sm.service.WarehouseWarrantService;
import org.jeecg.config.mybatis.TenantContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class InitInventoryTest {

    @Autowired
    ChInitSmStockService chInitSmStockService;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    WarehouseWarrantService warehouseWarrantService;

    @Test
    //中转库存表初始化
    public void chInitSmStock() {
        List<ChInitSmStock> chInitSmStockList = chInitSmStockService.getBaseMapper().selectAll();
        //筛选尺寸存在“米”的数据
        List<ChInitSmStock> filterSizeContainsMeterList = chInitSmStockList.stream().filter(
                stock -> stock.getSize().indexOf("米") != -1).collect(Collectors.toList());
        //筛选尺寸存在“*”的数据
        List<ChInitSmStock> filterSizeContainsMultiplyList = chInitSmStockList.stream().filter(
                stock -> stock.getSize().indexOf("*") != -1).collect(Collectors.toList());
        //筛选尺寸为数字的数据
        List<ChInitSmStock> filterNumberSizeList = chInitSmStockList.stream().filter(
                stock -> NumberUtil.isNumber(stock.getSize())).collect(Collectors.toList());

        //尺寸存在“米”的数据，赋值长度、宽度
        for (ChInitSmStock chInitSmStock : filterSizeContainsMeterList) {
            BigDecimal wide = new BigDecimal(chInitSmStock.getSize().substring(0, chInitSmStock.getSize().length() - 1))
                    .multiply(new BigDecimal(1000));
            chInitSmStock.setWide(wide);
            chInitSmStock.setLength(BigDecimal.ZERO);
        }

        //尺寸存在“*”的数据，赋值长度、宽度
        for (ChInitSmStock chInitSmStock : filterSizeContainsMultiplyList) {
            List<String> strSize = Arrays.asList(chInitSmStock.getSize().split("\\*"));
            if (NumberUtil.isNumber(strSize.get(0))) {
                chInitSmStock.setWide(new BigDecimal(strSize.get(0)));
            }

            if (StrUtil.equals(strSize.get(0), "C")) {
                chInitSmStock.setLength(BigDecimal.ZERO);

            }

            if (NumberUtil.isNumber(strSize.get(0))) {
                chInitSmStock.setLength(new BigDecimal(strSize.get(0)));
            }

        }
        //List合并
        filterSizeContainsMultiplyList.addAll(filterNumberSizeList);
        filterSizeContainsMeterList.addAll(filterSizeContainsMultiplyList);

        for (ChInitSmStock chInitSmStock : filterSizeContainsMeterList) {
            if (NumberUtil.isNumber(chInitSmStock.getSize())) {
                chInitSmStock.setWide(new BigDecimal(chInitSmStock.getSize()));
                chInitSmStock.setLength(BigDecimal.ZERO);
            }

            //筛选规格、赋值厚度
            if (NumberUtil.isNumber(chInitSmStock.getSpecs())) {
                chInitSmStock.setThick(new BigDecimal(chInitSmStock.getSpecs()));
            }
            if (chInitSmStock.getSpecs().indexOf("#") != -1) {
                chInitSmStock.setThick(BigDecimal.ZERO);
            }
            //产品名称赋值
            String prodName = chInitSmStockService.getBaseMapper().selectProdName(chInitSmStock);
            chInitSmStock.setProdName(prodName);
        }

        chInitSmStockService.updateBatchById(filterSizeContainsMeterList);

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

    private String warehouseId(String name) {
        String str = "";
        switch (name) {
            case "物流园库":
                str = "1";
                break;
            case "金海库":
                str = "2";
                break;
            case "厂内库":
                str = "3";
                break;
            case "直调库":
                str = "4";
                break;
            case "外购库":
                str = "5";
                break;
            case "协作库1":
                str = "6";
                break;
            case "协作库2":
                str = "7";
            case "协作库3":
                str = "8";
        }
        return str;
    }


    @Test
    //库存表初始化
    public void initSmInventory(){

        List<ChInitSmStock> chInitSmStockList = chInitSmStockService.getBaseMapper().selectAll();

        List<Inventory> inventoryList = newArrayList();

        for (ChInitSmStock chInitSmStock:chInitSmStockList){
            String weight = chInitSmStockService.getBaseMapper().selectWeight(chInitSmStock);
            Inventory inventory = new Inventory();

            //产品大类、仓库
            String classCode = prodClassCode(chInitSmStock.getType());
            String warehouse = warehouseId(chInitSmStock.getWarehouse());

            //库存物料种类转化
            if (ObjectUtil.isNull(inventory.getMatKind())) {
                switch (chInitSmStock.getType()) {
                    case "A":
                        inventory.setMatKind("SM");
                        break;
                    case "B":
                    case "C":
                    case "D":
                    case "E":
                        inventory.setMatKind("BW");
                        break;
                    case "F":
                        inventory.setMatKind("HR");
                        break;
                    case "G":
                        inventory.setMatKind("CR");
                        break;
                    case "H":
                        inventory.setMatKind("HP");
                        break;
                    case "I":
                        inventory.setMatKind("IR");
                        break;
                }
            }
            //设置库存
            inventory.setOldProdCname(chInitSmStock.getProdName())
                    .setStockHouseId(warehouse)
                    .setAvailableQty(chInitSmStock.getStockNum().doubleValue())
                    .setAvailableWeight(chInitSmStock.getStockWeight().doubleValue())
                    .setSgSign(chInitSmStock.getSteelNo())
                    .setMatLen(chInitSmStock.getLength().doubleValue())
                    .setMatWidth(chInitSmStock.getWide().doubleValue())
                    .setMatThick(chInitSmStock.getThick().doubleValue())
                    .setProdClassCode(classCode)
                    .setTenantId(2)
                    .setMatNo(chInitSmStock.getSteelCoil())
                    .setCustMatSpecs(chInitSmStock.getSpecs())
                    .setWeight(chInitSmStock.getStockWeight().doubleValue())
                    .setMatNum(chInitSmStock.getStockNum().doubleValue())
                    .setId(chInitSmStock.getId())
                    .setPieceWeight(new BigDecimal(weight).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
            inventoryList.add(inventory);
        }
        inventoryService.saveBatch(inventoryList);
    }

    @Test
    public void detailSubduction() {
        List<String> ids = CollectionUtil.newArrayList();
        ids.add("1397518563736068101");
        warehouseWarrantService.detailSubduction(ids);
    }

    @Test
    public void autoFenHuo() {
        TenantContext.setTenant("12");
        inventoryService.autoPrepareGoods();
    }

}
