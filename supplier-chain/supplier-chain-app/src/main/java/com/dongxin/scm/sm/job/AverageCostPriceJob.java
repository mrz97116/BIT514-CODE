package com.dongxin.scm.sm.job;

import com.dongxin.scm.sm.service.AverageCostService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class AverageCostPriceJob implements Job {
    @Autowired
    private AverageCostService averageCostService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        averageCostService.averagePriceDay();
    }

}
