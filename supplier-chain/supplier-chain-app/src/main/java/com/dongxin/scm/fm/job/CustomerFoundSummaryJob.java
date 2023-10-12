package com.dongxin.scm.fm.job;

import com.dongxin.scm.fm.service.CustomerFoundSummaryService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @Description:客户资金情况汇总
 * @Autor: liujiazhi
 * Date:2021/4/6
 * @Version: V1.0
 */
@Slf4j
public class CustomerFoundSummaryJob implements Job {

    @Autowired
    public CustomerFoundSummaryService customerFoundSummaryService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try {
            log.info("执行 CustomerFoundSummaryJob开始时间{}", DateUtils.gettimestamp());
            customerFoundSummaryService.customerFoundSummary();
            log.info("执行 CustomerFoundSummaryJob结束时间{}", DateUtils.gettimestamp());
        } catch (Exception e) {
            JobExecutionException e2 = new JobExecutionException(e);
            e2.setRefireImmediately(true);
        }
    }

}
