package com.dongxin.scm.test;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.Application;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.entity.JxProtocol;
import com.dongxin.scm.cm.service.CustomerBankService;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.cm.service.JxProtocolService;
import com.dongxin.scm.cm.service.OldSysCustomerService;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.fm.entity.CustomerFoundSummary;
import com.dongxin.scm.fm.entity.FundAccount;
import com.dongxin.scm.fm.entity.FundPool;
import com.dongxin.scm.fm.entity.SettleInfo;
import com.dongxin.scm.fm.enums.AccountTypeEnum;
import com.dongxin.scm.fm.enums.CommonCheckStatusEnum;
import com.dongxin.scm.fm.enums.StatementStateEnum;
import com.dongxin.scm.fm.service.CustomerFoundSummaryService;
import com.dongxin.scm.fm.service.FundAccountService;
import com.dongxin.scm.fm.service.FundPoolService;
import com.dongxin.scm.fm.service.SettleInfoService;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.entity.SalesOrderDet;
import com.dongxin.scm.om.service.PdfService;
import com.dongxin.scm.om.service.SalesOrderDetService;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.om.vo.BillDetVO;
import com.dongxin.scm.sm.entity.ChInitSmStock;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.service.ChInitSmStockService;
import com.dongxin.scm.sm.service.InventoryService;
import com.dongxin.scm.utils.BigDecimalUtils;
import org.jeecg.modules.system.service.ISysDepartService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static org.hibernate.validator.internal.util.CollectionHelper.newHashMap;

/**
 * @author ：melon
 * @date ：Created in 2020/12/16 10:22
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class SerialsFlowNumTest {

    private final static String PDF_URL = "http://172.16.5.88/webroot/decision/view/report?viewlet=SMS_ZX%252FCM%252Fdx_contract.cpt&format=pdf";

    private final static String PDF_XD_URL = "http://172.16.5.88/webroot/decision/view/report?viewlet=SMS_ZX%252FCM%252Fdx_contract_xd.cpt&format=pdf";

    private final static String EXCEL_URL = "http://172.16.5.88/webroot/decision/view/report?viewlet=SMS_ZX%252FRM%252Finvoice.cpt&format=excel";

    private final static String FILE_URL = "D:\\test\\";

    private final static String PDF_SUFFIX = ".pdf";

    private final static String EXCEL_SUFFIX = ".xlsx";

    @Autowired
    PdfService service;

    @Autowired
    JxProtocolService jxProtocolService;

    @Autowired
    CustomerProfileService customerProfileService;

    @Autowired
    OldSysCustomerService oldSysCustomerService;

    @Autowired
    CustomerBankService customerBankService;

    @Autowired
    ChInitSmStockService chInitSmStockService;

    @Autowired
    InventoryService inventoryService;


    @Autowired
    ISysDepartService sysDepartService;

    @Autowired
    CustomerFoundSummaryService customerFoundSummaryService;

    @Autowired
    FundAccountService fundAccountService;

    @Autowired
    FundPoolService fundPoolService;

    @Autowired
    SettleInfoService settleInfoService;

    @Autowired
    SalesOrderService salesOrderService;

    @Autowired
    SalesOrderDetService salesOrderDetService;

    @Test
    public void testGetNextId() {

//        Object id = SerialNoUtil.getSerialNo(SerialNoEnum.PAYMENT_RULE);

        sysDepartService.list();
//        System.out.println("====================" + id);
    }

    @Test
    public void pdfTest() {
        QueryWrapper<JxProtocol> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(JxProtocol::getBranchCompany, "柳州市超顺投资有限公司");
        List<JxProtocol> list = jxProtocolService.list(queryWrapper);
        for (JxProtocol jxProtocol : list) {
            String pdf = PDF_URL + "&id=" + jxProtocol.getId();
            String fileName = jxProtocol.getBranchCompany();
            service.reqPdf(pdf, fileName, FILE_URL, PDF_SUFFIX);
        }
    }

    @Test
    public void pdf_XD_Test() {
        QueryWrapper<JxProtocol> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(JxProtocol::getBranchCompany, "柳州市瑞拓小额贷款有限公司");
        List<JxProtocol> list = jxProtocolService.list(queryWrapper);
        for (JxProtocol jxProtocol : list) {
            String pdf = PDF_XD_URL + "&id=" + jxProtocol.getId();
            String fileName = jxProtocol.getBranchCompany();
            service.reqPdf(pdf, fileName, FILE_URL, PDF_SUFFIX);
        }
    }

    @Test
    public void pdfTest1() {
        String pdf = EXCEL_URL + "&id=1344207755137261569";
        String fileName = "111";
        service.reqPdf(pdf, fileName, FILE_URL, EXCEL_SUFFIX);
    }

    @Test
    public void excelInvoiceTest() {
        QueryWrapper<JxProtocol> queryWrapper = new QueryWrapper<>();
        List<JxProtocol> list = jxProtocolService.list(queryWrapper);
        for (JxProtocol jxProtocol : list) {
            String execl = EXCEL_URL + "&id=" + jxProtocol.getId();
            String fileName = jxProtocol.getBranchCompany();
            service.reqPdf(execl, fileName, FILE_URL, EXCEL_SUFFIX);
        }
    }

    @Test
    public void onePdf() {
        JxProtocol j = jxProtocolService.getBaseMapper().selectById("1354325481432358913");
        String pdf = PDF_URL + "&id=" + j.getId();
        String fileName = j.getBranchCompany();
        service.reqPdf(pdf, fileName, FILE_URL, PDF_SUFFIX);
    }

    @Test
    public void jxProtocol() {

        jxProtocolService.setLevel();

    }


    @Test
    public void yrmInit() {
        List<String> names = jxProtocolService.queryAllYrmCompany();


        List<Integer> tenantIds = newArrayList(5, 6, 7, 8);

        for (Integer tenantId : tenantIds) {
            for (String name : names) {
                CustomerProfile customerProfile = new CustomerProfile();
                customerProfile.setCompanyName(name);
                customerProfile.setTenantId(tenantId);
                customerProfileService.save(customerProfile);
            }
        }


    }

    @Test
    public void chInitSmStock() {
        List<ChInitSmStock> chInitSmStockList = chInitSmStockService.getBaseMapper().selectAll();
        //筛选尺寸存在“米”的数据
        List<ChInitSmStock> filterSizeContainsMeterList = chInitSmStockList.stream().filter(
                stock -> stock.getSize().contains("米")).collect(Collectors.toList());
        //筛选尺寸存在“*”的数据
        List<ChInitSmStock> filterSizeContainsMultiplyList = chInitSmStockList.stream().filter(
                stock -> stock.getSize().contains("*")).collect(Collectors.toList());
        //筛选尺寸为数字的数据
        List<ChInitSmStock> filterNumberSizeList = chInitSmStockList.stream().filter(
                stock -> NumberUtil.isNumber(stock.getSize())).collect(Collectors.toList());

        //尺寸存在“米”的数据，赋值长度、宽度
        for (ChInitSmStock chInitSmStock : filterSizeContainsMeterList) {
            BigDecimal wide = new BigDecimal(chInitSmStock.getSize().substring(0, chInitSmStock.getSize().length() - 1))
                    .multiply(new BigDecimal(1000));
            chInitSmStock.setWide(wide);
            chInitSmStock.setLength(BigDecimal.ZERO);
        }

        //尺寸存在“*”的数据，赋值长度、宽度
        for (ChInitSmStock chInitSmStock : filterSizeContainsMultiplyList) {
            List<String> strSize = Arrays.asList(chInitSmStock.getSize().split("\\*"));
            if (NumberUtil.isNumber(strSize.get(0))) {
                chInitSmStock.setWide(new BigDecimal(strSize.get(0)));
            }

            if (StrUtil.equals(strSize.get(0), "C")) {
                chInitSmStock.setLength(BigDecimal.ZERO);

            }

            if (NumberUtil.isNumber(strSize.get(0))) {
                chInitSmStock.setLength(new BigDecimal(strSize.get(0)));
            }

        }
        //List合并
        filterSizeContainsMultiplyList.addAll(filterNumberSizeList);
        filterSizeContainsMeterList.addAll(filterSizeContainsMultiplyList);

        for (ChInitSmStock chInitSmStock : filterSizeContainsMeterList) {
            if (NumberUtil.isNumber(chInitSmStock.getSize())) {
                chInitSmStock.setWide(new BigDecimal(chInitSmStock.getSize()));
                chInitSmStock.setLength(BigDecimal.ZERO);
            }

            //筛选规格、赋值厚度
            if (NumberUtil.isNumber(chInitSmStock.getSpecs())) {
                chInitSmStock.setThick(new BigDecimal(chInitSmStock.getSpecs()));
            }
            if (chInitSmStock.getSpecs().contains("#")) {
                chInitSmStock.setThick(BigDecimal.ZERO);
            }
            //产品名称赋值
            String prodName = chInitSmStockService.getBaseMapper().selectProdName(chInitSmStock);
            chInitSmStock.setProdName(prodName);
        }

        chInitSmStockService.updateBatchById(filterSizeContainsMeterList);

    }

    @Test
    public void initSmInventory() {
        List<ChInitSmStock> chInitSmStockList = chInitSmStockService.getBaseMapper().selectAll();
        List<Inventory> inventoryList = newArrayList();
        for (ChInitSmStock chInitSmStock : chInitSmStockList) {
            Inventory inventory = new Inventory();
            inventory.setOldProdCname(chInitSmStock.getProdName())
                    .setStockHouseId(chInitSmStock.getWarehouse())
                    .setAvailableQty(chInitSmStock.getStockNum().doubleValue())
                    .setAvailableWeight(chInitSmStock.getStockWeight().doubleValue())
                    .setSgSign(chInitSmStock.getSteelNo())
                    .setMatLen(chInitSmStock.getLength().doubleValue())
                    .setMatWidth(chInitSmStock.getWide().doubleValue())
                    .setMatThick(chInitSmStock.getThick().doubleValue())
                    .setProdClassCode(chInitSmStock.getType())
                    .setTenantId(2);
            inventoryList.add(inventory);
        }
        inventoryService.saveBatch(inventoryList);
    }

    @Test
    public void test_3() {

        List<SalesOrderDet> salesOrderDetList = new ArrayList<>();
        SalesOrderDet salesOrderDet = new SalesOrderDet();
        salesOrderDet.setPrice(new BigDecimal("4563"));
        salesOrderDet.setWeight(7.842d);

        SalesOrderDet salesOrderDet2 = new SalesOrderDet();
        salesOrderDet2.setPrice(new BigDecimal("4533"));
        salesOrderDet2.setWeight(36.972d);

        SalesOrderDet salesOrderDet3 = new SalesOrderDet();
        salesOrderDet3.setPrice(new BigDecimal("4533"));
        salesOrderDet3.setWeight(17.064d);

        SalesOrderDet salesOrderDet4 = new SalesOrderDet();
        salesOrderDet4.setPrice(new BigDecimal("4483"));
        salesOrderDet4.setWeight(28.8d);

        SalesOrderDet salesOrderDet5 = new SalesOrderDet();
        salesOrderDet5.setPrice(new BigDecimal("4483"));
        salesOrderDet5.setWeight(8.892d);

        salesOrderDetList.add(salesOrderDet);
        salesOrderDetList.add(salesOrderDet2);
        salesOrderDetList.add(salesOrderDet3);
        salesOrderDetList.add(salesOrderDet4);
        salesOrderDetList.add(salesOrderDet5);

        BigDecimal totPrice = BigDecimal.ZERO;

        for (SalesOrderDet orderDet : salesOrderDetList) {
            totPrice = totPrice.add(orderDet.getPrice().multiply(BigDecimal.valueOf(orderDet.getWeight())));
        }

        totPrice = totPrice.setScale(2, BigDecimal.ROUND_HALF_UP);


        totPrice = totPrice.setScale(2, RoundingMode.HALF_UP);


        System.out.println(totPrice);

    }


    @Test
    public void updateCustomerFoundSummaryData() {
        //四月五月的list集合
        List<CustomerFoundSummary> aprilCustomerFoundSummaryArrayList = newArrayList();
        List<CustomerFoundSummary> mayCustomerFoundSummaryArrayList = newArrayList();

        //四月五月时间
        String aprilMonth = "2021-04";
        String mayMonth = "2021-05";

        //查询四月所有数据
        QueryWrapper<CustomerFoundSummary> aprilCustomerFoundSummaryQueryWrapper = new QueryWrapper<>();
        aprilCustomerFoundSummaryQueryWrapper.lambda().eq(CustomerFoundSummary::getMonth, aprilMonth)
                .eq(CustomerFoundSummary::getTenantId, 2);
        List<CustomerFoundSummary> aprilCustomerFoundSummaryList = customerFoundSummaryService.list(aprilCustomerFoundSummaryQueryWrapper);


        //遍历四月数据
        for (CustomerFoundSummary customerFoundSummary : aprilCustomerFoundSummaryList) {
            QueryWrapper<FundAccount> fundAccountQueryWrapper = new QueryWrapper<>();
            fundAccountQueryWrapper.lambda().eq(FundAccount::getCustomerId, customerFoundSummary.getCustomerId())
                    .eq(FundAccount::getType, AccountTypeEnum.INCOME.getCode())
                    .eq(FundAccount::getTenantId, 2).eq(FundAccount::getDelFlag, 0);
            FundAccount fundAccount = fundAccountService.getOne(fundAccountQueryWrapper);

            //正确的余额
            BigDecimal balance = fundAccount.getAvailableAmount().add(fundAccount.getPreuseAmount());

            //获取五月的所有来款
            QueryWrapper<FundPool> fundPoolQueryWrapper = new QueryWrapper<>();
            fundPoolQueryWrapper.lambda().ge(FundPool::getCreateTime, "2021-05-01 00:00:00")
                    .eq(FundPool::getCustomerId, customerFoundSummary.getCustomerId())
                    .eq(FundPool::getStatus, CommonCheckStatusEnum.APPROVE.getCode())
                    .eq(FundPool::getTenantId, 2)
                    .eq(FundPool::getDelFlag, 0);
            List<FundPool> fundPoolList = fundPoolService.list(fundPoolQueryWrapper);

            //五月来款金额
            BigDecimal fundPoolAccount = BigDecimal.ZERO;
            if (CollectionUtil.isNotEmpty(fundPoolList)) {
                for (FundPool fundPool : fundPoolList) {
                    fundPoolAccount = fundPoolAccount.add(fundPool.getIncomingAmount());
                }
            }

            //减去来款金额
            balance = balance.subtract(fundPoolAccount);


            //五月结算数据
            QueryWrapper<SettleInfo> settleInfoQueryWrapper = new QueryWrapper<>();
            settleInfoQueryWrapper.lambda().ge(SettleInfo::getCreateTime, "2021-05-01 00:00:00")
                    .eq(SettleInfo::getCustomer, customerFoundSummary.getCustomerId())
                    .eq(SettleInfo::getStatementState, StatementStateEnum.NORMAL.getCode())
                    .eq(SettleInfo::getTenantId, 2)
                    .eq(SettleInfo::getDelFlag, 0);
            List<SettleInfo> settleInfoList = settleInfoService.list(settleInfoQueryWrapper);

            //五月结算金额
            BigDecimal settleAccount = BigDecimal.ZERO;
            if (CollectionUtil.isNotEmpty(settleInfoList)) {
                for (SettleInfo settleInfo : settleInfoList) {
                    settleAccount = settleAccount.add(settleInfo.getSettleAmount());
                }
            }

            //加上结算金额
            balance = balance.add(settleAccount);


            //给四月期末余额赋值
            customerFoundSummary.setEndingBanlance(balance);

            //放进四月集合
            aprilCustomerFoundSummaryArrayList.add(customerFoundSummary);


            //查询出对应的五月的数据
            QueryWrapper<CustomerFoundSummary> mayCustomerFoundSummaryQueryWrapper = new QueryWrapper<>();
            mayCustomerFoundSummaryQueryWrapper.lambda().eq(CustomerFoundSummary::getMonth, mayMonth)
                    .eq(CustomerFoundSummary::getTenantId, 2)
                    .eq(CustomerFoundSummary::getCustomerId, customerFoundSummary.getCustomerId());
            CustomerFoundSummary mayCustomerFoundSummary = customerFoundSummaryService.getOne(mayCustomerFoundSummaryQueryWrapper);

            //给五月期初余额赋值
            mayCustomerFoundSummary.setOpeningBalance(balance);

            //放进五月集合
            mayCustomerFoundSummaryArrayList.add(mayCustomerFoundSummary);
        }


        System.out.println(aprilCustomerFoundSummaryArrayList);
        System.out.println(mayCustomerFoundSummaryArrayList);


    }


    @Test
    public void selectEquipmentSuppliesErrorData() {
        QueryWrapper<SalesOrder> salesOrderQueryWrapper = new QueryWrapper<>();
        salesOrderQueryWrapper.lambda().eq(SalesOrder::getTenantId, 2)
                .eq(SalesOrder::getEquipmentSuppliesFlag, YesNoEnum.YES.getCode());
        List<SalesOrder> salesOrderList = salesOrderService.list(salesOrderQueryWrapper);

        List<SalesOrder> errorSalesOrderList = newArrayList();

        List<BillDetVO> billDetVOList = newArrayList();

        for (SalesOrder salesOrder : salesOrderList) {
            BigDecimal totalPrice = BigDecimal.ZERO;
            QueryWrapper<SalesOrderDet> salesOrderDetQueryWrapper = new QueryWrapper<>();
            salesOrderDetQueryWrapper.lambda().eq(SalesOrderDet::getParentId, salesOrder.getId());
            List<SalesOrderDet> salesOrderDetList = salesOrderDetService.list(salesOrderDetQueryWrapper);

            Map<BigDecimal, Double> priceAndWeight = newHashMap();
            salesOrderDetList.forEach(det -> priceAndWeight.merge(det.getPrice(), det.getWeight(), Double::sum));
            for (Map.Entry<BigDecimal, Double> entry : priceAndWeight.entrySet()) {
                totalPrice = totalPrice.add(BigDecimalUtils.multiply(entry.getKey(), entry.getValue()));
            }

            BigDecimal salesOrderDetTotalPrice = BigDecimal.ZERO;
            for(SalesOrderDet salesOrderDet : salesOrderDetList){
                salesOrderDetTotalPrice = salesOrderDetTotalPrice.add(salesOrderDet.getTotalPrice());
            }

            if (salesOrderDetTotalPrice.compareTo(totalPrice) != 0) {
                errorSalesOrderList.add(salesOrder);
                BillDetVO billDetVO = new BillDetVO();
                billDetVO.setId(salesOrder.getId());
                billDetVO.setProd_name(salesOrder.getCustomerId());
                billDetVO.setPrice(salesOrderDetTotalPrice);
                billDetVO.setAddPrice(totalPrice);
                billDetVOList.add(billDetVO);
            }
        }

        System.out.println(errorSalesOrderList);
        System.out.println(billDetVOList);

        BigDecimal errorNameGF = BigDecimal.ZERO;//1372008291602341889
        BigDecimal nameGF = BigDecimal.ZERO;

        BigDecimal errorNameJT = BigDecimal.ZERO;//1372008597568430081
        BigDecimal nameJT = BigDecimal.ZERO;

        BigDecimal errorNameZJ = BigDecimal.ZERO;//1372008681228017666
        BigDecimal nameZJ = BigDecimal.ZERO;
        for(BillDetVO billDetVO : billDetVOList){
            if(billDetVO.getProd_name().equals("1372008291602341889")){
                errorNameGF = errorNameGF.add(billDetVO.getPrice());
                nameGF = nameGF.add(billDetVO.getAddPrice());
            }

            if(billDetVO.getProd_name().equals("1372008597568430081")){
                errorNameJT = errorNameJT.add(billDetVO.getAddPrice());
                nameJT = nameJT.add(billDetVO.getAddPrice());
            }

            if(billDetVO.getProd_name().equals("1372008681228017666")){
                errorNameZJ = errorNameZJ.add(billDetVO.getPrice());
                nameZJ = nameZJ.add(billDetVO.getAddPrice());
            }
        }

        System.out.println(nameZJ);

    }


}
