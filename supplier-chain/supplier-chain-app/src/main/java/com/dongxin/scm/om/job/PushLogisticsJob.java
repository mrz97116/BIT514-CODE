package com.dongxin.scm.om.job;

import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.sm.service.GetActualDeliveryService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.quartz.Job;

/**
 * 定时为岑还推送物流园提单
 * 每小时一次，推送状态未推送
 * 遍历当天提单
 * 物流园库 物流园目的地不为空
 */
@Slf4j
public class PushLogisticsJob implements Job {

    @Autowired
    SalesOrderService salesOrderService;

    @Autowired
    GetActualDeliveryService getActualDeliveryService;

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        getActualDeliveryService.pushAllSalesOrder();
    }


}
