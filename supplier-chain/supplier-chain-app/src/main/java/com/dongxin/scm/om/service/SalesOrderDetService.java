package com.dongxin.scm.om.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.bd.entity.CompanyTenant;
import com.dongxin.scm.bd.entity.CompanyTenantDet;
import com.dongxin.scm.bd.enums.TheoryAndNoTheoryEnum;
import com.dongxin.scm.bd.mapper.CompanyTenantDetMapper;
import com.dongxin.scm.bd.mapper.CompanyTenantMapper;
import com.dongxin.scm.enums.ProdClassCodeEnum;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.entity.SettleImport;
import com.dongxin.scm.fm.service.SettleImportService;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.entity.SalesOrderDet;
import com.dongxin.scm.om.enums.WtModeEnum;
import com.dongxin.scm.om.mapper.SalesOrderDetMapper;
import com.dongxin.scm.om.mapper.SalesOrderMapper;
import com.dongxin.scm.sm.entity.GetActualDelivery;
import com.dongxin.scm.sm.entity.GetActualDeliveryDet;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.entity.Stack;
import com.dongxin.scm.sm.mapper.StackMapper;
import com.dongxin.scm.sm.service.GetActualDeliveryDetService;
import com.dongxin.scm.sm.service.GetActualDeliveryService;
import com.dongxin.scm.sm.service.InventoryService;
import com.dongxin.scm.utils.BigDecimalUtils;
import com.dongxin.scm.utils.DoubleUtils;
import org.jeecg.common.system.base.service.BaseService;
import org.jeecg.config.mybatis.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.newArrayList;
import static cn.hutool.core.collection.CollUtil.newHashMap;

/**
 * @Description: 销售单明细表
 * @Author: jeecg-boot
 * @Date: 2020-11-23
 * @Version: V1.0
 */
@Service
public class SalesOrderDetService extends BaseService<SalesOrderDetMapper, SalesOrderDet> {

    @Resource
    private SalesOrderDetMapper salesOrderDetMapper;
    @Resource
    private SalesOrderMapper salesOrderMapper;
    @Resource
    private StackMapper stackMapper;
    @Autowired
    private InventoryService inventoryService;
    @Resource
    private CompanyTenantMapper companyTenantMapper;
    @Resource
    private CompanyTenantDetMapper companyTenantDetMapper;
    @Autowired
    private SalesOrderService salesOrderService;
    @Autowired
    private SettleImportService settleImportService;
    @Autowired
    private GetActualDeliveryService getActualDeliveryService;
    @Autowired
    private GetActualDeliveryDetService getActualDeliveryDetService;


    public List<SalesOrderDet> selectByMainId(String mainId) {
        return salesOrderDetMapper.selectByMainId(mainId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delStackDet(String id) {
        //判断是否生成过提单
        SalesOrderDet salesOrderDet = getById(id);
        QueryWrapper<SalesOrder> salesOrderQueryWrapper = new QueryWrapper<>();
        salesOrderQueryWrapper.lambda().eq(SalesOrder::getId, salesOrderDet.getParentId());
        SalesOrder salesOrder = salesOrderMapper.selectOne(salesOrderQueryWrapper);
        QueryWrapper<Stack> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(Stack::getSaleBillNo, salesOrder.getSaleBillNo());
        Stack stack = stackMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(stack)) {
            throw new ScmException("该提单已经生成过装车单，不能删除提单明细！");
        }

        //删除提单明细退量
        Inventory inventory = inventoryService.getById(salesOrderDet.getInventoryId());
        //GD螺纹钢

        inventoryService.deductionAvailable(inventory, -salesOrderDet.getQty(), -salesOrderDet.getWeight());
        salesOrderDetMapper.deleteById(id);
        salesOrderService.updateYRMCanLadingBill(salesOrder.getCustomerId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void delBatchStackDet(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            delStackDet((String) id);
        }
    }

    public List<SalesOrderDet> getListByParentId(String parentId) {
        if (StrUtil.isBlank(parentId)) {
            throw new ScmException("parentId 参数为空");
        }
        QueryWrapper<SalesOrderDet> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SalesOrderDet::getParentId, parentId);
        return list(queryWrapper);
    }

    //查询公司对照信息表运费配置项，判断登录账户是否配置
    public Boolean queryDeliverExpense() {
        //获取登录账户的tenantId
        String tenantId = TenantContext.getTenant();
        //获取CompanyTenantDet的parentId
        QueryWrapper<CompanyTenantDet> companyTenantDetQueryWrapper = new QueryWrapper<>();
        companyTenantDetQueryWrapper.lambda().eq(CompanyTenantDet::getTenantCode, tenantId);
        CompanyTenantDet companyTenantDet = companyTenantDetMapper.selectOne(companyTenantDetQueryWrapper);
        if (ObjectUtil.isNotNull(companyTenantDet)) {
            //获取CompanyTenant的运费配置项字段
            QueryWrapper<CompanyTenant> companyTenantQueryWrapper = new QueryWrapper<>();
            companyTenantQueryWrapper.lambda().eq(CompanyTenant::getId, companyTenantDet.getParentId());
            CompanyTenant companyTenant = companyTenantMapper.selectOne(companyTenantQueryWrapper);
            return companyTenant.getSalesDetDeliverExpense().equals(YesNoEnum.YES.getCode());
        }
        return false;
    }

    //查询公司对照信息表加价配置项，判断登录账户是否配置
    public Boolean queryAddPrice() {
        String tenantId = TenantContext.getTenant();
        //获取CompanyTenantDet的parentId
        QueryWrapper<CompanyTenantDet> companyTenantDetQueryWrapper = new QueryWrapper<>();
        companyTenantDetQueryWrapper.lambda().eq(CompanyTenantDet::getTenantCode, tenantId);
        CompanyTenantDet companyTenantDet = companyTenantDetMapper.selectOne(companyTenantDetQueryWrapper);
        if (ObjectUtil.isNotNull(companyTenantDet)) {
            //获取CompanyTenant的运费配置项字段
            QueryWrapper<CompanyTenant> companyTenantQueryWrapper = new QueryWrapper<>();
            companyTenantQueryWrapper.lambda().eq(CompanyTenant::getId, companyTenantDet.getParentId());
            CompanyTenant companyTenant = companyTenantMapper.selectOne(companyTenantQueryWrapper);
            return companyTenant.getSalesDetAddPrice().equals(YesNoEnum.YES.getCode());
        }
        return false;
    }

    public Boolean queryIfShowTheoryWeight() {
        String tenantId = TenantContext.getTenant();
        //获取CompanyTenantDet的parentId
        QueryWrapper<CompanyTenantDet> companyTenantDetQueryWrapper = new QueryWrapper<>();
        companyTenantDetQueryWrapper.lambda().eq(CompanyTenantDet::getTenantCode, tenantId);
        CompanyTenantDet companyTenantDet = companyTenantDetMapper.selectOne(companyTenantDetQueryWrapper);
        if (ObjectUtil.isNotNull(companyTenantDet)) {
            //获取CompanyTenant的运费配置项字段
            QueryWrapper<CompanyTenant> companyTenantQueryWrapper = new QueryWrapper<>();
            companyTenantQueryWrapper.lambda().eq(CompanyTenant::getId, companyTenantDet.getParentId());
            CompanyTenant companyTenant = companyTenantMapper.selectOne(companyTenantQueryWrapper);
            if (companyTenant.getSalesDetTheoryWeight().equals(TheoryAndNoTheoryEnum.DISPLAY.getCode())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 广东公司需求，根据导入的实际数据生成装车单
     */
    public List<SalesOrderDet> updateSalesOrderDetList(List<SalesOrderDet> salesOrderDetList, String salesOrderId) {

        //获取导入结算数据
        SalesOrder salesOrder = salesOrderService.getById(salesOrderId);
        if(ObjectUtil.isNull(salesOrder)) {
            return null;
        }
        QueryWrapper<SettleImport> settleImportQueryWrapper = new QueryWrapper<>();
        settleImportQueryWrapper.lambda().eq(SettleImport::getSaleBillNo, salesOrder.getSaleBillNo());
        List<SettleImport> settleImportList = settleImportService.list(settleImportQueryWrapper);

        //获取到数据则进行操作
        if (CollectionUtil.isNotEmpty(settleImportList)) {
            //按照规格与牌号进行分组
            Map<String, List<SettleImport>> settleImportMap = settleImportList.stream().collect(
                    Collectors.groupingBy(
                            score -> score.getCustMatSpecs() + score.getSgSign()
                    ));

            for (SalesOrderDet salesOrderDet : salesOrderDetList) {

                //按照产品大类来分类
                String groupIdentification = "";
                if (salesOrderDet.getProdClassCode().equals(ProdClassCodeEnum.B.getValue())) {
                    groupIdentification = DoubleUtils.doubleTrans(salesOrderDet.getOrderThick()) + "*" + DoubleUtils.doubleTrans(salesOrderDet.getOrderLen() / 1000) + salesOrderDet.getSgSign();
                }
                if (salesOrderDet.getProdClassCode().equals(ProdClassCodeEnum.D.getValue())) {
                    groupIdentification = DoubleUtils.doubleTrans(salesOrderDet.getOrderThick()) + salesOrderDet.getSgSign();
                }
                List<SettleImport> mergeSettleImportList = settleImportMap.get(groupIdentification);
                BigDecimal number = BigDecimal.ZERO;
                BigDecimal weight = BigDecimal.ZERO;

                if (CollectionUtil.isNotEmpty(mergeSettleImportList)) {
                    for (SettleImport settleImport : mergeSettleImportList) {
                        number = number.add(BigDecimal.valueOf(settleImport.getQty()));
                        weight = weight.add(BigDecimal.valueOf(settleImport.getWeight()));
                    }
                }
                if (CollectionUtil.isEmpty(mergeSettleImportList)) {
                    number = number.add(BigDecimal.valueOf(salesOrderDet.getQty()));
                    weight = weight.add(BigDecimal.valueOf(salesOrderDet.getWeight()));
                }

                BigDecimal orderOriginWeight = BigDecimal.valueOf(salesOrderDet.getWeight());

                salesOrderDet.setQty(number.doubleValue());
                salesOrderDet.setWeight(weight.doubleValue());
                salesOrderDet.setTotalPrice(BigDecimalUtils.multiply(salesOrderDet.getPrice(), weight));

                salesOrderDet.setTheoryWeight(BigDecimal.valueOf(salesOrderDet.getWeight()));
                salesOrderDet.setTheoryPrice(salesOrderDet.getPrice());

                //如理计则需要换算理论重量，理论单价
                if (salesOrderDet.getWtMode().equals(WtModeEnum.WEIGHT_MANAGEMENT.getCode())) {
                    //计算理论重量，理论单价
                    BigDecimal theoryWeight = BigDecimalUtils.multiplyKeep3DecimalPlaces(salesOrderDet.getMatTheoryWt(), salesOrderDet.getQty());
                    salesOrderDet.setTheoryWeight(theoryWeight);
                    BigDecimal totalPrice = BigDecimalUtils.multiply(salesOrderDet.getPrice(), salesOrderDet.getWeight());
                    salesOrderDet.setTheoryPrice(BigDecimalUtils.divide(totalPrice, salesOrderDet.getTheoryWeight()));
                    salesOrderDet.setTotalPrice(BigDecimalUtils.multiply(salesOrderDet.getTheoryPrice(), salesOrderDet.getTheoryWeight()));
                } else {
                    salesOrderDet.setTheoryWeight(orderOriginWeight);
                }
            }
        } else {
            for (SalesOrderDet salesOrderDet : salesOrderDetList) {
                if ("12".equalsIgnoreCase(TenantContext.getTenant())
                        && (ProdClassCodeEnum.B.getValue().equalsIgnoreCase(salesOrderDet.getProdClassCode()))) {
                    BigDecimal theoryWeight = BigDecimalUtils.multiplyKeep3DecimalPlaces(salesOrderDet.getMatTheoryWt(), salesOrderDet.getQty());
                    salesOrderDet.setTheoryWeight(theoryWeight);
                } else {
                    salesOrderDet.setTheoryWeight(BigDecimal.valueOf(salesOrderDet.getWeight()));
                }
                salesOrderDet.setTheoryPrice(salesOrderDet.getPrice());
            }
        }
        return salesOrderDetList;
    }

    /**
     * 物流园提单根据物流园装车实绩生成装车单
     */
    public List<SalesOrderDet> replaceByWmsActual(List<SalesOrderDet> salesOrderDetList, String salesOrderId) {
        //获取导入结算数据

        QueryWrapper<GetActualDelivery> queryGetActualDeliveryWrapper = new QueryWrapper<>();
        queryGetActualDeliveryWrapper.lambda().eq(GetActualDelivery::getMainId, salesOrderId)
                .eq(GetActualDelivery::getConfirmStack, YesNoEnum.YES.getCode());

        GetActualDelivery getActualDelivery = getActualDeliveryService.getOne(queryGetActualDeliveryWrapper);

        if (null != getActualDelivery) {

            List<GetActualDeliveryDet> getActualDeliveryDetList = getActualDeliveryDetService.selectByMainId(getActualDelivery.getId());


            //获取到数据则进行操作
            if (CollectionUtil.isNotEmpty(getActualDeliveryDetList)) {

                Map<String, BigDecimal> qtyMap = new HashMap<>();
                Map<String, BigDecimal> weightMap = new HashMap<>();

                for (GetActualDeliveryDet det : getActualDeliveryDetList) {
                    qtyMap.merge(det.getDetailId(), det.getQuantity(), BigDecimal::add);
                    weightMap.merge(det.getDetailId(), det.getWeight(), BigDecimal::add);
                }

                for (SalesOrderDet salesOrderDet : salesOrderDetList) {
                    salesOrderDet.setQty(qtyMap.get(salesOrderDet.getId()).doubleValue());
                    salesOrderDet.setWeight(weightMap.get(salesOrderDet.getId()).doubleValue());
                    salesOrderDet.setTotalPrice(BigDecimalUtils.multiply(salesOrderDet.getPrice(), salesOrderDet.getWeight()));
                    salesOrderDet.setFleetTotalPrice(BigDecimalUtils.multiply(salesOrderDet.getFleetDeliverExpense(), salesOrderDet.getWeight()));
                    salesOrderDet.setDeliverTotalPrice(BigDecimalUtils.multiply(salesOrderDet.getDeliverExpense(), salesOrderDet.getWeight()));
                }
            }
        }
        return salesOrderDetList;
    }


    public Boolean queryIfShowTheoryPrice() {
        String tenantId = TenantContext.getTenant();
        //获取CompanyTenantDet的parentId
        QueryWrapper<CompanyTenantDet> companyTenantDetQueryWrapper = new QueryWrapper<>();
        companyTenantDetQueryWrapper.lambda().eq(CompanyTenantDet::getTenantCode, tenantId);
        CompanyTenantDet companyTenantDet = companyTenantDetMapper.selectOne(companyTenantDetQueryWrapper);
        if (ObjectUtil.isNotNull(companyTenantDet)) {
            //获取CompanyTenant的运费配置项字段
            QueryWrapper<CompanyTenant> companyTenantQueryWrapper = new QueryWrapper<>();
            companyTenantQueryWrapper.lambda().eq(CompanyTenant::getId, companyTenantDet.getParentId());
            CompanyTenant companyTenant = companyTenantMapper.selectOne(companyTenantQueryWrapper);
            return companyTenant.getSalesDetTheoryPrice().equals(TheoryAndNoTheoryEnum.DISPLAY.getCode());
        }
        return false;
    }

    //查询公司对照信息表基础单价配置项，判断登录账户是否配置
    public Boolean queryIfShowBasicPrice() {
        String tenantId = TenantContext.getTenant();
        //获取CompanyTenantDet的parentId
        QueryWrapper<CompanyTenantDet> companyTenantDetQueryWrapper = new QueryWrapper<>();
        companyTenantDetQueryWrapper.lambda().eq(CompanyTenantDet::getTenantCode, tenantId);
        CompanyTenantDet companyTenantDet = companyTenantDetMapper.selectOne(companyTenantDetQueryWrapper);
        if (ObjectUtil.isNotNull(companyTenantDet)) {
            //获取CompanyTenant的基础单价配置项字段
            QueryWrapper<CompanyTenant> companyTenantQueryWrapper = new QueryWrapper<>();
            companyTenantQueryWrapper.lambda().eq(CompanyTenant::getId, companyTenantDet.getParentId());
            CompanyTenant companyTenant = companyTenantMapper.selectOne(companyTenantQueryWrapper);
            if (companyTenant.getSalesDetBasicPrice().equals(YesNoEnum.YES.getCode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据提货委托单id查提单明细
     *
     * @param goodEntrustmentLetterId
     * @return
     */
    public List<SalesOrderDet> selectByGoodEntrustmentLetterId(String goodEntrustmentLetterId) {
        return salesOrderDetMapper.selectByGoodEntrustmentLetterId(goodEntrustmentLetterId);
    }

    /**
     *
     * @return
     */
    public List<String> getUniqueSaleOrderDet(List<String> salesOrderNos) {

        List<String> result = newArrayList();
        if (CollectionUtil.isEmpty(salesOrderNos)) {
            return result;
        }
        QueryWrapper<SalesOrder> queryWrapper = new QueryWrapper<>();

        queryWrapper.lambda().in(SalesOrder::getSaleBillNo, salesOrderNos);

        List<SalesOrder> salesOrderList = salesOrderService.list(queryWrapper);



        List<String> existOrderNos = salesOrderList.stream().map(SalesOrder::getSaleBillNo).collect(Collectors.toList());

        List<String> notExistOrderNos = salesOrderNos.stream().filter(i -> !existOrderNos.contains(i)).collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(notExistOrderNos)) {
            throw new ScmException("单号不存在" + notExistOrderNos.get(0));
        }


        for (SalesOrder salesOrder : salesOrderList) {
            List<SalesOrderDet> detList = getListByParentId(salesOrder.getId());

            for (SalesOrderDet det : detList) {
                if (ProdClassCodeEnum.B.getValue().equalsIgnoreCase(det.getProdClassCode())) {
                    result.add(salesOrder.getSaleBillNo() + DoubleUtils.doubleTrans(det.getOrderThick()) + "*" + DoubleUtils.doubleTrans(det.getOrderLen() / 1000) + det.getSgSign());
                } else if (ProdClassCodeEnum.D.getValue().equalsIgnoreCase(det.getProdClassCode())) {
                    result.add(salesOrder.getSaleBillNo() + DoubleUtils.doubleTrans(det.getOrderThick())+ det.getSgSign());
                }
            }

        }

        return result;


    }
}
