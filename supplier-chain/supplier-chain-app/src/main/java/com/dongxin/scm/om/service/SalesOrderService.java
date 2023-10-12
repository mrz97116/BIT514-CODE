package com.dongxin.scm.om.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dongxin.scm.bd.entity.CompanyTenant;
import com.dongxin.scm.bd.entity.CompanyTenantDet;
import com.dongxin.scm.bd.mapper.CompanyTenantDetMapper;
import com.dongxin.scm.bd.mapper.CompanyTenantMapper;
import com.dongxin.scm.bd.service.CompanyTenantDetService;
import com.dongxin.scm.bd.service.CompanyTenantService;
import com.dongxin.scm.bd.vo.CompanyTenantVo;
import com.dongxin.scm.cm.entity.CustomerConfiguration;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.service.CustomerConfigurationService;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.common.service.LockService;
import com.dongxin.scm.enums.ProdClassCodeEnum;
import com.dongxin.scm.enums.SerialNoEnum;
import com.dongxin.scm.enums.WmsSuperviseEnum;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.dto.FundAccountDTO;
import com.dongxin.scm.fm.dto.FundPoolDTO;
import com.dongxin.scm.fm.entity.FundDetail;
import com.dongxin.scm.fm.entity.SettleImport;
import com.dongxin.scm.fm.enums.FundDetailTypeEnum;
import com.dongxin.scm.fm.service.FundDetailService;
import com.dongxin.scm.fm.service.FundPoolService;
import com.dongxin.scm.fm.service.FundService;
import com.dongxin.scm.fm.service.SettleImportService;
import com.dongxin.scm.home.entity.SalesOrderSevenInfo;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.entity.SalesOrderDet;
import com.dongxin.scm.om.enums.LogisticsEnum;
import com.dongxin.scm.om.enums.PaymentFlagEnum;
import com.dongxin.scm.om.enums.WmsTypeEnum;
import com.dongxin.scm.om.enums.WtModeEnum;
import com.dongxin.scm.om.mapper.SalesOrderDetMapper;
import com.dongxin.scm.om.mapper.SalesOrderMapper;
import com.dongxin.scm.om.util.Constants;
import com.dongxin.scm.om.vo.CarNosAndShipNos;
import com.dongxin.scm.om.vo.CheckCarNoVO;
import com.dongxin.scm.om.vo.EquipmentAndMaterialsNetworkMergeFields;
import com.dongxin.scm.sm.entity.*;
import com.dongxin.scm.sm.entity.Stack;
import com.dongxin.scm.sm.enums.StatusEnum;
import com.dongxin.scm.sm.mapper.MatMapper;
import com.dongxin.scm.sm.service.*;
import com.dongxin.scm.sm.vo.OptionVO;
import com.dongxin.scm.utils.*;
import com.dongxin.scm.wms.NormalRequest;
import com.dongxin.scm.wms.PaginatedRequest;
import com.dongxin.scm.wms.condition.*;
import com.dongxin.scm.wms.entity.OrderDetMappingProdcode;
import com.dongxin.scm.wms.exception.WMSException;
import com.dongxin.scm.wms.service.OrderDetMappingProdcodeService;
import com.dongxin.scm.wms.service.WMSService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.base.service.BaseService;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.config.mybatis.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.newArrayList;
import static cn.hutool.core.collection.CollUtil.newHashMap;
import static java.util.Arrays.asList;

/**
 * @Description: 销售单
 * @Author: jeecg-boot
 * @Date: 2020-11-23
 * @Version: V1.0
 */
@Service
@Slf4j
public class SalesOrderService extends BaseService<SalesOrderMapper, SalesOrder> {

    @Resource
    private SalesOrderMapper salesOrderMapper;
    @Resource
    private SalesOrderDetMapper salesOrderDetMapper;
    @Resource
    private CompanyTenantMapper companyTenantMapper;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private StackService stackService;
    @Autowired
    private StackDetService stackDetService;
    @Autowired
    private SalesOrderDetService salesOrderDetService;
    @Autowired
    private CustomerProfileService customerProfileService;
    @Autowired
    private CustomerConfigurationService customerConfigurationService;
    @Autowired
    private ContractDetService contractDetService;
    @Autowired
    private AverageCostService averageCostService;
    @Autowired
    private CompanyTenantDetService companyTenantDetService;
    @Autowired
    private SettleImportService settleImportService;
    @Autowired
    private CompanyTenantService companyTenantService;
    @Resource
    private CompanyTenantDetMapper companyTenantDetMapper;
    @Autowired
    private FundDetailService fundDetailService;
    @Autowired
    private FundService fundService;
    @Autowired
    private LockService lockService;
    @Autowired
    private WMSService wmsService;
    @Autowired
    private LogisticsParkMatBasicInformationService logisticsParkMatBasicInformationService;
    @Autowired
    private PrepareOrderService prepareOrderService;
    @Resource
    private MatMapper matMapper;
    @Autowired
    private OrderDetMappingProdcodeService orderDetMappingProdcodeService;
    @Autowired
    private FundPoolService fundPoolService;

    /**
     * 添加提單
     *
     * @param salesOrder        主表数据
     * @param salesOrderDetList 附表数据
     * @param fundIds           选款id(来款表id)
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveMain(SalesOrder salesOrder, List<SalesOrderDet> salesOrderDetList, List<String> fundIds) {

        LockInfo lockInfo = lockService.lock(Constants.CUSTOMER_LOCK_HEADER + salesOrder.getCustomerId());

        try {
            String tenantId = TenantContext.getTenant();

            checkOrder(salesOrder);
            checkOrderSalesDet(salesOrderDetList);

            prepareOrderService.checkoutDetailsCount(salesOrder.getPrepareOrderId(), salesOrderDetList.size());

            //查询控款配置
            CompanyTenantVo companyTenantVo = companyTenantMapper.queryPaymentConfiguration(Integer.valueOf(tenantId));
            String paymentFlag = companyTenantVo.getConfiguration();

            //初始化单据编码
            String saleOrderNo = salesOrder.getSaleBillNo();
            if (StrUtil.isBlank(salesOrder.getSaleBillNo())) {
                //判断是否配置产品大类单据编码
                if (queryIfProdClassSerialNo(tenantId)) {
                    saleOrderNo = ProdClassToSerialNoUtils.generateSalesOrderNoWithProdClassPrefix(salesOrderDetList.get(0).getProdClassCode());
                } else {
                    saleOrderNo = SerialNoUtil.getSerialNo(SerialNoEnum.SALESORDER_NO);
                }
            }

            if (ObjectUtil.isNull(salesOrder.getSalesMan())) {
                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                salesOrder.setSalesMan(sysUser.getRealname());
            }

            //截止时间默认为当前时间
            Date newDate = DateUtil.offset(new Date(), DateField.DAY_OF_MONTH, 2);
            salesOrder.setCloseTime(newDate);

            salesOrder.setAutoSelectFund(CollectionUtil.isEmpty(fundIds) ? YesNoEnum.YES.getCode() : YesNoEnum.NO.getCode());
            salesOrder.setFundIds(String.join(",", fundIds));

            salesOrder.setSaleBillNo(saleOrderNo);
            salesOrder.setTenantId(null);
            //处理助记码
            CustomerProfile customerProfile = customerProfileService.getById(salesOrder.getCustomerId());
            salesOrder.setAlias(customerProfile.getAlias());
            //下单材料为中厚板材时，在主表标记上
            if (ProdClassCodeEnum.H.getValue().equals(salesOrderDetList.get(0).getProdClassCode())) {
                salesOrder.setProdClassCode(ProdClassCodeEnum.H.getValue());
            }
            //主表上的仓库取明细第一条的仓库id
            if (StrUtil.isEmpty(salesOrderDetList.get(0).getStockId())) {
                throw new ScmException("第一条明细的仓库不能为空！");
            }
            salesOrder.setStockId(salesOrderDetList.get(0).getStockId());
            //复制新增会将旧订单数据传过来，此处清空。
            salesOrder.setCreateBy(null);
            salesOrder.setCreateTime(null);
            salesOrder.setUpdateBy(null);
            salesOrder.setUpdateTime(null);
            salesOrder.setPushLogistics(null);
            salesOrder.setFailureCause(null);

            //新增提单制单时间默认为创建时间
            if (ObjectUtil.isEmpty(salesOrder.getOrderTime())) {
                salesOrder.setOrderTime(new Date());
            }
            salesOrder.setGdImportStackDetail(YesNoEnum.NO.getCode());
            save(salesOrder);
            BigDecimal totalPrice = BigDecimal.ZERO;
            BigDecimal totalWeight = BigDecimal.ZERO;

            List<String> matNos = newArrayList();
            if (CollectionUtil.isNotEmpty(salesOrderDetList)) {
                int count = 1;
                for (SalesOrderDet salesOrderDet : salesOrderDetList) {

                    weightComputation(salesOrderDet.getWeight(), salesOrderDet.getQty(), salesOrderDet.getMatTheoryWt(), count, salesOrderDet);

                    QueryWrapper<CompanyTenantDet> companyTenantDetQueryWrapper = new QueryWrapper<>();
                    companyTenantDetQueryWrapper.lambda().eq(CompanyTenantDet::getTenantCode, Integer.valueOf(tenantId));

                    CompanyTenantDet companyTenantDet = companyTenantDetService.getOne(companyTenantDetQueryWrapper);
                    String parentId = companyTenantDetMapper.selectOne(companyTenantDetQueryWrapper).getParentId();

                    QueryWrapper<CompanyTenant> companyTenantQueryWrapper = new QueryWrapper<>();
                    companyTenantQueryWrapper.lambda().eq(CompanyTenant::getId, parentId);

                    CompanyTenant companyTenant = companyTenantService.getById(companyTenantDet.getParentId());
                    if (ObjectUtil.isNotNull(companyTenantDet) && companyTenant.getAverageCostCheck().equals(YesNoEnum.YES.getCode())) {
                        averageCostService.averageCostPrice(salesOrder.getCreateTime(), salesOrderDet.getProdClassCode(), salesOrderDet.getPrice(), count);
                    }
                    checkSalesOrderDetNull(salesOrderDet);
                    totalWeight = totalWeight.add(BigDecimal.valueOf(salesOrderDet.getWeight()));

                    //外键设置
                    salesOrderDet.setParentId(salesOrder.getId());
                    salesOrderDet.setId(null);

                    //复制新增会将旧订单数据传过来，此处清空。
                    salesOrderDet.setCreateBy(null);
                    salesOrderDet.setCreateTime(null);
                    salesOrderDet.setUpdateBy(null);
                    salesOrderDet.setUpdateTime(null);


                    Inventory inventory = inventoryService.getById(salesOrderDet.getInventoryId());

                    if (ObjectUtil.isNull(inventory)) {
                        throw new ScmException("仓库无库存");
                    }

                    //计算库存总数
                    if (inventory.getAvailableQty() < salesOrderDet.getQty()) {
                        throw new ScmException("" + "第" + count + "条明细" + "" + "仓库库存物料" + "可用数量不足！库存总数：" + inventory.getAvailableQty() + "下单数：" + salesOrderDet.getQty());
                    }

                    //广东 螺纹钢 按照理论重量扣库存
                    double weight = inventoryWeight(salesOrderDet);
                    inventoryService.deductionAvailable(inventory, salesOrderDet.getQty(), weight);

                    BigDecimal detTotalPrice = BigDecimalUtils.multiply(salesOrderDet.getPrice(), salesOrderDet.getWeight());
                    salesOrderDet.setTotalPrice(detTotalPrice);
                    salesOrderDet.setInventoryId(inventory.getId());
                    salesOrderDet.setOriginWeight(salesOrderDet.getWeight());
                    salesOrderDet.setOriginQty(salesOrderDet.getQty());
                    salesOrderDet.setStockLocation(inventory.getStockLocation());
                    salesOrderDet.setUnit(inventory.getUnit());
                    salesOrderDet.setSurfaceTreatment(inventory.getSurfaceTreatment());
                    salesOrderDet.setPlatingWeight(inventory.getPlatingWeight());
                    salesOrderDet.setCustMatSpecs(inventory.getCustMatSpecs());
                    salesOrderDet.setProdCnameOther(inventory.getProdCnameOther());

                    matNos.add(salesOrderDet.getMatNo());

                    //合同控量校验配置
                    contractDetService.contractControl(salesOrderDet, salesOrder, count++);

                    salesOrderDetMapper.insert(salesOrderDet);

                    totalPrice = totalPrice.add(detTotalPrice);
                }
                salesOrder.setWeight(totalWeight.doubleValue());
            } else {
                throw new ScmException("请录入明细信息");
            }
            //设置销售单生成状态 否
            salesOrder.setCreateStackStatus(YesNoEnum.NO.getCode());
            salesOrder.setStatus(StatusEnum.NO_CREATE_STACK.getCode());


            if (PaymentFlagEnum.PAID.getCode().equals(paymentFlag)) {
                if (YesNoEnum.YES.getCode().equals(salesOrder.getEquipmentSuppliesFlag())) {
                    totalPrice = BigDecimal.ZERO;
                    //设备物资标识总价计算方式特殊，先将相同价格明细重量汇总，然后乘以价格，最后将不同价格的明细累加得到总价
                    Map<EquipmentAndMaterialsNetworkMergeFields, Double> priceAndWeight = newHashMap();

                    for (SalesOrderDet salesOrderDet : salesOrderDetList) {
                        EquipmentAndMaterialsNetworkMergeFields equipmentAndMaterialsNetworkMergeFields =
                                new EquipmentAndMaterialsNetworkMergeFields();
                        equipmentAndMaterialsNetworkMergeFields.setOldProdCname(salesOrderDet.getOldProdCname());
                        equipmentAndMaterialsNetworkMergeFields.setOrderThick(salesOrderDet.getOrderThick());
                        equipmentAndMaterialsNetworkMergeFields.setPrice(salesOrderDet.getPrice());

                        priceAndWeight.merge(equipmentAndMaterialsNetworkMergeFields, salesOrderDet.getWeight(), Double::sum);
                    }
                    for (Map.Entry<EquipmentAndMaterialsNetworkMergeFields, Double> entry : priceAndWeight.entrySet()) {
                        totalPrice = totalPrice.add(BigDecimalUtils.multiply(entry.getKey().getPrice(), entry.getValue()));
                    }
                }


                List<FundPoolDTO> fundPoolDTOList = fundService.autoSelectFundPool(totalPrice, salesOrder.getCustomerId(), fundIds);

                //选款校验  选款总金额与totalPrice对比
                checkFunds(fundPoolDTOList, totalPrice);

                fundService.generateSalesOrder(salesOrder.getCustomerId(), salesOrder.getId(), fundPoolDTOList, paymentFlag);

                salesOrder.setFundDesc(fundService.generateFundDesc(fundPoolDTOList));

                //控款，添加控款明细
                salesOrder.setPaymentFlag(PaymentFlagEnum.PAID.getCode()); //设置岑海付款标识
            } else {
                salesOrder.setPaymentFlag(PaymentFlagEnum.UNPAID.getCode()); //设置阳蕊明付款标识
            }

            salesOrder.setTotalAmount(totalPrice.setScale(2, RoundingMode.HALF_UP));
            salesOrder.setShipDate(new Date());
            if (tenantId.equalsIgnoreCase("2")) {
                salesOrder.setPushLogistics(LogisticsEnum.NO_LOGISTICS.getCode());
            }

            //广东期货预开单
            if (StrUtil.isNotBlank(salesOrder.getPrepareOrderId())) {
                prepareOrderService.updatePrepareOrderStatusById(salesOrder.getPrepareOrderId(), YesNoEnum.YES);

                PrepareOrder prepareOrder = prepareOrderService.getById(salesOrder.getPrepareOrderId());
                salesOrder.setCarNos(prepareOrder.getCarNos());
            } else {
                //广东现货开单，没有期货id
                if ("12".equalsIgnoreCase(tenantId)) {
                    salesOrder.setExtendBillNo(ProdClassToSerialNoUtils.gdGenerateExtendBillNo(salesOrderDetList.get(0).getProdClassCode()));
                }
            }

            if ("12".equalsIgnoreCase(tenantId)) {
                CarNosAndShipNos carNosAndShipNos = getCarNosAndShipNos(matNos);
                salesOrder.setCarNos(carNosAndShipNos.carNos);
                salesOrder.setShipNos(carNosAndShipNos.shipNos);
            }
            salesOrderMapper.updateById(salesOrder);

        } finally {
            lockService.releaseLock(lockInfo);
        }

    }

    private CarNosAndShipNos getCarNosAndShipNos(List<String> matNos) {

        CarNosAndShipNos result = new CarNosAndShipNos();

        matNos = matNos.stream().filter(StrUtil::isNotBlank).collect(Collectors.toList());

        matNos = CollectionUtil.distinct(matNos);

        if (CollectionUtil.isEmpty(matNos)) {
            return result;
        }
        QueryWrapper<Mat> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(Mat::getMatNo, matNos);
        List<Mat> matList = matMapper.selectList(queryWrapper);
        if (CollectionUtil.isEmpty(matList)) {
            return result;
        }
        List<String> carNos = matList.stream().map(Mat::getCarNo).collect(Collectors.toList());

        List<String> shipNos = matList.stream().map(Mat::getShipNo).collect(Collectors.toList());

        carNos = CollectionUtil.distinct(carNos);
        shipNos = CollectionUtil.distinct(shipNos);

        result.carNos = String.join("、", carNos);
        result.shipNos = String.join("、", shipNos);
        return result;
    }


    //选款选择
    private void checkFunds(List<FundPoolDTO> fundPoolDTOList, BigDecimal totalAmount) {

        if (CollectionUtil.isEmpty(fundPoolDTOList)) {
            throw new ScmException("参数错误，请检查选款资金参数");
        }

        BigDecimal totalFundAmount = BigDecimal.ZERO;

        for (FundPoolDTO fundPoolDTO : fundPoolDTOList) {
            totalFundAmount = totalFundAmount.add(fundPoolDTO.getUseAmount());

            if (fundPoolDTO.getAvailAmount().compareTo(fundPoolDTO.getUseAmount()) < 0) {
                log.error("款项余额不足, fundId: {}，availAmount: {}，amount: {}",
                        fundPoolDTO.getFundId(), fundPoolDTO.getAvailAmount(), fundPoolDTO.getUseAmount());
                throw new ScmException("款项余额不足，请重新选择");
            }
        }

        if (totalAmount.compareTo(totalFundAmount) != 0) {
            log.error("checkFunds failed, totalAmount: {}, totalFundAmount: {}", totalAmount, totalFundAmount);
            throw new ScmException("资金计算错误，请联系工程师");
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
            return companyTenant.getProdClassSerialNo().equals(YesNoEnum.YES.getCode());
        }

        return false;
    }

    private void checkSalesOrderDetNull(SalesOrderDet salesOrderDet) {
        if (salesOrderDet.getQty() == null) {
            throw new ScmException("请输入数量");
        }

        if (salesOrderDet.getPrice() == null) {
            throw new ScmException("请输入单价");
        }

        if (salesOrderDet.getWeight() == null) {
            throw new ScmException("请输入重量");
        }

        if (salesOrderDet.getQty() == 0 && ObjectUtil.notEqual(TenantContext.getTenant(), "2")) {
            throw new ScmException("数量不能等于0");
        }
        if (salesOrderDet.getQty() < 0) {
            throw new ScmException("数量不能小于0");
        }
        if (salesOrderDet.getWeight() <= 0) {
            throw new ScmException("重量不能小于或等于0");
        }
        if (salesOrderDet.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ScmException("单价不能小于或等于0");
        }
        if (salesOrderDetService.queryAddPrice()) {
            if (ObjectUtil.isNull(salesOrderDet.getAddPrice())) {
                salesOrderDet.setAddPrice(BigDecimal.ZERO);
            }
            if (salesOrderDet.getAddPrice().compareTo(BigDecimal.ZERO) < 0 && ObjectUtil.notEqual(TenantContext.getTenant(), "2")) {
                throw new ScmException("加价不能小于0");
            }
        }
        if (salesOrderDetService.queryDeliverExpense()) {
            if (ObjectUtil.isNull(salesOrderDet.getDeliverExpense())) {
                salesOrderDet.setDeliverExpense(BigDecimal.ZERO);
            }

            if (salesOrderDet.getDeliverExpense().compareTo(BigDecimal.ZERO) < 0) {
                throw new ScmException("运费不能小于0");
            }
        }
        if (salesOrderDetService.queryIfShowBasicPrice()) {
            if (ObjectUtil.isNull(salesOrderDet.getBasicPrice())) {
                throw new ScmException("基础单价不能为空");
            }
            if (salesOrderDet.getBasicPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new ScmException("基础单价不能小于0");
            }
        }

        //长宽厚为空默认赋0
        salesOrderDet.setOrderLen(ScmNumberUtils.ifNullZero(salesOrderDet.getOrderLen()));
        salesOrderDet.setOrderWidth(ScmNumberUtils.ifNullZero(salesOrderDet.getOrderWidth()));
        salesOrderDet.setOrderThick(ScmNumberUtils.ifNullZero(salesOrderDet.getOrderThick()));

        if (salesOrderDet.getOrderLen() < 0) {
            throw new ScmException("长度不能小于0");
        }
        if (salesOrderDet.getOrderWidth() < 0) {
            throw new ScmException("宽度不能小于0");
        }
        if (salesOrderDet.getOrderThick() < 0) {
            throw new ScmException("厚度不能小于0");
        }

    }


    /**
     * 生成装车单
     *
     * @param salesOrder
     * @param salesOrderDetList
     * @throws ParseException
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateMain(SalesOrder salesOrder, List<SalesOrderDet> salesOrderDetList) {

        LockInfo lockInfo = lockService.lock(Constants.CUSTOMER_LOCK_HEADER + salesOrder.getCustomerId());

        try {

            if (StatusEnum.WRITE_OFF.getCode().equals(salesOrder.getStatus())) {
                throw new ScmException("红冲状态不可操作");
            }

            //判断明细是否为空
            if (CollectionUtil.isEmpty(salesOrderDetList)) {
                log.error(StrUtil.format("提单明细为空，salesOrderId：{}", salesOrder.getId()));
                throw new ScmException("提单明细不能为空");
            }

//            for (SalesOrderDet salesOrderDet : salesOrderDetList) {
//                if (!salesOrderDet.getParentId().equalsIgnoreCase(salesOrder.getId())){
//                    log.error(StrUtil.format("提单与明细不对应，salesOrderId：{}", salesOrder.getId()));
//                    throw new ScmException("提单与明细不对应，请刷新后重试");
//                }
//            }


            gdUpdateTheoryPrice(salesOrderDetList);

            //生成装车单
            stackService.addStackBySaleOrder(salesOrder, salesOrderDetList);
            //更新提单主表数据   设置销售单生成状态 是
            salesOrder.setCreateStackStatus(YesNoEnum.YES.getCode());
            //修改前的总价 修改后的总价
            BigDecimal nowTotalPrice = BigDecimal.ZERO;

            int count = 0;
            for (SalesOrderDet salesOrderDet : salesOrderDetList) {
                count++;
                if (ObjectUtil.notEqual(TenantContext.getTenant(), "11") && ObjectUtil.notEqual(TenantContext.getTenant(), "2") && (salesOrderDet.getQty() < 0 || salesOrderDet.getWeight() < 0)) {
                    throw new ScmException("数量不能小于或等于0");
                }

                //总重量判断
                weightComputation(salesOrderDet.getWeight(), salesOrderDet.getQty(), salesOrderDet.getMatTheoryWt(), count, salesOrderDet);

                SalesOrderDet existSalesOrderDet = salesOrderDetService.getById(salesOrderDet.getId());
                //处理库存量
                Inventory inventory = inventoryService.getById(existSalesOrderDet.getInventoryId());
                //计算库存总数
                if (inventory.getAvailableQty() < (salesOrderDet.getQty() - existSalesOrderDet.getQty())) {
                    throw new ScmException("库存材料可用数量不足！" + "库存总数：" + inventory.getAvailableQty() + "增加量：" + (salesOrderDet.getQty() - existSalesOrderDet.getQty()));
                }

                // 岑海  提单新增销售单 件数不能超过原提单的件数
                if ("2".equals(TenantContext.getTenant())) {
                    if (existSalesOrderDet.getQty() < salesOrderDet.getQty()) {
                        throw new ScmException("新增销售单件数大于原提单件数！" + "新增销售单件数" + salesOrderDet.getQty() +
                                "原提单件数" + existSalesOrderDet.getQty());
                    }
                }

                Integer tenantId = Integer.valueOf(TenantContext.getTenant());
                QueryWrapper<CompanyTenantDet> companyTenantDetQueryWrapper = new QueryWrapper<>();
                companyTenantDetQueryWrapper.lambda().eq(CompanyTenantDet::getTenantCode, tenantId);

                CompanyTenantDet companyTenantDet = companyTenantDetService.getOne(companyTenantDetQueryWrapper);
                String parentId = companyTenantDetMapper.selectOne(companyTenantDetQueryWrapper).getParentId();

                QueryWrapper<CompanyTenant> companyTenantQueryWrapper = new QueryWrapper<>();
                companyTenantQueryWrapper.lambda().eq(CompanyTenant::getId, parentId);

                CompanyTenant companyTenant = companyTenantService.getById(companyTenantDet.getParentId());
                if (ObjectUtil.isNotNull(companyTenantDet) && companyTenant.getAverageCostCheck().equals(YesNoEnum.YES.getCode())) {
                    averageCostService.averageCostPrice(salesOrder.getCreateTime(), salesOrderDet.getProdClassCode(), salesOrderDet.getPrice(), count);
                }
                //数量、重量  处理实际量
                double qty = salesOrderDet.getQty();
                double weight = inventoryWeight(salesOrderDet);
                inventoryService.deductionPractical(inventory, qty, weight);
                //可销售数量、可销售重量  处理可销售量
                double availableQty = salesOrderDet.getQty() - existSalesOrderDet.getQty();
                double availableWeight = inventoryWeight(salesOrderDet) - inventoryWeight(existSalesOrderDet);
                inventoryService.deductionAvailable(inventory, availableQty, availableWeight);

                //计算 修改前的总价 修改后的总价
                if ("12".equalsIgnoreCase(TenantContext.getTenant())
                        && ProdClassCodeEnum.B.getValue().equalsIgnoreCase(salesOrderDet.getProdClassCode())) {
                    nowTotalPrice = nowTotalPrice.add(BigDecimalUtils.multiply(salesOrderDet.getTheoryPrice(), salesOrderDet.getTheoryWeight()));
                } else {
                    nowTotalPrice = nowTotalPrice.add(BigDecimalUtils.multiply(salesOrderDet.getPrice(), salesOrderDet.getWeight()));
                }

            }

            if (YesNoEnum.YES.getCode().equalsIgnoreCase(salesOrder.getEquipmentSuppliesFlag())) {
                BigDecimal equipmentTotalPrice = BigDecimal.ZERO;
                //设备物资标识总价计算方式特殊，先将相同价格明细重量汇总，然后乘以价格，最后将不同价格的明细累加得到总价
                Map<EquipmentAndMaterialsNetworkMergeFields, BigDecimal> priceAndWeight = newHashMap();

                for (SalesOrderDet salesOrderDet : salesOrderDetList) {
                    EquipmentAndMaterialsNetworkMergeFields equipmentAndMaterialsNetworkMergeFields = new EquipmentAndMaterialsNetworkMergeFields();
                    equipmentAndMaterialsNetworkMergeFields.setPrice(salesOrderDet.getPrice());
                    equipmentAndMaterialsNetworkMergeFields.setOrderThick(salesOrderDet.getOrderThick());
                    equipmentAndMaterialsNetworkMergeFields.setOldProdCname(salesOrderDet.getOldProdCname());

                    salesOrderDet.setWeightBigDecimal(BigDecimal.valueOf(salesOrderDet.getWeight()));

                    priceAndWeight.merge(equipmentAndMaterialsNetworkMergeFields, salesOrderDet.getWeightBigDecimal(), BigDecimal::add);
                }

                for (Map.Entry<EquipmentAndMaterialsNetworkMergeFields, BigDecimal> entry : priceAndWeight.entrySet()) {
                    equipmentTotalPrice = equipmentTotalPrice.add(BigDecimalUtils.multiply(entry.getKey().getPrice(), entry.getValue()));
                }

                BigDecimal diffAmount = equipmentTotalPrice.subtract(nowTotalPrice);
                BigDecimal firstSaleOrderPrice = salesOrderDetList.get(0).getTotalPrice();
                salesOrderDetList.get(0).setTotalPrice(firstSaleOrderPrice.add(diffAmount));


                QueryWrapper<Stack> stackQueryWrapper = new QueryWrapper<>();
                stackQueryWrapper.lambda().eq(Stack::getSalesOrderId, salesOrder.getId());
                Stack stack = stackService.getOne(stackQueryWrapper);

                stack.setTotalAmount(stack.getTotalAmount().add(diffAmount));

                List<StackDet> stackDetList = stackDetService.selectByStackId(stack.getId());

                BigDecimal firstStackDetPrice = stackDetList.get(0).getTotalAmount();

                stackDetList.get(0).setTotalAmount(firstStackDetPrice.add(diffAmount));
                nowTotalPrice = equipmentTotalPrice;

                stackService.updateById(stack);
                stackDetService.updateById(stackDetList.get(0));

            }
            //扣来款可用金额  ** nowTotalPrice
            for (SalesOrderDet salesOrderDet : salesOrderDetList) {
                //通过明细id查询为修改前的数据
                SalesOrderDet previousSalesOrderDet = salesOrderDetMapper.selectById(salesOrderDet.getId());
                //设置库存材料id
                salesOrderDet.setInventoryId(previousSalesOrderDet.getInventoryId());
            }
            SalesOrder salesOrderInDb = getById(salesOrder.getId());

            List<FundPoolDTO> fundPoolDTOList;
            if (salesOrder.getPaymentFlag().equalsIgnoreCase(PaymentFlagEnum.PAID.getCode()) && !"pre".equals(salesOrder.getStatus())) {

                //提单控款
                //查询所有的预用记录
                QueryWrapper<FundDetail> fundDetailQueryWrapper = new QueryWrapper<>();
                fundDetailQueryWrapper.lambda().eq(FundDetail::getOutTradeNo, salesOrder.getId())
                        .eq(FundDetail::getType, FundDetailTypeEnum.PRE_USE.getCode());
                List<FundDetail> preUseFundDetailList = fundDetailService.list(fundDetailQueryWrapper);

                fundPoolDTOList = fundService.getFundDTOByPreUseDetail(preUseFundDetailList);

                fundPoolDTOList.addAll(fundService.getFundPoolDTOByFundIds(preUseFundDetailList,
                        salesOrderInDb.getAutoSelectFund().equalsIgnoreCase(YesNoEnum.YES.getCode()), salesOrderInDb.generateFundIdList(), salesOrderInDb.getCustomerId()));

                //总预用
                BigDecimal totalPreUse = BigDecimal.ZERO;

                //来款总可用
                BigDecimal totalAvailAmount = BigDecimal.ZERO;

                for (FundPoolDTO fundPoolDTO : fundPoolDTOList) {
                    totalPreUse = totalPreUse.add(fundPoolDTO.getSalesOrderPreUseAmount());

                    totalAvailAmount = totalAvailAmount.add(fundPoolDTO.getAvailAmount());
                }

                //补款金额 如果为负数则表示 需要退款
                BigDecimal needAddAmount;

                //预用款项 + 可用款项 大于等于装车单总金额
                if (totalPreUse.add(totalAvailAmount).compareTo(nowTotalPrice) >= 0) {

                    needAddAmount = nowTotalPrice.subtract(totalPreUse);

                    //重新选择来款
                    fundPoolDTOList = fundService.reSelectFundAmount(needAddAmount, fundPoolDTOList);

                } else {
                    throw new ScmException("所选款项不足覆盖装车单金额，请重新选款");

                }

                //删除所有的提单预用记录
                fundDetailService.cleanUnloadRecord(salesOrder.getId());

            } else {
                //阳蕊明 提单不控款的场景
                fundPoolDTOList = fundService.autoSelectFundPool(nowTotalPrice, salesOrder.getCustomerId(), newArrayList());

            }
            //更新款项数据
            fundService.geneStackFundDetail(fundPoolDTOList, salesOrder.getCustomerId(), salesOrder.getId());

            salesOrder.setTotalAmount(nowTotalPrice);
            salesOrderMapper.updateById(salesOrder);

            if (Constants.YRM_TENANT_IDS.contains(TenantContext.getTenant())) {
                updateYRMCanLadingBill(salesOrder.getCustomerId());
            }
        } finally {
            lockService.releaseLock(lockInfo);

        }
    }

    private void gdUpdateTheoryPrice(List<SalesOrderDet> salesOrderDetList) {
        for (SalesOrderDet det : salesOrderDetList) {
            if ("12".equalsIgnoreCase(TenantContext.getTenant())
                    && ProdClassCodeEnum.B.getValue().equalsIgnoreCase(det.getProdClassCode())) {
                det.setTotalPrice(BigDecimalUtils.multiply(det.getTheoryPrice(), det.getTheoryWeight()));
            }
        }
    }


    /**
     * 编辑提单
     *
     * @param salesOrder
     * @param salesOrderDets
     */
    @Transactional(rollbackFor = Exception.class)
    public void editSalesOrder(SalesOrder salesOrder, List<SalesOrderDet> salesOrderDets) {

        LockInfo lockInfo = lockService.lock(Constants.CUSTOMER_LOCK_HEADER + salesOrder.getCustomerId());

        try {
            //明细的仓库和主表的仓库不一致，不能修改
            if (!TenantContext.getTenant().equalsIgnoreCase("10")) {
                checkTheSameStockId(salesOrder, salesOrderDets);
            }


            checkOrderSalesDet(salesOrderDets);

            //修改主表信息
            editConsigneeAndCarNo(salesOrder);

            //已空款提单不支持明细编辑

            if (salesOrder.getPaymentFlag().equalsIgnoreCase(PaymentFlagEnum.PAID.getCode())) {
                return;
            }
            SalesOrder salesOrder1 = getById(salesOrder.getId());

            if (StrUtil.isNotBlank(salesOrder1.getPushLogistics()) && salesOrder1.getPushLogistics().equals(LogisticsEnum.LOGISTICS.getCode())) {
                throw new ScmException("推送物流园提单暂时不支持修改");
            }

            if (salesOrder1.getCreateStackStatus().equalsIgnoreCase(YesNoEnum.YES.getCode())) {
                return;
            }
//            for (SalesOrderDet salesOrderDet : salesOrderDets) {
//                if (StrUtil.isNotEmpty(salesOrderDet.getParentId()) && (!salesOrderDet.getParentId().equalsIgnoreCase(salesOrder.getId()))){
//                    log.error(StrUtil.format("提单与明细不对应，salesOrderId：{}", salesOrder.getId()));
//                    throw new ScmException("提单与明细不对应，请刷新后重试");
//                }
//            }
            List<SalesOrderDet> oldSalesOrderDets = salesOrderDetService.getListByParentId(salesOrder.getId());
            BigDecimal oldTotalPrice = BigDecimal.ZERO;
            BigDecimal totalPrice = BigDecimal.ZERO;
            BigDecimal totalWeight = BigDecimal.ZERO;
            //删除明细
            for (SalesOrderDet salesOrderDet : oldSalesOrderDets) {
                //退预交
                log.info("deductionAvailableById{}", salesOrderDet.getId());
                double weight = inventoryWeight(salesOrderDet);
                inventoryService.deductionAvailableById(salesOrderDet.getInventoryId(), -salesOrderDet.getQty(), -weight);
                //计算旧订单总金额
                oldTotalPrice = oldTotalPrice.add(salesOrderDet.getTotalPrice());

                salesOrderDetService.removeById(salesOrderDet.getId());

            }

            //新增明细
            if (CollectionUtil.isNotEmpty(salesOrderDets)) {
                for (SalesOrderDet salesOrderDet : salesOrderDets) {

                    log.info("salesOrderDets{}", salesOrderDets);

                    checkSalesOrderDetNull(salesOrderDet);

                    totalWeight = totalWeight.add(BigDecimal.valueOf(salesOrderDet.getWeight()));

                    //外键设置
                    salesOrderDet.setParentId(salesOrder.getId());

                    //将旧订单数据传过来，此处清空。
                    salesOrderDet.setId(null);
                    salesOrderDet.setCreateBy(null);
                    salesOrderDet.setCreateTime(null);
                    salesOrderDet.setUpdateBy(null);
                    salesOrderDet.setUpdateTime(null);

                    Inventory inventory = inventoryService.getById(salesOrderDet.getInventoryId());
                    salesOrderDet.setUnit(inventory.getUnit());

                    if (ObjectUtil.isNull(inventory)) {
                        throw new ScmException("仓库无库存");
                    }
                    //计算库存总数
                    if (inventory.getAvailableQty() < salesOrderDet.getQty()) {
                        throw new ScmException("库存材料可用数量不足！" + "库存总数：" + inventory.getAvailableQty() + "下单数：" + salesOrderDet.getQty());
                    }

                    double weight = inventoryWeight(salesOrderDet);
                    inventoryService.deductionAvailable(inventory, salesOrderDet.getQty(), weight);
                    BigDecimal detTotalPrice = BigDecimalUtils.multiply(salesOrderDet.getPrice(), salesOrderDet.getWeight());
                    salesOrderDet.setTotalPrice(detTotalPrice);
                    salesOrderDet.setInventoryId(inventory.getId());
                    salesOrderDet.setOriginWeight(salesOrderDet.getWeight());
                    salesOrderDet.setOriginQty(salesOrderDet.getQty());
                    salesOrderDet.setStockLocation(inventory.getStockLocation());
                    salesOrderDetMapper.insert(salesOrderDet);

                    totalPrice = totalPrice.add(detTotalPrice);
                }
                salesOrder.setTotalAmount(totalPrice.setScale(2, RoundingMode.HALF_UP));
                salesOrder.setWeight(totalWeight.doubleValue());
                updateById(salesOrder);
            } else {
                throw new ScmException("请录入明细信息");
            }
        } finally {
            lockService.releaseLock(lockInfo);
        }


    }

    @Transactional(rollbackFor = Exception.class)
    public void delMain(String id) {


        //判断是否生成过装车单
        SalesOrder existSalesOrder = getById(id);
        if (StatusEnum.WRITE_OFF.getCode().equals(existSalesOrder.getStatus())) {
            throw new ScmException("红冲状态的提单不允许删除");
        }

        QueryWrapper<Stack> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Stack::getSaleBillNo, existSalesOrder.getSaleBillNo());
        Stack stack = stackService.getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(stack)) {
            throw new ScmException("该提单已经生成过装车单，不允许删除!");
        }


        SalesOrder salesOrder = salesOrderMapper.selectById(id);

        LockInfo lockInfo = lockService.lock(Constants.CUSTOMER_LOCK_HEADER + salesOrder.getCustomerId());

        try {
            List<SalesOrderDet> salesOrderDets = salesOrderDetMapper.selectByMainId(id);

            //合同控量租户ID校验
            QueryWrapper<CustomerConfiguration> customerConfigWrapper = new QueryWrapper<>();
            customerConfigWrapper.lambda().eq(CustomerConfiguration::getTenantId, TenantContext.getTenant());
            List<CustomerConfiguration> customerConfigList = customerConfigurationService.list(customerConfigWrapper);
            if (customerConfigList.size() > 1) {
                log.error(StrUtil.format("当前租户为：{}，合同控量中存在{}条合同控量配置记录！", TenantContext.getTenant(), customerConfigList.size()));
                throw new ScmException(StrUtil.format("当前租户为：{}，合同控量中存在{}条合同控量配置记录！", TenantContext.getTenant(), customerConfigList.size()));
            }
            //删除提单减扣，合同明细预交量
            if (CollectionUtil.isNotEmpty(customerConfigList) &&
                    customerConfigList.get(0).getFlag().equals(YesNoEnum.YES.getCode())) {
                contractDetService.subPreAndDeliverAmount(salesOrderDets);
            }
            //删除提单退款
            if (CollectionUtil.isNotEmpty(salesOrderDets)) {

                for (SalesOrderDet salesOrderDet : salesOrderDets) {

                    Inventory inventory = inventoryService.getById(salesOrderDet.getInventoryId());
                    double weight = inventoryWeight(salesOrderDet);
                    inventoryService.deductionAvailable(inventory, -salesOrderDet.getQty(), -weight);
                }
            }
            QueryWrapper<SalesOrderDet> salesOrderDetQueryWrapper = new QueryWrapper<>();
            salesOrderDetQueryWrapper.lambda().eq(SalesOrderDet::getParentId, id);
            List<SalesOrderDet> salesOrderDetList = salesOrderDetService.list(salesOrderDetQueryWrapper);
            List<String> salesOrderDetIds = salesOrderDetList.stream().map(SalesOrderDet::getId).collect(Collectors.toList());

            salesOrderDetService.removeByIds(salesOrderDetIds);
            removeById(id);

            if (salesOrder.getPaymentFlag().equalsIgnoreCase(PaymentFlagEnum.PAID.getCode())) {
                fundDetailService.cleanUnloadRecord(salesOrder.getId());
            }


            if (StrUtil.isNotBlank(salesOrder.getPushLogistics()) && salesOrder.getPushLogistics().equals(LogisticsEnum.LOGISTICS.getCode())) {


                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

                try {
                    NormalRequest.NormalResponse response = wmsService.revokeBillOfLadingPlan(RevokeBillOfLadingPlanParam.builder()
                            .mainId(salesOrder.getId())
                            .billOFLadingNo(salesOrder.getSaleBillNo())
                            .operatorName(sysUser.getRealname())
                            .build());

                    if (response.getState() != 1) {
                        throw new ScmException(response.getMessage());
                    }
                } catch (WMSException e) {
                    log.error("删除提单失败,提单id:{}", salesOrder.getId(), e);
                    throw new ScmException(StrUtil.format("删除提单失败"));
                }

            } else if (StrUtil.isNotBlank(salesOrder.getPushLogistics()) && salesOrder.getPushLogistics().equals(LogisticsEnum.LOGISTICS_TRA.getCode())) {


                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

                try {
                    NormalRequest.NormalResponse response = wmsService.revokeTransferOwnershipPlan(RevokeTransferOwnershipPlanParam.builder()
                            .transferOwnershipPlanNo(salesOrder.getSaleBillNo())
                            .operatorName(sysUser.getRealname())
                            .build());

                    if (response.getState() != 1) {
                        throw new ScmException(response.getMessage());
                    }
                } catch (WMSException e) {
                    log.error("删除提单失败,提单id:{}", salesOrder.getId(), e);
                    throw new ScmException(StrUtil.format("删除提单失败"));
                }

            }

            if (StrUtil.isNotEmpty(salesOrder.getPrepareOrderId())) {
                prepareOrderService.deleteSalesOrder(salesOrder.getPrepareOrderId());
            }


        } finally {
            lockService.releaseLock(lockInfo);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            delMain((String) id);
        }
    }

    public SalesOrder getBySalesBillNo(String salesBillNo) {
        if (StrUtil.isBlank(salesBillNo)) {
            return null;
        }
        QueryWrapper<SalesOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SalesOrder::getSaleBillNo, salesBillNo);
        List<SalesOrder> salesOrders = list(queryWrapper);

        if (CollectionUtil.isNotEmpty(salesOrders)) {
            return salesOrders.get(0);
        } else {
            return null;
        }
    }

    public List<SalesOrderSevenInfo> sevenDaySalesOrder() {
        //获取七天前的日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -7);
        Date sevenDaysAgoDate = calendar.getTime();

        Integer delFlag = 0;

        //查询出过去七天的所有数据
        QueryWrapper<Stack> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ge(Stack::getCreateTime, sevenDaysAgoDate).eq(Stack::getDelFlag, delFlag);
        List<Stack> StackList = stackService.list(queryWrapper);
        if (CollectionUtils.isEmpty(StackList)) {
            return null;
        }

        List<SalesOrderSevenInfo> salesOrderSevenInfoList = newArrayList();
        BigDecimal numberOfPayments = BigDecimal.ZERO;
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
            SalesOrderSevenInfo salesOrderSevenInfo = new SalesOrderSevenInfo();
            salesOrderSevenInfo.setDate(yearMonthDayInt);
            salesOrderSevenInfo.setNumberOfPayments(numberOfPayments);
            salesOrderSevenInfo.setDateString(daysAgoDateString);
            salesOrderSevenInfoList.add(salesOrderSevenInfo);
        }

        for (Stack stack : StackList) {
            String dateString = simpleDateFormat.format(stack.getCreateTime());
            String year = dateString.substring(0, 4);
            String month = dateString.substring(dateString.indexOf("-") + 1, dateString.lastIndexOf("-"));
            String day = dateString.substring(dateString.lastIndexOf("-") + 1);
            String yearMonthDay = year + month + day;
            int yearMonthDayInt = Integer.parseInt(yearMonthDay);

            BigDecimal bigDecimalOne = BigDecimal.ONE;
            for (SalesOrderSevenInfo salesOrderSevenInfo : salesOrderSevenInfoList) {
                if (salesOrderSevenInfo.getDate() == yearMonthDayInt) {
                    salesOrderSevenInfo.setNumberOfPayments(salesOrderSevenInfo.getNumberOfPayments().add(bigDecimalOne));
                }
            }
        }

        return salesOrderSevenInfoList;
    }

    /**
     * 修改提货人和车号
     */
    @Transactional(rollbackFor = Exception.class)
    public void editConsigneeAndCarNo(SalesOrder salesOrder) {
        //更新提单

        SalesOrder salesOrder1 = getById(salesOrder.getId());
        salesOrder1.setCustomerId(salesOrder.getCustomerId());
        salesOrder1.setConsigneeName(salesOrder.getConsigneeName());
        salesOrder1.setRemark(salesOrder.getRemark());
        salesOrder1.setIdCard(salesOrder.getIdCard());
        salesOrder1.setPhone(salesOrder.getPhone());
        salesOrder1.setDestination(salesOrder.getDestination());
        salesOrder1.setSalesMan(salesOrder.getSalesMan()); // 修改提单页面增添业务员 addBy liujiazhi 2021.3.22
        salesOrder1.setReceivingName(salesOrder.getReceivingName());
        salesOrder1.setReceivingAddress(salesOrder.getReceivingAddress());
        salesOrder1.setReceivingPhone(salesOrder.getReceivingPhone());
        salesOrder1.setOrderTime(salesOrder.getOrderTime());
        salesOrder1.setCarNo(salesOrder.getCarNo());
        salesOrder1.setShipperName(salesOrder.getShipperName());
        salesOrder1.setFundDesc(salesOrder.getFundDesc());
        salesOrder1.setFleetName(salesOrder.getFleetName());
        salesOrder1.setAttach(salesOrder.getAttach());
        updateById(salesOrder1);

        //查询该提单是否生成销售单
        SalesOrder exist = getById(salesOrder.getId());
        if (StatusEnum.WRITE_OFF.getCode().equals(exist.getStatus())) {
            throw new ScmException("红冲状态不可操作！");
        }
        QueryWrapper<Stack> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(Stack::getSaleBillNo, exist.getSaleBillNo());
        Stack stack = stackService.getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(stack)) {
            //已生成销售单
            stack.setConsigneeName(salesOrder.getConsigneeName());
            stack.setCustomerId(salesOrder.getCustomerId());
            stack.setRemark(salesOrder.getRemark());
            stack.setIdCard(salesOrder.getIdCard());
            stack.setPhone(salesOrder.getPhone());
            stack.setDestination(salesOrder.getDestination());
            stack.setSalesMan(salesOrder.getSalesMan()); // 修改提单页面增添业务员 addBy liujiazhi 2021.3.22
            stack.setStockNoClass(salesOrder.getStockId());
            stack.setReceivingAddress(salesOrder.getReceivingAddress());
            stack.setReceivingName(salesOrder.getReceivingName());
            if (StrUtil.isNotEmpty(salesOrder.getCarNo())) {
                stack.setCarNo(salesOrder.getCarNo());
            }
            stackService.updateById(stack);
        }
    }

    /**
     * 一张销售单不同仓库的货不能生成一张装车单(阳蕊明)
     */
    public void checkOrderSalesDet(List<SalesOrderDet> salesOrderDetList) {

        if (Constants.YRM_TENANT_IDS.contains(TenantContext.getTenant()) || "2".equalsIgnoreCase(TenantContext.getTenant())) {
            for (SalesOrderDet salesOrderDet : salesOrderDetList) {
                if (!salesOrderDet.getStockId().equals(salesOrderDetList.get(0).getStockId())) {
                    throw new ScmException("一张销售单不同仓库的货不能生成一张装车单!");
                }
            }
        }
    }

    //明细的仓库和主表的仓库不一致，不能修改
    public void checkTheSameStockId(SalesOrder salesOrder, List<SalesOrderDet> salesOrderDets) {
        int count = 0;
        for (SalesOrderDet salesOrderDet : salesOrderDets) {
            count++;
            if (!salesOrder.getStockId().equals(salesOrderDet.getStockId())) {
                throw new ScmException(StrUtil.format("第{}条明细仓库与主表仓库不一致，请重新设置", count));
            }
        }
    }

    public void updatePageList(IPage<SalesOrder> pageList) {
        if (Constants.YRM_TENANT_IDS.contains(TenantContext.getTenant())) {
            List<String> customerIdList = newArrayList();
            for (SalesOrder salesOrder : pageList.getRecords()) {
                customerIdList.add(salesOrder.getCustomerId());
            }
            Map<String, FundAccountDTO> fundAccountDTOMap = newHashMap();
            for (String customerId : customerIdList) {
                FundAccountDTO fundAccountDTO = fundService.getFundAccountDTO(customerId);
                fundAccountDTOMap.put(customerId, fundAccountDTO);
            }

            for (SalesOrder salesOrder : pageList.getRecords()) {
                FundAccountDTO fundAccountDTO = fundAccountDTOMap.get(salesOrder.getCustomerId());
                salesOrder.setIncomeAvailableAccount(fundAccountDTO.getIncomeAvailAmount());
                salesOrder.setCreditAvailableAccount(fundAccountDTO.getCreditAvailAbleAmount());
                salesOrder.setTotalAccountYRM(salesOrder.getTotalAmount());
                BigDecimal customerNeedToPayAmount = salesOrder.getTotalAmount().subtract(salesOrder.getIncomeAvailableAccount());
                if (customerNeedToPayAmount.compareTo(BigDecimal.ZERO) > 0) {
                    salesOrder.setCustomerNeedToPayAmount(customerNeedToPayAmount);
                } else {
                    salesOrder.setCustomerNeedToPayAmount(BigDecimal.ZERO);
                }

            }

        }
    }

    public void checkPriceAccuracy(SalesOrderDet salesOrderDet) {
        BigDecimal deliverExpense = salesOrderDet.getDeliverExpense();
        BigDecimal deliverTotalPrice = salesOrderDet.getDeliverTotalPrice();
        BigDecimal basicPrice = salesOrderDet.getBasicPrice();
        BigDecimal price = salesOrderDet.getPrice();
        BigDecimal totalPrice = salesOrderDet.getTotalPrice();
        BigDecimal addPrice = salesOrderDet.getAddPrice();
        Double weight = salesOrderDet.getWeight();
        //显示基础单价和运费，不显示加价，判断提单明细价格计算是否正确
        if (salesOrderDetService.queryIfShowBasicPrice() && salesOrderDetService.queryDeliverExpense() && !salesOrderDetService.queryAddPrice()) {
            BigDecimal virtualPrice = deliverExpense.add(basicPrice);
            if (virtualPrice.compareTo(price) != 0) {
                throw new ScmException("单价计算有误");
            }
            BigDecimal totalPriceBigDecimal = BigDecimalUtils.multiply(weight, virtualPrice);
            if (totalPriceBigDecimal.compareTo(totalPrice) != 0) {
                throw new ScmException("总价计算有误");
            }
            BigDecimal deliverTotalPriceBigDecimal = BigDecimalUtils.multiply(weight, virtualPrice);
            if (deliverTotalPriceBigDecimal.compareTo(deliverTotalPrice) != 0) {
                throw new ScmException("运费总价计算有误");
            }
        }
        //显示基础单价、运费和加价，判断提单明细价格计算是否正确
        if (salesOrderDetService.queryIfShowBasicPrice() && salesOrderDetService.queryAddPrice() && salesOrderDetService.queryDeliverExpense()) {
            BigDecimal virtualPrice = deliverExpense.add(basicPrice).add(addPrice);
            if (virtualPrice.compareTo(price) != 0) {
                throw new ScmException("单价计算有误");
            }
            BigDecimal totalPriceBigDecimal = BigDecimalUtils.multiply(weight, virtualPrice);
            if (totalPriceBigDecimal.compareTo(totalPrice) != 0) {
                throw new ScmException("总价计算有误");
            }
            BigDecimal deliverTotalPriceBigDecimal = BigDecimalUtils.multiply(weight, deliverExpense);
            if (deliverTotalPriceBigDecimal.compareTo(deliverTotalPrice) != 0) {
                throw new ScmException("运费总价计算有误");
            }
        }
        //显示基础单价和加价，不显示运费，判断提单明细价格计算是否正确
        if (salesOrderDetService.queryIfShowBasicPrice() && salesOrderDetService.queryAddPrice()) {
            BigDecimal virtualPrice = basicPrice.add(addPrice);
            if (virtualPrice.compareTo(price) != 0) {
                throw new ScmException("单价计算有误");
            }
            BigDecimal totalPriceBigDecimal = BigDecimalUtils.multiply(weight, virtualPrice);
            if (totalPriceBigDecimal.compareTo(totalPrice) != 0) {
                throw new ScmException("总价计算有误");
            }
        }
    }

    public CheckCarNoVO checkCarNo(String id) {
        String tenantId = TenantContext.getTenant();
        CheckCarNoVO checkCarNoVO = new CheckCarNoVO();
        checkCarNoVO.setBooleanValue(false);
        //只有广东分公司需要做校验
        if (tenantId.equals("12")) {
            SalesOrder salesOrder = getById(id);
            if (ObjectUtil.isNull(salesOrder)) {
                return null;
            }
            String carNo = salesOrder.getCarNo();

            QueryWrapper<SettleImport> settleImportQueryWrapper = new QueryWrapper<>();
            settleImportQueryWrapper.lambda().eq(SettleImport::getSaleBillNo, salesOrder.getSaleBillNo());
            List<SettleImport> settleImportList = settleImportService.list(settleImportQueryWrapper);
            if (CollectionUtil.isNotEmpty(settleImportList)) {
                String importCarNo = settleImportList.get(0).getCarNo();
                if (StringUtils.isBlank(carNo) || StringUtils.isBlank(importCarNo)) {
                    checkCarNoVO.setBooleanValue(true);
                    checkCarNoVO.setCarNo(importCarNo);
                } else if (!carNo.equals(importCarNo)) {
                    checkCarNoVO.setBooleanValue(true);
                    checkCarNoVO.setCarNo(importCarNo);
                }
            }

        }

        return checkCarNoVO;
    }

    public List<OptionVO> selectPushDestination() {
        List<OptionVO> optionVOList = newArrayList();

        try {
            PaginatedRequest.PaginatedRequestModel<GetAddressCondition> requestBody = new PaginatedRequest.PaginatedRequestModel<>();
            requestBody.setPageSize(99999);
            requestBody.setPageNumber(1);
            requestBody.setCondition(GetAddressCondition.builder().build());
            PaginatedRequest.PaginatedResponseModel getAddress = wmsService.getAddress(requestBody);
            for (Map<String, Object> pushDestination : getAddress.getData()) {
                OptionVO optionVO = new OptionVO();
                optionVO.setText(pushDestination.get("address") == null ? null : pushDestination.get("address").toString());
                optionVO.setValue(pushDestination.get("address") == null ? null : pushDestination.get("address").toString());
                optionVOList.add(optionVO);
            }
        } catch (WMSException e) {
            log.error("selectPushDestination error", e);
        }


        return optionVOList;
    }

    public List<OptionVO> selectPushCarNo() {
        List<OptionVO> optionVOList = newArrayList();

        try {
            PaginatedRequest.PaginatedRequestModel<GetPlateNoCondition> requestBody = new PaginatedRequest.PaginatedRequestModel<>();
            requestBody.setPageSize(999);
            requestBody.setPageNumber(1);
            requestBody.setCondition(GetPlateNoCondition.builder().build());
            PaginatedRequest.PaginatedResponseModel getPlateNo = wmsService.getPlateNo(requestBody);
            for (Map<String, Object> pushCarNo : getPlateNo.getData()) {
                OptionVO optionVO = new OptionVO();
                optionVO.setText(pushCarNo.get("plateNo") == null ? null : pushCarNo.get("plateNo").toString());
                optionVO.setValue(pushCarNo.get("plateNo") == null ? null : pushCarNo.get("plateNo").toString());
                optionVOList.add(optionVO);
            }
        } catch (WMSException e) {
            log.error("selectPushCarNo error", e);
        }

        return optionVOList;

    }

    /**
     * 修改提单状态为待推送
     */
    @Transactional(rollbackFor = Exception.class)
    public void waitPush(List<String> salesOrderIdList) {

        if (CollectionUtils.isEmpty(salesOrderIdList)) {
            throw new ScmException(StrUtil.format("请选择待推送数据"));
        }

        QueryWrapper<SalesOrder> salesOrderQueryWrapper = new QueryWrapper<>();
        salesOrderQueryWrapper.lambda().in(SalesOrder::getId, salesOrderIdList);

        List<SalesOrder> salesOrderList = list(salesOrderQueryWrapper);
        for (SalesOrder salesOrder : salesOrderList) {

            checkPush(salesOrder);

            if (salesOrder.getPushLogistics().equalsIgnoreCase(LogisticsEnum.WAIT_PUSH.getCode())) {
                throw new ScmException(StrUtil.format("提单{}已添加推送队列，不需要再次添加", salesOrder.getSaleBillNo()));
            }
            salesOrder.setPushLogistics(LogisticsEnum.WAIT_PUSH.getCode());
            updateById(salesOrder);
        }
    }

    /**
     * 提单立刻推送物流园
     */
//    @Transactional(rollbackFor = Exception.class)
    public void pushLogisticsNow(String id) {
        SalesOrder salesOrder = getById(id);

        checkPush(salesOrder);

        try {
            String type = WmsTypeEnum.PUSH_ORDER.getCode();
            pushLogistics(id, type);
        } catch (ScmException e) {
            salesOrder.setPushLogistics(LogisticsEnum.LOGISTICS_ERR.getCode());
            salesOrder.setFailureCause(e.getMessage());
            updateById(salesOrder);
            log.error("推送失败，id：" + salesOrder.getId() + "原因：" + e.getMessage());
            throw new ScmException(StrUtil.format("提单{}推送失败，原因{}", salesOrder.getSaleBillNo(), e.getMessage()));
        }

    }

    /**
     * 物流园过户推送
     */
    public void pushTransfer(String id) {
        SalesOrder salesOrder = getById(id);

        checkPush(salesOrder);

        try {
            String type = WmsTypeEnum.PUSH_TRANSFER.getCode();
            pushLogistics(id, type);
        } catch (ScmException e) {
            salesOrder.setPushLogistics(LogisticsEnum.LOGISTICS_ERR.getCode());
            salesOrder.setFailureCause(e.getMessage());
            updateById(salesOrder);
            log.error("推送失败，id：" + salesOrder.getId());
            throw new ScmException(StrUtil.format("提单{}推送过户失败，原因{}", salesOrder.getSaleBillNo(), e));
        }

    }

    /**
     * 推送物流园 内容检查
     */
    public void checkPush(SalesOrder salesOrder) {

        QueryWrapper<SalesOrderDet> salesOrderDetQueryWrapper = new QueryWrapper<>();
        salesOrderDetQueryWrapper.lambda().eq(SalesOrderDet::getParentId, salesOrder.getId());
        List<SalesOrderDet> salesOrderDetList = salesOrderDetService.list(salesOrderDetQueryWrapper);

        if (!(salesOrder.getStockId().equals("1") || salesOrder.getStockId().equals("1486547246272360449"))) {
            throw new ScmException(StrUtil.format("提单{}不是物流园库/安正仓，请重新选择", salesOrder.getSaleBillNo()));
        }
        if (salesOrder.getPushLogistics().equalsIgnoreCase(LogisticsEnum.LOGISTICS.getCode()) || salesOrder.getPushLogistics().equalsIgnoreCase(LogisticsEnum.LOGISTICS_OK.getCode())) {
            throw new ScmException(StrUtil.format("提单{}已推送，请重新选择", salesOrder.getSaleBillNo()));
        } else if (salesOrder.getPushLogistics().equalsIgnoreCase(LogisticsEnum.LOGISTICS_ERR.getCode())) {
            salesOrder.setFailureCause("");
            updateById(salesOrder);
        }
        for (SalesOrderDet salesOrderDet : salesOrderDetList) {
            if (!(salesOrderDet.getProdClassCode().equals(ProdClassCodeEnum.B.getValue()) || salesOrderDet.getProdClassCode().equals(ProdClassCodeEnum.D.getValue()))) {
                throw new ScmException(StrUtil.format("推送物流园暂时只支持棒线", salesOrder.getSaleBillNo()));
            }
        }
    }

/**
 * 需要等待物流园开通信息更新接口.
 * 已推送数据修改信息(司机、车号等)

 @Transactional(rollbackFor = Exception.class)
 public void editLogistics(String id) {
 SalesOrder salesOrder = getById(id);
 if (!salesOrder.getPushLogistics().equalsIgnoreCase(LogisticsEnum.LOGISTICS.getCode())) {
 throw new ScmException(StrUtil.format("提单{}推送状态有误，请重新选择", salesOrder.getSaleBillNo()));
 }
 //需要等待修改接口

 }
 */

    /**
     * 提单撤销推送
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelLogistics(List<String> salesOrderIdList, String type) {

        if (CollectionUtils.isEmpty(salesOrderIdList)) {
            throw new ScmException(StrUtil.format("请选择推送数据"));
        }

        QueryWrapper<SalesOrder> salesOrderQueryWrapper = new QueryWrapper<>();
        salesOrderQueryWrapper.lambda().in(SalesOrder::getId, salesOrderIdList);

        List<SalesOrder> salesOrderList = list(salesOrderQueryWrapper);
        for (SalesOrder salesOrder : salesOrderList) {
            if (!(salesOrder.getStockId().equals("1") || salesOrder.getStockId().equals("1486547246272360449"))) {
                throw new ScmException(StrUtil.format("提单{}不是物流园库/安正仓，请重新选择", salesOrder.getSaleBillNo()));
            }

            if (salesOrder.getPushLogistics().equals(LogisticsEnum.LOGISTICS_ERR.getCode())) {
                //推送失败状态将状态改回不推送
                salesOrder.setPushLogistics(LogisticsEnum.NO_LOGISTICS.getCode());
                salesOrder.setFailureCause("");
                updateById(salesOrder);

            } else if (salesOrder.getPushLogistics().equals(LogisticsEnum.WAIT_PUSH.getCode())) {
                //待推送状态将状态改回不推送
                salesOrder.setPushLogistics(LogisticsEnum.NO_LOGISTICS.getCode());
                updateById(salesOrder);

            } else if (salesOrder.getPushLogistics().equals(LogisticsEnum.LOGISTICS.getCode()) || salesOrder.getPushLogistics().equals(LogisticsEnum.LOGISTICS_TRA.getCode())) {

                //已推送提单调用物流园撤销接口，然后更改状态为不推送
                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

                try {
                    NormalRequest.NormalResponse response;
                    if (type.equalsIgnoreCase(WmsTypeEnum.CANCEL_PUSH_ORDER.getCode())) {
                        response = wmsService.revokeBillOfLadingPlan(RevokeBillOfLadingPlanParam.builder()
                                .mainId(salesOrder.getId())
                                .billOFLadingNo(salesOrder.getSaleBillNo())
                                .operatorName(sysUser.getRealname())
                                .build());
                    } else {
                        response = wmsService.revokeTransferOwnershipPlan(RevokeTransferOwnershipPlanParam.builder()
                                .transferOwnershipPlanNo(salesOrder.getSaleBillNo())
                                .operatorName(sysUser.getRealname())
                                .build());
                    }
                    if (response.getState() != 1) {
                        throw new ScmException(response.getMessage());
                    }
                } catch (WMSException e) {
                    log.error("提单撤销失败", e);
                    throw new ScmException("提单撤销失败", e);
                }
                salesOrder.setPushLogistics(LogisticsEnum.NO_LOGISTICS.getCode());
                updateById(salesOrder);

            } else {
                throw new ScmException(StrUtil.format("提单{}推送状态有误，请重新选择", salesOrder.getSaleBillNo()));
            }
        }
    }


    /**
     * 提单推送物流园
     */
    @Transactional(rollbackFor = Exception.class)
    public void pushLogistics(String id, String type) {
        SalesOrder salesOrder = getById(id);

        QueryWrapper<SalesOrderDet> salesOrderDetQueryWrapper = new QueryWrapper<>();
        salesOrderDetQueryWrapper.lambda().eq(SalesOrderDet::getParentId, id);
        List<SalesOrderDet> salesOrderDetList = salesOrderDetService.list(salesOrderDetQueryWrapper);

        //判断是否生成装车单
        if (salesOrder.getCreateStackStatus().equalsIgnoreCase(YesNoEnum.YES.getCode())) {
            throw new ScmException("该提单已经生成装车单");
        }
        if (salesOrder.getPushLogistics().equals(LogisticsEnum.LOGISTICS.getCode())) {
            throw new ScmException("该提单已经推送");
        }

        //获取租户id
        String tenantId = "2";
        //查询控款配置
        CompanyTenantVo companyTenantVo = companyTenantMapper.queryPaymentConfiguration(Integer.valueOf(tenantId));

        try {

//            String property = environment.getProperty("spring.profiles.active");
            //只有是岑海、物流园仓、开单选择推送才会把提单推送给物流园
            //新增 安正仓 推送
            String stockCode;
            if ((salesOrder.getStockId().equals("1"))) {
                stockCode = "W01";
            } else if (salesOrder.getStockId().equals("1486547246272360449")) {
                stockCode = "WF1";
            } else {
                throw new ScmException("该提单不是物流园库/安正仓，请核对");
            }


            //推送提单明细
            List<SubmitBillOfLadingPlanParam.Detail> pushSalesOrderDetList = newArrayList();
            //推送过户明细
            List<SubmitTransferOwnershipPlanParam.Detail> transferSalesOrderDetList = newArrayList();

            //校验推送物流园的提单不能有重复规格的材料
            Map<String, List<SalesOrderDet>> checkSalesOrderDetMap = newHashMap();
            checkSalesOrderDetMap = salesOrderDetList.stream().collect(
                    Collectors.groupingBy(
                            score -> score.getOldProdCname() + score.getSgSign() + score.getOrderLen()
                                    + score.getOrderWidth() + score.getOrderThick()
                    ));

            if (salesOrderDetList.size() != checkSalesOrderDetMap.size()) {
                throw new ScmException("推送物流园提单不得有重复规格的材料");
            }

            //处理推送提单明细
            int count = 0;
            List<OrderDetMappingProdcode> orderDetMappingProdcodeList = newArrayList();
            for (SalesOrderDet salesOrderDet : salesOrderDetList) {
                count++;
                if (!(salesOrderDet.getProdClassCode().equals(ProdClassCodeEnum.B.getValue())
                        || salesOrderDet.getProdClassCode().equals(ProdClassCodeEnum.D.getValue()))) {
                    throw new ScmException("推送物流园暂时只支持棒材与线材！");
                }
                //获取物料园基础信息
                QueryWrapper<LogisticsParkMatBasicInformation> logisticsParkMatBasicInformationQueryWrapper = new QueryWrapper<>();
                logisticsParkMatBasicInformationQueryWrapper.lambda()
                        .eq(LogisticsParkMatBasicInformation::getSysProductName, salesOrderDet.getOldProdCname())
                        .eq(LogisticsParkMatBasicInformation::getSysSgSign, salesOrderDet.getSgSign())
                        .eq(LogisticsParkMatBasicInformation::getSysThick, salesOrderDet.getOrderThick())
                        .eq(LogisticsParkMatBasicInformation::getSysWidth, salesOrderDet.getOrderWidth())
                        .eq(LogisticsParkMatBasicInformation::getSysLength, salesOrderDet.getOrderLen());

                //查询映射表中名称规格相同的物料，存在一对多的关系
                List<LogisticsParkMatBasicInformation> logisticInfoList =
                        logisticsParkMatBasicInformationService.list(logisticsParkMatBasicInformationQueryWrapper);

                if (CollectionUtil.isEmpty(logisticInfoList)) {
                    throw new ScmException(StrUtil.format("第" + count + "条明细没有在物流园材料基础信息中维护。"));
                }

                //遍历码表，查询所有的同名同规格库存数

                BigDecimal salesOrderDetQty = BigDecimal.valueOf(salesOrderDet.getQty());
                BigDecimal salesOrderDetWeight = BigDecimal.valueOf(salesOrderDet.getWeight());


                BigDecimal needCount = salesOrderDetQty;
                for (LogisticsParkMatBasicInformation logisticInfo : logisticInfoList) {

                    if (needCount.compareTo(BigDecimal.ZERO) == 0) {
                        break;
                    }

                    //获取物流园库存信息
                    PaginatedRequest.PaginatedRequestModel<GetInventoryCondition> requestBody = new PaginatedRequest.PaginatedRequestModel<>();
                    requestBody.setPageSize(10);
                    requestBody.setPageNumber(1);
                    requestBody.setCondition(GetInventoryCondition.builder()
                            .warehouseCode(stockCode)
                            .productCode(logisticInfo.getProductCode())
                            .customerName(companyTenantVo.getCompanyName())
                            .build());

                    PaginatedRequest.PaginatedResponseModel product = wmsService.getInventory(requestBody);


                    //如果存在库存
                    if (product != null && CollectionUtil.isNotEmpty(product.getData())) {

                        //根据物流园货物库存位置推送提单明细
                        for (Map<String, Object> recode : product.getData()) {
                            BigDecimal logisticsParkQuantity = new BigDecimal(recode.get("quantity").toString());
//                                BigDecimal logisticsParkWeight = new BigDecimal(recode.get("weight").toString());
                            if (logisticsParkQuantity.compareTo(BigDecimal.ZERO) > 0) {

                                OrderDetMappingProdcode orderDetMappingProdcode = new OrderDetMappingProdcode();
                                orderDetMappingProdcode.setOrderDetId(salesOrderDet.getId());
                                orderDetMappingProdcode.setProdCode(logisticInfo.getProductCode());
                                orderDetMappingProdcodeList.add(orderDetMappingProdcode);

                                //本次物料数量
                                BigDecimal thisTimeCount = logisticsParkQuantity.min(salesOrderDetQty);
                                needCount = needCount.subtract(thisTimeCount);
                                BigDecimal pathWeight = BigDecimalUtils.multiply(thisTimeCount, salesOrderDet.getMatTheoryWt(), 3);

                                if (type.equalsIgnoreCase(WmsTypeEnum.PUSH_ORDER.getCode())) {
                                    pushSalesOrderDetList.add(buildSubmitBillOfLadingPlanParam(salesOrderDet, logisticInfo,
                                            recode, thisTimeCount, pathWeight));
                                } else if (type.equalsIgnoreCase(WmsTypeEnum.PUSH_TRANSFER.getCode())) {
                                    transferSalesOrderDetList.add(submitTransferOwnershipPlanParam(salesOrderDet, logisticInfo,
                                            recode, thisTimeCount, pathWeight));
                                }

                                if (needCount.compareTo(BigDecimal.ZERO) == 0) {
                                    break;
                                }
                            }
                        }
                    }

                }

                if (needCount.compareTo(BigDecimal.ZERO) != 0) {
                    throw new ScmException("第" + count + "条明细物流园库存数不足，请检查");
                }

            }


            //获取地址、车号
            String pushDestination = salesOrder.getPushDestination();
            if (StrUtil.isBlank(pushDestination)) {
                if (StrUtil.isBlank(salesOrder.getDestination())) {
                    throw new ScmException("目的地为空");
                }
                pushDestination = salesOrder.getDestination();
            }

            //获取提货单据有效时间
            Date date = DateUtils.getDaysBeforeAndAfterTheCurrentTime(salesOrder.getOrderTime(), 3);
            SimpleDateFormat stringFormat = new SimpleDateFormat("yyyy-MM-dd");
            String effectiveDate = stringFormat.format(date) + " 23:59:59";

            String sysUser = salesOrder.getCreateBy();

            String customerName = companyTenantVo.getCompanyName();
            QueryWrapper<CustomerProfile> customerProfileQueryWrapper = new QueryWrapper<>();
            customerProfileQueryWrapper.lambda().eq(CustomerProfile::getId, salesOrder.getCustomerId());
            CustomerProfile customerProfile = customerProfileService.getOne(customerProfileQueryWrapper);
            String receiptCompany = customerProfile.getCompanyName();
            String settlemtCompany = companyTenantVo.getCompanyName();

            int i;
            if (StrUtil.isEmpty(customerProfile.getWmsSupervise()) || customerProfile.getWmsSupervise().equalsIgnoreCase(WmsSuperviseEnum.SUPERVISE.getValue())) {
                i = 0;
            } else if (customerProfile.getWmsSupervise().equalsIgnoreCase(WmsSuperviseEnum.DISTRIBUTION.getValue())) {
                i = 1;
            } else {
                i = 2;
            }

            if (stockCode.equalsIgnoreCase("Wf1")) {
                i = 2;
            }
//            i = StrUtil.isNotEmpty(customerProfile.getWmsSupervise()) ? customerProfile.getWmsSupervise() : 0;
            String fleet = (i == 1) ? salesOrder.getFleetName() : "个体";


            if (type.equalsIgnoreCase(WmsTypeEnum.PUSH_ORDER.getCode())) {
                NormalRequest.NormalResponse pushSalesOrder = buildSubmitBillOfLadingPlan(salesOrder,
                        pushSalesOrderDetList, stockCode, pushDestination, effectiveDate, sysUser, receiptCompany, customerName, settlemtCompany, fleet, i);

                if (StringUtils.isNotBlank(pushSalesOrder.getMessage())) {
                    throw new ScmException(StrUtil.format("推送提单失败：" + pushSalesOrder.getMessage()));
                }
                salesOrder.setPushLogistics(LogisticsEnum.LOGISTICS.getCode());

            } else if (type.equalsIgnoreCase(WmsTypeEnum.PUSH_TRANSFER.getCode())) {
                NormalRequest.NormalResponse pushTransfer = buildSubmitTransferOwnershipPlan(salesOrder, transferSalesOrderDetList, effectiveDate, customerName, receiptCompany);

                if (StringUtils.isNotBlank(pushTransfer.getMessage())) {
                    throw new ScmException(StrUtil.format("推送过户失败：" + pushTransfer.getMessage()));
                }
                salesOrder.setPushLogistics(LogisticsEnum.LOGISTICS_TRA.getCode());
            }

//            salesOrder.setDestination(pushDestination);

            updateById(salesOrder);

            orderDetMappingProdcodeService.saveBatch(orderDetMappingProdcodeList);

        } catch (WMSException e) {
            log.error("pushLogistics error", e);
            throw new ScmException("推送提单至物流园失败：" + e);
        }

    }

    public Map<String, String> getCarNoMap(List<String> orderBillNos) {
        if (CollectionUtil.isEmpty(orderBillNos)) {
            return newHashMap();
        }

        Map<String, String> result = newHashMap();

        QueryWrapper<SalesOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(SalesOrder::getSaleBillNo, orderBillNos);
        List<SalesOrder> salesOrderList = list(queryWrapper);

        for (SalesOrder salesOrder : salesOrderList) {
            if (StrUtil.isNotBlank(salesOrder.getCarNo())) {
                result.put(salesOrder.getSaleBillNo(), salesOrder.getCarNo().replaceAll(" ", ""));
            }
        }
        return result;

    }

    private void checkOrder(SalesOrder salesOrder) {
        if ("12".equalsIgnoreCase(TenantContext.getTenant())) {
            if (StrUtil.isBlank(salesOrder.getConsigneeName())) {
                throw new ScmException("请选择提货人信息");
            }
        }
    }

    /**
     * 提单推送物流园
     */
    private NormalRequest.NormalResponse buildSubmitBillOfLadingPlan(SalesOrder salesOrder, List<SubmitBillOfLadingPlanParam.Detail> pushSalesOrderDetList, String stckCode, String pushDestination, String effectiveDate, String sysUser, String receiptCompany, String customerName, String settlemtCompany, String fleet, int i) throws WMSException {
        //推送
        return wmsService.submitBillOfLadingPlan(SubmitBillOfLadingPlanParam.builder()
                .billOFLadingNo(salesOrder.getSaleBillNo())
                .effectiveDate(effectiveDate)
                .operatorName(sysUser)
                .warehouseCode(stckCode)
                .customerName(customerName)
                .receiptCompany(receiptCompany)
                .settlemtCompany(settlemtCompany)
                .destination(pushDestination.replaceAll(" ", ""))
                .transportMode(3)
                .transCompany(fleet)
                .plateNo(salesOrder.getCarNo().replaceAll(" ", ""))
                .vehicleColor("黄色")
                .driverName(salesOrder.getConsigneeName().replaceAll(" ", ""))
                .driverIDCard(salesOrder.getIdCard())
                .driverPhone(salesOrder.getPhone())
                .isSupervise(i)
                .remark(salesOrder.getRemark())
                .detail(pushSalesOrderDetList)
                .mainId(salesOrder.getId())
                .build());
    }

    /**
     * 推送物流园明细
     */
    private SubmitBillOfLadingPlanParam.Detail buildSubmitBillOfLadingPlanParam(SalesOrderDet salesOrderDet, LogisticsParkMatBasicInformation logisticInfo, Map<String, Object> recode, BigDecimal min, BigDecimal pathWeight) {
        return SubmitBillOfLadingPlanParam.Detail.builder()
                .detailId(salesOrderDet.getId())
                .productCode(logisticInfo.getProductCode())
                .productName(logisticInfo.getProductName())
                .steelGradeName(logisticInfo.getSteelGradeName())
                .steelMillsName(logisticInfo.getSteelMillsName())
                .standardName(logisticInfo.getStandardName())
                .length(logisticInfo.getLength())
                .width(logisticInfo.getWidth())
                .thick(logisticInfo.getThick())
                .packageCount(logisticInfo.getPackageCount())
                .singleWeight(logisticInfo.getSingleWeight())
                .weightMode(logisticInfo.getWeightMode())
                .numberUnit(logisticInfo.getNumberUnit())
                .weightUnit(logisticInfo.getWeightUnit())
                .originalCustomerName(recode.get("originalCustomerName").toString())
                .quantity(min)
                .weight(pathWeight)
                .materialNo(salesOrderDet.getMatNo())
                .build();
    }

    /**
     * 推送物流园过户
     */
    private NormalRequest.NormalResponse buildSubmitTransferOwnershipPlan(SalesOrder salesOrder, List<SubmitTransferOwnershipPlanParam.Detail> transferSalesOrderDetList,
                                                                          String effectiveDate, String customerName, String receiptCompany) throws WMSException {
        return wmsService.submitTransferOwnershipPlan(SubmitTransferOwnershipPlanParam.builder()
                .warehouseCode("W01")
                .transferOwnershipPlanNo(salesOrder.getSaleBillNo())
                .customerName(customerName)
                .nextCustomerName(receiptCompany)
                .settlemtCompany(receiptCompany)
                .effectiveDate(effectiveDate)
                .operatorName(salesOrder.getCreateBy())
                .remark(salesOrder.getRemark())
                .detail(transferSalesOrderDetList)
                .build());
    }

    /**
     * 过户明细
     */
    private SubmitTransferOwnershipPlanParam.Detail submitTransferOwnershipPlanParam(SalesOrderDet salesOrderDet, LogisticsParkMatBasicInformation logisticInfo, Map<String, Object> recode, BigDecimal min, BigDecimal pathWeight) {
        return SubmitTransferOwnershipPlanParam.Detail.builder()
//                .detailId(salesOrderDet.getId())
                .productCode(logisticInfo.getProductCode())
                .productName(logisticInfo.getProductName())
                .steelGradeName(logisticInfo.getSteelGradeName())
                .steelMillsName(logisticInfo.getSteelMillsName())
                .standardName(logisticInfo.getStandardName())
                .length(logisticInfo.getLength())
                .width(logisticInfo.getWidth())
                .thick(logisticInfo.getThick())
                .packageCount(logisticInfo.getPackageCount())
                .singleWeight(logisticInfo.getSingleWeight())
                .weightMode(logisticInfo.getWeightMode())
                .numberUnit(logisticInfo.getNumberUnit())
                .weightUnit(logisticInfo.getWeightUnit())
                .originalCustomerName(recode.get("originalCustomerName").toString())
                .quantity(min)
                .weight(pathWeight)
                .materialNo(salesOrderDet.getMatNo())
                .build();
    }

    //螺纹钢 理计，则总重量= 数量*理计重量   卷材重量为录入(库存)重量
    public void weightComputation(Double weight, Double qty, Double matTheoryWt, int count, SalesOrderDet salesOrderDet) {

        //岑海 螺纹钢 理计，则总重量= 数量*理计重量
        if ("2".equals(TenantContext.getTenant())) {
            int comparativeResult = 0;
            if (salesOrderDet.getWtMode().equals(WtModeEnum.WEIGHT_MANAGEMENT.getCode())
                    && (salesOrderDet.getProdClassCode().equals(ProdClassCodeEnum.B.getValue())
                    || salesOrderDet.getProdClassCode().equals(ProdClassCodeEnum.D.getValue()))) {

                comparativeResult = BigDecimal.valueOf(weight).compareTo(BigDecimalUtils.multiply(qty, matTheoryWt));

                if (!(comparativeResult == 0)) {
                    throw new ScmException("第" + count + "条数据所填总重量与计算重量不相等");
                }
            } else if (salesOrderDet.getProdClassCode().equals(ProdClassCodeEnum.F.getValue())
                    || salesOrderDet.getProdClassCode().equals(ProdClassCodeEnum.G.getValue())) {

                Inventory inventory = inventoryService.getById(salesOrderDet.getInventoryId());
                comparativeResult = BigDecimal.valueOf(weight).compareTo(BigDecimal.valueOf(inventory.getWeight()));

                if (!(comparativeResult == 0)) {
                    throw new ScmException("第" + count + "条数据所填总重量与入库重量不相等");
                }

            }
        }
    }

    /**
     * 修改发票生成状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void editCreateInvoiceStatus(String ids, Date createInvoiceTime) {
        List<SalesOrder> salesOrderList = this.selectBatchSalesOrder(asList(ids.split(",")));
        for (int i = 0; i < salesOrderList.size(); i++) {
            SalesOrder salesOrder = salesOrderMapper.selectById(salesOrderList.get(i).getId());
            salesOrder.setCreateInvoiceStatus(YesNoEnum.YES.getCode());
            salesOrder.setCreateInvoiceTime(createInvoiceTime);
            salesOrderMapper.updateById(salesOrder);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public List<SalesOrder> selectBatchSalesOrder(Collection<? extends Serializable> idList) {
        List<SalesOrder> salesOrderList = new ArrayList<>();
        for (Serializable id : idList) {
            List<SalesOrder> salesOrders = salesOrderMapper.selectByMainId(id.toString());
            salesOrderList.addAll(salesOrders);
        }
        return salesOrderList;
    }

    //GD螺纹钢按实际重量销售，按理计出入库
    private double inventoryWeight(SalesOrderDet salesOrderDet) {
        double weight;
        if ("12".equalsIgnoreCase(TenantContext.getTenant())
                && (ProdClassCodeEnum.B.getValue().equalsIgnoreCase(salesOrderDet.getProdClassCode()))) {
            weight = BigDecimalUtils.multiplyKeep3DecimalPlaces(salesOrderDet.getMatTheoryWt(), salesOrderDet.getQty()).doubleValue();
        } else {
            weight = salesOrderDet.getWeight();
        }
        return weight;
    }


    //更新阳蕊明客户可开单状态
    public void updateYRMCanLadingBill() {
        long start = System.currentTimeMillis();
        log.info("job updateYRMCanLadingBill start");
        try {
            QueryWrapper<SalesOrder> queryWrapper = new QueryWrapper<>();

            queryWrapper.lambda().in(SalesOrder::getTenantId, Constants.YRM_TENANT_IDS)
                    .eq(SalesOrder::getCreateStackStatus, YesNoEnum.NO.getCode());


            List<SalesOrder> unStackSaleOrders = list(queryWrapper);

            List<String> customerIds = unStackSaleOrders.stream().map(SalesOrder::getCustomerId).collect(Collectors.toList());

            customerIds = CollectionUtil.distinct(customerIds);

            Map<String, CustomerProfile> customerProfileMap = customerProfileService.getCustomerInfoMap(customerIds);

            List<SalesOrder> overDueSalesOrders = newArrayList();

            Date now = new Date();

            for (SalesOrder unStackSaleOrder : unStackSaleOrders) {
                String customerId = unStackSaleOrder.getCustomerId();

                CustomerProfile customerProfile = customerProfileMap.get(customerId);

                if (DateUtil.betweenDay(unStackSaleOrder.getOrderTime(), now, true) >= customerProfile.getOrderMaxDueDays()) {
                    overDueSalesOrders.add(unStackSaleOrder);
                }
            }

            //按客户id汇总各客户的超期总金额
            Map<String, BigDecimal> customerIdAndUnPaidAmountMap = newHashMap();

            List<String> overDueCustomerIds = newArrayList();
            for (SalesOrder overDueSalesOrder : overDueSalesOrders) {
                customerIdAndUnPaidAmountMap.merge(overDueSalesOrder.getCustomerId(), overDueSalesOrder.getTotalAmount(), BigDecimal::add);
                overDueCustomerIds.add(overDueSalesOrder.getCustomerId());
            }

            overDueCustomerIds = CollectionUtil.distinct(overDueCustomerIds);
            //超期提单客户的余额
            Map<String, BigDecimal> customerIdAndAvailAmountMap = fundPoolService.getCustomerAvailAmount(overDueCustomerIds);

            List<String> overDueUnPaidCustomerIds = newArrayList();

            for (Map.Entry<String, BigDecimal> entry : customerIdAndUnPaidAmountMap.entrySet()) {
                if (entry.getValue().compareTo(customerIdAndAvailAmountMap.getOrDefault(entry.getKey(), BigDecimal.ZERO)) > 0) {
                    overDueUnPaidCustomerIds.add(entry.getKey());
                }
            }
            customerProfileService.updateYRMCanLadingBill();

            if (CollectionUtil.isNotEmpty(overDueUnPaidCustomerIds)) {
                customerProfileService.updateCanNoLadingBill(overDueCustomerIds);
            }
            long times = System.currentTimeMillis() - start;
            log.info("updateYRMCanLadingBill end 耗时" + times + "毫秒");
        } catch (Exception e) {
            log.error("job updateYRMCanLadingBill error", e);
        }

    }

    public void updateYRMCanLadingBill(String customerId) {
        if (Constants.YRM_TENANT_IDS.contains(TenantContext.getTenant())) {
            try {
                QueryWrapper<SalesOrder> queryWrapper = new QueryWrapper<>();

                queryWrapper.lambda().eq(SalesOrder::getCreateStackStatus, YesNoEnum.NO.getCode())
                        .eq(SalesOrder::getCustomerId, customerId);

                List<SalesOrder> unStackSaleOrders = list(queryWrapper);

                CustomerProfile customerProfile = customerProfileService.getById(customerId);

                Date now = new Date();

                BigDecimal overDueSaleOrderAmount = BigDecimal.ZERO;

                for (SalesOrder unStackSaleOrder : unStackSaleOrders) {
                    if (DateUtil.betweenDay(unStackSaleOrder.getOrderTime(), now, true) >= customerProfile.getOrderMaxDueDays()) {
                        overDueSaleOrderAmount = overDueSaleOrderAmount.add(unStackSaleOrder.getTotalAmount());
                    }
                }

                Map<String, BigDecimal> customerIdAndAvailAmount = fundPoolService.getCustomerAvailAmount(Collections.singletonList(customerId));

                if (customerIdAndAvailAmount.getOrDefault(customerId, BigDecimal.ZERO).compareTo(overDueSaleOrderAmount) >= 0) {
                    UpdateWrapper<CustomerProfile> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.lambda().set(CustomerProfile::getCanLadingBill, YesNoEnum.YES.getCode())
                            .eq(CustomerProfile::getId, customerId);
                    customerProfileService.update(updateWrapper);
                }
            } catch (Exception e) {
                log.error("updateYRMCanLadingBill error", e);
            }
        }

    }

    public void printNum(String ids) {

        List<String> idList = Arrays.asList(ids.split(","));

        List<SalesOrder> salesOrderList = listByIds(idList);

        for (SalesOrder salesOrder : salesOrderList) {
            salesOrder.setPrintNum(ObjectUtil.isNotEmpty(salesOrder.getPrintNum()) ? salesOrder.getPrintNum() + 1 : 1);
        }

        updateBatchById(salesOrderList);

    }
}
