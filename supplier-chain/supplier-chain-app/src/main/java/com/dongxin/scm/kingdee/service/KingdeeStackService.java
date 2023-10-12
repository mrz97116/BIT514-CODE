package com.dongxin.scm.kingdee.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.fm.entity.FundPool;
import com.dongxin.scm.kingdee.entity.KingdeeIncomeDetail;
import com.dongxin.scm.kingdee.entity.KingdeeStack;
import com.dongxin.scm.kingdee.mapper.KingdeeStackMapper;
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
 * @Description: 金蝶业务单据表
 * @Author: jeecg-boot
 * @Date: 2021-03-26
 * @Version: V1.0
 */
@Service
@Slf4j
public class KingdeeStackService extends BaseService<KingdeeStackMapper, KingdeeStack> {


    @Autowired
    private StackService stackService;

    @Transactional(rollbackFor = Exception.class)
    public void syncKingdeeStack(Date startTime, Date endTime) {
        log.info("============================syncKingdeeStack start==============");
        syncInsertStack(startTime, endTime);
//        syncUpdateStack(startTime, endTime);

        log.info("============================syncKingdeeStack end==============");


    }



    private void syncInsertStack(Date startTime, Date endTime) {
        //查询所有createTime在时间段内的数据
        QueryWrapper<Stack> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(Stack::getCreateTime, startTime, endTime);

        List<Stack> stackList = stackService.list(queryWrapper);

        if (CollectionUtil.isNotEmpty(stackList)) {
            List<KingdeeStack> kingdeeStackList = newArrayList();
            for (Stack stack : stackList) {
                KingdeeStack kingdeeStack = new KingdeeStack();
                kingdeeStack.setId(stack.getId());
                kingdeeStack.setCreateTime(stack.getCreateTime());
                kingdeeStack.setUpdateTime(stack.getUpdateTime());
                kingdeeStack.setBillNo(stack.getStackingNo());
                kingdeeStack.setBillDate(stack.getCreateTime());
                kingdeeStack.setCustomerId(stack.getCustomerId());
                kingdeeStack.setStockId(stack.getStockNoClass());
                kingdeeStack.setBizAmount(stack.getTotalAmount());
                kingdeeStack.setAmount(stack.getTotalAmount());
                kingdeeStack.setType("1");
                kingdeeStack.setTenantId(stack.getTenantId());
                kingdeeStackList.add(kingdeeStack);
            }
            saveBatch(kingdeeStackList);
        }
    }


    private void syncUpdateStack(Date startTime, Date endTime) {
        //查询所有createTime在时间段内的数据
        QueryWrapper<Stack> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(Stack::getUpdateTime, startTime, endTime);

        List<Stack> stackList = stackService.list(queryWrapper);

        if (CollectionUtil.isNotEmpty(stackList)) {
            List<KingdeeStack> kingdeeStackList = newArrayList();
            for (Stack stack : stackList) {
                KingdeeStack kingdeeStack = new KingdeeStack();
                kingdeeStack.setId(stack.getId());
                kingdeeStack.setCreateTime(stack.getCreateTime());
                kingdeeStack.setUpdateTime(stack.getUpdateTime());
                kingdeeStack.setBillNo(stack.getStackingNo());
                kingdeeStack.setBillDate(stack.getCreateTime());
                kingdeeStack.setCustomerId(stack.getCustomerId());
                kingdeeStack.setStockId(stack.getStockNoClass());
                kingdeeStack.setBizAmount(stack.getTotalAmount());
                kingdeeStack.setAmount(stack.getTotalAmount());
                kingdeeStack.setType("1");
                kingdeeStack.setTenantId(stack.getTenantId());
                kingdeeStackList.add(kingdeeStack);
            }
            saveOrUpdateBatch(kingdeeStackList);
        }
    }

}
