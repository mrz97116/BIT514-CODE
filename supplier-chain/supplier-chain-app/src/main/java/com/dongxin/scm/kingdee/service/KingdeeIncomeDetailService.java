package com.dongxin.scm.kingdee.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.fm.entity.FundPool;
import com.dongxin.scm.fm.service.FundPoolService;
import com.dongxin.scm.kingdee.entity.KingdeeBasicInfo;
import com.dongxin.scm.kingdee.entity.KingdeeIncomeDetail;
import com.dongxin.scm.kingdee.mapper.KingdeeIncomeDetailMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 金蝶资金明细表
 * @Author: jeecg-boot
 * @Date:   2021-03-26
 * @Version: V1.0
 */
@Service
@Slf4j
public class KingdeeIncomeDetailService extends BaseService<KingdeeIncomeDetailMapper, KingdeeIncomeDetail> {

    @Autowired
    private FundPoolService fundPoolService;

    @Transactional
    public void syncIncomeDetail(Date startTime, Date endTime) {


        log.info("============================syncIncomeDetail start==============");
        //查询所有updateTime在时间段内的数据
        QueryWrapper<FundPool> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(FundPool::getUpdateTime, startTime, endTime);
        List<FundPool> fundPoolListList = fundPoolService.list(queryWrapper);

        if (CollectionUtil.isNotEmpty(fundPoolListList)) {
            List<KingdeeIncomeDetail> kingdeeIncomeDetailList = newArrayList();
            for (FundPool fundPool : fundPoolListList) {
                KingdeeIncomeDetail incomeDetail = new KingdeeIncomeDetail();
                incomeDetail.setId(fundPool.getId());
                incomeDetail.setCreateTime(fundPool.getCreateTime());
                incomeDetail.setUpdateTime(fundPool.getUpdateTime());
                incomeDetail.setBillNo(fundPool.getId());
                incomeDetail.setDate(fundPool.getCreateTime());
                incomeDetail.setCustomerId(fundPool.getCustomerId());
                incomeDetail.setSettleType("1");
                incomeDetail.setAmount(fundPool.getIncomingAmount());
                incomeDetail.setActualAmount(fundPool.getIncomingAmount());
                incomeDetail.setType("1");
                kingdeeIncomeDetailList.add(incomeDetail);
            }
            //有id，批量更新
            saveBatch(kingdeeIncomeDetailList);
        }

        log.info("============================syncIncomeDetail end==============");
    }

}
