package com.dongxin.scm.sm.job;


import cn.hutool.core.collection.CollUtil;
import com.dongxin.scm.sm.dto.DailySalesDateDTO;
import com.dongxin.scm.sm.entity.DailyStatement;
import com.dongxin.scm.sm.mapper.DailyStatementMapper;
import com.dongxin.scm.sm.mapper.InventoryMapper;
import com.dongxin.scm.sm.service.DailyStatementService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
public class DailyStatementJob implements Job {

    @Resource
    private InventoryMapper inventoryMapper;
    @Resource
    private DailyStatementMapper dailyStatementMapper;
    @Autowired
    private DailyStatementService dailyStatementService;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("DailyStatementJob  时间:{}", DateUtils.getTimestamp());

        //价格（基础价、成交价）、成交量 进货量 库存量
        List<DailySalesDateDTO> priceMsg = inventoryMapper.priceMsg();
        List<DailySalesDateDTO> transactionWeightList = inventoryMapper.transactionWeight();
        List<DailySalesDateDTO> stockWeightList = inventoryMapper.stockWeight();
        List<DailySalesDateDTO> inventoryWeightList = inventoryMapper.inventoryWeight();

        //清除 sm_daily_statement 表的数据
        dailyStatementMapper.deleteTableData();

        for (DailySalesDateDTO transactionWeight : transactionWeightList) {
            DailyStatement dailyStatement = new DailyStatement();
            //设置品种 成交量
            dailyStatement.setMat(transactionWeight.getMat())
                    .setTransactionWeight(transactionWeight.getTransactionWeight());

            //设置 基础价、成交价
            for (DailySalesDateDTO price : priceMsg) {
                if (price.getMat().equals(transactionWeight.getMat())) {
                    dailyStatement.setBasePrice(price.getBasePrice())
                            .setTransactionPrice(price.getTransactionPrice());
                    break;
                }
            }

            //设置 进货量
            if (CollUtil.isEmpty(stockWeightList)) {
                dailyStatement.setStockWeight((double) 0);
            }
            for (DailySalesDateDTO stockWeight : stockWeightList) {
                if (stockWeight.getMat().equals(transactionWeight.getMat())) {
                    dailyStatement.setStockWeight(stockWeight.getStockWeight());
                    break;
                } else {
                    dailyStatement.setStockWeight((double) 0);
                }
            }

            //设置 库存量、计重方式、规格
            for (DailySalesDateDTO inventoryWeight : inventoryWeightList) {
                if (inventoryWeight.getMat().equals(transactionWeight.getMat())) {
                    dailyStatement.setInventoryWeight(inventoryWeight.getInventoryWeight())
                            .setWtMode(inventoryWeight.getWtMode())
                            .setCustMatSpecs(inventoryWeight.getCustMatSpecs());
                    break;
                } else {
                    dailyStatement.setInventoryWeight((double) 0);
                }
            }
            dailyStatementService.save(dailyStatement);
        }
    }
}
