package com.dongxin.scm.sm.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.mapper.MatMapper;
import com.dongxin.scm.sm.service.MatService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 * @author chenyuncong
 */
@Slf4j
public class MatJob implements Job {

    @Autowired
    private MatService matService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("执行 MatJob，删除未入库的材料，时间{}", DateUtils.gettimestamp());

        //删除仓库id为空的材料  ch
        QueryWrapper<Mat> matQueryWrapper = new QueryWrapper<>();
        matQueryWrapper.lambda().isNull(Mat::getStockHouseId).eq(Mat::getTenantId,2);
        boolean remove = matService.remove(matQueryWrapper);
    }
}
