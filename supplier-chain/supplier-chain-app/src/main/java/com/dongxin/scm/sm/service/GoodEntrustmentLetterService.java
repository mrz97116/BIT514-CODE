package com.dongxin.scm.sm.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.common.service.LockService;
import com.dongxin.scm.enums.SerialNoEnum;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.om.entity.SalesOrderDet;
import com.dongxin.scm.om.service.SalesOrderDetService;
import com.dongxin.scm.om.util.Constants;
import com.dongxin.scm.sm.dto.GoodEntrustmentLetterDTO;
import com.dongxin.scm.sm.entity.*;
import com.dongxin.scm.sm.enums.ShippingMatStatusEnum;
import com.dongxin.scm.sm.mapper.GoodEntrustmentLetterMapper;
import com.dongxin.scm.utils.SerialNoUtil;
import org.jeecg.config.mybatis.TenantContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.api.client.util.Lists.newArrayList;

/**
 * @Description: 提货委托管理
 * @Author: jeecg-boot
 * @Date: 2021-11-03
 * @Version: V1.0
 */
@Service
public class GoodEntrustmentLetterService extends BaseService<GoodEntrustmentLetterMapper, GoodEntrustmentLetter> {
    @Autowired
    private LockService lockService;
    @Autowired
    private ShippingManagementService shippingManagementService;

    @Transactional(rollbackFor = Exception.class)
    public void saveMain(GoodEntrustmentLetterDTO goodEntrustmentLetterDTO) {
        LockInfo lockInfo = lockService.lock(Constants.GOODENTRUSTMENTLETTER_LOCK_HEADER + TenantContext.getTenant());
        try {
            //新增提货委托单
            GoodEntrustmentLetter goodEntrustmentLetter = new GoodEntrustmentLetter();
            BeanUtils.copyProperties(goodEntrustmentLetterDTO, goodEntrustmentLetter);
            goodEntrustmentLetter.setLetterNo(SerialNoUtil.getSerialNo(SerialNoEnum.GOOD_ENTRUSTMENT_LETTER_NO));
            save(goodEntrustmentLetter);

            List<String> shippingManagementIds = goodEntrustmentLetterDTO.getShippingManagementIds();

            String shipNo = "", dispatchShipBillNo = "";

            List<ShippingManagement> shippingManagementList = newArrayList();

            Double weight = 0.0;
            int num = 0;
            for (String shippingManagementId : shippingManagementIds) {
                ShippingManagement shippingManagement = shippingManagementService.getById(shippingManagementId);



                if (shippingManagement == null) {
                    throw new ScmException("参数错误，未查询到对应的船运单");
                }

                weight += shippingManagement.getWeight();
                num ++;

                shipNo = shippingManagement.getShipNo();
                dispatchShipBillNo = shippingManagement.getDispatchShipBillNo();

                shippingManagement.setMatStatus(ShippingMatStatusEnum.DELIVERY_COMMISSION.getCode());
                shippingManagement.setDeliveryCommissionId(goodEntrustmentLetter.getId());
                shippingManagementService.updateById(shippingManagement);
                shippingManagementList.add(shippingManagement);
            }
            if (goodEntrustmentLetter.getConsignationDate() == null) {
                goodEntrustmentLetter.setConsignationDate(new Date());
            }



            //产品大类
            Set set = new HashSet();

            for (ShippingManagement shippingManagement : shippingManagementList) {
                set.add(shippingManagement.getProdClassCode());
            }
            goodEntrustmentLetter.setProdClassCode(CollectionUtil.join(set, ","));

            goodEntrustmentLetter.setNum(num);
            goodEntrustmentLetter.setWeight(weight);

            goodEntrustmentLetter.setShipNo(shipNo);
            goodEntrustmentLetter.setDispatchShipBillNo(dispatchShipBillNo);
            goodEntrustmentLetter.setArrivalStatus(YesNoEnum.NO.getCode());

            String carNos = shippingManagementService.getCarNos(shippingManagementList);

            if (StrUtil.isNotBlank(carNos)) {
                goodEntrustmentLetter.setCarNos(carNos);
            }
            updateById(goodEntrustmentLetter);

        } finally {
            lockService.releaseLock(lockInfo);
        }
    }

    //删除提货委托单
    @Transactional(rollbackFor = Exception.class)
    public void clearConnect(String id) {
        QueryWrapper<ShippingManagement> shippingManagementQueryWrapper = new QueryWrapper<>();
        shippingManagementQueryWrapper.lambda().eq(ShippingManagement::getDeliveryCommissionId, id);
        List<ShippingManagement> shippingManagementList = shippingManagementService.list(shippingManagementQueryWrapper);

        List<ShippingManagement> vanishList = newArrayList();
        if (CollectionUtil.isNotEmpty(shippingManagementList)) {
            for (ShippingManagement shippingManagement : shippingManagementList){
                if (shippingManagement.getMatStatus().equalsIgnoreCase(ShippingMatStatusEnum.IN_STOCK.getCode())){
                    throw new ScmException("已有物料到货，无法删除！材料号："+shippingManagement.getMatNo());
                }
            }
            //去除材料表的提货委托单id
            shippingManagementList.forEach(s -> {
                s.setMatStatus(ShippingMatStatusEnum.ON_BOARD.getCode());
                s.setDeliveryCommissionId(null);
                vanishList.add(s);
            });
            shippingManagementService.updateBatchById(vanishList);
            this.removeById(id);
        } else {
            throw new ScmException("无相应提单明细信息");
        }
    }

    /**
     * 根据船运单更新提货委托状态
     * @param shippingManagementList
     * @param isSetArrive true为更新到已确认到货， false为更新到未到货
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateArriveStatus(List<ShippingManagement> shippingManagementList, boolean isSetArrive) {
        if (CollectionUtil.isEmpty(shippingManagementList)) {
            return;
        }
        try {
            List<String> deliveryCommissionIds = shippingManagementList.stream().map(ShippingManagement::getDeliveryCommissionId).collect(Collectors.toList());
            deliveryCommissionIds = deliveryCommissionIds.stream().filter(StrUtil::isNotBlank).collect(Collectors.toList());
            deliveryCommissionIds = CollectionUtil.distinct(deliveryCommissionIds);
            if (CollectionUtil.isNotEmpty(deliveryCommissionIds)) {
                QueryWrapper<GoodEntrustmentLetter> queryWrapper = new QueryWrapper<>();

                queryWrapper.lambda().in(GoodEntrustmentLetter::getId, deliveryCommissionIds);

                queryWrapper.lambda().eq(GoodEntrustmentLetter::getArrivalStatus, isSetArrive ? YesNoEnum.NO.getCode() : YesNoEnum.YES.getCode());

                List<GoodEntrustmentLetter> goodEntrustmentLetters = list(queryWrapper);

                for (GoodEntrustmentLetter goodEntrustmentLetter : goodEntrustmentLetters) {
                    updateArriveStatus(goodEntrustmentLetter);
                }
            }
        } catch (Exception e) {
            log.error("updateArriveStatus error", e);
        }
    }

    private void updateArriveStatus(GoodEntrustmentLetter goodEntrustmentLetter) {
        List<ShippingManagement> shippingManagementList = shippingManagementService.getByDeliveryCommissionId(goodEntrustmentLetter.getId());

        boolean allArrived = true;

        for (ShippingManagement shippingManagement : shippingManagementList) {
            if (!shippingManagement.getMatStatus().equalsIgnoreCase(ShippingMatStatusEnum.IN_STOCK.getCode())) {
                allArrived = false;
                break;
            }
        }

        if (allArrived) {
            goodEntrustmentLetter.setArrivalStatus(YesNoEnum.YES.getCode());
            goodEntrustmentLetter.setStayDays(0);
        } else {
            goodEntrustmentLetter.setArrivalStatus(YesNoEnum.NO.getCode());
            long longStayDays = DateUtil.between(goodEntrustmentLetter.getConsignationDate(), new Date(), DateUnit.DAY);
            goodEntrustmentLetter.setStayDays((int) longStayDays);

        }
        updateById(goodEntrustmentLetter);
    }

    public void updateStayDays() {
        QueryWrapper<GoodEntrustmentLetter> queryWrapper = new QueryWrapper<>();

        queryWrapper.lambda().eq(GoodEntrustmentLetter::getArrivalStatus, YesNoEnum.NO.getCode());

        List<GoodEntrustmentLetter> goodEntrustmentLetters = list(queryWrapper);

        for (GoodEntrustmentLetter goodEntrustmentLetter : goodEntrustmentLetters) {
            long longStayDays = DateUtil.between(goodEntrustmentLetter.getConsignationDate(), new Date(), DateUnit.DAY);
            goodEntrustmentLetter.setStayDays((int) longStayDays);
        }
        updateBatchById(goodEntrustmentLetters);
    }

    public void addRemarks(String ids, String remark) {

        List<String> idList = Arrays.asList(ids.split(","));

        List<GoodEntrustmentLetter> goodEntrustmentLetterList = listByIds(idList);

        for (GoodEntrustmentLetter goodEntrustmentLetter : goodEntrustmentLetterList) {
            goodEntrustmentLetter.setRemark(remark);
        }
        updateBatchById(goodEntrustmentLetterList);
    }

    /*
    @Transactional(rollbackFor = Exception.class)
    public void updateByGoodEntrustmentLetter(GoodEntrustmentLetterDTO goodEntrustmentLetterDTO, List<SalesOrderDet> salesOrderDetList) {
        if (CollectionUtil.isEmpty(salesOrderDetList)) {
            throw new ScmException("材料明细不能为空");
        }
        LockInfo lockInfo = lockService.lock(Constants.GOODENTRUSTMENTLETTER_LOCK_HEADER + TenantContext.getTenant());
        try {
            Class<GoodEntrustmentLetter> goodEntrustmentLetterClass = GoodEntrustmentLetter.class;
            GoodEntrustmentLetter goodEntrustmentLetter = goodEntrustmentLetterClass.newInstance();
            BeanUtils.copyProperties(goodEntrustmentLetterDTO, goodEntrustmentLetter);
            updateById(goodEntrustmentLetter);
            //将旧材料的提货委托单id清空
            QueryWrapper<SalesOrderDet> salesOrderDetQueryWrapper = new QueryWrapper();
            salesOrderDetQueryWrapper.lambda().eq(SalesOrderDet::getGoodEntrustmentLetterId, goodEntrustmentLetterDTO.getId());
            List<SalesOrderDet> oldSalesOrderDetList = salesOrderDetService.list(salesOrderDetQueryWrapper);
            List<SalesOrderDet> updateSalesOrderDetList = CollectionUtil.newArrayList();
            oldSalesOrderDetList.forEach(oldSalesOrderDet -> {
                oldSalesOrderDet.setGoodEntrustmentLetterId("");
                updateSalesOrderDetList.add(oldSalesOrderDet);
            });
            salesOrderDetService.updateBatchById(updateSalesOrderDetList);
            salesOrderDetQueryWrapper.clear();
            updateSalesOrderDetList.clear();
            //更新新的材料信息
            salesOrderDetList.forEach(salesOrderDet -> {
                salesOrderDetQueryWrapper.lambda()
                        .eq(SalesOrderDet::getOrderLen, salesOrderDet.getOrderLen())
                        .eq(SalesOrderDet::getOrderThick, salesOrderDet.getOrderThick())
                        .eq(SalesOrderDet::getOrderWidth, salesOrderDet.getOrderWidth())
                        .eq(SalesOrderDet::getMatNo, salesOrderDet.getMatNo())
                        .eq(SalesOrderDet::getSgSign, salesOrderDet.getSgSign())
                        .eq(SalesOrderDet::getProdClassCode, salesOrderDet.getProdClassCode())
                        .eq(SalesOrderDet::getWtMode, salesOrderDet.getWtMode())
                        .eq(SalesOrderDet::getWeight, salesOrderDet.getWeight());
                SalesOrderDet updateSalesOrderDet = salesOrderDetService.getOne(salesOrderDetQueryWrapper);
                if (ObjectUtil.isNotNull(updateSalesOrderDet)) {
                    updateSalesOrderDet.setGoodEntrustmentLetterId(goodEntrustmentLetter.getId());
                    updateSalesOrderDetList.add(updateSalesOrderDet);
                } else {
                    throw new ScmException("无相应材料数据");
                }
                salesOrderDetQueryWrapper.clear();
            });
            salesOrderDetService.updateBatchById(updateSalesOrderDetList);
        } catch (IllegalAccessException illegalAccessException) {
            illegalAccessException.printStackTrace();
        } catch (InstantiationException instantiationException) {
            instantiationException.printStackTrace();
        } finally {
            lockService.releaseLock(lockInfo);
        }
    }*/

    @Transactional(rollbackFor = Exception.class)
    public void arrivalCheck(GoodEntrustmentLetterDTO goodEntrustmentLetterDTO) {
        //获取id列表和状态
        /* List<String> ids = goodEntrustmentLetterDTO.getIds();
        String arrivalStatus = goodEntrustmentLetterDTO.getArrivalStatus();
        QueryWrapper<GoodEntrustmentLetter> goodEntrustmentLetterQueryWrapper = new QueryWrapper<>();
        List<GoodEntrustmentLetter> updateGoodEntrustmentLetterList = CollectionUtil.newArrayList();
        for (String id : ids) {
            goodEntrustmentLetterQueryWrapper.lambda().eq(GoodEntrustmentLetter::getId, id);
            GoodEntrustmentLetter updateGoodEntrustmentLetter = getOne(goodEntrustmentLetterQueryWrapper);
            if (ObjectUtil.isNull(updateGoodEntrustmentLetter)) {
                throw new ScmException("查询不到对应提货装车单");
            }
            updateGoodEntrustmentLetter.setArrivalStatus(arrivalStatus);
            updateGoodEntrustmentLetterList.add(updateGoodEntrustmentLetter);
            goodEntrustmentLetterQueryWrapper.clear();
        }
        updateBatchById(updateGoodEntrustmentLetterList);

         */
    }
}
