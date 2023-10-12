package com.dongxin.scm.sm.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.sm.entity.ImportStockInfo;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.entity.ShippingManagement;
import com.dongxin.scm.sm.enums.ShippingMatStatusEnum;
import com.dongxin.scm.sm.enums.StorageStatusEnum;
import com.dongxin.scm.sm.mapper.ImportStockInfoMapper;
import com.dongxin.scm.sm.vo.OptionVO;
import com.dongxin.scm.utils.ScmStrUtils;
import com.google.common.collect.Lists;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;
import static org.hibernate.validator.internal.util.CollectionHelper.newHashMap;

/**
 * @Description: 导入入库信息
 * @Author: jeecg-boot
 * @Date: 2021-07-21
 * @Version: V1.0
 */
@Service
public class ImportStockInfoService extends BaseService<ImportStockInfoMapper, ImportStockInfo> {
    @Autowired
    private MatService matService;

    @Autowired
    private StockService stockService;

    @Autowired
    WarehouseWarrantService warehouseWarrantService;

    @Autowired
    private ShippingManagementService shippingManagementService;

    @Autowired
    private GoodEntrustmentLetterService goodEntrustmentLetterService;

    @Transactional(rollbackFor = Exception.class)
    public void importCheck(List<ImportStockInfo> importStockInfos) {

        importStockInfos = importStockInfos.stream().filter(i-> StrUtil.isNotEmpty(i.getMatNo()))
                .collect(Collectors.toList());

        List<String> matNoList = newArrayList();

        List<Mat> matList = newArrayList();

        for (ImportStockInfo importStockInfo : importStockInfos) {
            matNoList.add(importStockInfo.getMatNo());
            importStockInfo.setMatNo(importStockInfo.getMatNo().replaceAll(" ", ""));
            importStockInfo.setStockId(importStockInfo.getStockId().replaceAll(" ", ""));

            if (!importStockInfos.get(0).getStockId().equalsIgnoreCase(importStockInfo.getStockId())) {
                throw new ScmException("一次导入只支持同一个仓库的物料信息");
            }
        }
        List<String> duplicateMatNos = ScmStrUtils.getDuplicateElements(matNoList);

        if (CollectionUtil.isNotEmpty(duplicateMatNos)) {
            throw new ScmException(StrUtil.format("文件内材料号重复：{}，请检查",duplicateMatNos.get(0)));
        }

        Map<String, Mat> matMap = matService.getMap(matNoList);

        Map<String, ImportStockInfo> inDbImportStockInfoMap = this.getMap(matNoList);

        List<ImportStockInfo> toBeImportStockInfoList = newArrayList();

        if (StrUtil.isEmpty(importStockInfos.get(0).getStockId())) {
            throw new ScmException("仓库名错误");
        }
        int countSize = 0;
        for (ImportStockInfo importStockInfo : importStockInfos) {
            countSize++;
            Mat mat = matMap.get(importStockInfo.getMatNo());

            if (mat == null) {
                throw new ScmException(StrUtil.format("第{}条系统无对应材料材料号为:{}的材料", countSize, importStockInfo.getMatNo()));
            }
            if (StrUtil.isNotEmpty(importStockInfo.getRemarks())) {
                mat.setRemarks(StrUtil.emptyIfNull(mat.getRemarks()) + importStockInfo.getRemarks());
            }
            mat.setStockLocation(importStockInfo.getGoodsNo());
            mat.setStockTime(importStockInfo.getStockTime());

            matList.add(mat);

            //入库状态赋值
            importStockInfo.setStorageStatus(StorageStatusEnum.NOT_STORED.getCode());

            //制单时间
            importStockInfo.setPreparationTime(new Date());
            importStockInfo.setProdCnameOther(mat.getOldProdCname());
            importStockInfo.setSurfaces(mat.getSgSign());
            importStockInfo.setCopyCodeNetWeight(BigDecimal.valueOf(mat.getMatNetWt()));
            importStockInfo.setQty(BigDecimal.valueOf(mat.getMatNum()));
            importStockInfo.setShipNo(mat.getShipNo());
            importStockInfo.setStandards(mat.generateCustMatSpecs());
            importStockInfo.setMatWidth(mat.getMatWidth());
            importStockInfo.setMatThick(mat.getMatThick());
            importStockInfo.setMatLen(mat.getMatLen());
            importStockInfo.setProdClassCode(mat.getProdClassCode());


            if (ObjectUtil.isNotEmpty(inDbImportStockInfoMap.get(importStockInfo.getMatNo()))) {
                log.error(StrUtil.format("第{}条系统已入库材料号为:{}的材料", countSize, importStockInfo.getMatNo()));
                throw new ScmException(StrUtil.format("第{}条系统已入库材料号为:{}的材料", countSize, importStockInfo.getMatNo()));
            }
            toBeImportStockInfoList.add(importStockInfo);
        }

        //插入数据
        saveBatch(toBeImportStockInfoList);

        matService.updateWarehouse(matList, importStockInfos.get(0).getStockId(), "", null, null);

        List<ShippingManagement> shippingManagementList = shippingManagementService.getByMatNos(matNoList);

        //更新船运单状态为入库
        for (ShippingManagement shippingManagement : shippingManagementList) {
            shippingManagement.setMatStatus(ShippingMatStatusEnum.IN_STOCK.getCode());
        }
        shippingManagementService.updateBatchById(shippingManagementList);

        goodEntrustmentLetterService.updateArriveStatus(shippingManagementList, true);
    }



    /**
    @Transactional(rollbackFor = Exception.class)
    public void warehousing(List<String> importStockInfoIdList) {
        //获取入库材料信息
        List<ImportStockInfo> importStockInfoList = listByIds(importStockInfoIdList);
        String stockId = importStockInfoList.get(0).getStockId();
        String remarks = importStockInfoList.get(0).getRemarks();

        //校验同一次入库操作只能入库同一个仓库
        Set<ImportStockInfo> stockCheck = new TreeSet<ImportStockInfo>(new Comparator<ImportStockInfo>() {
            public int compare(ImportStockInfo a, ImportStockInfo b) {
                // 字符串则按照asicc码升序排列
                return a.getStockId().compareTo(b.getStockId());
            }
        });
        stockCheck.addAll(importStockInfoList);
        if (stockCheck.size() > 1) {
            throw new ScmException(StrUtil.format("同一批入库只能入相同仓库的材料"));
        }

        //校验同一次入库操作只能入库同一个备注
        Set<ImportStockInfo> remarksCheck = new TreeSet<ImportStockInfo>(new Comparator<ImportStockInfo>() {
            public int compare(ImportStockInfo a, ImportStockInfo b) {
                // 字符串则按照asicc码升序排列
                return a.getStockId().compareTo(b.getStockId());
            }
        });
        remarksCheck.addAll(importStockInfoList);
        if (remarksCheck.size() > 1) {
            throw new ScmException(StrUtil.format("同一批入库只能入相同备注的材料"));
        }

        //获取材料号
        List<String> matNoList = newArrayList();
        //key=材料 , value=货物编码
        Map<String, String> goodsNoMap = new HashMap<>();
        for (ImportStockInfo importStockInfo : importStockInfoList) {
            matNoList.add(importStockInfo.getMatNo());
            goodsNoMap.put(importStockInfo.getMatNo(), importStockInfo.getGoodsNo());
        }

        //获取Mat表材料信息
        QueryWrapper<Mat> matQueryWrapper = new QueryWrapper<>();
        matQueryWrapper.lambda().in(Mat::getMatNo, matNoList);
        List<Mat> matList = matService.list(matQueryWrapper);

        Map<String, Mat> matMap = new HashMap<>();
        for (Mat mat : matList) {
            matMap.put(mat.getMatNo(), mat);
            mat.setGoodsNo(goodsNoMap.get(mat.getMatNo()));
        }

        //入库前校验材料信息,更改船运单入库状态
        for (ImportStockInfo importStockInfo : importStockInfoList) {
            Mat mat = matMap.get(importStockInfo.getMatNo());
            if (ObjectUtil.isNull(mat)) {
                log.error(StrUtil.format("系统未匹配到材料号为:" + importStockInfo.getMatNo() + "的材料信息." + importStockInfo));
                throw new ScmException(StrUtil.format("系统未匹配到材料号为:" + importStockInfo.getMatNo() + "的材料信息."));
            }
            if (mat.getStockType().equals(StockTypeEnum.ON_STOCK.getCode())) {
                log.error(StrUtil.format("材料号为:" + mat.getMatNo() + "的材料已入库." + mat));
                throw new ScmException(StrUtil.format("材料号为:" + mat.getMatNo() + "的材料已入库."));
            }
            if (importStockInfo.getMatNo().equals(StorageStatusEnum.STORED.getCode())) {
                log.error(StrUtil.format("材料号为:" + importStockInfo.getMatNo() + "的材料已入库" + importStockInfo));
                throw new ScmException(StrUtil.format("材料号为:" + importStockInfo.getMatNo() + "的材料已入库"));
            }
            QueryWrapper<ShippingManagement> shippingManagementQueryWrapper = new QueryWrapper<>();
            shippingManagementQueryWrapper.lambda().eq(ShippingManagement::getMatNo, importStockInfo.getMatNo());
            ShippingManagement shippingManagement = shippingManagementQueryWrapper.getEntity();
//            shippingManagement.setMatStatus(StockTypeEnum.ON_STOCK.getCode());
        }

        //入库
        matService.updateWarehouse(matList, stockId, remarks, null, null);

    }
    */

    public List<OptionVO> selectStorageStatus() {
        List<OptionVO> typeOptionVO = Lists.newArrayList();
        StorageStatusEnum[] storageStatusEnums = StorageStatusEnum.values();
        for (StorageStatusEnum storageStatusEnum : storageStatusEnums) {
            OptionVO optionVO = new OptionVO();
            optionVO.setText(storageStatusEnum.getDesc());
            optionVO.setValue(storageStatusEnum.getCode());

            typeOptionVO.add(optionVO);
        }

        return typeOptionVO;
    }

    public void updateImportStockInfoSelectDate(IPage<ImportStockInfo> pageList) {
        Map<String, String> storageStatusEnumsMap = new HashMap<>();
        StorageStatusEnum[] storageStatusEnums = StorageStatusEnum.values();
        for (StorageStatusEnum storageStatusEnum : storageStatusEnums) {
            storageStatusEnumsMap.put(storageStatusEnum.getCode(), storageStatusEnum.getDesc());
        }

        List<String> stockIdList = newArrayList();

        for (ImportStockInfo importStockInfo : pageList.getRecords()) {
            stockIdList.add(importStockInfo.getStockId());
        }

        Map<String, String> stockNameMap = stockService.getStockHouseIdMap(stockIdList);

        for (ImportStockInfo importStockInfo : pageList.getRecords()) {
            importStockInfo.setStockId(stockNameMap.get(importStockInfo.getStockId()));
            importStockInfo.setStorageStatus(storageStatusEnumsMap.get(importStockInfo.getStorageStatus()));
        }
    }

    public Map<String, ImportStockInfo> getMap(List<String> matNos) {

        Map<String, ImportStockInfo> result = newHashMap();
        QueryWrapper<ImportStockInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(ImportStockInfo::getMatNo, matNos);
        List<ImportStockInfo> list = list(queryWrapper);

        for (ImportStockInfo importStockInfo : list) {
            result.put(importStockInfo.getMatNo(), importStockInfo);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection idList) {

        if (CollectionUtil.isEmpty(idList)) {
            throw new ScmException("参数错误");
        }
        List<String> matIds = newArrayList();
        QueryWrapper<ImportStockInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(ImportStockInfo::getId, idList);
        List<ImportStockInfo> importStockInfos = list(queryWrapper);

        List<String> matNos = importStockInfos.stream().map(ImportStockInfo::getMatNo).collect(Collectors.toList());
        List<Mat> matList = matService.getByMatNo(matNos);

        for (Mat mat : matList) {
            matIds.add(mat.getId());
        }
        queryWrapper.lambda().in(ImportStockInfo::getId, idList);
        warehouseWarrantService.detailSubduction(matIds);

        List<ShippingManagement> shippingManagementList = shippingManagementService.getByMatNos(matNos);

        for (ShippingManagement shippingManagement : shippingManagementList) {
            shippingManagement.setMatStatus(ShippingMatStatusEnum.DELIVERY_COMMISSION.getCode());
        }
        shippingManagementService.updateBatchById(shippingManagementList);

        goodEntrustmentLetterService.updateArriveStatus(shippingManagementList, false);
        return super.removeByIds(idList);
    }
}
