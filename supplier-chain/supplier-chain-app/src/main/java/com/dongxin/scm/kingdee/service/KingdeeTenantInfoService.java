package com.dongxin.scm.kingdee.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.kingdee.entity.KingdeeTenantInfo;
import com.dongxin.scm.kingdee.mapper.KingdeeTenantInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.jeecg.modules.system.entity.SysTenant;
import org.jeecg.modules.system.service.impl.SysTenantServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.RollbackException;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 租户关系映射表
 * @Author: jeecg-boot
 * @Date:   2021-03-26
 * @Version: V1.0
 */
@Service
@Slf4j
public class KingdeeTenantInfoService extends BaseService<KingdeeTenantInfoMapper, KingdeeTenantInfo> {

    @Autowired
    private SysTenantServiceImpl sysTenantService;

    /**
     * 根据时间段同步金蝶租户关系映射表
     * @param startTime
     * @param endTime
     */
    @Transactional(rollbackFor = Exception.class)
    public void syncTenantInfo(Date startTime, Date endTime) {
        log.info("============================syncTenantInfo start==============");
        //查询所有createTime在时间段内的数据
        QueryWrapper<SysTenant> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(SysTenant::getCreateTime, startTime, endTime);
        List<SysTenant> sysTenantList = sysTenantService.list(queryWrapper);

        if (CollectionUtil.isNotEmpty(sysTenantList)) {
            List<KingdeeTenantInfo> kingdeeTenantInfoList = newArrayList();
            for (SysTenant sysTenant : sysTenantList) {
                KingdeeTenantInfo kingdeeTenantInfo = new KingdeeTenantInfo();
                kingdeeTenantInfo.setCreateTime(sysTenant.getCreateTime());
                kingdeeTenantInfo.setId(sysTenant.getId() + "");
                kingdeeTenantInfo.setTenantId(sysTenant.getId());
                kingdeeTenantInfo.setName(sysTenant.getName());
                kingdeeTenantInfo.setUpdateTime(sysTenant.getCreateTime());
                kingdeeTenantInfoList.add(kingdeeTenantInfo);
            }
            saveBatch(kingdeeTenantInfoList);

        }

        log.info("============================syncTenantInfo end==============");



    }
}
