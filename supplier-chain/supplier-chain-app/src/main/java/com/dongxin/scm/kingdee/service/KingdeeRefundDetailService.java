package com.dongxin.scm.kingdee.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.kingdee.entity.KingdeeRefundDetail;
import com.dongxin.scm.kingdee.entity.KingdeeStack;
import com.dongxin.scm.kingdee.mapper.KingdeeRefundDetailMapper;
import com.dongxin.scm.sm.entity.Stack;
import com.dongxin.scm.sm.service.StackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 金蝶退款数据表
 * @Author: jeecg-boot
 * @Date:   2021-03-26
 * @Version: V1.0
 */
@Service
@Slf4j
public class KingdeeRefundDetailService extends BaseService<KingdeeRefundDetailMapper, KingdeeRefundDetail> {


    @Autowired
    private StackService stackService;


    @Transactional
    public void syncInsertRefund(Date startTime, Date endTime) {
        log.info("===================syncInsertRefund start");

        //查询所有createTime在时间段内的数据

        List<Stack> stackList = stackService.selectDeleteStack(startTime, endTime);

        if (CollectionUtil.isNotEmpty(stackList)) {
            List<KingdeeRefundDetail> kingdeeRefundDetails = newArrayList();
            for (Stack stack : stackList) {
                KingdeeRefundDetail kingdeeRefundDetail = new KingdeeRefundDetail();
                kingdeeRefundDetail.setId(stack.getId());
                kingdeeRefundDetail.setCreateTime(stack.getCreateTime());
                kingdeeRefundDetail.setUpdateTime(stack.getUpdateTime());
                kingdeeRefundDetail.setBillNo(stack.getStackingNo());
                kingdeeRefundDetail.setDate(new Date());
                kingdeeRefundDetail.setCustomerId(stack.getCustomerId());
                kingdeeRefundDetail.setSettleType("3");
                kingdeeRefundDetail.setAmount(stack.getTotalAmount());
                kingdeeRefundDetail.setTenantId(Integer.valueOf(stack.getTenantId()));
                kingdeeRefundDetails.add(kingdeeRefundDetail);
            }
            saveBatch(kingdeeRefundDetails);
        }

        log.info("===================syncInsertRefund end");

    }
}
