package com.dongxin.scm.sm.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dongxin.scm.bd.entity.CompanyTenant;
import com.dongxin.scm.bd.entity.CompanyTenantDet;
import com.dongxin.scm.bd.mapper.CompanyTenantDetMapper;
import com.dongxin.scm.bd.mapper.CompanyTenantMapper;
import com.dongxin.scm.cm.entity.CustomerConfiguration;
import com.dongxin.scm.cm.service.CustomerConfigurationService;
import com.dongxin.scm.cm.service.SalesmanInfoService;
import com.dongxin.scm.common.service.LockService;
import com.dongxin.scm.enums.AddEditEnum;
import com.dongxin.scm.enums.ProdClassCodeEnum;
import com.dongxin.scm.enums.SerialNoEnum;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.dto.FundPoolDTO;
import com.dongxin.scm.fm.entity.FundDetail;
import com.dongxin.scm.fm.entity.SettleInfo;
import com.dongxin.scm.fm.enums.InvoiceStatusEnum;
import com.dongxin.scm.fm.enums.SettleStatusEnum;
import com.dongxin.scm.fm.enums.StatementStateEnum;
import com.dongxin.scm.fm.service.*;
import com.dongxin.scm.home.entity.SettleInfoVO;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.entity.SalesOrderDet;
import com.dongxin.scm.om.enums.PaymentFlagEnum;
import com.dongxin.scm.om.mapper.SalesOrderMapper;
import com.dongxin.scm.om.service.*;
import com.dongxin.scm.om.service.ContractDetService;
import com.dongxin.scm.om.service.ContractService;
import com.dongxin.scm.om.service.SalesOrderDetService;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.om.util.Constants;
import com.dongxin.scm.om.vo.EquipmentAndMaterialsNetworkMergeFields;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.entity.Stack;
import com.dongxin.scm.sm.entity.StackDet;
import com.dongxin.scm.sm.enums.StatusEnum;
import com.dongxin.scm.sm.mapper.MatMapper;
import com.dongxin.scm.sm.mapper.StackDetMapper;
import com.dongxin.scm.sm.mapper.StackMapper;
import com.dongxin.scm.sm.vo.OptionVO;
import com.dongxin.scm.sm.vo.SalesOrderDetVO;
import com.dongxin.scm.sm.vo.SelectSettlenInfoVO;
import com.dongxin.scm.sm.vo.StackDetailVO;
import com.dongxin.scm.utils.BigDecimalUtils;
import com.dongxin.scm.utils.DateUtils;
import com.dongxin.scm.utils.ProdClassToSerialNoUtils;
import com.dongxin.scm.utils.SerialNoUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.service.BaseService;
import org.jeecg.config.mybatis.TenantContext;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.impl.SysUserServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.*;

/**
 * @Description: 销售单主表
 * @Author: jeecg-boot
 * @Date: 2020-11-24
 * @Version: V1.0
 */
@Service
public class StackService extends BaseService<StackMapper, Stack> {

    @Resource
    private StackMapper stackMapper;
    @Resource
    private StackDetMapper stackDetMapper;
    @Resource
    private SalesOrderMapper salesOrderMapper;
    @Autowired
    private StackDetService stackDetService;
    @Autowired
    private MatService matService;
    @Autowired
    private SalesOrderDetService salesOrderDetService;
    @Autowired
    private SalesOrderService salesOrderService;
    @Resource
    private MatMapper matMapper;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private FundAccountService fundAccountService;
    @Autowired
    private SalesmanInfoService salesmanInfoService;
    @Autowired
    private ContractDetService contractDetService;
    @Autowired
    private CustomerConfigurationService customerConfigurationService;
    @Autowired
    private ContractService contractService;
    @Autowired
    SysUserServiceImpl sysUserService;
    @Autowired
    FinanceDetailService financeDetailService;
    @Autowired
    FundDetailService fundDetailService;
    @Resource
    private CompanyTenantDetMapper companyTenantDetMapper;
    @Resource
    private CompanyTenantMapper companyTenantMapper;
    @Autowired
    private SettleInfoService settleInfoService;
    @Autowired
    private FundService fundService;
    @Autowired
    private LockService lockService;

    @Transactional(rollbackFor = Exception.class)
    public void updateMain(Stack stack, List<StackDet> stackDetList) {
        if (stack.getStackPageUpdate().equals(YesNoEnum.NO.getCode())) {
            SalesOrder salesOrder1 = salesOrderService.getById(stack.getSalesOrderId());

            //修改前的总价 修改后的总价
            BigDecimal previousTotalPrice = BigDecimal.ZERO;
            BigDecimal nowTotalPrice = BigDecimal.ZERO;
            //修改后的总重量
            double totalWeight = 0;


            if (StatusEnum.WRITE_OFF.getCode().equals(stack.getStatus())) {
                throw new ScmException("红冲状态装车单不可操作");
            }


            //计算 修改前的总价 修改后的总价
            int count = 1;
            for (StackDet stackDet : stackDetList) {
                if (stackDet.getQty() <= 0 || stackDet.getWeight() <= 0) {
                    throw new ScmException("输入的数量、重量不能小于或等于0");
                }

                //岑海需要编辑基础单价+运费+加价
                if (TenantContext.getTenant().equalsIgnoreCase("2")){
                    //总单价 = 基础单价 + 运费 + 加价
                    BigDecimal totalPrice =  stackDet.getDeliverExpense().add(stackDet.getBasicPrice().add(BigDecimal.valueOf(stackDet.getAddPrice())));

                    if (stackDet.getPrice().compareTo(totalPrice) != 0) {
                        throw new ScmException("第"+count+"条明细单价计算有误，请重试");
                    }
                }

                //合同控量，更新合同明细预交量、已交量，更新合同主表状态
                SalesOrderDet salesOrderDet = new SalesOrderDet();
                salesOrderDet.setId(stackDet.getId());
                salesOrderDet.setContractListNo(stackDet.getContractListNo());
                salesOrderDet.setProdCname(stackDet.getProdCname());
                salesOrderDet.setProdCnameOther(stackDet.getProdCnameOther());
                salesOrderDet.setSgSign(stackDet.getSgSign());
                salesOrderDet.setProdClassCode(stackDet.getProdClassCode());
                salesOrderDet.setWeight(stackDet.getWeight());
                salesOrderDet.setQty(stackDet.getQty());
                salesOrderDet.setParentId(salesOrder1.getId());
                salesOrderDet.setOrderThick(stackDet.getMatThick());
                salesOrderDet.setOrderWidth(stackDet.getMatWidth());
                contractService.contractControl(salesOrderDet, AddEditEnum.EDIT.getCode(), count++);

                StackDet previousDet = stackDetService.getById(stackDet.getId());
                //设置库存id
                stackDet.setInventoryId(previousDet.getInventoryId());

                totalWeight = totalWeight + stackDet.getWeight();

                //未修改前的总价
                previousTotalPrice = previousTotalPrice.add(BigDecimalUtils.multiply(previousDet.getPrice(), previousDet.getWeight()));

                //修改后的总价
                nowTotalPrice = nowTotalPrice.add(BigDecimalUtils.multiply(stackDet.getPrice(), stackDet.getWeight()));

                //处理库存量
                Inventory inventory = inventoryService.getById(previousDet.getInventoryId());
                //计算库存总数
                if (inventory.getAvailableQty() < (stackDet.getQty() - previousDet.getQty())) {
                    throw new ScmException("库存材料可用数量不足！" + "库存总数：" + inventory.getAvailableQty() + "增加量：" + (stackDet.getQty() - previousDet.getQty()));
                }
//                if (inventory.getAvailableWeight() < (stackDet.getWeight() - previousDet.getWeight())) {
//                    throw new ScmException("库存材料可用重量不足！" + "库存重量：" + inventory.getAvailableWeight() + "增加量：" + (stackDet.getWeight() - previousDet.getWeight()));
//                }
                //数量、重量  处理实际量
                //GD螺纹钢按理计出入库
                double qty = stackDet.getQty() - previousDet.getQty();
                double weight = stackDet.getWeight() - inventoryWeight(previousDet);
                inventoryService.deductionPractical(inventory, qty, weight);
                //可销售数量、可销售重量  处理可销售量
                double availableQty = stackDet.getQty() - previousDet.getQty();
                double availableWeight = stackDet.getWeight() - inventoryWeight(previousDet);
                inventoryService.deductionAvailable(inventory, availableQty, availableWeight);
            }

            BigDecimal equipmentTotalPrice = BigDecimal.ZERO;
            if (YesNoEnum.YES.getCode().equalsIgnoreCase(salesOrder1.getEquipmentSuppliesFlag())) {

                //设备物资标识总价计算方式特殊，先将相同价格明细重量汇总，然后乘以价格，最后将不同价格的明细累加得到总价
                Map<EquipmentAndMaterialsNetworkMergeFields, Double> priceAndWeight = newHashMap();

                for (StackDet stackDet : stackDetList) {
                    EquipmentAndMaterialsNetworkMergeFields equipmentAndMaterialsNetworkMergeFields = new EquipmentAndMaterialsNetworkMergeFields();
                    equipmentAndMaterialsNetworkMergeFields.setOldProdCname(stackDet.getOldProdCname());
                    equipmentAndMaterialsNetworkMergeFields.setOrderThick(stackDet.getMatThick());
                    equipmentAndMaterialsNetworkMergeFields.setPrice(stackDet.getPrice());

                    priceAndWeight.merge(equipmentAndMaterialsNetworkMergeFields, stackDet.getWeight(), Double::sum);
                }
                for (Map.Entry<EquipmentAndMaterialsNetworkMergeFields, Double> entry : priceAndWeight.entrySet()) {
                    equipmentTotalPrice = equipmentTotalPrice.add(BigDecimalUtils.multiply(entry.getKey().getPrice(), entry.getValue()));
                }


                BigDecimal firstStackPrice = stackDetList.get(0).getTotalAmount();
                if (previousTotalPrice.compareTo(nowTotalPrice) != 0) {
                    stackDetList.get(0).setTotalAmount(firstStackPrice.add(equipmentTotalPrice.subtract(nowTotalPrice)));
                    nowTotalPrice = equipmentTotalPrice;
                }

            }


            //判断是退款还是扣款
            BigDecimal priceSpread = nowTotalPrice.subtract(previousTotalPrice);
            // 修改来款可用金额  **


            SalesOrder salesOrder = salesOrderService.getById(stack.getSalesOrderId());
            if (ObjectUtil.isEmpty(salesOrder)) {
                throw new ScmException("不存在提单号为" + stack.getSaleBillNo() + "的提单!");
            }

            fundService.stackDeductAndRefund(priceSpread, salesOrder.getCustomerId(), salesOrder);


            //修改 销售单总金额、总重量
            if (YesNoEnum.YES.getCode().equalsIgnoreCase(salesOrder1.getEquipmentSuppliesFlag())) {
                stack.setTotalAmount(equipmentTotalPrice);
            } else {
                stack.setTotalAmount(nowTotalPrice.setScale(2, BigDecimal.ROUND_HALF_UP));
            }

            checkFundAmount(stack);

            stack.setWeight(totalWeight);
            stack.setModifyFlag(YesNoEnum.YES.getCode());
            stackMapper.updateById(stack);

            //2.子表数据重新插入
            if (CollectionUtil.isNotEmpty(stackDetList)) {
                for (StackDet entity : stackDetList) {
                    //外键设置
                    entity.setStackId(stack.getId());
                    stackDetMapper.updateById(entity);
                }
            }

        } else {
            updateById(stack);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void delMain(String id) {

        Stack stack = getById(id);
        LockInfo lockInfo = lockService.lock(stack.getCustomerId());
        try {
            refundAmountAndInventory(id);
            stackDetMapper.deleteByMainId(id);
            stackMapper.deleteById(id);
        } finally {
            lockService.releaseLock(lockInfo);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delBatchMain(List<String> idList) {
        for (String id : idList) {
            delMain(id);
        }
    }

    /**
     * 删除装车单退量
     *
     * @param mainId 装车单id
     */
    @Transactional(rollbackFor = Exception.class)
    public void refundAmountAndInventory(String mainId) {
        Stack stack = stackMapper.selectById(mainId);

        if (StatusEnum.WRITE_OFF.getCode().equals(stack.getStatus())) {
            throw new ScmException("红冲状态不可删除");
        }

        QueryWrapper<SalesOrder> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.lambda().eq(SalesOrder::getSaleBillNo, stack.getSaleBillNo());
        SalesOrder salesOrder1 = salesOrderService.getOne(queryWrapper1);

        //判断销售单是否已结算
        Stack existStack = stackMapper.selectById(mainId);
        if (StrUtil.isNotEmpty(existStack.getSettledId())) {
            //已结算 不允许删除
            throw new ScmException("该销售单已结算，不允许删除！");
        }
        //未结算 退量
        List<StackDet> existStackDets = stackDetMapper.selectByMainId(mainId);
        if (CollectionUtil.isEmpty(existStackDets)) {
            throw new ScmException("未找到对应的提单明细，提单id：" + mainId);
        }


        for (StackDet stackDet : existStackDets) {
            Inventory inventory = inventoryService.getById(stackDet.getInventoryId());
            //GD螺纹钢按实际重量销售，按理计出入库
            double weight = inventoryWeight(stackDet);

            inventoryService.deductionPractical(inventory, -stackDet.getQty(), -weight);
        }
        //合同控量租户ID校验
        QueryWrapper<CustomerConfiguration> customerConfigWrapper = new QueryWrapper<>();
        customerConfigWrapper.lambda().eq(CustomerConfiguration::getTenantId, TenantContext.getTenant());
        List<CustomerConfiguration> customerConfigList = customerConfigurationService.list(customerConfigWrapper);
        if (customerConfigList.size() > 1) {
            log.error(StrUtil.format("当前租户为：{}，合同控量中存在{}条合同控量配置记录！", TenantContext.getTenant(), customerConfigList.size()));
            throw new ScmException(StrUtil.format("当前租户为：{}，合同控量中存在{}条合同控量配置记录！", TenantContext.getTenant(), customerConfigList.size()));
        }

        //校验是否合同控量
        if (CollectionUtil.isNotEmpty(customerConfigList) &&
                customerConfigList.get(0).getFlag().equals(YesNoEnum.YES.getCode())) {
            //减扣合同已交件数（重量），增加预交件数（重量）
            contractDetService.refundPreAndDeliver(existStackDets);
        }

        //校验是否装车单合同控量
        if (CollectionUtil.isNotEmpty(customerConfigList) &&
                customerConfigList.get(0).getStackContractConfiguration().equals(YesNoEnum.YES.getCode())) {
            //减扣合同已交金额（重量）
            contractDetService.refundPriceAndWeight(existStackDets);
        }

        QueryWrapper<SalesOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SalesOrder::getSaleBillNo, existStack.getSaleBillNo());
        SalesOrder salesOrder = salesOrderMapper.selectOne(queryWrapper);
        if (ObjectUtil.isEmpty(salesOrder)) {
            throw new ScmException("未找到对应的提单，提单编号：" + existStack.getSaleBillNo());
        }

        fundService.deleteStackAndRefund(salesOrder.getId());

        if (salesOrder.getPaymentFlag().equalsIgnoreCase(PaymentFlagEnum.PAID.getCode())) {

            QueryWrapper<SalesOrderDet> salesOrderDetQueryWrapper = new QueryWrapper<>();
            salesOrderDetQueryWrapper.lambda().eq(SalesOrderDet::getParentId, salesOrder.getId());
            List<SalesOrderDet> salesOrderDetList = salesOrderDetService.list(salesOrderDetQueryWrapper);
            BigDecimal totalAmount = salesOrderDetList.stream().map(SalesOrderDet::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            List<FundPoolDTO> fundPoolDTOList = fundService.autoSelectFundPool(totalAmount,
                    salesOrder.getCustomerId(), salesOrder.generateFundIdList());

            fundService.generateSalesOrder(salesOrder.getCustomerId(), salesOrder.getId(), fundPoolDTOList,
                    salesOrder.getPaymentFlag());
        }
        //修改对应提单的生成销售单状态
        salesOrder.setCreateStackStatus(YesNoEnum.NO.getCode());
        salesOrderMapper.updateById(salesOrder);
    }

    /**
     * 提单生成装车单
     */
    @Transactional(rollbackFor = Exception.class)
    public void addStackBySaleOrder(SalesOrder main, List<SalesOrderDet> salesOrderDets) {
        //通过提单号查询是否已经生成过销售单
        QueryWrapper<Stack> stackWrapper = new QueryWrapper<>();
        stackWrapper.lambda().eq(Stack::getSaleBillNo, main.getSaleBillNo()).eq(Stack::getStatus, StatusEnum.NO_CREATE_STACK.getCode());
        List<Stack> existStack = stackMapper.selectList(stackWrapper);
        if (existStack.isEmpty()) {
            //无，生成销售单，新增主表数据
            SalesOrder salesOrder = salesOrderService.getById(main.getId());
            Stack stack = new Stack();
            BeanUtil.copyProperties(salesOrder, stack);
            // id、系统字段设置为空
            stack.setId(main.getStackId());
            stack.setSalesOrderId(main.getId());
            stack.setCreateBy(null);
            stack.setCreateTime(null);
            stack.setUpdateBy(null);
            stack.setUpdateTime(null);
            stack.setSysOrgCode(null);
            stack.setTenantId(null);
            //设置设置仓库id、提单编号、结算状态（未结算）
            stack.setStockNoClass(salesOrder.getStockId());
            stack.setCarNo(main.getCarNo());

            //初始化单据编码
            String stackingNo = main.getStackNo();

            if (StrUtil.isBlank(stackingNo)) {
                //判断是否配置产品大类单据编码
                if (queryIfProdClassSerialNo(main.getTenantId())) {
                    stackingNo = ProdClassToSerialNoUtils.generateStackNoWithProdClassPrefix(salesOrder.getProdClassCode());
                } else {
                    stackingNo = SerialNoUtil.getSerialNo(SerialNoEnum.STACK_NO);
                }
            }

            stack.setStackingNo(stackingNo);
            stack.setSettled(SettleStatusEnum.UNSETTLE.getCode());
            if (ObjectUtil.isNull(main.getShipDate())) {
                stack.setShipDate(DateUtils.clearHourMinuteSecond(new Date()));
            } else {
                stack.setShipDate(main.getShipDate());
            }
            //销售单总价 总重量
            BigDecimal stackTotal = BigDecimal.ZERO;
            double totalWeight = 0.00;
            for (SalesOrderDet salesOrderDet : salesOrderDets) {

                if ("12".equalsIgnoreCase(TenantContext.getTenant())
                        && ProdClassCodeEnum.B.getValue().equalsIgnoreCase(salesOrderDet.getProdClassCode())) {
                    stackTotal = stackTotal.add(BigDecimalUtils.multiply(salesOrderDet.getTheoryPrice(), salesOrderDet.getTheoryWeight()));

                } else {
                    stackTotal = stackTotal.add(BigDecimalUtils.multiply(salesOrderDet.getPrice(), salesOrderDet.getWeight()));
                }
                totalWeight = totalWeight + salesOrderDet.getWeight();
            }
            stack.setTotalAmount(stackTotal);
            stack.setWeight(totalWeight);
            stack.setShipperName(main.getShipperName());
            stack.setRemark(main.getRemark());
            stack.setDestination(main.getDestination());

            save(stack);

            //新增子表数据
            int count = 1;
            for (SalesOrderDet salesOrderDet : salesOrderDets) {
                StackDet stackDet = new StackDet();
                BeanUtil.copyProperties(salesOrderDet, stackDet);

                //合同控量，更新合同明细预交量、已交量，更新合同主表状态
                contractService.contractControl(salesOrderDet, AddEditEnum.ADD.getCode(), count++);
                contractService.stackContractControl(salesOrderDet, count++);

                //id、系统字段设置为空
                stackDet.setId(salesOrderDet.getStackDetId());
                stackDet.setSalesOrderDetId(salesOrderDet.getId());
                stackDet.setCreateBy(null);
                stackDet.setCreateTime(null);
                stackDet.setUpdateBy(null);
                stackDet.setUpdateTime(null);
                stackDet.setSysOrgCode(null);
                stackDet.setOldProdCname(null);
                stackDet.setTenantId(null);
                stackDet.setStackId(stack.getId());
                stackDet.setStackingNo(stackingNo);
                if ("12".equalsIgnoreCase(TenantContext.getTenant())
                        && ProdClassCodeEnum.B.getValue().equalsIgnoreCase(salesOrderDet.getProdClassCode())) {
                    stackDet.setTotalAmount(BigDecimalUtils.multiply(salesOrderDet.getTheoryPrice(), salesOrderDet.getTheoryWeight()));
                } else {
                    stackDet.setTotalAmount(BigDecimalUtils.multiply(salesOrderDet.getPrice(), salesOrderDet.getWeight()));
                }
                stackDet.setMatLen(salesOrderDet.getOrderLen());
                stackDet.setMatWidth(salesOrderDet.getOrderWidth());
                stackDet.setMatThick(salesOrderDet.getOrderThick());
                stackDet.setContractListNo(salesOrderDet.getContractListNo());
                stackDet.setOldProdCname(salesOrderDet.getOldProdCname());
                stackDet.setTheoryWeight(salesOrderDet.getTheoryWeight());
                stackDet.setTheoryPrice(salesOrderDet.getTheoryPrice());
                stackDet.setSurfaceTreatment(salesOrderDet.getSurfaceTreatment());
                stackDet.setPlatingWeight(salesOrderDet.getPlatingWeight());

                //品名中文别名
                Inventory inventory = inventoryService.getById(salesOrderDet.getInventoryId());
                stackDet.setProdCnameOther(inventory.getProdCnameOther());

                SalesOrderDet exist = salesOrderDetService.getById(salesOrderDet.getId());
                stackDet.setInventoryId(exist.getInventoryId());
                stackDetService.save(stackDet);
            }

        } else {
            throw new ScmException("提单已经生成过销售单!");
        }
    }

    /**
     * 按件发货生成销售单
     */
    @Transactional(rollbackFor = Exception.class)
    public void sendOutGoods(SalesOrderDetVO salesOrderDetVo) {

        //提单明细
        SalesOrderDet salesOrderDet = salesOrderDetService.getById(salesOrderDetVo.getSaleBillNo());
        //提单
        SalesOrder salesOrder = salesOrderService.getBySalesBillNo(salesOrderDet.getSaleBillNo());

        LockInfo lockInfo = lockService.lock(Constants.CUSTOMER_LOCK_HEADER + salesOrder.getCustomerId());
        try {

            //勾选的材料号
            List<String> matNos = salesOrderDetVo.getMatNos();

            QueryWrapper<Mat> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(Mat::getMatNo, matNos);

            List<Mat> mats = matService.list(queryWrapper);

            double weight = 0.0;
            for (Mat mat : mats) {
                weight = weight + mat.getMatNetWt();
            }
            if (weight < salesOrderDet.getWeight()) {
                throw new ScmException("已选材料重量超出可用重量！");
            }

            //生成销售单信息
            Stack stack = new Stack();
            String stackingNo = saleOrderNum(salesOrderDet.getProdClassCode());
            stack.setCustomerId(salesOrder.getCustomerId());
            stack.setStackingNo(stackingNo);
            stack.setCarNo(salesOrderDetVo.getCarNo());
            stack.setSaleBillNo(salesOrder.getSaleBillNo());
            stack.setContractNo(salesOrder.getContractNo());
            stack.setOrderNo(salesOrder.getOrderNo());
            stack.setQty(salesOrderDet.getQty());
            stack.setWeight(weight);
            stack.setSettled(YesNoEnum.NO.getCode());
            save(stack);
            List<StackDet> stackDetList = newArrayList();
            for (Mat mat : mats) {
                StackDet stackDet = new StackDet();
                BeanUtil.copyProperties(mat, stackDet);
                stackDet.setProdClassCode(salesOrderDet.getProdClassCode());
                stackDet.setTenantId(null);
                stackDet.setStackingNo(stackingNo);
                stackDet.setStackId(stack.getId());
                stackDet.setTotalAmount(BigDecimalUtils.multiply(salesOrderDet.getPrice(), mat.getMatNetWt()));
                stackDetList.add(stackDet);
            }
            stackDetService.saveBatch(stackDetList);
            List<String> matIds = mats.stream().map(Mat::getId).collect(Collectors.toList());
            matMapper.deleteBatchIds(matIds);

        } finally {
            lockService.releaseLock(lockInfo);
        }
    }

    private String saleOrderNum(String productCode) {
        String now = DateFormatUtils.format(new Date(), "yyyyMMdd");
        Long maxNum = baseMapper.stackingNoMaxNum(now);
        String stackingNo;
        if (ObjectUtil.isNull(maxNum)) {
            stackingNo = productCode + now + "000";
        } else {
            stackingNo = productCode + (maxNum + 1);
        }
        return stackingNo;
    }

    /**
     * 按量发货生成装车单
     */
    @Transactional(rollbackFor = Exception.class)
    public void sendMeasured(String id) {
        //根据材料规格查询出物流园仓库的量
        SalesOrderDet salesOrderDet = salesOrderDetService.getById(id);

        SalesOrder salesOrder = salesOrderService.getBySalesBillNo(salesOrderDet.getSaleBillNo());
        if (null == salesOrder) {
            log.error(StrUtil.format("sendMeasured error, id:{}", id));
            throw new ScmException("参数错误，请联系技术检查");
        }

        LockInfo lockInfo = lockService.lock(Constants.CUSTOMER_LOCK_HEADER + salesOrder.getCustomerId());

        try {
            QueryWrapper<Mat> queryWrapper = new QueryWrapper<>();
            //牌号， 长宽厚 品名代码 品名中文别名
            queryWrapper.lambda().eq(Mat::getProdClassCode, salesOrderDet.getProdClassCode())
                    .eq(Mat::getStockHouseId, salesOrder.getStockId())
                    .eq(Mat::getSgSign, salesOrderDet.getSgSign())
                    .eq(Mat::getProdCode, salesOrderDet.getProdCode())
                    .eq(Mat::getProdCnameOther, salesOrderDet.getProdCnameOther())
                    .eq(Mat::getMatLen, salesOrderDet.getOrderLen())
                    .eq(Mat::getMatWidth, salesOrderDet.getOrderWidth())
                    .eq(Mat::getMatThick, salesOrderDet.getOrderThick());

            List<Mat> matList = matService.list(queryWrapper);

            if (CollectionUtil.isEmpty(matList)) {
                throw new ScmException("库存无此材料！");
            }
            Mat mat = matList.get(0);
            if (salesOrderDet.getWeight() > mat.getMatNetWt()) {
                throw new ScmException("库存材料可用重量不足！");
            }

            //生成装车单信息
            Stack stack = new Stack();
            String stackingNo = saleOrderNum(salesOrderDet.getProdClassCode());
            stack.setStackingNo(stackingNo);
            stack.setCustomerId(salesOrder.getCustomerId());
            stack.setSaleBillNo(salesOrder.getSaleBillNo());
            stack.setContractNo(salesOrder.getContractNo());
            stack.setOrderNo(salesOrder.getOrderNo());
            stack.setQty(salesOrderDet.getQty());
            stack.setWeight(salesOrderDet.getWeight());
            stack.setSettled(YesNoEnum.NO.getCode());
            save(stack);
            StackDet stackDet = new StackDet();
            BeanUtil.copyProperties(mat, stackDet);
            stackDet.setTenantId(null);
            stackDet.setStackingNo(stackingNo);
            stackDet.setStackId(stack.getId());
            stackDet.setProdClassCode(salesOrderDet.getProdClassCode());
            stackDetService.save(stackDet);

            //扣减材料信息
            Mat updateMat = new Mat();
            updateMat.setId(mat.getId());
            updateMat.setAvailableWeight(mat.getAvailableWeight() - stack.getWeight());
            matService.updateById(updateMat);
        } finally {
            lockService.releaseLock(lockInfo);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public List<SelectSettlenInfoVO> pageListStack(StackDetailVO stackDetailVO, String ids) {

        return baseMapper.selectStack(stackDetailVO, ids);
    }

    //修改装车单总价
    @Transactional(rollbackFor = Exception.class)
    public void updateTotalPrice(String salesOrderNo, BigDecimal priceSpread) {
        QueryWrapper<Stack> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Stack::getSaleBillNo, salesOrderNo);
        Stack stack = stackMapper.selectOne(queryWrapper);
        stack.setTotalAmount(priceSpread);
        updateById(stack);
    }

    //结算后重新计算装车单总价和明细单价
    @Transactional(rollbackFor = Exception.class)
    public void updateTotalPriceAfterSettled(String stackId, BigDecimal discount) {
        if (discount.compareTo(BigDecimal.ZERO) >= 0) {
            //每吨优惠大于或等于0
            BigDecimal totalPrice = BigDecimal.ZERO;
            QueryWrapper<StackDet> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(StackDet::getStackId, stackId);
            List<StackDet> stackDets = stackDetMapper.selectList(wrapper);
            for (StackDet stackDet : stackDets) {
                BigDecimal discountPrice = stackDet.getPrice().subtract(discount);
                if (discountPrice.compareTo(BigDecimal.ZERO) < 1 && ObjectUtil.notEqual(TenantContext.getTenant(), "11")) {
                    throw new ScmException("每吨优惠金额不能大于或等于材料单价");
                }
                //设置 装车单明细 优惠后的单价 优惠后的总价
                stackDet.setDiscountPrice(discountPrice);
                BigDecimal detTotalPrice = stackDet.getTotalAmount().subtract(BigDecimalUtils.multiply(discount, stackDet.getWeight()));
                stackDet.setDiscountTotalAmount(detTotalPrice);
                stackDetService.updateById(stackDet);

                totalPrice = totalPrice.add(detTotalPrice);
            }
            Stack stack = stackMapper.selectById(stackId);
            stack.setDiscount(discount);
            stack.setSettledTotalPrice(totalPrice);
            stack.setSettled(SettleStatusEnum.SETTLED.getCode());
            updateById(stack);
        }
    }


    //判断是否配置产品大类单据编码
    private boolean queryIfProdClassSerialNo(String tenantId) {
        //获取CompanyTenantDet的parentId
        QueryWrapper<CompanyTenantDet> companyTenantDetQueryWrapper = new QueryWrapper<>();
        companyTenantDetQueryWrapper.lambda().eq(CompanyTenantDet::getTenantCode, tenantId);
        CompanyTenantDet companyTenantDet = companyTenantDetMapper.selectOne(companyTenantDetQueryWrapper);
        if (ObjectUtil.isNotNull(companyTenantDet)) {
            //获取CompanyTenant的基础单价配置项字段
            QueryWrapper<CompanyTenant> companyTenantQueryWrapper = new QueryWrapper<>();
            companyTenantQueryWrapper.lambda().eq(CompanyTenant::getId, companyTenantDet.getParentId());
            CompanyTenant companyTenant = companyTenantMapper.selectOne(companyTenantQueryWrapper);
            if (companyTenant.getProdClassSerialNo().equals(YesNoEnum.YES.getCode())) {
                return true;
            }
        }
        return false;
    }

    public String salesShow() {
        //格式化时间到年月
        Date today = new Date();
        String ThisMonth = DateUtils.getThisMonth(today);

        String tenantId = TenantContext.getTenant();
        Integer delFlag = 0;

        //从数据库中得到本月数据List
        List<BigDecimal> totalSales = baseMapper.monthSales(ThisMonth, tenantId, delFlag);
        if (CollectionUtils.isEmpty(totalSales)) {
            return BigDecimal.ZERO.toString();
        }

        //初始化本月销售总额
        BigDecimal salesThis = BigDecimal.ZERO;
        //for循环计算本月销售总额
        for (BigDecimal big : totalSales) {
            salesThis = salesThis.add(big);
        }

        return salesThis.toString();
    }

    public String daythan() {
        //格式化出今天和昨天的日期
        Date today = new Date();
        String Today = DateUtils.getToday(today);
        String Yesterday = DateUtils.getYesterday(today);

        String tenantId = TenantContext.getTenant();
        Integer delFlag = 0;

        //从数据库中得到今日和昨日数据List
        List<BigDecimal> todaySales = baseMapper.somedaySales(Today, tenantId, delFlag);
        List<BigDecimal> yesterdaySales = baseMapper.somedaySales(Yesterday, tenantId, delFlag);

        //初始化今日以及昨日销售总额
        BigDecimal salesToday = BigDecimal.ZERO;
        BigDecimal salesYesterday = BigDecimal.ZERO;
        //for循环计算今日和昨日销售总额
        for (BigDecimal sales : todaySales) {
            salesToday = salesToday.add(sales);
        }
        for (BigDecimal sales : yesterdaySales) {
            salesYesterday = salesYesterday.add(sales);
        }

        BigDecimal salesDaythan = BigDecimal.ZERO;
        //判断前一天的销售额非0后，计算日比*100%
        if (salesYesterday.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal salesDaythanT = salesToday.divide(salesYesterday, 4, BigDecimal.ROUND_HALF_UP);
            salesDaythan = BigDecimalUtils.multiply(salesDaythanT, new BigDecimal("100"));
        }

        return salesDaythan.toString();
    }

    public String salesMonththan() {
        //格式化时间到年月，并取数值为上月
        Integer delFlag = 0;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, -1);
        Date LastMonth = calendar.getTime();
        QueryWrapper<Stack> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().gt(Stack::getCreateTime, LastMonth).eq(Stack::getDelFlag, delFlag);
        List<Stack> stackList = list(queryWrapper);
        //从数据库中得到上月数据List
        if (CollectionUtils.isEmpty(stackList)) {
            return "0";
        }

        //初始化上月销售总额
        BigDecimal salesLast = BigDecimal.ZERO;
        //for循环计算上月销售总额
        for (Stack stack : stackList) {
            salesLast = salesLast.add(stack.getTotalAmount());
        }
        //获取本月销售总金额
        BigDecimal salesThisMonth = new BigDecimal(salesShow());
        //计算月同比
        BigDecimal monthThanTransfer = salesThisMonth.divide(salesLast, 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal monthThan = BigDecimalUtils.multiply(monthThanTransfer, new BigDecimal("100"));
        return monthThan.toString();
    }

    public String dayAvg() {
        //获取本月总销售额
        String salesThisMonthString = salesShow();
        BigDecimal salesThisMonth = new BigDecimal(salesThisMonthString);

        //求本月天数
        Date today = new Date();
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd");
        String count = sdfDate.format(today);
        BigDecimal days = new BigDecimal(count);

        //计算日均销售金额
        BigDecimal daysAvg = salesThisMonth.divide(days, 2, BigDecimal.ROUND_HALF_UP);
        return daysAvg.toString();
    }

    public List<String> salesRanking() {
        //获取业务员姓名
        Map<String, BigDecimal> salesNameMap = salesmanInfoService.getSalesmanMap();

        //当月一号
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.DAY_OF_MONTH, -1);
        Date thisMonthNumberOne = calendar1.getTime();
        Integer delFlag = 0;
        //查询出本月的所有数据
        List<Stack> stackList = baseMapper.getMonthStack(thisMonthNumberOne, delFlag);
        if (CollectionUtils.isEmpty(stackList)) {
            List<String> noDataList = newArrayList();
            String noData = "本月还未有数据！";
            noDataList.add(noData);
            return noDataList;
        }
        //翻译业务员名字
        List<String> idsList = newArrayList();
        for (Stack stackIds : stackList) {
            idsList.add(stackIds.getSalesMan());
        }
        Map<String, String> salesmanInfoMap = salesmanInfoService.getNameMap(idsList);
        for (Stack stackMap : stackList) {
            stackMap.setSalesMan(salesmanInfoMap.get(stackMap.getSalesMan()));
        }

        for (Stack stack : stackList) {
            try {
                //业务员对应的销售额累加
                salesNameMap.put(stack.getSalesMan(),
                        salesNameMap.get(stack.getSalesMan()).add(stack.getTotalAmount()));
            } catch (NullPointerException e) {

            }
        }

        List<String> nullString = newArrayList();
        try {
            if (salesNameMap.isEmpty()) {
                nullString.add("没有业务员可排行");
                return nullString;
            }
        } catch (NullPointerException e) {
            nullString.add("没有业务员可排行！");
            return nullString;
        }


        //排序
        Set<Map.Entry<String, BigDecimal>> entrySet = salesNameMap.entrySet();
        List<Map.Entry<String, BigDecimal>> list = new ArrayList<Map.Entry<String, BigDecimal>>(entrySet);
        list.sort(new Comparator<Map.Entry<String, BigDecimal>>() {
            @Override
            public int compare(Map.Entry<String, BigDecimal> o1, Map.Entry<String, BigDecimal> o2) {
                //按照要求根据 User 的 age 的升序进行排,如果是倒序就是o2-o1
                return o2.getValue().intValue() - (o1.getValue()).intValue();
            }
        });

        List<String> salesRankingList = newArrayList();
        for (Map.Entry<String, BigDecimal> entry : list) {
            String string = entry.getKey() + " " + "------- ---------" + " " + "销售额: ￥" + entry.getValue();
            salesRankingList.add(string);
        }

        if (salesRankingList.size() < 8) {
            return salesRankingList;
        } else {
            List<String> rankingScreening = newArrayList();
            for (int i = 0; i < 8; i++) {
                rankingScreening.add(salesRankingList.get(i));
            }
            return rankingScreening;
        }

    }

    public List<SettleInfoVO> sevenDaySalesStatistics() {
        //获取七天前的日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -7);
        Date sevenDaysAgoDate = calendar.getTime();

        Integer delFlag = 0;

        //查询出过去七天的所有数据
        List<Stack> stackList = baseMapper.sevenDayStackInfo(sevenDaysAgoDate, delFlag);

        List<SettleInfoVO> settleInfoVOList = newArrayList();
        BigDecimal saleAccount = BigDecimal.ZERO;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = -6; i <= 0; i++) {
            Calendar calendarDate = Calendar.getInstance();
            calendarDate.setTime(new Date());
            calendarDate.add(Calendar.DATE, i);
            Date daysAgoDate = calendarDate.getTime();
            String daysAgoDateString = simpleDateFormat.format(daysAgoDate);
            String year = daysAgoDateString.substring(0, 4);
            String month = daysAgoDateString.substring(daysAgoDateString.indexOf("-") + 1, daysAgoDateString.lastIndexOf("-"));
            String day = daysAgoDateString.substring(daysAgoDateString.lastIndexOf("-") + 1);
            String yearMonthDay = year + month + day;

            int yearMonthDayInt = Integer.parseInt(yearMonthDay);

            String date = DateFormat.getDateInstance().format(daysAgoDate);
            String monthDay = date.substring(date.indexOf("-") + 1);

            SettleInfoVO settleInfoVO = new SettleInfoVO();
            settleInfoVO.setDate(yearMonthDayInt);
            settleInfoVO.setAccount(saleAccount);
            settleInfoVO.setDateString(monthDay);
            settleInfoVOList.add(settleInfoVO);
        }

        if (CollectionUtils.isEmpty(stackList)) {
            return settleInfoVOList;
        }

        for (Stack stack : stackList) {
            String dateString = simpleDateFormat.format(stack.getCreateTime());
            String year = dateString.substring(0, 4);
            String month = dateString.substring(dateString.indexOf("-") + 1, dateString.lastIndexOf("-"));
            String day = dateString.substring(dateString.lastIndexOf("-") + 1);
            String yearMonthDay = year + month + day;
            int yearMonthDayInt = Integer.parseInt(yearMonthDay);

            for (SettleInfoVO settleInfoVO : settleInfoVOList) {
                if (settleInfoVO.getDate() == yearMonthDayInt) {
                    settleInfoVO.setAccount(settleInfoVO.getAccount().add(stack.getTotalAmount()));
                }
            }
        }
        return settleInfoVOList;
    }


    public Map<String, String> listCreateBy(List<String> createBylist) {
        List<OptionVO> a = selectUnsettleCustomerId();
        Map<String, String> createByMap = new HashMap<>();
        if (CollectionUtil.isEmpty(createBylist)) {
            return createByMap;
        }
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        //塞条件:id在ids里
        queryWrapper.lambda().in(SysUser::getUsername, createBylist);
        //根据ids的id记录去查询CustomerProfile的所有记录
        List<SysUser> sysUserList = sysUserService.list(queryWrapper);
        for (SysUser sysUserTmp : sysUserList) {
            createByMap.put(sysUserTmp.getUsername(), sysUserTmp.getRealname());
        }
        //最终返回 id,CompanyName的map集合
        return createByMap;

    }

    public List<OptionVO> selectUnsettleCustomerId() {
        return baseMapper.selectUnsettleCustomerId();
    }

    public List<Stack> selectDeleteStack(Date startTime, Date endTime) {
        return baseMapper.selectDeleteStack(startTime, endTime);
    }

    @Transactional(rollbackFor = Exception.class)
    public void stackWriteOffAlone(String id) throws ParseException {

        Stack stack = getById(id);

        if (stack.getSettled().equals(SettleStatusEnum.UNSETTLE.getCode())) {
            throw new ScmException("该装车单还未结算");
        }

        if (stack.getStatus().equals(StatusEnum.WRITE_OFF.getCode())) {
            throw new ScmException("该装车单已冲红");
        }

        String tenantId = TenantContext.getTenant();
        if (queryWriteOffReviewer(tenantId)) {

            if (stack.getStatus().equals(StatusEnum.WRITE_OFF_REVIEWER.getCode())) {
                throw new ScmException("该装车单已经是待复核状态");
            }

            stack.setStatus(StatusEnum.WRITE_OFF_REVIEWER.getCode());
            updateById(stack);

        } else {
            stackWriteOff(id);

        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void stackWriteOffReviewer(String id) throws ParseException {
        Stack stack = getById(id);

        if (stack.getStatus().equals(StatusEnum.WRITE_OFF_REVIEWER.getCode())) {
            stackWriteOff(id);
        } else {
            throw new ScmException("未进行冲红复核装车单不需要弃审");

        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void stackWriteOffGiveUp(String id) throws ParseException {
        Stack stack = getById(id);

        if (stack.getStatus().equals(StatusEnum.WRITE_OFF_REVIEWER.getCode())) {

            stack.setStatus(StatusEnum.NO_CREATE_STACK.getCode());
            updateById(stack);
        } else {
            throw new ScmException("该装车单不在审核中");
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void stackWriteOff(String id) throws ParseException {
        Stack stack = getById(id);
        stack.setStatus(StatusEnum.WRITE_OFF.getCode());

        updateById(stack);

        Stack stackWriteOff = new Stack();
        BeanUtils.copyProperties(stack, stackWriteOff);
        stackWriteOff.setId(null);
        stackWriteOff.setCreateTime(null);
        stackWriteOff.setCreateBy(null);
        stackWriteOff.setUpdateBy(null);
        stackWriteOff.setUpdateTime(null);
        stackWriteOff.setSysOrgCode(null);

        stackWriteOff.setTotalAmount(stack.getTotalAmount().negate());
        stackWriteOff.setWeight(-stack.getWeight());
        stackWriteOff.setSettledTotalPrice(stack.getSettledTotalPrice().negate());
        stackWriteOff.setStackingNo("-" + stack.getStackingNo());
        stackWriteOff.setSettled(SettleStatusEnum.SETTLED.getCode());

        save(stackWriteOff);

        QueryWrapper<StackDet> stackDetQueryWrapper = new QueryWrapper<>();
        stackDetQueryWrapper.lambda().eq(StackDet::getStackId, id);
        List<StackDet> stackDetList = stackDetService.list(stackDetQueryWrapper);
        if (CollectionUtil.isEmpty(stackDetList)) {
            log.error(StrUtil.format("查询不到对应的装车单明细,装车单id：" + id));
            throw new ScmException(StrUtil.format("查询不到对应的装车单明细"));
        }

        List<StackDet> stackDetListWriteOff = newArrayList();
        BigDecimal writeOffTotalAccount = BigDecimal.ZERO;
        for (StackDet stackDet : stackDetList) {
            StackDet stackDetWriteOff = new StackDet();
            BeanUtils.copyProperties(stackDet, stackDetWriteOff);
            stackDetWriteOff.setId(null);
            stackDetWriteOff.setCreateTime(null);
            stackDetWriteOff.setCreateBy(null);
            stackDetWriteOff.setUpdateBy(null);
            stackDetWriteOff.setUpdateTime(null);
            stackDetWriteOff.setSysOrgCode(null);

            stackDetWriteOff.setWeight(-stackDetWriteOff.getWeight());
            stackDetWriteOff.setQty(-stackDetWriteOff.getQty());
            stackDetWriteOff.setTotalAmount(stackDetWriteOff.getTotalAmount().negate());
            stackDetWriteOff.setDiscountTotalAmount(stackDetWriteOff.getDiscountTotalAmount().negate());
            stackDetWriteOff.setStackId(stackWriteOff.getId());
            stackDetWriteOff.setStackingNo("-" + stackDetWriteOff.getStackingNo());

            writeOffTotalAccount = writeOffTotalAccount.add(stackDet.getDiscountTotalAmount());

            stackDetListWriteOff.add(stackDetWriteOff);
            //GD螺纹钢按实际重量销售，按理计出入库
            double weight = inventoryWeight(stackDet);
            inventoryService.deductionPracticalById(stackDet.getInventoryId(), -stackDet.getQty(), -weight);
            inventoryService.deductionAvailableById(stackDet.getInventoryId(), -stackDet.getQty(), -weight);
        }

        stackDetService.saveBatch(stackDetListWriteOff);

        //生成结算单
        SettleInfo settleWriteOff = new SettleInfo();
        //产品大类
        Set set = new HashSet();
        for (StackDet stackDet : stackDetList) {
            set.add(stackDet.getProdClassCode());
        }
        settleWriteOff.setProdCode(CollectionUtil.join(set, ","));
        settleWriteOff.setCustomer(stackWriteOff.getCustomerId());
        settleWriteOff.setRowTotal(stackWriteOff.getTotalAmount());
        settleWriteOff.setSettleAmount(stackWriteOff.getSettledTotalPrice());
        settleWriteOff.setDiscount(BigDecimal.ZERO);
        settleWriteOff.setStatus(SettleStatusEnum.SETTLED.getCode());
        //初始化单据编码
        String settleNo = null;
        //判断是否配置产品大类单据编码
        if (queryIfProdClassSerialNo(stackWriteOff.getTenantId())) {
            settleNo = ProdClassToSerialNoUtils.generateSettleNoWithProdClassPrefix(stackDetList.get(0).getProdClassCode());
        } else {
            settleNo = SerialNoUtil.getSerialNo(SerialNoEnum.SETTLE_NO);
        }
        settleWriteOff.setSettleNo(settleNo);
        settleWriteOff.setSettleName(stackWriteOff.getSalesMan());
        settleWriteOff.setStackId(stackWriteOff.getId());
        settleWriteOff.setStatementState(StatementStateEnum.NORMAL.getCode());
        settleWriteOff.setInvoiceStatus(InvoiceStatusEnum.NO.getCode());
        settleWriteOff.setShipDate(stackWriteOff.getShipDate());
        settleWriteOff.setSaleBillNo(stackWriteOff.getSaleBillNo());
        settleWriteOff.setStackingNo(stackWriteOff.getStackingNo());
        settleWriteOff.setRemark("红冲结算单");
        settleInfoService.save(settleWriteOff);

        QueryWrapper<SettleInfo> settleInfoQueryWrapper = new QueryWrapper<>();
        settleInfoQueryWrapper.lambda().eq(SettleInfo::getStackId, id);
        SettleInfo settleImport = settleInfoService.getOne(settleInfoQueryWrapper);

        settleImport.setStatementState(StatementStateEnum.WRITE_OFF.getCode());

        settleInfoService.updateById(settleImport);

        updateById(stack);

        stackWriteOff.setSettledId(settleWriteOff.getId());

        updateById(stackWriteOff);

        fundAccountService.stackWriteOff(writeOffTotalAccount, stack.getCustomerId(), stack.getId(), stack.getSalesOrderId());

        stackWriteOffAddBlueSingle(stack);
    }

    //将原来的提单改为红冲状态，新生成提单，装车单。
    private void stackWriteOffAddBlueSingle(Stack stack) throws ParseException {

        if (StringUtils.isEmpty(stack.getSalesOrderId())) {
            throw new ScmException(StrUtil.format("提单id为空，请联系技术人员处理"));
        }
        SalesOrder olSalesOrder = salesOrderService.getById(stack.getSalesOrderId());
        if (ObjectUtil.isEmpty(olSalesOrder)) {
            throw new ScmException(StrUtil.format("查询不到对应的提单，请联系技术人员处理"));
        }

        QueryWrapper<SalesOrder> salesOrderQueryWrapper = new QueryWrapper<>();
        salesOrderQueryWrapper.lambda().eq(SalesOrder::getSaleBillNo, stack.getSaleBillNo())
                .eq(SalesOrder::getStatus, StatusEnum.WRITE_OFF.getCode());

        SalesOrder salesOrderInDb = salesOrderService.getOne(salesOrderQueryWrapper);
        if (ObjectUtil.isEmpty(salesOrderInDb)) {
            olSalesOrder.setStatus(StatusEnum.WRITE_OFF.getCode());
        } else {
            //暂时性解决二次红冲与唯一索引产生的问题
            olSalesOrder.setStatus(StatusEnum.WRITE_OFF_TWO.getCode());
        }

        salesOrderService.updateById(olSalesOrder);

        SalesOrder addSalesOrder = new SalesOrder();
        BeanUtils.copyProperties(olSalesOrder, addSalesOrder);
        addSalesOrder.setId(null);
        addSalesOrder.setCreateBy(null);
        addSalesOrder.setUpdateTime(null);
        addSalesOrder.setUpdateBy(null);
        addSalesOrder.setSysOrgCode(null);
        addSalesOrder.setStatus(StatusEnum.NO_CREATE_STACK.getCode());
        addSalesOrder.setCreateStackStatus(YesNoEnum.NO.getCode());
        addSalesOrder.setStackNo(stack.getStackingNo());


        QueryWrapper<SalesOrderDet> salesOrderDetQueryWrapper = new QueryWrapper<>();
        salesOrderDetQueryWrapper.lambda().eq(SalesOrderDet::getParentId, olSalesOrder.getId());
        List<SalesOrderDet> olSalesOrderDetList = salesOrderDetService.list(salesOrderDetQueryWrapper);

        List<SalesOrderDet> addSalesOrderDetList = newArrayList();
        for (SalesOrderDet olSalesOrderDet : olSalesOrderDetList) {
            SalesOrderDet addSalesOrderDet = new SalesOrderDet();
            BeanUtils.copyProperties(olSalesOrderDet, addSalesOrderDet);
            addSalesOrderDet.setId(null);
            addSalesOrderDet.setCreateBy(null);
            addSalesOrderDet.setUpdateTime(null);
            addSalesOrderDet.setUpdateBy(null);
            addSalesOrderDet.setSysOrgCode(null);

            addSalesOrderDetList.add(addSalesOrderDet);
        }

        salesOrderService.saveMain(addSalesOrder, addSalesOrderDetList, newArrayList());

        for (SalesOrderDet salesOrderDetInDb : olSalesOrderDetList) {

            QueryWrapper<StackDet> stackDetQueryWrapper = new QueryWrapper<>();
            stackDetQueryWrapper.lambda().eq(StackDet::getSalesOrderDetId, salesOrderDetInDb.getId());
            List<StackDet> stackDetList = stackDetService.list(stackDetQueryWrapper);
            List<StackDet> filterList = stackDetList.stream().filter(i -> BigDecimal.valueOf(i.getWeight()).compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
            StackDet stackDet = filterList.get(0);

            for (SalesOrderDet salesOrderDet : addSalesOrderDetList) {
                if ((StrUtil.isNotEmpty(salesOrderDet.getMatNo()) && salesOrderDet.getMatNo().equals(stackDet.getMatNo())) &&
                        salesOrderDet.getWtMode().equals(stackDet.getWtMode()) &&
                        salesOrderDet.getOrderThick().equals(stackDet.getMatThick()) &&
                        salesOrderDet.getOrderWidth().equals(stackDet.getMatWidth()) &&
                        salesOrderDet.getOrderLen().equals(stackDet.getMatLen())) {

                    salesOrderDet.setWeight(salesOrderDetInDb.getWeight());
                    salesOrderDet.setQty(salesOrderDetInDb.getQty());
                    salesOrderDet.setPrice(salesOrderDetInDb.getPrice());
                }
            }
        }

        //生成装车单
        salesOrderService.updateMain(addSalesOrder, addSalesOrderDetList);

    }

    public BigDecimal sumUnSettleStackAmount(String customerId) {
        return stackMapper.sumUnSettleStackAmount(customerId);
    }

    //判断是否配置冲红复核
    private boolean queryWriteOffReviewer(String tenantId) {
        //获取CompanyTenantDet的parentId
        QueryWrapper<CompanyTenantDet> companyTenantDetQueryWrapper = new QueryWrapper<>();
        companyTenantDetQueryWrapper.lambda().eq(CompanyTenantDet::getTenantCode, tenantId);
        CompanyTenantDet companyTenantDet = companyTenantDetMapper.selectOne(companyTenantDetQueryWrapper);
        if (ObjectUtil.isNotNull(companyTenantDet)) {
            //获取CompanyTenant的基础单价配置项字段
            QueryWrapper<CompanyTenant> companyTenantQueryWrapper = new QueryWrapper<>();
            companyTenantQueryWrapper.lambda().eq(CompanyTenant::getId, companyTenantDet.getParentId());
            CompanyTenant companyTenant = companyTenantMapper.selectOne(companyTenantQueryWrapper);
            return companyTenant.getWriteOffReviewer().equals(YesNoEnum.YES.getCode());
        }
        return false;
    }

    //校验流水金额
    private boolean checkFundAmount(Stack stack) {

        QueryWrapper<FundDetail> fundDetailQueryWrapper = new QueryWrapper<>();
        fundDetailQueryWrapper.lambda().eq(FundDetail::getOutTradeNo, stack.getSalesOrderId());
        List<FundDetail> fundDetailList = fundDetailService.list(fundDetailQueryWrapper);

        BigDecimal fundDetailAmount = fundDetailList.stream().map(FundDetail::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);

        if (fundDetailAmount.compareTo(stack.getTotalAmount()) != 0){
            throw new ScmException("提单" + stack.getSaleBillNo() + "扣款金额异常，请稍后重试");
        }

        return true;

    }

        //GD螺纹钢按实际重量销售，按理计出入库
    private double inventoryWeight(StackDet stackDet) {
        double weight;
        if ("12".equalsIgnoreCase(TenantContext.getTenant())
                && (ProdClassCodeEnum.B.getValue().equalsIgnoreCase(stackDet.getProdClassCode()))) {
            weight = BigDecimalUtils.multiplyKeep3DecimalPlaces(stackDet.getMatTheoryWt(), stackDet.getQty()).doubleValue();
        } else {
            weight = stackDet.getWeight();
        }
        return weight;
    }
}

