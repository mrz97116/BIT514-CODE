package com.dongxin.scm.sm.job;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.enums.ProdClassCodeEnum;
import com.dongxin.scm.fm.entity.SettleMatCoding;
import com.dongxin.scm.fm.service.SettleMatCodingService;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.entity.InventoryTiming;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.entity.Processing;
import com.dongxin.scm.sm.service.InventoryService;
import com.dongxin.scm.sm.service.InventoryTimingService;
import com.dongxin.scm.sm.service.MatService;
import com.dongxin.scm.sm.service.ProcessingService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * 阳蕊明专用，更新超期未付款客户，设置为不允许下单
 */

public class OverDuePaidCustomerJob implements Job {
   @Autowired
   private SalesOrderService salesOrderService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        salesOrderService.updateYRMCanLadingBill();
    }
}

