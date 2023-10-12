package com.dongxin.scm.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.Application;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.fm.entity.FundDetail;
import com.dongxin.scm.fm.enums.FundDetailTypeEnum;
import com.dongxin.scm.fm.service.FundAccountService;
import com.dongxin.scm.fm.service.FundDetailService;
import com.dongxin.scm.fm.service.SettleMatCodingService;
import com.dongxin.scm.kingdee.service.*;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.entity.SalesOrderDet;
import com.dongxin.scm.om.service.SalesOrderDetService;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.om.vo.BillDetVO;
import com.dongxin.scm.sm.dto.ProcessYrmDTO;
import com.dongxin.scm.sm.entity.Stack;
import com.dongxin.scm.sm.entity.UnsettleSummary;
import com.dongxin.scm.sm.mapper.MatMapper;
import com.dongxin.scm.sm.service.ProcessingService;
import com.dongxin.scm.sm.service.StackService;
import com.dongxin.scm.sm.service.UnsettleSummaryService;
import com.dongxin.scm.utils.BigDecimalUtils;
import com.dongxin.scm.utils.DateUtils;
import org.jeecg.config.mybatis.TenantContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.dongxin.scm.utils.FileReadUtils.readTxtFile;
import static com.google.common.collect.Lists.newArrayList;
import static org.hibernate.validator.internal.util.CollectionHelper.newHashMap;

/**
 * @author ：melon
 * @date ：Created in 2020/12/16 10:22
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class FunctionTest {

    @Resource
    MatMapper matMapper;

    @Autowired
    private KingdeeStackService kingdeeStackService;

    @Autowired
    private KingdeeTenantInfoService kingdeeTenantInfoService;

    @Autowired
    private KingdeeBasicInfoService kingdeeBasicInfoService;

    @Autowired
    private KingdeeIncomeDetailService kingdeeIncomeDetailService;

    @Autowired
    private KingdeeRefundDetailService kingdeeRefundDetailService;

    @Autowired
    private StackService stackService;

    @Autowired
    private SalesOrderService salesOrderService;

    @Autowired
    private FundDetailService fundDetailService;

    @Autowired
    private FundAccountService fundAccountService;

    @Autowired
    private ProcessingService processingService;

    @Autowired
    private SettleMatCodingService settleMatCodingService;

    @Autowired
    private SalesOrderDetService salesOrderDetService;

    @Autowired
    private UnsettleSummaryService unsettleSummaryService;


    @Test
    public void test_unSettle_summary() {
        TenantContext.setTenant("2");

        unsettleSummaryService.insertUnsettleSummary();

    }

    @Test
    public void readFile() throws ParseException {


        List<String> ids = matMapper.selectMatRecoverId();


        Map<String, String> map = readTxtFile("D:\\sm_mat_4 (3).sql", ids);


        System.out.println("+++++++++++++++++++++++++++++++++++++");
        map.forEach((k, v) -> {

            System.out.println("update sm_mat set car_no = '" + v + "' where id = '" + k + "';");

        });
        System.out.println("+++++++++++++++++++++++++++++++++++++");


    }

    @Test
    public void test_regex() {
        String str = "INSERT INTO `recover`.`sm_mat`(`mat_act_width`, `mat_net_wt`, `mat_width`, `storage_time`, `preuse_qty`, `mat_act_thick`, `car_no`, `order_cust_cname`, `finishing_requirements`, `purchase_price`, `stock_type`, `struc_code`, `tenant_id`, `surface_quality_level`, `del_flag`, `sg_sign`, `id`, `flatless_accu`, `surface_treatment`, `dealer_id`, `mat_act_wt`, `stock_house_id`, `create_by`, `wt_mode`, `add_type`, `stacking_no`, `prod_cname`, `mat_no`, `prod_cname_other`, `degrease_flag`, `prod_code`, `plating_structure`, `cake_no`, `update_by`, `prod_class_code`, `old_prod_cname`, `available_qty`, `cost_price`, `mat_status`, `update_time`, `available_weight`, `consign_user_name`, `optimal_face_toward`, `stock_no`, `oil_demand`, `trim_type`, `sell_flag`, `cust_mat_specs`, `raw_material_no`, `mat_act_len`, `prod_class_desc`, `plating_weight`, `mat_thick`, `sys_org_code`, `piece_weight`, `prod_class`, `jx_prod_name`, `mat_len`, `preuse_weight`, `remarks`, `mat_theory_wt`, `mat_num`, `mat_discrep_wt`, `technical_standard`, `stock_location`, `create_time`, `mat_kind`, `trim_flag`, `width_tol_accu`) VALUES (NULL, 1.481, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'on_route', NULL, 6, NULL, 0, 'HRB400E', '1368819261738827777', NULL, NULL, NULL, NULL, NULL, 'liyanyun', NULL, NULL, NULL, '热轧带肋钢筋', 'BB2130193042', '热轧带肋钢筋', NULL, NULL, NULL, NULL, NULL, 'B', NULL, NULL, NULL, NULL, NULL, NULL, '柳州市阳蕊明物资有限公司', NULL, NULL, NULL, NULL, NULL, '25', NULL, NULL, NULL, NULL, 25, 'A10', '热轧带肋钢筋', NULL, NULL, 0, NULL, '桂BD6307', NULL, 1, -0.002, NULL, NULL, '2021-03-08 15:01:58', 'BW', NULL, NULL); #start 1622427 end 1715173 time 2021-03-08 15:01:55";


        String[] strArray = str.split(",");
        Pattern compile = Pattern.compile("^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-Z0-9]{4}[A-Z0-9挂学警港澳]{1}$");

        for (String s : strArray) {
            if (!s.equalsIgnoreCase("null"))
                s = s.replace("'", "").trim();
            Matcher matcher = compile.matcher(s);

            while (matcher.find()) {
                System.out.println(matcher.group(0));
            }
        }

    }

    @Test
    public void testKingdeeSync() {
        Date start = DateUtils.parseDate("2020-05-01 00:00:00");
        Date end = DateUtils.parseDate("2021-04-01 00:00:00");
//        kingdeeStackService.syncKingdeeStack(start, end);
//        kingdeeTenantInfoService.syncTenantInfo(start, end);
//        kingdeeBasicInfoService.syncBasicInfo(start, end);
//        kingdeeIncomeDetailService.syncIncomeDetail(start, end);
        kingdeeRefundDetailService.syncInsertRefund(start, end);

    }


    @Test
    public void aaa(){

        List<Stack> stackListNo = newArrayList();

        QueryWrapper<Stack> queryWrapperStack = new QueryWrapper<>();
        queryWrapperStack.lambda().eq(Stack::getTenantId,2).eq(Stack::getDelFlag,0).eq(Stack::getSettled,"unsettle");
        List<Stack> stackList = stackService.list(queryWrapperStack);


        for(Stack stack : stackList){
            QueryWrapper<SalesOrder> salesOrderQueryWrapper = new QueryWrapper<>();
            salesOrderQueryWrapper.lambda().eq(SalesOrder::getSaleBillNo,stack.getSaleBillNo())
                    .eq(SalesOrder::getTenantId,2);
            SalesOrder salesOrder = salesOrderService.getOne(salesOrderQueryWrapper);

            QueryWrapper<FundDetail> detailQueryWrapper = new QueryWrapper<>();
            detailQueryWrapper.lambda().eq(FundDetail::getOutTradeNo,salesOrder.getId())
                    .eq(FundDetail::getType, FundDetailTypeEnum.PRE_USE.getCode());
            List<FundDetail> fundDetailList = fundDetailService.list(detailQueryWrapper);

            BigDecimal preUseAccount = BigDecimal.ZERO;
            for(FundDetail fundDetail : fundDetailList){
                preUseAccount = preUseAccount.add(fundDetail.getAmount());
            }

            if(preUseAccount.compareTo(stack.getTotalAmount()) != 0){
                Stack stackNo = new Stack();
                stackNo.setCustomerId(stack.getCustomerId());
                stackNo.setTotalAmount(stack.getTotalAmount());
                stackNo.setSettledTotalPrice(preUseAccount);
                stackNo.setCreateTime(stack.getCreateTime());
                stackNo.setCreateTime(stack.getCreateTime());
                stackNo.setUpdateTime(stack.getUpdateTime());
                stackNo.setStackingNo(stack.getStackingNo());
                stackNo.setSaleBillNo(stack.getSaleBillNo());
                //提单id
                stackNo.setSettledId(salesOrder.getId());

                stackListNo.add(stackNo);
            }
        }

//        for(Stack stackNo : stackListNo){
//            //查询资金账户
//            QueryWrapper<FundAccount> fundAccountQueryWrapper = new QueryWrapper<>();
//            fundAccountQueryWrapper.lambda().eq(FundAccount::getCustomerId,stackNo.getCustomerId())
//                    .eq(FundAccount::getType, AccountTypeEnum.INCOME.getCode());
//            FundAccount fundAccount = fundAccountService.getOne(fundAccountQueryWrapper);
//
//            //查询资金明细
//            QueryWrapper<FundDetail> fundDetailQueryWrapper = new QueryWrapper<>();
//            fundDetailQueryWrapper.lambda().eq(FundDetail::getOutTradeNo,stackNo.getSettledId())
//                    .eq(FundDetail::getType,FundDetailTypeEnum.PRE_USE.getCode());
//            List<FundDetail> fundDetailList = fundDetailService.list(fundDetailQueryWrapper);
//            FundDetail fundDetail = fundDetailList.get(0);
//
//            //装车单金额大于资金明细金额
//            if(stackNo.getTotalAmount().compareTo(stackNo.getSettledTotalPrice()) > 0){
//                //修改金额
//                BigDecimal modifyAccount = stackNo.getTotalAmount().subtract(stackNo.getSettledTotalPrice());
//
//                //-可用金额
//                fundAccount.setAvailableAmount(fundAccount.getAvailableAmount().subtract(modifyAccount));
//                //+预用金额
//                fundAccount.setPreuseAmount(fundAccount.getPreuseAmount().add(modifyAccount));
//                fundAccountService.updateById(fundAccount);
//
//                //+明细金额
//                fundDetail.setAmount(fundDetail.getAmount().add(modifyAccount));
//                fundDetailService.updateById(fundDetail);
//
//            }
//
//            //资金明细金额大于装车单金额
//            if(stackNo.getTotalAmount().compareTo(stackNo.getSettledTotalPrice()) < 0){
//                //修改金额
//                BigDecimal modifyAccount = stackNo.getSettledTotalPrice().subtract(stackNo.getTotalAmount());
//
//                //+可用金额
//                fundAccount.setAvailableAmount(fundAccount.getAvailableAmount().add(modifyAccount));
//                //-预用金额
//                fundAccount.setPreuseAmount(fundAccount.getPreuseAmount().subtract(modifyAccount));
//                fundAccountService.updateById(fundAccount);
//
//                //-明细金额
//                fundDetail.setAmount(fundDetail.getAmount().subtract(modifyAccount));
//                fundDetailService.updateById(fundDetail);
//            }
//        }

        System.out.println(stackListNo);



    }


    @Test
    public void test_addYrm(){

        String inventoryId = "1384397648938319873";
        String customerId = "123";
        String salesManId = "456";
        String remarks = "正在测试Yrm加工...";

        List<ProcessYrmDTO> processDtoList = new ArrayList<>();
        ProcessYrmDTO yrmDTO1 = new ProcessYrmDTO();

        yrmDTO1.setLen(7040.0);
        yrmDTO1.setCakeNum(4.0);
        yrmDTO1.setCakeWeight(396.0);
        yrmDTO1.setWeight(1586.0);
        processDtoList.add(yrmDTO1);

        ProcessYrmDTO yrmDTO2 = new ProcessYrmDTO();
        yrmDTO2.setLen(7440.0);
        yrmDTO2.setCakeNum(7.0);
        yrmDTO2.setCakeWeight(419.0);
        yrmDTO2.setWeight(2932.0);
        processDtoList.add(yrmDTO2);

        ProcessYrmDTO yrmDTO3 = new ProcessYrmDTO();
        yrmDTO3.setLen(4800.0);
        yrmDTO3.setCakeNum(35.0);
        yrmDTO3.setCakeWeight(270.0);
        yrmDTO3.setWeight(9459.0);
        processDtoList.add(yrmDTO3);

        ProcessYrmDTO yrmDTO4 = new ProcessYrmDTO();
        yrmDTO4.setLen(4840.0);
        yrmDTO4.setCakeNum(38.7);
        yrmDTO4.setCakeWeight(273.0);
        yrmDTO4.setWeight(10533.0);
        processDtoList.add(yrmDTO4);

        processingService.processMatYrm(inventoryId,processDtoList,customerId,salesManId,remarks);
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
            for (SalesOrderDet salesOrderDet : salesOrderDetList) {
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

        BigDecimal errorNameGF = BigDecimal.ZERO;//13720082916023
    }
}
