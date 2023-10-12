package com.dongxin.scm.sm.job;

import com.dongxin.scm.bd.service.UnsettledService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class UnsettledJob implements Job {
    @Autowired
    private UnsettledService unsettledService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        unsettledService.unsettledSalesOrder();
    }

}
