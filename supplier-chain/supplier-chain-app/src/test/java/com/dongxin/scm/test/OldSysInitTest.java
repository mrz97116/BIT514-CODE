package com.dongxin.scm.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dongxin.scm.Application;
import com.dongxin.scm.cm.entity.CustomerBank;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.entity.OldSysCustomer;
import com.dongxin.scm.cm.service.CustomerBankService;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.cm.service.OldSysCustomerService;
import com.dongxin.scm.common.enums.CommonCheckStatusEnum;
import com.dongxin.scm.fm.entity.Bank;
import com.dongxin.scm.fm.entity.FundPool;
import com.dongxin.scm.fm.entity.InitMoeny;
import com.dongxin.scm.fm.enums.InitFundPoolTypeEnum;
import com.dongxin.scm.fm.enums.PaymentMethodEnum;
import com.dongxin.scm.fm.mapper.FundPoolMapper;
import com.dongxin.scm.fm.service.BankService;
import com.dongxin.scm.fm.service.FundAccountService;
import com.dongxin.scm.fm.service.FundDetailService;
import com.dongxin.scm.fm.service.FundPoolService;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.entity.SalesOrderDet;
import com.dongxin.scm.om.service.SalesOrderDetService;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.om.vo.CompanyInfoPage;
import com.dongxin.scm.sm.entity.InitStack;
import com.dongxin.scm.sm.entity.InitStackDet;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.mapper.StackMapper;
import com.dongxin.scm.sm.service.StackService;
import com.dongxin.scm.sm.vo.OptionVO;
import org.jeecg.config.mybatis.TenantContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：melon
 * @date ：Created in 2021/3/9 19:50
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class OldSysInitTest {

    public static int TENANT_ID = 12;

    @Autowired
    OldSysCustomerService oldSysCustomerService;

    @Autowired
    CustomerProfileService customerProfileService;

    @Autowired
    CustomerBankService customerBankService;

    @Autowired
    FundPoolService fundPoolService;

    @Resource
    FundPoolMapper fundPoolMapper;

    @Autowired
    BankService bankService;

    @Autowired
    FundAccountService fundAccountService;

    @Autowired
    FundDetailService fundDetailService;

    @Autowired
    SalesOrderService salesOrderService;

    @Autowired
    SalesOrderDetService salesOrderDetService;

    @Autowired
    StackService stackService;

    @Resource
    StackMapper stackMapper;

    /**
     * 导入旧系统客户数据  第一步
     */
    @Test
    public void importCustomers_1() {
        TenantContext.setTenant("12");

        QueryWrapper<OldSysCustomer> wrapper = new QueryWrapper<>();

        List<OldSysCustomer> list = oldSysCustomerService.list(wrapper);
        for (OldSysCustomer oldSysCustomer : list) {
            CustomerProfile customerProfile = new CustomerProfile();
            customerProfile.setCompanyName(oldSysCustomer.getCompanyName());
            customerProfile.setAlias(oldSysCustomer.getAlias());
            customerProfile.setTaxNo(oldSysCustomer.getTaxNo());
            customerProfile.setAddress(oldSysCustomer.getAddress());
            customerProfile.setLandLineNo(oldSysCustomer.getLandLineNo());
            //租户id 2
            customerProfile.setTenantId(TENANT_ID);
            customerProfileService.save(customerProfile);

            CustomerBank customerBank = new CustomerBank();
            customerBank.setCustomerId(customerProfile.getId());
            customerBank.setBank(oldSysCustomer.getBank());
            customerBank.setBankBranch(oldSysCustomer.getBankBranch());
            customerBank.setBankCardNo(oldSysCustomer.getBankCardNo());
            //租户id 2
            customerBank.setTenantId(TENANT_ID);
            customerBankService.save(customerBank);
        }
    }

    //第二步，初始化财务数据
    @Test
    public void initFundPool() {

        TenantContext.setTenant("12");
        List<InitMoeny> initMoenyList = fundPoolMapper.selectInitFundPool();

        QueryWrapper<CustomerProfile> queryWrapperCustomer = new QueryWrapper<>();
        queryWrapperCustomer.lambda().eq(CustomerProfile::getTenantId, TENANT_ID);
        List<CustomerProfile> customerProfileList = customerProfileService.list(queryWrapperCustomer);
        Map<String, String> customerMap = new HashMap<>();
        for (CustomerProfile customerProfile : customerProfileList) {
            customerMap.put(customerProfile.getCompanyName(), customerProfile.getId());
        }

        QueryWrapper<Bank> bankQueryWrapper = new QueryWrapper<>();
        bankQueryWrapper.lambda().eq(Bank::getBankName, "中国工商银行");
        List<Bank> bankList = bankService.list(bankQueryWrapper);
        Bank bank = bankList.get(0);

        List<String> noHaveCustomer = new ArrayList();
        for (InitMoeny initMoeny : initMoenyList) {
            String customerId = customerMap.get(initMoeny.getCompanyName());
            if (StringUtils.isBlank(customerId)) {
                noHaveCustomer.add(initMoeny.getCompanyName());
                continue;
            }

            FundPool fundPool = new FundPool();
            fundPool.setTenantId(TENANT_ID);
            fundPool.setCustomerId(customerId);
            fundPool.setIncomingAmount(initMoeny.getAccount());
            fundPool.setPaymentBank(bank.getId());
            fundPool.setIncomingDate(new Date());
            fundPool.setVerifyDate(new Date());
            fundPool.setStatus(CommonCheckStatusEnum.PENDING_VERIFY.getCode());

            fundPool.setPaymentMethod(PaymentMethodEnum.CASH.getCode());
            fundPoolService.save(fundPool);

        }

        System.out.println("====================");
        for (String s : noHaveCustomer) {
            System.out.println("'" + s + "',");
        }



    }

    @Test
    public void test_auditFundPools() {
        TenantContext.setTenant("12");
        QueryWrapper<FundPool> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FundPool::getStatus, CommonCheckStatusEnum.PENDING_VERIFY.getCode());
        List<FundPool> fundPoolList = fundPoolService.list(queryWrapper);
        List<String> fundPoolIds = fundPoolList.stream().map(FundPool::getId).collect(Collectors.toList());

        fundPoolService.batchAudit(fundPoolIds);
    }





    /**
     * 提单生成销售单
     *
     * @throws ParseException
     */
    @Test
    public void initStack() throws ParseException {
        String status = "装车";
        List<String> billIds = stackMapper.initQueryBillIds(status);
        List<InitStack> stacks = stackMapper.initQueryStack(billIds);
        for (InitStack one : stacks) {
            SalesOrder salesOrder = new SalesOrder();
            salesOrder.setId(one.getBillId());
            salesOrder.setStackId(one.getId());
            String customerId = customerProfileService.getIdByName(one.getCompanyName(), TENANT_ID);
            salesOrder.setCustomerId(customerId);
            salesOrder.setSalesMan(one.getCreator());

            salesOrder.setCarNo(one.getCarNo());
            salesOrder.setRemark(one.getRemark());
            salesOrder.setShipDate(one.getCarTime());
            salesOrder.setDestination(one.getDest());
            salesOrder.setTenantId(String.valueOf(TENANT_ID));

            List<InitStackDet> stackDets = stackMapper.initQueryStackDets(one.getId());
            List<SalesOrderDet> salesOrderDets = new ArrayList<>();
            for (InitStackDet det : stackDets) {
                SalesOrderDet salesOrderDet = new SalesOrderDet();

                QueryWrapper<SalesOrderDet> salesOrderDetQueryWrapper = new QueryWrapper<>();
                salesOrderDetQueryWrapper.lambda().eq(SalesOrderDet::getParentId, one.getBillId());
                List<SalesOrderDet> list = salesOrderDetService.list(salesOrderDetQueryWrapper);
                List<SalesOrderDet> collect = list.stream().filter(i -> det.getProdInventory().equals(i.getInventoryId())).collect(Collectors.toList());

                salesOrderDet.setId(String.valueOf(collect.get(0).getId()));
                salesOrderDet.setStackDetId(String.valueOf(det.getId()));
                salesOrderDet.setSgSign(det.getSgSign());
                salesOrderDet.setPrice(det.getPrice());
                salesOrderDet.setQty(det.getQty());
                salesOrderDet.setWeight(det.getWeight());
                salesOrderDet.setTotalPrice(det.getSubtotal());
                salesOrderDet.setInventoryId(det.getProdInventory());
                salesOrderDet.setWtMode(det.getWtMode());

                OptionVO stock = stackMapper.initQueryStockId(det.getStack());
                salesOrderDet.setStockId(stock.getValue());
                salesOrderDet.setStockName(stock.getText());

                Inventory inventory = stackMapper.initQueryInventory(det.getProdInventory());
                salesOrderDet.setProdCname(inventory.getProdCname());
                salesOrderDet.setProdCnameOther(inventory.getProdCnameOther());
                salesOrderDet.setOldProdCname(inventory.getOldProdCname());
                salesOrderDet.setProdClassCode(inventory.getProdClassCode());
                salesOrderDet.setOrderThick(inventory.getMatThick());
                salesOrderDet.setOrderWidth(inventory.getMatWidth());
                salesOrderDet.setOrderLen(inventory.getMatLen());
                salesOrderDet.setTenantId(TENANT_ID);

                salesOrderDets.add(salesOrderDet);
            }
            salesOrderService.updateMain(salesOrder, salesOrderDets);
        }
    }


    @Test
    public void initCMJZCustomerInfo(){
        QueryWrapper<CustomerProfile> customerProfileQueryWrapper = new QueryWrapper<>();
        customerProfileQueryWrapper.lambda().eq(CustomerProfile::getTenantId,6);
        List<CustomerProfile> customerProfileList = customerProfileService.list(customerProfileQueryWrapper);

        for(CustomerProfile customerProfile : customerProfileList){
            CustomerProfile addCustomerProfile = new CustomerProfile();
            addCustomerProfile.setCompanyName(customerProfile.getCompanyName());
            addCustomerProfile.setTenantId(12);

            customerProfileService.save(addCustomerProfile);
        }
    }

}
