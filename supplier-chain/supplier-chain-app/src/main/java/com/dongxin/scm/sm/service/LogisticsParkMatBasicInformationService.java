package com.dongxin.scm.sm.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.entity.LogisticsParkMatBasicInformation;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.mapper.LogisticsParkMatBasicInformationMapper;
import com.dongxin.scm.wms.PaginatedRequest;
import com.dongxin.scm.wms.condition.GetProductCondition;
import com.dongxin.scm.wms.exception.WMSException;
import com.dongxin.scm.wms.service.WMSService;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * @Description: 物流园材料基础信息
 * @Author: jeecg-boot
 * @Date: 2021-09-16
 * @Version: V1.0
 */
@Service
public class LogisticsParkMatBasicInformationService extends BaseService<LogisticsParkMatBasicInformationMapper, LogisticsParkMatBasicInformation> {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private WMSService wmsService;

    /**
     * 根据产地获取物流园基础信息
     * @param steelMillsName
     */
    public void getLogisticsParkMatBasicInformation(String steelMillsName) {
        try {
            PaginatedRequest.PaginatedRequestModel<GetProductCondition> requestBody = new PaginatedRequest.PaginatedRequestModel<>();
            requestBody.setPageSize(9999);
            requestBody.setPageNumber(1);
            requestBody.setCondition(GetProductCondition.builder().steelMillsName(steelMillsName).build());
            PaginatedRequest.PaginatedResponseModel product = wmsService.getProduct(requestBody);
            List<Map<String, Object>> LogisticsParkMatBasicInformationList = product.getData();
            List<LogisticsParkMatBasicInformation> addLogisticsParkMatBasicInformationList = newArrayList();

            for (Map<String, Object> record : LogisticsParkMatBasicInformationList) {
                LogisticsParkMatBasicInformation logisticsParkMatBasicInformation = new LogisticsParkMatBasicInformation();
                logisticsParkMatBasicInformation.setProductCode(record.get("productCode") == null ? null : record.get("productCode").toString());
                logisticsParkMatBasicInformation.setProductName(record.get("productName") == null ? null : record.get("productName").toString());
                logisticsParkMatBasicInformation.setSteelMillsName(record.get("steelMillsName") == null ? null : record.get("steelMillsName").toString());
                logisticsParkMatBasicInformation.setStandardName(record.get("standardName") == null ? null : record.get("standardName").toString());
                logisticsParkMatBasicInformation.setLength(record.get("length") == null ? null : new BigDecimal(record.get("length").toString()));
                logisticsParkMatBasicInformation.setWidth(record.get("width") == null ? null : new BigDecimal(record.get("width").toString()));
                logisticsParkMatBasicInformation.setThick(record.get("thick") == null ? null : new BigDecimal(record.get("thick").toString()));
                logisticsParkMatBasicInformation.setPackageCount(record.get("packageCount") == null ? null : Integer.valueOf(record.get("packageCount").toString()));
                logisticsParkMatBasicInformation.setSingleWeight(record.get("singleWeight") == null ? null : new BigDecimal(record.get("singleWeight").toString()));
                logisticsParkMatBasicInformation.setWeightMode(record.get("weightMode") == null ? null : Integer.valueOf(record.get("weightMode").toString()));
                logisticsParkMatBasicInformation.setNumberUnit(record.get("numberUnit") == null ? null : record.get("numberUnit").toString());
                logisticsParkMatBasicInformation.setWeightUnit(record.get("weightUnit") == null ? null : record.get("weightUnit").toString());
                logisticsParkMatBasicInformation.setSteelGradeName(record.get("steelGradeName") == null ? null : record.get("steelGradeName").toString());

                QueryWrapper<LogisticsParkMatBasicInformation> logisticsParkMatBasicInformationQueryWrapper = new QueryWrapper<>();
                logisticsParkMatBasicInformationQueryWrapper.lambda().eq(LogisticsParkMatBasicInformation::getProductName, logisticsParkMatBasicInformation.getProductName())
                        .eq(LogisticsParkMatBasicInformation::getNumberUnit, logisticsParkMatBasicInformation.getNumberUnit())
                        .eq(LogisticsParkMatBasicInformation::getProductCode, logisticsParkMatBasicInformation.getProductCode());

                LogisticsParkMatBasicInformation logisticsParkMatBasicInformationInDb = getOne(logisticsParkMatBasicInformationQueryWrapper);
                if (ObjectUtil.isNotEmpty(logisticsParkMatBasicInformationInDb)) {
                    continue;
                }

                //根据获取到的物流园物料信息自动匹配Mat表中物料信息并赋值
                QueryWrapper<Inventory> inventoryQueryWrapper = new QueryWrapper<>();
                inventoryQueryWrapper.lambda().eq(Inventory::getSgSign,logisticsParkMatBasicInformation.getSteelGradeName())
                        .eq(Inventory::getMatThick,logisticsParkMatBasicInformation.getThick())
                        .eq(Inventory::getMatLen,logisticsParkMatBasicInformation.getLength())
                        .eq(Inventory::getMatWidth,logisticsParkMatBasicInformation.getWidth())
                        .ne(Inventory::getWtMode,logisticsParkMatBasicInformation.getWeightMode());

                List<Inventory> inventoryList = inventoryService.list(inventoryQueryWrapper);

                if (ObjectUtil.isNotEmpty(inventoryList)) {
                    logisticsParkMatBasicInformation.setSysProductName(inventoryList.get(0).getOldProdCname());
                    logisticsParkMatBasicInformation.setSysThick(BigDecimal.valueOf(inventoryList.get(0).getMatThick()));
                    logisticsParkMatBasicInformation.setSysWidth(BigDecimal.valueOf(inventoryList.get(0).getMatWidth()));
                    logisticsParkMatBasicInformation.setSysLength(BigDecimal.valueOf(inventoryList.get(0).getMatLen()));
                    logisticsParkMatBasicInformation.setSysSgSign(inventoryList.get(0).getSgSign());
                } else {
                    continue;
                }

                addLogisticsParkMatBasicInformationList.add(logisticsParkMatBasicInformation);
            }
            saveOrUpdateBatch(addLogisticsParkMatBasicInformationList);

        } catch (WMSException e) {
            e.printStackTrace();
        }
    }

    public void checkUpdateContent(LogisticsParkMatBasicInformation param) {
        QueryWrapper<LogisticsParkMatBasicInformation> logisticsParkMatBasicInformationQueryWrapper = new QueryWrapper<>();
        logisticsParkMatBasicInformationQueryWrapper.lambda().eq(LogisticsParkMatBasicInformation::getSysProductName, param.getSysProductName())
                .eq(LogisticsParkMatBasicInformation::getSteelMillsName, param.getSteelMillsName())
                .eq(LogisticsParkMatBasicInformation::getSysSgSign, param.getSysSgSign())
                .eq(LogisticsParkMatBasicInformation::getSysThick, param.getSysThick())
                .eq(LogisticsParkMatBasicInformation::getSysWidth, param.getSysWidth())
                .eq(LogisticsParkMatBasicInformation::getSysLength, param.getSysLength());
        List<LogisticsParkMatBasicInformation> logisticsParkMatBasicInformationList = list(logisticsParkMatBasicInformationQueryWrapper);
        if (CollectionUtils.isNotEmpty(logisticsParkMatBasicInformationList)) {
            throw new ScmException(StrUtil.format("以维护有此规格材料"));
        }
    }
}
