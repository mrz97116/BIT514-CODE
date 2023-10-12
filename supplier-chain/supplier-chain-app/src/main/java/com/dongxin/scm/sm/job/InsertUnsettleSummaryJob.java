package com.dongxin.scm.sm.job;

import com.dongxin.scm.sm.service.*;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 插入未结算量数据
 */

public class InsertUnsettleSummaryJob implements Job {
    @Autowired
    private UnsettleSummaryService unsettleSummaryService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        unsettleSummaryService.insertUnsettleSummary();
    }

}

