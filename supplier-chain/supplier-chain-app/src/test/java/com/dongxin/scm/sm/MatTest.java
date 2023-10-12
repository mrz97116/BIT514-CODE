package com.dongxin.scm.sm;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.Application;
import com.dongxin.scm.enums.ProdClassCodeEnum;
import com.dongxin.scm.fm.entity.SettleMatCoding;
import com.dongxin.scm.fm.service.SettleMatCodingService;
import com.dongxin.scm.fm.vo.BankCategory;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.entity.OldSystemDataConversion;
import com.dongxin.scm.sm.mapper.InventoryMapper;
import com.dongxin.scm.sm.mapper.MatMapper;
import com.dongxin.scm.sm.service.InventoryService;
import com.dongxin.scm.sm.service.MatService;
import com.dongxin.scm.sm.service.OldSystemDataConversionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class MatTest {

    @Autowired
    MatService matService;

    @Resource
    MatMapper matMapper;

    @Resource
    InventoryMapper inventoryMapper;

    @Autowired
    OldSystemDataConversionService oldSystemDataConversionService;

    @Autowired
    SettleMatCodingService settleMatCodingService;

    @Autowired
    InventoryService inventoryService;

    @Test
    public void mat() {
        List<Mat> list = matMapper.queryByCH();
        List<Mat> matList = CollectionUtil.newArrayList();
        for (Mat mat : list) {
            QueryWrapper<OldSystemDataConversion> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.lambda().eq(OldSystemDataConversion::getProdCname, mat.getProdCname())
                    .eq(OldSystemDataConversion::getProdClassCode, mat.getProdClassCode())
                    .eq(OldSystemDataConversion::getSgSign, mat.getSgSign());
            OldSystemDataConversion oldSystemDataConversion = oldSystemDataConversionService.getOne(queryWrapper1);
            mat.setOldProdCname(oldSystemDataConversion.getOldProdCname());
            matList.add(mat);
        }
//        matService.updateBatchById(matList);
    }

    @Test
    public void ware_housing_select_test() {

        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
        /*
        queryWrapper.lambda().eq(Inventory::getProdClassCode, mat.getProdClassCode())
                .eq(Inventory::getStockHouseId, mat.getStockHouseId())
                .eq(Inventory::getSgSign, mat.getSgSign())
                .eq(Inventory::getProdCname, mat.getProdCname())
                .eq(Inventory::getProdCnameOther, mat.getProdCnameOther())
                .eq(Inventory::getMatLen, mat.getMatLen())
                .eq(Inventory::getMatThick, mat.getMatThick())
                .eq(Inventory::getWtMode, mat.getWtMode())
                .gt(Inventory::getMatNum, 0);
        inventoryMapper.selectOne()
         */
    }


    @Test
    public void addMatCode() {
        QueryWrapper<Mat> matQueryWrapper = new QueryWrapper<>();
        matQueryWrapper.lambda().eq(Mat::getTenantId, "2").eq(Mat::getDelFlag, "0");
        List<Mat> matList = matService.list(matQueryWrapper);

        for (Mat mat : matList) {
            if (mat.getProdClassCode().equals(ProdClassCodeEnum.B.getValue())
                    || mat.getProdClassCode().equals(ProdClassCodeEnum.D.getValue())) {
                QueryWrapper<SettleMatCoding> settleMatCodingQueryWrapper = new QueryWrapper<>();
                settleMatCodingQueryWrapper.lambda().eq(SettleMatCoding::getOldProdCname, mat.getOldProdCname())
                        .eq(SettleMatCoding::getSgSign, mat.getSgSign())
                        .eq(SettleMatCoding::getMatThick, mat.getMatThick());
                SettleMatCoding settleMatCoding = settleMatCodingService.getOne(settleMatCodingQueryWrapper);
                if (ObjectUtil.isNotEmpty(settleMatCoding)) {
                    mat.setMatCode(settleMatCoding.getMatCode());
                }

            } else {
                QueryWrapper<SettleMatCoding> settleMatCodingQueryWrapper = new QueryWrapper<>();
                settleMatCodingQueryWrapper.lambda().eq(SettleMatCoding::getOldProdCname, mat.getOldProdCname())
                        .eq(SettleMatCoding::getSgSign, mat.getSgSign());
                List<SettleMatCoding> settleMatCodingList = settleMatCodingService.list(settleMatCodingQueryWrapper);
                if (CollectionUtil.isNotEmpty(settleMatCodingList)) {
                    SettleMatCoding settleMatCoding = settleMatCodingList.get(0);
                    mat.setMatCode(settleMatCoding.getMatCode());
                }
            }
        }


        QueryWrapper<Inventory> inventoryQueryWrapper = new QueryWrapper<>();
        inventoryQueryWrapper.lambda().eq(Inventory::getDelFlag, "0").eq(Inventory::getTenantId, "2");
        List<Inventory> inventoryList = inventoryService.list(inventoryQueryWrapper);

        for (Inventory inventory : inventoryList) {
            if (inventory.getProdClassCode().equals(ProdClassCodeEnum.B.getValue())
                    || inventory.getProdClassCode().equals(ProdClassCodeEnum.D.getValue())) {
                QueryWrapper<SettleMatCoding> settleMatCodingQueryWrapper = new QueryWrapper<>();
                settleMatCodingQueryWrapper.lambda().eq(SettleMatCoding::getOldProdCname, inventory.getOldProdCname())
                        .eq(SettleMatCoding::getSgSign, inventory.getSgSign())
                        .eq(SettleMatCoding::getMatThick, inventory.getMatThick());
                SettleMatCoding settleMatCoding = settleMatCodingService.getOne(settleMatCodingQueryWrapper);
                if (ObjectUtil.isNotEmpty(settleMatCoding)) {
                    inventory.setMatCode(settleMatCoding.getMatCode());
                }

            } else {
                QueryWrapper<SettleMatCoding> settleMatCodingQueryWrapper = new QueryWrapper<>();
                settleMatCodingQueryWrapper.lambda().eq(SettleMatCoding::getOldProdCname, inventory.getOldProdCname())
                        .eq(SettleMatCoding::getSgSign, inventory.getSgSign());
                List<SettleMatCoding> settleMatCodingList = settleMatCodingService.list(settleMatCodingQueryWrapper);
                if (CollectionUtil.isNotEmpty(settleMatCodingList)) {
                    SettleMatCoding settleMatCoding = settleMatCodingList.get(0);
                    inventory.setMatCode(settleMatCoding.getMatCode());
                }
            }
        }

        matService.updateBatchById(matList);
        inventoryService.updateBatchById(inventoryList);
    }
}
