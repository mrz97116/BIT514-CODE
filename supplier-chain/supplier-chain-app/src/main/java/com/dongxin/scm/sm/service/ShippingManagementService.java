package com.dongxin.scm.sm.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.common.service.DataLogService;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.entity.ShippingManagement;
import com.dongxin.scm.sm.enums.ShippingMatStatusEnum;
import com.dongxin.scm.sm.enums.StockTypeEnum;
import com.dongxin.scm.sm.enums.TransportWayEnum;
import com.dongxin.scm.sm.mapper.ShippingManagementMapper;
import com.dongxin.scm.sm.vo.OptionVO;
import com.dongxin.scm.utils.DateUtils;
import com.dongxin.scm.utils.ScmBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 船运管理
 * @Author: jeecg-boot
 * @Date: 2021-07-20
 * @Version: V1.0
 */
@Service
public class ShippingManagementService extends BaseService<ShippingManagementMapper, ShippingManagement> {
    @Autowired
    private DataLogService dataLogService;
    @Autowired
    private MatService matService;

    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<ShippingManagement> list) {
        boolean flag = super.saveBatch(list);
        String tableName = ShippingManagement.class.getAnnotation(TableName.class).value();
        for (ShippingManagement shippingManagement : list) {
            String dataContent = JSONObject.toJSONString(shippingManagement);
            dataLogService.dataLog(tableName, shippingManagement.getId(), dataContent);
        }
        return flag;
    }

    //将船运管理单中的id更新到对应材料中，并更改材料的库存状态为虚拟状态
    @Transactional(rollbackFor = Exception.class)
    public void updateMatByShippingManagement(Mat mat, ShippingManagement importShippingManagement) {
        mat.setStockType(StockTypeEnum.ON_BOARD.getCode());
        mat.setRemarks(importShippingManagement.getRemark());
        mat.setTransportWay(TransportWayEnum.SHIPPING.getCode());
        matService.updateById(mat);
    }

    //将材料表中的id更新到对应船运管理单中，并设置物料状态为即将入库
    public ShippingManagement updateShippingManagementByMat(Mat mat, ShippingManagement importShippingManagement) {
        String shipNo = importShippingManagement.getShipNo();
        String carNo = importShippingManagement.getCarNo();
        ScmBeanUtils.copySysOutsideProperties(mat, importShippingManagement);
        importShippingManagement.setProductName(mat.getOldProdCname());
        importShippingManagement.setMatId(mat.getId());
        importShippingManagement.setMatStatus(ShippingMatStatusEnum.ON_BOARD.getCode());
        importShippingManagement.setProdClassCode(mat.getProdClassCode());
        importShippingManagement.setMatSpecs(mat.getCustMatSpecs());
        importShippingManagement.setWeight(mat.getMatNetWt());
        importShippingManagement.setSteelNo(mat.getSgSign());
        importShippingManagement.setActualDeliveryLocation(mat.getActualDeliveryLocation());
        //船单赋值与校验
        if (StrUtil.isEmpty(mat.getShipNo())) {
            importShippingManagement.setShipNo(shipNo);
        } else {
            if (StrUtil.isNotEmpty(importShippingManagement.getShipNo())) {
                if (!mat.getShipNo().equalsIgnoreCase(importShippingManagement.getShipNo())) {
                    throw new ScmException(StrUtil.format("材料号{}与经销船号：{}，物流船号：{}不对应", mat.getMatNo(), mat.getShipNo(), shipNo));
                }
            } else {
                importShippingManagement.setShipNo(mat.getShipNo());
            }

        }

        //车牌号赋值与校验
        if (StrUtil.isEmpty(mat.getCarNo())) {
            importShippingManagement.setCarNo(carNo);
        } else {
            if (StrUtil.isNotEmpty(importShippingManagement.getCarNo())) {
                if (!mat.getCarNo().equalsIgnoreCase(importShippingManagement.getCarNo())) {
                    throw new ScmException(StrUtil.format("材料号{}与经销车号：{}，物流车号：{}不对应", mat.getMatNo(), mat.getCarNo(), carNo));
                }
            } else {
                importShippingManagement.setCarNo(mat.getCarNo());
            }

        }
        if (StrUtil.isNotEmpty(mat.getDeliveryTime())) {
            importShippingManagement.setDeliveryTime(DateUtils.parseDate(mat.getDeliveryTime()));
        }
        return importShippingManagement;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateBatchRemarks(List<ShippingManagement> shippingManagementList, String remark) {
        if (ObjectUtil.isNull(remark)) {
            throw new ScmException("请填写备注");
        }
        QueryWrapper<ShippingManagement> updateShippingManagementQueryWrapper = new QueryWrapper<>();
        List<ShippingManagement> updateShippingManagementList = CollectionUtil.newArrayList();
        int count = 0;
        for (ShippingManagement shippingManagement : shippingManagementList) {
            count++;
            updateShippingManagementQueryWrapper.lambda()
                    .eq(ShippingManagement::getId, shippingManagement.getId());
            ShippingManagement updateShippingManagement = getOne(updateShippingManagementQueryWrapper);
            if (ObjectUtil.isNull(updateShippingManagement)) {
                throw new ScmException(StrUtil.format("第{}条船运单不存在", count));
            }
            updateShippingManagement.setRemark(remark);
            updateShippingManagementList.add(updateShippingManagement);
            updateShippingManagementQueryWrapper.clear();
        }
        updateBatchById(updateShippingManagementList);
    }

    public List<ShippingManagement> queryPrepareOrderDetById(String id) {
        QueryWrapper<ShippingManagement> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ShippingManagement::getPrepareOrderId, id);
        return list(queryWrapper);
    }


    public void delConnectFromPrepareOrder(List<String> shippingManagementIds)  {
        if (CollectionUtil.isEmpty(shippingManagementIds)) {

            throw new ScmException("请选择对应明细");
        }
        QueryWrapper<ShippingManagement> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(ShippingManagement::getId, shippingManagementIds);

        List<ShippingManagement> result = list(queryWrapper);

        result.forEach(this::delConnectFromPrepareOrder);

        updateBatchById(result);
    }

    public void delConnectFromPrepareOrder(String id) {
        ShippingManagement shippingManagement = getById(id);
        delConnectFromPrepareOrder(shippingManagement);
        updateById(shippingManagement);
    }

    public void delConnectFromPrepareOrder(ShippingManagement shippingManagement) {
        shippingManagement.setRemark("");
        shippingManagement.setPrepareOrderId("");
        shippingManagement.setPrepareStatus("");
        shippingManagement.setMatStatus(ShippingMatStatusEnum.ON_BOARD.getCode());

        QueryWrapper<Mat> matQueryWrapper = new QueryWrapper<>();
        matQueryWrapper.lambda().eq(Mat::getMatNo ,shippingManagement.getMatNo());
        Mat mat = matService.getOne(matQueryWrapper);
        mat.setStockType(StockTypeEnum.ON_ROUTE.getCode());
        mat.setRemarks(null);
        mat.setTransportWay(TransportWayEnum.CARTAGE.getCode());
        matService.updateById(mat);

    }

    public List<OptionVO> getThicks(String prodClassCode) {
        return getBaseMapper().getMatThicks(prodClassCode);
    }

    public List<ShippingManagement> getByPrepareOrderId(String prepareOrderId) {
        QueryWrapper<ShippingManagement> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ShippingManagement::getPrepareOrderId, prepareOrderId);
        return list(queryWrapper);
    }

    public List<ShippingManagement> getByDeliveryCommissionId(String deliveryCommissionId) {
        QueryWrapper<ShippingManagement> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ShippingManagement::getDeliveryCommissionId, deliveryCommissionId);
        return list(queryWrapper);
    }

    public void batchAddRemarks(String ids, String remark) {

        List<String> idList = Arrays.asList(ids.split(","));

        List<ShippingManagement> shippingManagementList = listByIds(idList);

        for (ShippingManagement shippingManagement : shippingManagementList) {
            shippingManagement.setRemark(remark);
        }
        updateBatchById(shippingManagementList);
    }

    public List<ShippingManagement> getByMatNos(List<String> matNos) {

        if (CollectionUtil.isEmpty(matNos)) {
            return newArrayList();
        }
        QueryWrapper<ShippingManagement> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(ShippingManagement::getMatNo, matNos);

        return list(queryWrapper);
    }

    public String getCarNos(List<ShippingManagement> shippingManagementList) {

        if (CollectionUtil.isEmpty(shippingManagementList)) {
            return "";
        }
        List<String> matNos = shippingManagementList.stream().map(ShippingManagement::getMatNo).collect(Collectors.toList());

        if (CollectionUtil.isEmpty(matNos)) {
            return "";
        }
        List<Mat> matList = matService.getByMatNo(matNos);

        List<String> carNoList = matList.stream().map(Mat::getCarNo).collect(Collectors.toList());

        carNoList = CollectionUtil.distinct(carNoList);

        carNoList = carNoList.stream().filter(StrUtil::isNotBlank).collect(Collectors.toList());

        if (CollectionUtil.isEmpty(carNoList)) {
            return "";
        }

        return String.join("、", carNoList);
    }



}
