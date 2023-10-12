package com.dongxin.scm.fm;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.Application;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.common.enums.CommonCheckStatusEnum;
import com.dongxin.scm.enums.ProdClassCodeEnum;
import com.dongxin.scm.fm.dto.CreditDTO;
import com.dongxin.scm.fm.entity.*;
import com.dongxin.scm.fm.enums.AccountTypeEnum;
import com.dongxin.scm.fm.enums.FinanceDetailEnum;
import com.dongxin.scm.fm.enums.FundDetailTypeEnum;
import com.dongxin.scm.fm.mapper.SettleMatCodingMapper;
import com.dongxin.scm.fm.service.*;
import com.dongxin.scm.fm.vo.CustomerBalanceCheckVO;
import com.dongxin.scm.fm.vo.FundAccountVO;
import com.dongxin.scm.fm.vo.IdAndAccountVO;
import com.dongxin.scm.om.util.Constants;
import com.dongxin.scm.sm.entity.*;
import com.dongxin.scm.sm.service.*;
import com.dongxin.scm.utils.BigDecimalUtils;
import com.dongxin.scm.fm.vo.SettleAccountVO;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.sm.entity.*;
import com.dongxin.scm.sm.entity.Stack;
import com.dongxin.scm.sm.service.*;
import com.dongxin.scm.utils.DateUtils;
import com.dongxin.scm.utils.ProdClassToSerialNoUtils;
import org.jeecg.config.mybatis.TenantContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.querydsl.QuerydslUtils;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class SettleTest {

    @Autowired
    private FundAccountService fundAccountService;

    @Autowired
    private FundDetailService fundDetailService;

    @Autowired
    private SettleInfoService settleInfoService;

    @Autowired
    private StackService stackService;

    @Autowired
    private MatService matService;

    @Autowired
    private InventoryService inventoryService;

    @Resource
    private SettleMatCodingMapper settleMatCodingMapper;

    @Autowired
    private SettleMatCodingService settleMatCodingService;

    @Autowired
    private InventoryTimingService inventoryTimingService;

    @Autowired
    private FinancialReconciliationService financialReconciliationService;

    @Autowired
    private CustomerProfileService customerProfileService;

    @Autowired
    private FundPoolService fundPoolService;

    @Autowired
    private SalesOrderService salesOrderService;

    @Autowired
    private FundPoolDetailService fundPoolDetailService;

    @Autowired
    private EquipmentSuppliesService equipmentSuppliesService;

    @Autowired
    private RefundRecordsService refundRecordsService;

    @Autowired
    private StackDetService stackDetService;

    @Autowired
    private CreditService creditService;


    @Test
    public void updateDiscountErrorData() {
        List<FundAccountVO> stackIdList = fundAccountService.selectUnSettleAccountAndStackSettleAccount();

        List<FundDetail> updateDiscountFundDetailList = newArrayList();
        List<FundDetail> updateSettleFundDetailList = newArrayList();
        List<SettleInfo> updateSettleInfo = newArrayList();


        for (FundAccountVO fundAccountVO : stackIdList) {
            String stackId = fundAccountVO.getCustomerId();

            BigDecimal updateAccount = new BigDecimal(0.01);

            //修改优惠明细  -1
            QueryWrapper<FundDetail> discountFundDetailQueryWrapper = new QueryWrapper<>();
            discountFundDetailQueryWrapper.lambda().eq(FundDetail::getCustomerId, "1372008361596887041")
                    .eq(FundDetail::getType, "discount")
                    .eq(FundDetail::getOutTradeNo, stackId);
            List<FundDetail> discountFundDetailList = fundDetailService.list(discountFundDetailQueryWrapper);
            FundDetail discountFundDetail = discountFundDetailList.get(0);
            discountFundDetail.setAmount(discountFundDetail.getAmount().subtract(updateAccount));
            updateDiscountFundDetailList.add(discountFundDetail);

            //修改结算明细  +1
            QueryWrapper<FundDetail> preUseFundDetailQueryWrapper = new QueryWrapper<>();
            preUseFundDetailQueryWrapper.lambda().eq(FundDetail::getCustomerId, "1372008361596887041")
                    .eq(FundDetail::getType, "settle")
                    .eq(FundDetail::getOutTradeNo, stackId);
            List<FundDetail> settleFundDetailList = fundDetailService.list(preUseFundDetailQueryWrapper);
            FundDetail settleFundDetail = settleFundDetailList.get(0);
            settleFundDetail.setAmount(settleFundDetail.getAmount().add(updateAccount));
            updateSettleFundDetailList.add(settleFundDetail);

            //修改结算单结算金额  +1
            QueryWrapper<SettleInfo> settleInfoQueryWrapper = new QueryWrapper<>();
            settleInfoQueryWrapper.lambda().eq(SettleInfo::getStackId, stackId)
                    .eq(SettleInfo::getDelFlag, 0);
            SettleInfo settleInfo = settleInfoService.getOne(settleInfoQueryWrapper);
            settleInfo.setSettleAmount(settleInfo.getSettleAmount().add(updateAccount));
            updateSettleInfo.add(settleInfo);

        }

        System.out.println(updateDiscountFundDetailList);
        fundDetailService.updateBatchById(updateDiscountFundDetailList);
        fundDetailService.updateBatchById(updateSettleFundDetailList);
        settleInfoService.updateBatchById(updateSettleInfo);
    }

    public static String doubleTrans(double d) {
        if (Math.round(d) - d == 0) {
            return String.valueOf((long) d);
        }
        return String.valueOf(d);
    }


    @Test
    public void test() {
        String name = "客户:广东省_广州市_广州市均盛金属有限公司";

        String string = name.substring(name.lastIndexOf("_") + 1);
        List<FinanceDetail> financeDetailList = newArrayList();
        FinanceDetail financeDetail = new FinanceDetail();
        financeDetail.setCustomerId("杨淦雅");
        financeDetail.setType(FinanceDetailEnum.INIT_DATA.getCode());
        financeDetailList.add(financeDetail);
        financeDetail.setCustomerId("测试");
        financeDetail.setType(FinanceDetailEnum.PAYMENT.getCode());

        Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
        ca.setTime(new Date()); //设置时间为当前时间
        ca.add(Calendar.DATE, -2); //日期减1
        Date lastMonth = ca.getTime();

        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        String queryDate =sdf.format(lastMonth );

        GetActualDelivery actualDelivery = new GetActualDelivery();
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//注意月份是MM
            Date date = simpleDateFormat.parse("2021-02-35 01:00:00");
            String stringDate = date.toString();
            actualDelivery.setStockOutDate(date);
        } catch (ParseException e){

        }



        double a = 3.0d, b = 3.1d;
        System.out.println(doubleTrans(a));
        System.out.println(doubleTrans(b));


        System.out.println(name);
    }


    @Test
    public void repairSettleRedCentDate() {
        List<Stack> updateStackSettleList = newArrayList();
        List<SettleInfo> updateSettleSettleAccountList = newArrayList();
        List<FundDetail> updateFundDetailList = newArrayList();

        List<IdAndAccountVO> updateStackSettleAccountList = settleInfoService.updateStackSettleAccount();
        for (IdAndAccountVO idAndAccountVO : updateStackSettleAccountList) {
            Stack stack = stackService.getById(idAndAccountVO.getId());
            stack.setSettledTotalPrice(idAndAccountVO.getAccount());
            updateStackSettleList.add(stack);
        }

        List<IdAndAccountVO> updateSettleMainSettleAccountList = settleInfoService.updateSettleMainSettleAccount();
        for (IdAndAccountVO idAndAccountVO : updateSettleMainSettleAccountList) {
            QueryWrapper<SettleInfo> settleInfoQueryWrapper = new QueryWrapper<>();
            settleInfoQueryWrapper.lambda().eq(SettleInfo::getStackId, idAndAccountVO.getId());
            SettleInfo settleInfo = settleInfoService.getOne(settleInfoQueryWrapper);
            settleInfo.setSettleAmount(idAndAccountVO.getAccount());
            updateSettleSettleAccountList.add(settleInfo);
        }

        List<IdAndAccountVO> errorFundDetSettleAccountList = settleInfoService.errorFundDetSettleAccount();
        for (IdAndAccountVO idAndAccountVO : errorFundDetSettleAccountList) {
            QueryWrapper<FundDetail> fundDetailQueryWrapper = new QueryWrapper<>();
            fundDetailQueryWrapper.lambda().eq(FundDetail::getOutTradeNo, idAndAccountVO.getId())
                    .eq(FundDetail::getType, FundDetailTypeEnum.SETTLE.getCode());
            List<FundDetail> fundDetailList = fundDetailService.list(fundDetailQueryWrapper);
            FundDetail fundDetail = fundDetailList.get(0);

            FundAccount fundAccount = fundAccountService.getById(fundDetail.getAccountId());

            BigDecimal stackDetAccount = idAndAccountVO.getStackDetAccount();
            BigDecimal fundDetSettleAccount = idAndAccountVO.getFundDetSettleAccount();
            if (stackDetAccount.compareTo(fundDetSettleAccount) > 0) {
                BigDecimal difference = stackDetAccount.subtract(fundDetSettleAccount);
                fundDetail.setAmount(fundDetail.getAmount().add(difference));
                updateFundDetailList.add(fundDetail);

                fundAccount.setAvailableAmount(fundAccount.getAvailableAmount().subtract(difference));
                fundAccount.setSettleAmount(fundAccount.getSettleAmount().add(difference));

                fundAccountService.updateById(fundAccount);
            }

            if (stackDetAccount.compareTo(fundDetSettleAccount) < 0) {
                BigDecimal difference = fundDetSettleAccount.subtract(stackDetAccount);
                fundDetail.setAmount(fundDetail.getAmount().subtract(difference));
                updateFundDetailList.add(fundDetail);

                fundAccount.setSettleAmount(fundAccount.getAvailableAmount().add(difference));
                fundAccount.setSettleAmount(fundAccount.getSettleAmount().subtract(difference));

                fundAccountService.updateById(fundAccount);
            }
        }

        stackService.updateBatchById(updateStackSettleList);
        settleInfoService.updateBatchById(updateSettleSettleAccountList);
        fundDetailService.updateBatchById(updateFundDetailList);

        System.out.println(updateFundDetailList);
    }

    @Test
    public void matCode() {
        QueryWrapper<Mat> matQueryWrapper = new QueryWrapper<>();
        matQueryWrapper.lambda().eq(Mat::getTenantId, 2).isNull(Mat::getMatCode);
        List<Mat> matList = matService.list(matQueryWrapper);

        QueryWrapper<Inventory> inventoryQueryWrapper = new QueryWrapper<>();
        inventoryQueryWrapper.lambda().eq(Inventory::getTenantId, 2).isNull(Inventory::getMatCode);
        List<Inventory> inventoryList = inventoryService.list(inventoryQueryWrapper);

        QueryWrapper<InventoryTiming> inventoryTimingQueryWrapper = new QueryWrapper<>();
        inventoryTimingQueryWrapper.lambda().eq(InventoryTiming::getTenantId, 2).isNull(InventoryTiming::getMatCode);
        List<InventoryTiming> inventoryTimingList = inventoryTimingService.list(inventoryTimingQueryWrapper);

        List<Mat> updateMatList = newArrayList();
        List<Inventory> updateInventoryList = newArrayList();
        List<InventoryTiming> updateInventoryTimingList = newArrayList();
        if (CollectionUtil.isNotEmpty(matList)) {
            for (Mat mat : matList) {
                if (mat.getProdClassCode().equals(ProdClassCodeEnum.D.getValue()) || mat.getProdClassCode().equals(ProdClassCodeEnum.B.getValue())) {
                    QueryWrapper<SettleMatCoding> matCodingQueryWrapperDB = new QueryWrapper<>();
                    matCodingQueryWrapperDB.lambda().eq(SettleMatCoding::getOldProdCname, mat.getOldProdCname())
                            .eq(SettleMatCoding::getSgSign, mat.getSgSign())
                            .eq(SettleMatCoding::getMatThick, mat.getMatThick());
                    SettleMatCoding settleMatCodingDB = settleMatCodingService.getOne(matCodingQueryWrapperDB);
                    if (ObjectUtil.isNotEmpty(settleMatCodingDB)) {
                        mat.setMatCode(settleMatCodingDB.getMatCode());
                        updateMatList.add(mat);
                    }
                } else {
                    QueryWrapper<SettleMatCoding> matCodingQueryWrapperDB = new QueryWrapper<>();
                    matCodingQueryWrapperDB.lambda().eq(SettleMatCoding::getOldProdCname, mat.getOldProdCname())
                            .eq(SettleMatCoding::getSgSign, mat.getSgSign());
                    SettleMatCoding settleMatCodingDB = settleMatCodingService.getOne(matCodingQueryWrapperDB);
                    if (ObjectUtil.isNotEmpty(settleMatCodingDB)) {
                        mat.setMatCode(settleMatCodingDB.getMatCode());
                        updateMatList.add(mat);
                    }
                }
                if (mat.getStockHouseId().equals("5")) {
                    mat.setMatCode("6.07.003");
                }
            }

        }

        if (CollectionUtil.isNotEmpty(inventoryList)) {
            for (Inventory inventory : inventoryList) {
                if (inventory.getProdClassCode().equals(ProdClassCodeEnum.D.getValue()) || inventory.getProdClassCode().equals(ProdClassCodeEnum.B.getValue())) {
                    QueryWrapper<SettleMatCoding> settleMatCodingQueryWrapper = new QueryWrapper<>();
                    settleMatCodingQueryWrapper.lambda().eq(SettleMatCoding::getOldProdCname, inventory.getOldProdCname())
                            .eq(SettleMatCoding::getSgSign, inventory.getSgSign())
                            .eq(SettleMatCoding::getMatThick, inventory.getMatThick());
                    SettleMatCoding settleMatCoding = settleMatCodingService.getOne(settleMatCodingQueryWrapper);
                    if (ObjectUtil.isNotEmpty(settleMatCoding)) {
                        inventory.setMatCode(settleMatCoding.getMatCode());
                        updateInventoryList.add(inventory);
                    }
                } else {
                    QueryWrapper<SettleMatCoding> settleMatCodingQueryWrapper = new QueryWrapper<>();
                    settleMatCodingQueryWrapper.lambda().eq(SettleMatCoding::getOldProdCname, inventory.getOldProdCname())
                            .eq(SettleMatCoding::getSgSign, inventory.getSgSign());
                    SettleMatCoding settleMatCoding = settleMatCodingService.getOne(settleMatCodingQueryWrapper);
                    if (ObjectUtil.isNotEmpty(settleMatCoding)) {
                        inventory.setMatCode(settleMatCoding.getMatCode());
                        updateInventoryList.add(inventory);
                    }
                }
                if (inventory.getStockHouseId().equals("5")) {
                    inventory.setMatCode("6.07.003");
                }
            }
        }

        if (CollectionUtil.isNotEmpty(inventoryTimingList)) {
            for (InventoryTiming inventoryTiming : inventoryTimingList) {
                if (inventoryTiming.getProdClassCode().equals(ProdClassCodeEnum.D.getValue()) || inventoryTiming.getProdClassCode().equals(ProdClassCodeEnum.B.getValue())) {
                    QueryWrapper<SettleMatCoding> settleMatCodingQueryWrapper = new QueryWrapper<>();
                    settleMatCodingQueryWrapper.lambda().eq(SettleMatCoding::getOldProdCname, inventoryTiming.getOldProdCname())
                            .eq(SettleMatCoding::getSgSign, inventoryTiming.getSgSign())
                            .eq(SettleMatCoding::getMatThick, inventoryTiming.getMatThick());
                    SettleMatCoding settleMatCoding = settleMatCodingService.getOne(settleMatCodingQueryWrapper);
                    if (ObjectUtil.isNotEmpty(settleMatCoding)) {
                        inventoryTiming.setMatCode(settleMatCoding.getMatCode());
                        updateInventoryTimingList.add(inventoryTiming);
                    }
                } else {
                    QueryWrapper<SettleMatCoding> settleMatCodingQueryWrapper = new QueryWrapper<>();
                    settleMatCodingQueryWrapper.lambda().eq(SettleMatCoding::getOldProdCname, inventoryTiming.getOldProdCname())
                            .eq(SettleMatCoding::getSgSign, inventoryTiming.getSgSign());
                    SettleMatCoding settleMatCoding = settleMatCodingService.getOne(settleMatCodingQueryWrapper);
                    if (ObjectUtil.isNotEmpty(settleMatCoding)) {
                        inventoryTiming.setMatCode(settleMatCoding.getMatCode());
                        updateInventoryTimingList.add(inventoryTiming);
                    }
                }
                if (inventoryTiming.getStockHouseId().equals("5")) {
                    inventoryTiming.setMatCode("6.07.003");
                }
            }
        }

        inventoryService.updateBatchById(updateInventoryList);
        matService.updateBatchById(updateMatList);
        inventoryTimingService.updateBatchById(updateInventoryTimingList);
    }

    @Test
    public void stockCode() {
        QueryWrapper<Mat> matQueryWrapper = new QueryWrapper<>();
        matQueryWrapper.lambda().eq(Mat::getStockHouseId, "5");
        List<Mat> matList = matService.list(matQueryWrapper);

        QueryWrapper<Inventory> inventoryQueryWrapper = new QueryWrapper<>();
        inventoryQueryWrapper.lambda().eq(Inventory::getStockHouseId, "5");
        List<Inventory> inventoryList = inventoryService.list(inventoryQueryWrapper);

        QueryWrapper<InventoryTiming> inventoryTimingQueryWrapper = new QueryWrapper<>();
        inventoryTimingQueryWrapper.lambda().eq(InventoryTiming::getStockHouseId, "5");
        List<InventoryTiming> inventoryTimingList = inventoryTimingService.list(inventoryTimingQueryWrapper);

        matList.forEach(mat -> mat.setMatCode("6.07.003"));
        inventoryList.forEach(inventory -> inventory.setMatCode("6.07.003"));
        inventoryTimingList.forEach(inventoryTiming -> inventoryTiming.setMatCode("6.07.003"));

        matService.updateBatchById(matList);
        inventoryService.updateBatchById(inventoryList);
        inventoryTimingService.updateBatchById(inventoryTimingList);

        System.out.println(matList);
    }

    @Test
    public void updateInventoryTiming() {
        inventoryTimingService.addStockBalance();
    }


    @Test
    public void checkSettleAccount(){
        QueryWrapper<StackDet> stackDetQueryWrapper = new QueryWrapper<>();
        stackDetQueryWrapper.lambda().gt(StackDet::getDiscountTotalAmount,0);
        List<StackDet> stackDetList = stackDetService.list(stackDetQueryWrapper);

        List<StackDet> stackDetList1 = newArrayList();
        List<BigDecimal> aaa = newArrayList();
        for(StackDet stackDet : stackDetList){
            BigDecimal settleAccount = BigDecimalUtils.multiply(stackDet.getDiscountPrice(),stackDet.getWeight());
            if(settleAccount.compareTo(stackDet.getDiscountTotalAmount()) != 0){
                stackDetList1.add(stackDet);
                BigDecimal bbb = stackDet.getDiscountTotalAmount().subtract(settleAccount);
                aaa.add(bbb);
            }
        }

        System.out.println(stackDetList1);
    }

    @Test
    public void selectCustomerAccount(){
        QueryWrapper<FinancialReconciliation> financialReconciliationQueryWrapper = new QueryWrapper<>();
        financialReconciliationQueryWrapper.lambda().eq(FinancialReconciliation::getDate,"2021-07-01 00:00:00")
                .eq(FinancialReconciliation::getTenantId,2);
        List<FinancialReconciliation> financialReconciliationList = financialReconciliationService.list(financialReconciliationQueryWrapper);

        List<SettleAccountVO> settleAccountVOList = newArrayList();
        for(FinancialReconciliation financialReconciliation : financialReconciliationList){
            String customerId = financialReconciliation.getCustomerId();
            QueryWrapper<FundAccount> fundAccountQueryWrapper = new QueryWrapper<>();
            fundAccountQueryWrapper.lambda().eq(FundAccount::getCustomerId,customerId)
                    .eq(FundAccount::getType, AccountTypeEnum.INCOME.getCode());
            FundAccount fundAccount = fundAccountService.getOne(fundAccountQueryWrapper);
            BigDecimal customerAccount = fundAccount.getAvailableAmount().add(fundAccount.getPreuseAmount());
            if(financialReconciliation.getEndingCreditAccount().compareTo(customerAccount) != 0){
                String customerName = customerProfileService.getOneName(customerId);
                SettleAccountVO settleAccountVO = new SettleAccountVO();
                settleAccountVO.setCustomerId(customerId);
                settleAccountVO.setSettleId(customerName);
                settleAccountVO.setIncomeTotalSettleAccount(customerAccount);
                settleAccountVO.setCreditTotalSettleAccount(financialReconciliation.getEndingCreditAccount());
                settleAccountVOList.add(settleAccountVO);

            }
        }

        System.out.println(settleAccountVOList);
    }

    @Test
    public void selectSettleAccount(){
        QueryWrapper<CustomerProfile> customerProfileQueryWrapper = new QueryWrapper<>();
        customerProfileQueryWrapper.lambda().eq(CustomerProfile::getTenantId,2);
        List<CustomerProfile> customerProfileList = customerProfileService.list(customerProfileQueryWrapper);

        List<SettleAccountVO> settleAccountVOList = newArrayList();
        for(CustomerProfile customerProfile : customerProfileList){
            String customerId = customerProfile.getId();
            QueryWrapper<FundAccount> fundAccountQueryWrapper = new QueryWrapper<>();
            fundAccountQueryWrapper.lambda().eq(FundAccount::getType,AccountTypeEnum.INCOME.getCode())
                    .eq(FundAccount::getCustomerId,customerId);
            FundAccount fundAccount = fundAccountService.getOne(fundAccountQueryWrapper);

            BigDecimal customerAccount = fundAccount.getAvailableAmount().add(fundAccount.getPreuseAmount());

            QueryWrapper<FundPool> fundPoolQueryWrapper = new QueryWrapper<>();
            fundPoolQueryWrapper.lambda().eq(FundPool::getCustomerId,customerId);
            List<FundPool> fundPoolList = fundPoolService.list(fundPoolQueryWrapper);
            BigDecimal fundPoolAccount = BigDecimal.ZERO;
            if(CollectionUtil.isNotEmpty(fundPoolList)){
                for(FundPool fundPool : fundPoolList){
                    fundPoolAccount = fundPoolAccount.add(fundPool.getIncomingAmount());
                }
                BigDecimal settleAccount = settleInfoService.selectSettleAccount(customerId);

                if(ObjectUtil.isEmpty(settleAccount)){
                    settleAccount = BigDecimal.ZERO;
                }

                BigDecimal difference = fundPoolAccount.subtract(settleAccount);

                if(difference.compareTo(customerAccount) != 0){
                    SettleAccountVO settleAccountVO = new SettleAccountVO();
                    settleAccountVO.setCustomerId(customerId);
                    settleAccountVO.setSettleId(customerProfile.getCompanyName());
                    settleAccountVO.setIncomeTotalSettleAccount(difference);
                    settleAccountVO.setCreditTotalSettleAccount(customerAccount);

                    settleAccountVO.setIncomePreUseAccount(fundPoolAccount);
                    settleAccountVO.setCreditPreUseAccount(settleAccount);
                    settleAccountVOList.add(settleAccountVO);
                }

            }



        }

        System.out.println(settleAccountVOList);
    }


    @Test
    public void selectFundPoolDet(){
        List<SalesOrder> salesOrderList = salesOrderService.list();
        List<Map<String,String>> aaa = newArrayList();
        for(SalesOrder salesOrder : salesOrderList){
            QueryWrapper<FundPoolDetail> fundPoolDetailQueryWrapper = new QueryWrapper<>();
            fundPoolDetailQueryWrapper.lambda().eq(FundPoolDetail::getOutTradeNo,salesOrder.getId());
            List<FundPoolDetail> fundPoolDetailList = fundPoolDetailService.list(fundPoolDetailQueryWrapper);
            if(CollectionUtil.isEmpty(fundPoolDetailList)){
                Map<String,String> stringStringMap = new HashMap<>();
                stringStringMap.put(salesOrder.getId(),salesOrder.getTenantId());
                aaa.add(stringStringMap);
            }
        }
        System.out.println(aaa);
    }


    @Test
    public void addFundPoolDet(){
        FundPoolDetail fundPoolDetail = new FundPoolDetail();
        fundPoolDetail.setType(FundDetailTypeEnum.PRE_USE.getCode());
        fundPoolDetail.setAccountId("1372008682897350658");
        fundPoolDetail.setCustomerId("1372008682796687361");
        fundPoolDetail.setFundPoolId("1372009872733294593");
        fundPoolDetail.setOutTradeNo("1384734010330484738");
        fundPoolDetail.setAmount(new BigDecimal("175021.44"));

        fundPoolDetailService.save(fundPoolDetail);

    }

    @Test
    public void fundAccountCheck(){
        Integer tenantId = 11;

        QueryWrapper<CustomerProfile> customerProfileQueryWrapper = new QueryWrapper<>();
        customerProfileQueryWrapper.lambda().eq(CustomerProfile::getTenantId,tenantId);
        List<CustomerProfile> customerProfileList = customerProfileService.list(customerProfileQueryWrapper);

        List<CustomerBalanceCheckVO> customerBalanceCheckVOList = newArrayList();
        for(CustomerProfile customerProfile : customerProfileList){
            String customerId = customerProfile.getId();
            QueryWrapper<FundAccount> fundAccountQueryWrapper = new QueryWrapper<>();
            fundAccountQueryWrapper.lambda().eq(FundAccount::getType,AccountTypeEnum.INCOME.getCode())
                    .eq(FundAccount::getCustomerId,customerId);
            FundAccount fundAccount = fundAccountService.getOne(fundAccountQueryWrapper);

            //客户余额
            BigDecimal customerAccount = fundAccount.getAvailableAmount().add(fundAccount.getPreuseAmount());

            QueryWrapper<FundPool> fundPoolQueryWrapper = new QueryWrapper<>();
            fundPoolQueryWrapper.lambda().eq(FundPool::getCustomerId,customerId);
            List<FundPool> fundPoolList = fundPoolService.list(fundPoolQueryWrapper);
            BigDecimal fundPoolAccount = BigDecimal.ZERO;

            if(CollectionUtil.isNotEmpty(fundPoolList)){
                for(FundPool fundPool : fundPoolList){
                    fundPoolAccount = fundPoolAccount.add(fundPool.getIncomingAmount());
                }
            }

            //结算单金额
            BigDecimal settleAccount = BigDecimal.ZERO;
            BigDecimal settleInfoAccount = settleInfoService.selectSettleAccount(customerId);
            if(ObjectUtil.isNotEmpty(settleInfoAccount)){
                settleAccount = settleAccount.add(settleInfoAccount);
            }

            //设备物资网结算金额
            BigDecimal equipmentSuppliesSettleAccount = BigDecimal.ZERO;
            QueryWrapper<EquipmentSupplies> equipmentSuppliesQueryWrapper = new QueryWrapper<>();
            equipmentSuppliesQueryWrapper.lambda()
                    .eq(EquipmentSupplies::getTenantId, customerProfile.getTenantId())
                    .eq(EquipmentSupplies::getCustomer, customerProfile.getCompanyName());
            List<EquipmentSupplies> equipmentSuppliesList = equipmentSuppliesService.list(equipmentSuppliesQueryWrapper);

            if(CollectionUtil.isNotEmpty(equipmentSuppliesList)){
                for(EquipmentSupplies equipmentSupplies : equipmentSuppliesList){
                    equipmentSuppliesSettleAccount = equipmentSuppliesSettleAccount.add(equipmentSupplies.getTotalPrice());
                }
            }


            //审核通过退款金额
            BigDecimal approveRefundRecordsAccount = BigDecimal.ZERO;
            QueryWrapper<RefundRecords> recordsQueryWrapper = new QueryWrapper<>();
            recordsQueryWrapper.lambda().eq(RefundRecords::getCustomerId, customerProfile.getId())
                    .eq(RefundRecords::getTenantId, customerProfile.getTenantId())
                    .eq(RefundRecords::getStatus, "approve");
            List<RefundRecords> approveRecordsList = refundRecordsService.list(recordsQueryWrapper);
            if(CollectionUtil.isNotEmpty(approveRecordsList)){
                for(RefundRecords refundRecords : approveRecordsList){
                    approveRefundRecordsAccount = approveRefundRecordsAccount.add(refundRecords.getRefundAmount());
                }
            }

            //未审核退款金额
            BigDecimal unApproveRefundRecordsAccount = BigDecimal.ZERO;
            QueryWrapper<RefundRecords> refundRecordsQueryWrapper = new QueryWrapper<>();
            refundRecordsQueryWrapper.lambda().eq(RefundRecords::getCustomerId, customerProfile.getId())
                    .eq(RefundRecords::getTenantId, customerProfile.getTenantId())
                    .eq(RefundRecords::getStatus, CommonCheckStatusEnum.PENDING_VERIFY.getCode());
            List<RefundRecords> unApproveRefundRecordsList = refundRecordsService.list(refundRecordsQueryWrapper);
            if(CollectionUtil.isNotEmpty(unApproveRefundRecordsList)){
                for(RefundRecords refundRecords : unApproveRefundRecordsList){
                    unApproveRefundRecordsAccount = unApproveRefundRecordsAccount.add(refundRecords.getRefundAmount());
                }
            }

            //计算差额
            BigDecimal balance = fundPoolAccount.subtract(settleAccount)
                    .subtract(equipmentSuppliesSettleAccount)
                    .subtract(approveRefundRecordsAccount)
                    .subtract(unApproveRefundRecordsAccount);

            if(balance.compareTo(customerAccount) != 0){
                BigDecimal difference = customerAccount.subtract(balance);
                CustomerBalanceCheckVO customerBalanceCheckVO = new CustomerBalanceCheckVO();
                customerBalanceCheckVO.setCustomerId(customerId);
                customerBalanceCheckVO.setCustomerName(customerProfile.getCompanyName());
                customerBalanceCheckVO.setCustomerAccount(customerAccount);
                customerBalanceCheckVO.setFundPoolAccount(fundPoolAccount);
                customerBalanceCheckVO.setSettleAccount(settleAccount);
                customerBalanceCheckVO.setEquipmentSuppliesSettleAccount(equipmentSuppliesSettleAccount);
                customerBalanceCheckVO.setApproveRefundRecordsAccount(approveRefundRecordsAccount);
                customerBalanceCheckVO.setUnApproveRefundRecordsAccount(unApproveRefundRecordsAccount);
                customerBalanceCheckVO.setBalance(balance);
                customerBalanceCheckVO.setDifference(difference);
                customerBalanceCheckVOList.add(customerBalanceCheckVO);
            }

        }
        for (CustomerBalanceCheckVO customerBalanceCheckVO : customerBalanceCheckVOList) {
            System.out.println(customerBalanceCheckVO.toString());
        }
    }


    @Test
    public void brushWriteOffData(){
        QueryWrapper<SettleInfo> settleInfoQueryWrapper = new QueryWrapper<>();
        settleInfoQueryWrapper.lambda().eq(SettleInfo::getSettleNo,"JZJS2108000382")
                .eq(SettleInfo::getTenantId,10);
        SettleInfo writeOffSettleInfo = settleInfoService.getOne(settleInfoQueryWrapper);

        SettleInfo settleInfo = new SettleInfo();
        BeanUtils.copyProperties(writeOffSettleInfo,settleInfo);
        settleInfo.setId(null);
        settleInfo.setSettleNo(ProdClassToSerialNoUtils.generateSettleNoWithProdClassPrefix("B"));
        settleInfo.setSettleAmount(settleInfo.getSettleAmount().negate());

        settleInfoService.save(settleInfo);

        Stack writeStack = stackService.getById(writeOffSettleInfo.getStackId());
        writeOffSettleInfo.setStackingNo("-"+writeOffSettleInfo.getStackingNo());
        stackService.updateById(writeStack);
        Stack stack = new Stack();
        BeanUtils.copyProperties(writeStack,stack);
        stack.setId(null);
        stack.setWeight(-stack.getWeight());
//        stack.setSettled(settleInfo.getId());
        stack.setSettledTotalPrice(stack.getSettledTotalPrice().negate());
        stack.setStackingNo(stack.getStackingNo().substring(1));
        stackService.save(stack);

        QueryWrapper<StackDet> writeStackDetQueryWrapper = new QueryWrapper<>();
        writeStackDetQueryWrapper.lambda().eq(StackDet::getStackId,writeStack.getId());
        List<StackDet> writeStackDetList = stackDetService.list(writeStackDetQueryWrapper);
        List<StackDet> stackDetList  = newArrayList();
        for(StackDet writeOffStackDet : writeStackDetList){
            StackDet stackDet = new StackDet();
            BeanUtils.copyProperties(writeOffStackDet,stackDet);
            stackDet.setId(null);
            stackDet.setWeight(-stackDet.getWeight());
            stackDet.setQty(-stackDet.getQty());
            stackDet.setDiscountTotalAmount(stackDet.getDiscountTotalAmount().negate());
            stackDet.setStackId(stack.getId());
            stackDetList.add(stackDet);
        }

        stackDetService.saveBatch(stackDetList);

        settleInfo.setStackId(stack.getId());
        settleInfoService.updateById(settleInfo);

    }

    @Test
    public void test_all_not_equals_customer() {
        for (String yrmTenantId : Constants.YRM_TENANT_IDS) {
            TenantContext.setTenant(yrmTenantId);

            List<CustomerProfile> customerProfiles = customerProfileService.list();

            List<String> customerIds = customerProfiles.stream().map(CustomerProfile::getId).collect(Collectors.toList());
            Map<String, BigDecimal> availMap = fundPoolService.getCustomerAvailAmount(customerIds);
            Map<String, BigDecimal> totalIncomeMap = fundPoolService.getCustomerTotalIncomeAmount(customerIds);

            List<String> companyNames = newArrayList();


            for (CustomerProfile customerProfile : customerProfiles) {
                String customerId = customerProfile.getId();
                BigDecimal availAmount = availMap.getOrDefault(customerProfile.getId(), BigDecimal.ZERO);
                BigDecimal totalIncomeAmount = totalIncomeMap.getOrDefault(customerProfile.getId(), BigDecimal.ZERO);

                BigDecimal totalStackAmount = stackService.sumUnSettleStackAmount(customerId);

                CreditDTO creditDTO = creditService.getCreditAvailAmountAndTotalCreditAmount(customerId);

                QueryWrapper<RefundRecords> refundRecordsQueryWrapper = new QueryWrapper<>();
                refundRecordsQueryWrapper.lambda().eq(RefundRecords::getCustomerId, customerId)
                        .eq(RefundRecords::getStatus, CommonCheckStatusEnum.APPROVE.getCode());
                List<RefundRecords> refundRecords = refundRecordsService.list(refundRecordsQueryWrapper);

                BigDecimal refundTotalAmount = BigDecimal.ZERO;

                for (RefundRecords refundRecord : refundRecords) {
                    refundTotalAmount = refundTotalAmount.add(refundRecord.getRefundAmount());
                }
                BigDecimal totalIncome = totalIncomeAmount.add(creditDTO.getTotalCreditAmount().subtract(creditDTO.getTotalAvailAmount()));

                if (totalIncome.subtract(totalStackAmount.add(availAmount).add(refundTotalAmount)).abs().compareTo(BigDecimal.ONE) >= 0)  {
                    companyNames.add(customerProfile.getCompanyName());
                }

            }

            System.out.println("============租户" + yrmTenantId);
            for (String companyName : companyNames) {
                System.out.println(companyName);
            }

        }
    }





}
