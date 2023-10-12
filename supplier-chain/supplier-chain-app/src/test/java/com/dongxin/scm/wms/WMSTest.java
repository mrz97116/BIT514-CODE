package com.dongxin.scm.wms;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.Application;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.enums.LogisticsEnum;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.sm.service.GetActualDeliveryService;
import com.dongxin.scm.wms.condition.GetAccessCodeCondition;
import com.dongxin.scm.wms.condition.GetProductCondition;
import com.dongxin.scm.wms.condition.GetWarehouseCondition;
import com.dongxin.scm.wms.config.WMSContext;
import com.dongxin.scm.wms.exception.WMSException;
import com.dongxin.scm.wms.service.WMSService;
import org.jeecg.config.mybatis.TenantContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class WMSTest {

    @Autowired
    private WMSService wmsService;

    @Autowired
    private WMSContext wmsContext;

    @Autowired
    private GetActualDeliveryService getActualDeliveryService;

    @Autowired
    SalesOrderService salesOrderService;

    @Test
    public void refreshAccessCode() {
        GetAccessCodeCondition getAccessCodeCondition = GetAccessCodeCondition.builder()
                .appId(wmsContext.getAppId())
                .publicKey(wmsContext.getPublicKey())
                .build();
        HttpResponse execute = HttpUtil.createPost(wmsContext.getServerUrl() + wmsContext.getAccessCodeGetAPI())
                .body(getAccessCodeCondition.toJson())
                .execute();
        System.out.println("JSONUtil.toJsonPrettyStr(execute) = " + JSONUtil.toJsonPrettyStr(execute.body()));
    }

    @Test
    public void getProductTest() {
        try {
            PaginatedRequest.PaginatedRequestModel<GetWarehouseCondition> requestBody = new PaginatedRequest.PaginatedRequestModel<>();
            requestBody.setPageSize(10);
            requestBody.setPageNumber(1);
            requestBody.setCondition(GetWarehouseCondition.builder().build());
            PaginatedRequest.PaginatedResponseModel product = wmsService.getWarehouse(requestBody);
            System.out.println("JSONUtil.toJsonPrettyStr(product) = " + JSONUtil.toJsonPrettyStr(product));
        } catch (WMSException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sync_stack() {
        TenantContext.setTenant("2");
        String jsonString = "{\n" +
                "    \"mainId\":\"1462703009696755713\",\n" +
                "    \"billOfLadingNo\":\"ZC202111220004\",\n" +
                "    \"stockOutNoteNo\":\"LN211122152554\",\n" +
                "    \"stockOutDate\":\"2021-11-22 16:42:28\",\n" +
                "    \"operateFlag\":\"I\",\n" +
                "    \"operatorName\":\"贺鹏方\",\n" +
                "    \"remark\":null,\n" +
                "    \"detail\":[\n" +
                "        {\n" +
                "            \"detailId\":\"1462703010011328514\",\n" +
                "            \"materialNo\":\"B3170260010\",\n" +
                "            \"productCode\":\"b5ad5e43-4e18-490c-8db3-cbafac25197f\",\n" +
                "            \"productName\":\"热轧光圆钢筋\",\n" +
                "            \"steelGradeName\":\"HPB300\",\n" +
                "            \"steelMillsName\":\"柳钢\",\n" +
                "            \"standardName\":\"GB/T 1499.2-2018\",\n" +
                "            \"length\":0.0,\n" +
                "            \"width\":0.0,\n" +
                "            \"thick\":6.0,\n" +
                "            \"packageCount\":100,\n" +
                "            \"weightMode\":0,\n" +
                "            \"numberUnit\":\"件\",\n" +
                "            \"weightUnit\":\"吨\",\n" +
                "            \"originalCustomerName\":\"柳州钢铁股份有限公司\",\n" +
                "            \"quantity\":1,\n" +
                "            \"weight\":2.3,\n" +
                "            \"isOwenership\":0\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        com.dongxin.scm.wms.condition.GetActualDelivery param = JSON.parseObject(jsonString, com.dongxin.scm.wms.condition.GetActualDelivery.class);
        getActualDeliveryService.addGetActualDelivery(param);
        System.out.println(param);
    }

    @Test
    public void transfer() {
        TenantContext.setTenant("2");
        String jsonString = "{\n" +
                "    \"transferOwnershipPlanNo\":\"ZC202202150001\",\n" +
                "    \"transferOwnershipNo\":\"LZGH202202150001\",\n" +
                "    \"transferDate\":\"2022-02-15 09:00:01\",\n" +
                "    \"operateFlag\":\"I\",\n" +
                "    \"operatorName\":\"贺鹏方\",\n" +
                "    \"remark\":null,\n" +
                "    \"detail\":[\n" +
                "        {\n" +
                "            \"materialNo\":\"\",\n" +
                "            \"productCode\":\"b8998471-5b95-4071-9700-48c8c7f06fd4\",\n" +
                "            \"productName\":\"热轧带肋钢筋\",\n" +
                "            \"steelGradeName\":\"HRB400E\",\n" +
                "            \"steelMillsName\":\"柳钢\",\n" +
                "            \"standardName\":\"GB/T 1499.2-2018\",\n" +
                "            \"length\":12000.0000,\n" +
                "            \"width\":0.0,\n" +
                "            \"thick\":22.0000,\n" +
                "            \"packageCount\":80,\n" +
                "            \"weightMode\":0,\n" +
                "            \"numberUnit\":\"件\",\n" +
                "            \"weightUnit\":\"吨\",\n" +
                "            \"originalCustomerName\":\"柳州钢铁股份有限公司\",\n" +
                "            \"quantity\":1,\n" +
                "            \"weight\":2.8600,\n" +
                "            \"isOwenership\":0\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        com.dongxin.scm.wms.condition.GetTransferDelivery param = JSON.parseObject(jsonString, com.dongxin.scm.wms.condition.GetTransferDelivery.class);
        getActualDeliveryService.addGetTransferDelivery(param);
    }

    /**
     * 岑还推送物流园定时任务测试
     *
     * @throws JobExecutionException
     */
    @Test
    public void pushLogistics() {
        TenantContext.setTenant("2");

        QueryWrapper<SalesOrder> salesOrderQueryWrapper = new QueryWrapper<>();

        salesOrderQueryWrapper.lambda().eq(SalesOrder::getStockId, "1")
                .eq(SalesOrder::getTenantId, 2)
//                .isNotNull(SalesOrder::getPushDestination)
                .eq(SalesOrder::getPushLogistics, LogisticsEnum.WAIT_PUSH.getCode())
                .like(SalesOrder::getCreateTime, LocalDate.now());

        List<SalesOrder> salesOrderList = salesOrderService.list(salesOrderQueryWrapper);
        if (ObjectUtil.isNotEmpty(salesOrderList)) {

            for (SalesOrder salesOrder : salesOrderList) {
                try {
                    String type = "";
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
