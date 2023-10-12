package com.dongxin.scm.sm.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.common.service.LockService;
import com.dongxin.scm.enums.SerialNoEnum;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.om.entity.PrepareOrderDetVO;
import com.dongxin.scm.om.entity.PrepareOrderVO;
import com.dongxin.scm.om.util.Constants;
import com.dongxin.scm.sm.dto.ShippingManagementDTO;
import com.dongxin.scm.sm.entity.*;
import com.dongxin.scm.sm.enums.ShippingMatStatusEnum;
import com.dongxin.scm.sm.enums.StockTypeEnum;
import com.dongxin.scm.sm.mapper.PrepareOrderMapper;
import com.dongxin.scm.utils.BigDecimalUtils;
import com.dongxin.scm.utils.ScmBeanUtils;
import com.dongxin.scm.utils.SerialNoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlProcessor;
import org.springframework.stereotype.Service;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;
import static org.hibernate.validator.internal.util.CollectionHelper.newHashSet;

/**
 * @Description: 预开单信息表
 * @Author: jeecg-boot
 * @Date: 2021-10-31
 * @Version: V1.0
 */
@Service
public class PrepareOrderService extends BaseService<PrepareOrderMapper, PrepareOrder> {

    @Autowired
    ShippingManagementService shippingManagementService;

    @Autowired
    private MatService matService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private StockService stockService;

    @Autowired
    private LockService lockService;

    @Autowired
    private WarehouseWarrantService warehouseWarrantService;

    @Transactional(rollbackFor = Exception.class)
    public void prepareOrder(ShippingManagementDTO shippingManagementDTO) {
        List<ShippingManagement> list = shippingManagementDTO.getList();
        String remark = shippingManagementDTO.getRemark();
        String customerId = shippingManagementDTO.getCustomerId();
        String destination = shippingManagementDTO.getDestination();
        String prepareOrderId;
        if (StrUtil.isBlank(shippingManagementDTO.getPrepareOrderNo())) {
            PrepareOrder prepareOrder = new PrepareOrder();
            String prepareOrderNo = SerialNoUtil.getSerialNo(SerialNoEnum.PREPARE_ORDER_NO);
            prepareOrder.setCustomerId(customerId);
            prepareOrder.setRemarks(remark);
            prepareOrder.setPrepareOrderStatus(YesNoEnum.NO.getCode());
            prepareOrder.setStockStatus(StockTypeEnum.ON_BOARD.getCode());
            prepareOrder.setPrepareOrderNo(prepareOrderNo);
            prepareOrder.setDestination(destination);
            prepareOrder.setDispatchShipBillNo(list.get(0).getDispatchShipBillNo());
            prepareOrder.setShipNo(list.get(0).getShipNo());
            save(prepareOrder);
            prepareOrderId = prepareOrder.getId();
        } else {
            PrepareOrder prepareOrder = queryByPrepareOrderNo(shippingManagementDTO.getPrepareOrderNo());
            remark = prepareOrder.getRemarks();
            destination = prepareOrder.getDestination();
            if (ObjectUtil.isNull(prepareOrder)) {
                throw new ScmException("未查询到材料号");
            }
            prepareOrderId = prepareOrder.getId();
        }

        for (ShippingManagement item : list) {
            item.setRemark(remark);
            item.setDestinationName(destination);
            item.setPrepareOrderId(prepareOrderId);
            item.setMatStatus(ShippingMatStatusEnum.PREPARE_ORDER.getCode());
            item.setPrepareStatus("1");

        }
        shippingManagementService.updateBatchById(list);
        updateCarNos(prepareOrderId);
    }

    public void updatePrepareOrderStatusById(String prepareOrderId, YesNoEnum yesNoEnum) {
        if (StrUtil.isBlank(prepareOrderId)) {
            throw new ScmException("预开单id为空");
        }
        PrepareOrder prepareOrder = getById(prepareOrderId);
        prepareOrder.setPrepareOrderStatus(yesNoEnum.getCode());
        updateById(prepareOrder);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String id) {
        PrepareOrder prepareOrder = getById(id);
        if(prepareOrder.getStockStatus().equals(StockTypeEnum.ON_STOCK.getCode())) {
            throw new ScmException("请先取消入库再删除！！！");
        }
        removeById(id);
        List<ShippingManagement> shippingManagementList = shippingManagementService.queryPrepareOrderDetById(id);
        for (ShippingManagement shippingManagement : shippingManagementList) {
            shippingManagementService.delConnectFromPrepareOrder(shippingManagement.getId());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delConnectFromPrepareOrder(String shippingManagementId) {
        ShippingManagement shippingManagement = shippingManagementService.getById(shippingManagementId);
        shippingManagementService.delConnectFromPrepareOrder(shippingManagement);
        shippingManagementService.updateById(shippingManagement);
        updateCarNos(shippingManagement.getPrepareOrderId());
    }

    private void updateCarNos(String prepareOrderId) {
        PrepareOrder prepareOrder = getById(prepareOrderId);

        List<ShippingManagement> shippingManagementList = shippingManagementService.getByPrepareOrderId(prepareOrderId);

        String carNos = shippingManagementService.getCarNos(shippingManagementList);

        if (StrUtil.isNotEmpty(carNos)) {
            prepareOrder.setCarNos(carNos);
            updateById(prepareOrder);
        }
    }

    @Override
    public boolean removeByIds(Collection idList) {
        for (Object o : idList) {
            deleteById(String.valueOf(o));
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public void putInStorageBatch(List<String> prepareOrderIds) {
        for (String prepareOrderId : prepareOrderIds) {
            putInStorage(prepareOrderId);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void putInStorage(String prepareOrderId) {

        LockInfo lockInfo = lockService.lock(prepareOrderId);

        try {
            List<ShippingManagement> shippingManagementList = shippingManagementService
                    .queryPrepareOrderDetById(prepareOrderId);
            List<String> matNos = newArrayList();

            for (ShippingManagement shippingManagement : shippingManagementList) {
                matNos.add(shippingManagement.getMatNo());
                shippingManagement.setMatStatus(ShippingMatStatusEnum.IN_STOCK.getCode());
            }
            shippingManagementService.updateBatchById(shippingManagementList);

            List<Mat> matList = matService.getByMatNo(matNos);

            if (matList.size() != shippingManagementList.size()) {
                log.error(StrUtil.format("船运单材料数与在库材料数不一致，请确认,prepareOrderId:{},matList size:{}, shippingManagementList size:{}",
                        prepareOrderId, matList.size(), shippingManagementList.size()));
                throw new ScmException("船运单材料数与在库材料数不一致，请确认");
            }
            PrepareOrder prepareOrder = getById(prepareOrderId);

            if (prepareOrder.getStockStatus().equalsIgnoreCase(StockTypeEnum.ON_STOCK.getCode())) {
                throw new ScmException("预开单已入库，请勿重复入库");
            }
            Stock stock = stockService.getByName(prepareOrder.getDestination());

            String warehouseWarrantId = matService.updateWarehouse(matList, stock.getId(), prepareOrder.getRemarks(), "", null);

            //设置预开单入库状态
            prepareOrder.setStockStatus(StockTypeEnum.ON_STOCK.getCode());
            prepareOrder.setWarehouseWarrantId(warehouseWarrantId);
            updateById(prepareOrder);
        } finally {
            lockService.releaseLock(lockInfo);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void cancelStorage(String prepareOrderId) {
        PrepareOrder prepareOrder = getById(prepareOrderId);


        if (null == prepareOrder) {
            log.error(StrUtil.format("参数异常，请检查prepareOrderId {}", prepareOrderId));
            throw new ScmException("参数异常，请检查");
        }

        if (!prepareOrder.getStockStatus().equalsIgnoreCase(StockTypeEnum.ON_STOCK.getCode())) {
            log.error(StrUtil.format("提单未入库，请检查prepareOrderId {}", prepareOrderId));
            throw new ScmException("提单未入库");
        }

        if (YesNoEnum.YES.getCode().equalsIgnoreCase(prepareOrder.getPrepareOrderStatus())) {
            log.error(StrUtil.format("提单已开单，请先删除提单 prepareOrderId {}", prepareOrderId));
            throw new ScmException("提单已开单，请先删除提单");
        }

        warehouseWarrantService.subduction(prepareOrder.getWarehouseWarrantId(), true);

        prepareOrder.setStockStatus(StockTypeEnum.ON_BOARD.getCode());
        updateById(prepareOrder);

        List<ShippingManagement> shippingManagementList = shippingManagementService
                .queryPrepareOrderDetById(prepareOrderId);

        List<String> matNos = newArrayList();

        for (ShippingManagement shippingManagement : shippingManagementList) {
            shippingManagement.setMatStatus(ShippingMatStatusEnum.PREPARE_ORDER.getCode());
            matNos.add(shippingManagement.getMatNo());
        }

        List<Mat> matList = matService.getByMatNo(matNos);

        for (Mat mat : matList) {
            mat.setStockType(StockTypeEnum.ON_BOARD.getCode());
        }
        matService.updateBatchById(matList);
        shippingManagementService.updateBatchById(shippingManagementList);
    }

    public void deleteSalesOrder(String prepareOrderId) {
        PrepareOrder prepareOrder = getById(prepareOrderId);
        if (null == prepareOrder) {
            log.error("删除提单错误:prepareOrderId:" + prepareOrderId);
            throw new ScmException("删除提单错误");

        }
        prepareOrder.setPrepareOrderStatus(YesNoEnum.NO.getCode());
        updateById(prepareOrder);
    }

    public List<PrepareOrderDetVO> generatePrepareOrderVO(String prepareOrderId) {

        List<PrepareOrderDetVO> detVOList = newArrayList();

        List<ShippingManagement> shippingManagementList = shippingManagementService
                .queryPrepareOrderDetById(prepareOrderId);

        List<String> stockIds = newArrayList();
        for (ShippingManagement shippingManagement : shippingManagementList) {
            String matNo = shippingManagement.getMatNo();

            Inventory inventory = inventoryService.getByMatNo(matNo);

            if (inventory == null) {
                throw new ScmException(StrUtil.format("材料号:{}未入库，请先进行入库后再开单", shippingManagement.getMatNo()));
            }

            PrepareOrderDetVO detVO = new PrepareOrderDetVO();

            ScmBeanUtils.copyProperties(inventory, detVO);

            detVO.setOrderLen(inventory.getMatLen());
            detVO.setOrderThick(inventory.getMatThick());
            detVO.setOrderWidth(inventory.getMatWidth());
            detVO.setQty(1.0);
            detVO.setWeight(shippingManagement.getWeight());
            detVO.setInventoryId(inventory.getId());
            detVO.setStockId(inventory.getStockHouseId());
            detVO.setStockLocation(inventory.getStockLocation());
            detVO.setBasicPrice(shippingManagement.getPrice());
            detVO.setPrice(shippingManagement.getPrice());
            detVO.setTotalPrice(BigDecimalUtils.multiply(shippingManagement.getPrice(), detVO.getWeight()));
            detVO.setWtMode(inventory.getWtMode());
            stockIds.add(inventory.getStockHouseId());
            detVOList.add(detVO);
        }

        Map<String, String> stockIdAndName = stockService.getStockHouseIdMap(CollectionUtil.distinct(stockIds));

        for (PrepareOrderDetVO prepareOrderDetVO : detVOList) {
            prepareOrderDetVO.setStockName(stockIdAndName.get(prepareOrderDetVO.getStockId()));
        }

        return detVOList;
    }

    public void checkoutDetailsCount(String prepareOrderId, int orderDetCount) {
        if (StrUtil.isNotEmpty(prepareOrderId)) {
            PrepareOrder prepareOrder = getById(prepareOrderId);

            if (prepareOrder == null) {
                log.error(StrUtil.format("checkoutDetailsCount error,prepareOrderId:{}", prepareOrderId));
                throw new ScmException("参数错误，未查询到对应的预开单信息");
            }
            List<ShippingManagement> shippingManagementList = shippingManagementService.queryPrepareOrderDetById(prepareOrderId);

            if (shippingManagementList.size() != orderDetCount) {
                throw new ScmException("提单明细数量与预开单明细数量不一致，请在预开单界面编辑明细后再生成提单");
            }
        }
    }

    public PrepareOrder queryByPrepareOrderNo(String prepareOrderNo) {
        QueryWrapper<PrepareOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(PrepareOrder::getPrepareOrderNo, prepareOrderNo);
        return getOne(queryWrapper);
    }


    @Transactional(rollbackFor = Exception.class)
    public void updatePrepareOrderPrice(String prepareOrderIds, BigDecimal amount) {

        List<String> prepareOrderIdList = Arrays.asList(prepareOrderIds.split(","));

        List<PrepareOrder> prepareOrderList = listByIds(prepareOrderIdList);

        if (CollectionUtil.isEmpty(prepareOrderList) || amount == null) {
            throw new ScmException("参数错误，请检查");
        }

        for (PrepareOrder prepareOrder : prepareOrderList) {
            List<ShippingManagement> shippingManagementList = shippingManagementService.getByPrepareOrderId(prepareOrder.getId());

            for (ShippingManagement shippingManagement : shippingManagementList) {
                shippingManagement.setPrice(shippingManagement.getPrice().add(amount));
            }
            shippingManagementService.updateBatchById(shippingManagementList);
        }

    }


}
