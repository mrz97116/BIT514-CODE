package com.dongxin.scm.sm.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dongxin.scm.enums.WmsOperateEnum;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.enums.LogisticsEnum;
import com.dongxin.scm.om.enums.WmsTypeEnum;
import com.dongxin.scm.om.enums.WtModeEnum;
import com.dongxin.scm.om.service.SalesOrderDetService;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.sm.entity.GetActualDelivery;
import com.dongxin.scm.sm.entity.GetActualDeliveryDet;
import com.dongxin.scm.sm.mapper.GetActualDeliveryMapper;
import com.dongxin.scm.utils.DateUtils;
import com.dongxin.scm.wms.condition.SubmitBillOfLadingPlanParam;
import com.dongxin.scm.wms.service.WMSService;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.newArrayList;

/**
 * @Description: 获取装车实际
 * @Author: jeecg-boot
 * @Date: 2021-09-22
 * @Version: V1.0
 */
@Service
public class GetActualDeliveryService extends BaseService<GetActualDeliveryMapper, GetActualDelivery> {

    @Autowired
    private GetActualDeliveryDetService getActualDeliveryDetService;

    @Autowired
    private SalesOrderService salesOrderService;

    @Transactional(rollbackFor = Exception.class)
    public void delMain(String id) {
        getActualDeliveryDetService.deleteBatchByMainId(id);
        logicDeleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            getActualDeliveryDetService.deleteBatchByMainId(id.toString());
            logicDeleteById(id);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addGetTransferDelivery(com.dongxin.scm.wms.condition.GetTransferDelivery getTransferDelivery) {
        com.dongxin.scm.wms.condition.GetActualDelivery getActualDelivery = new com.dongxin.scm.wms.condition.GetActualDelivery();
        getActualDelivery.setBillOfLadingNo(getTransferDelivery.getTransferOwnershipNo());
        getActualDelivery.setStockOutNoteNo(getTransferDelivery.getTransferOwnershipNo());
        getActualDelivery.setStockOutDate(getTransferDelivery.getTransferDate());
        getActualDelivery.setOperateFlag(getTransferDelivery.getOperateFlag());
        getActualDelivery.setOperatorName(getTransferDelivery.getOperatorName());
        getActualDelivery.setRemark((StrUtil.isNotEmpty(getTransferDelivery.getRemark())? getTransferDelivery.getRemark() : "过户"));
        getActualDelivery.setDetail(getTransferDelivery.getDetail());

        QueryWrapper<SalesOrder> salesOrderQueryWrapper = new QueryWrapper<>();
        salesOrderQueryWrapper.lambda().eq(SalesOrder::getSaleBillNo, getTransferDelivery.getTransferOwnershipPlanNo())
                .eq(SalesOrder::getTenantId,2);
        SalesOrder salesOrder = salesOrderService.getOne(salesOrderQueryWrapper);

        getActualDelivery.setMainId(salesOrder.getId());

        addGetActualDelivery(getActualDelivery);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addGetActualDelivery(com.dongxin.scm.wms.condition.GetActualDelivery getActualDelivery) {
        //复制主表数据
        GetActualDelivery addGetActualDelivery = new GetActualDelivery();
        BeanUtils.copyProperties(getActualDelivery, addGetActualDelivery);

        QueryWrapper<GetActualDelivery> getActualDeliveryQueryWrapper = new QueryWrapper<>();
        getActualDeliveryQueryWrapper.lambda().eq(GetActualDelivery::getMainId, addGetActualDelivery.getMainId());
        GetActualDelivery getActualDeliveryIndb = getOne(getActualDeliveryQueryWrapper);

        //重复判断
        if (ObjectUtil.isNotNull(getActualDeliveryIndb)) {
            throw new ScmException("该提货单已存在");
        }

        //获取对应提单数据
        QueryWrapper<SalesOrder> salesOrderQueryWrapper = new QueryWrapper<>();
        salesOrderQueryWrapper.lambda().eq(SalesOrder::getId, addGetActualDelivery.getMainId()).eq(SalesOrder::getTenantId, 2);
        SalesOrder salesOrder = salesOrderService.getOne(salesOrderQueryWrapper);

        addGetActualDelivery.setMainId(salesOrder.getId());
        addGetActualDelivery.setBillOfLadingNo(salesOrder.getSaleBillNo());
        addGetActualDelivery.setBillDate(salesOrder.getOrderTime());
        addGetActualDelivery.setTenantId(2);
        addGetActualDelivery.setStockOutDate(DateUtils.parseDate(getActualDelivery.getStockOutDate()));
        addGetActualDelivery.setConfirmStack(YesNoEnum.NO.getCode());
        addGetActualDelivery.setSalesCreateBy(salesOrder.getCreateBy());
        addGetActualDelivery.setCarNo(salesOrder.getCarNo());
        addGetActualDelivery.setCustomerId(salesOrder.getCustomerId());

        //物流园通知类型，如果为I则表示装车类型为确认装车  类型为D，则表示取消装车
        if (WmsOperateEnum.I.getValue().equalsIgnoreCase(getActualDelivery.getOperateFlag())) {
            addGetActualDelivery.setStackStatusType(YesNoEnum.YES.getCode());
        } else if (WmsOperateEnum.D.getValue().equalsIgnoreCase(getActualDelivery.getOperateFlag())) {
            //新增一个getActualDelivery，类型type 为取消装车，需要客户点击确认。
            addGetActualDelivery.setStackStatusType(YesNoEnum.NO.getCode());
        }
        //新增主表数据
        save(addGetActualDelivery);

        //获取附表数据
        List<GetActualDeliveryDet> getActualDeliveryDetList = getActualDelivery.getDetail();

        //按照产品代码分组
        Map<String, List<GetActualDeliveryDet>> getActualDeliverDetMap = new HashMap<>();
        getActualDeliverDetMap = getActualDeliveryDetList.stream().collect(
                Collectors.groupingBy(
                        GetActualDeliveryDet::getProductCode
                ));

        List<GetActualDeliveryDet> addGetActualDeliveryDetList = newArrayList();

        //出来明细数据
        for (Map.Entry<String, List<GetActualDeliveryDet>> entry : getActualDeliverDetMap.entrySet()) {
            List<GetActualDeliveryDet> mergeGetActualDeliveryDetList = entry.getValue();

            GetActualDeliveryDet copyGetActualDeliveryDet = mergeGetActualDeliveryDetList.get(0);
            int weightMode = copyGetActualDeliveryDet.getWeightMode();
            if (weightMode == 0) {
                weightMode = 1;
            } else {
                weightMode = 0;
            }

            GetActualDeliveryDet addGetActualDeliveryDet = new GetActualDeliveryDet();
            BeanUtils.copyProperties(copyGetActualDeliveryDet, addGetActualDeliveryDet);
            addGetActualDeliveryDet.setWeightMode(weightMode);
            addGetActualDeliveryDet.setGetActualDeliveryId(addGetActualDelivery.getId());

            addGetActualDeliveryDet.setQuantity(BigDecimal.ZERO);
            addGetActualDeliveryDet.setWeight(BigDecimal.ZERO);
            addGetActualDeliveryDet.setTenantId(2);

            //合并件数、重量
            for (GetActualDeliveryDet recode : mergeGetActualDeliveryDetList) {
                addGetActualDeliveryDet.setQuantity(addGetActualDeliveryDet.getQuantity().add(recode.getQuantity()));
                addGetActualDeliveryDet.setWeight(addGetActualDeliveryDet.getWeight().add(recode.getWeight()));
            }
            addGetActualDeliveryDetList.add(addGetActualDeliveryDet);
        }

        //更改物流园回推状态
        salesOrder.setPushLogistics(LogisticsEnum.LOGISTICS_OK.getCode());
        salesOrderService.updateById(salesOrder);

        getActualDeliveryDetService.saveBatch(addGetActualDeliveryDetList);
    }

    public void updatePageDataList(IPage<GetActualDeliveryDet> pageList) {
        WtModeEnum[] wtModeEnums = WtModeEnum.values();
        Map<String, String> wtModeEnumMap = new HashMap<>();
        for (WtModeEnum wtModeEnum : wtModeEnums) {
            wtModeEnumMap.put(wtModeEnum.getCode(), wtModeEnum.getName());
        }

        for (GetActualDeliveryDet getActualDeliveryDet : pageList.getRecords()) {
            if (ObjectUtil.isNotEmpty(getActualDeliveryDet.getWeightMode())) {
                getActualDeliveryDet.setWeightMode(Integer.valueOf(wtModeEnumMap.get(getActualDeliveryDet.getWeightMode().toString())));
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public String confirmStack(String id, String stackStatus) {
        GetActualDelivery getActualDelivery = getById(id);
        if (ObjectUtil.isEmpty(getActualDelivery)) {
            throw new ScmException(StrUtil.format("查找不到数据，" + id));
        }
        if (getActualDelivery.getConfirmStack().equals(YesNoEnum.YES.getCode())) {
            throw new ScmException(StrUtil.format("该数据已确认"));
        }
        if (getActualDelivery.getStackStatusType().equals(YesNoEnum.NO.getCode())) {
            throw new ScmException(StrUtil.format("该数据为取消装车单据"));
        }
        getActualDelivery.setConfirmStack(YesNoEnum.YES.getCode());
        updateById(getActualDelivery);

        SalesOrder salesOrder = salesOrderService.getById(getActualDelivery.getMainId());

        if (null == salesOrder) {
            log.error(String.format("confirmStack 提单不存在 提单id：{}", getActualDelivery.getMainId()));
            throw new ScmException("提单不存在，请检查");
        }

        salesOrder.setPushLogistics(LogisticsEnum.LOGISTICS_OK_CONFIRMED.getCode());
        salesOrderService.updateById(salesOrder);

        if (salesOrder.getCreateStackStatus().equals(YesNoEnum.NO.getCode())) {
            stackStatus = "确定成功";
        } else {
            stackStatus = "确定成功(提单已经生成装车单)";
        }
        return stackStatus;
    }

//        //查询实际装车明细
//        List<GetActualDeliveryDet> getActualDeliveryDetList = getActualDeliveryDetService.selectByMainId(id);
//
//        //根据产品编码
//        Map<String, GetActualDeliveryDet> getActualDeliveryDetMap = newHashMap();
//        for (GetActualDeliveryDet getActualDeliveryDet : getActualDeliveryDetList) {
//            getActualDeliveryDetMap.put(getActualDeliveryDet.getProductCode(), getActualDeliveryDet);
//        }
//
//        //获取提单数据
//        SalesOrder salesOrder = salesOrderService.getById(getActualDelivery.getMainId());
//        salesOrder.setShipDate(getActualDelivery.getStockOutDate());
//
//        //获取提单明细数据
//        QueryWrapper<SalesOrderDet> salesOrderDetQueryWrapper = new QueryWrapper<>();
//        salesOrderDetQueryWrapper.lambda().eq(SalesOrderDet::getParentId, salesOrder.getId());
//        List<SalesOrderDet> salesOrderDetList = salesOrderDetService.list(salesOrderDetQueryWrapper);
//
//        //修改提单明细件数与重量
//        for (SalesOrderDet salesOrderDet : salesOrderDetList) {
//            if (StrUtil.isBlank(salesOrderDet.getProductCode())) {
//                log.error(StrUtil.format("没有产品编码,提单id：" + salesOrder.getId()));
//                throw new ScmException(StrUtil.format("没有产品编码"));
//            }
//            GetActualDeliveryDet getActualDeliveryDet = getActualDeliveryDetMap.get(salesOrderDet.getProductCode());
//            salesOrderDet.setQty(getActualDeliveryDet.getQuantity().doubleValue());
//            salesOrderDet.setWeight(getActualDeliveryDet.getWeight().doubleValue());
//        }
//
//        //生成装车单
//        salesOrderService.updateMain(salesOrder, salesOrderDetList);

    @Transactional(rollbackFor = Exception.class)
    public void noStack(String id) {

        GetActualDelivery getActualDelivery = getById(id);
        if (getActualDelivery.getStackStatusType().equalsIgnoreCase(YesNoEnum.NO.getCode())) {
            if (getActualDelivery.getConfirmStack().equalsIgnoreCase(YesNoEnum.NO.getCode())) {
                getActualDelivery.setConfirmStack(YesNoEnum.YES.getCode());
                updateById(getActualDelivery);
            } else if (getActualDelivery.getConfirmStack().equalsIgnoreCase(YesNoEnum.YES.getCode())) {
                throw new ScmException(StrUtil.format("该数据已确认，无需再次点击"));
            }
        } else {
            throw new ScmException("该数据为确定装车数据");

        }


    }

    //    @Transactional(rollbackFor = Exception.class)
    public void pushAllSalesOrder() {

        QueryWrapper<SalesOrder> salesOrderQueryWrapper = new QueryWrapper<>();
        salesOrderQueryWrapper.lambda().eq(SalesOrder::getTenantId, 2)
                .eq(SalesOrder::getStockId, "1")
                .eq(SalesOrder::getPushLogistics, LogisticsEnum.WAIT_PUSH.getCode())
                .between(SalesOrder::getCreateTime,
                        DateUtils.clearHourMinuteSecond(DateUtils.getDaysBeforeAndAfterTheCurrentTime(-3)), DateUtils.getDateLastTime(new Date()));

        List<SalesOrder> salesOrderList = salesOrderService.list(salesOrderQueryWrapper);
        if (ObjectUtil.isNotEmpty(salesOrderList)) {
            for (SalesOrder salesOrder : salesOrderList) {
                try {
                    String type = WmsTypeEnum.PUSH_ORDER.getCode();
                    salesOrderService.pushLogistics(salesOrder.getId(), type);
                } catch (Exception e) {
                    salesOrder.setPushLogistics(LogisticsEnum.LOGISTICS_ERR.getCode());
                    salesOrder.setFailureCause(e.getMessage());
                    salesOrderService.updateById(salesOrder);
                }
            }
        }
    }
}
