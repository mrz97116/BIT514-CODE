package com.dongxin.scm.sm.service;

import com.dongxin.scm.sm.entity.UnsettleSummary;
import com.dongxin.scm.sm.mapper.UnsettleSummaryMapper;
import com.dongxin.scm.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Description: 客户月度未结算量汇总统计
 * @Author: jeecg-boot
 * @Date: 2021-10-14
 * @Version: V1.0
 */
@Service
@Slf4j
public class UnsettleSummaryService extends BaseService<UnsettleSummaryMapper, UnsettleSummary> {


    @Transactional(rollbackFor = Exception.class)
    public void insertUnsettleSummary() {

        try {
            List<UnsettleSummary> unsettleSummaryList = baseMapper.queryUnsettleSummary(
                    DateUtils.getLastMonthLast2Day(), new Date(), 2);

            for (UnsettleSummary unsettleSummary : unsettleSummaryList) {
                unsettleSummary.setMonth(DateUtils.parseDate(DateUtils.getFirstDay()));
            }

            saveBatch(unsettleSummaryList);
        } catch (Exception e) {
            log.error("insertUnsettleSummary error", e);
        }
    }


}
