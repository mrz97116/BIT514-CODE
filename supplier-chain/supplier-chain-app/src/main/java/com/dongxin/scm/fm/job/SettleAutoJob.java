package com.dongxin.scm.fm.job;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.bd.entity.CompanyTenant;
import com.dongxin.scm.bd.entity.CompanyTenantDet;
import com.dongxin.scm.bd.mapper.CompanyTenantDetMapper;
import com.dongxin.scm.bd.mapper.CompanyTenantMapper;
import com.dongxin.scm.common.enums.DelFlagStateEnum;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.fm.enums.SettleStatusEnum;
import com.dongxin.scm.fm.service.SettleInfoService;
import com.dongxin.scm.sm.entity.Stack;
import com.dongxin.scm.sm.mapper.StackMapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.config.mybatis.TenantContext;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:自动结算
 * @Autor: lidai
 * Date:2021/6/18
 * @Version: V1.0
 */
@Slf4j
public class SettleAutoJob implements Job {
    @Autowired
    private SettleInfoService settleInfoService;
    @Resource
    private StackMapper stackMapper;
    @Resource
    private CompanyTenantMapper companyTenantMapper;
    @Autowired
    private CompanyTenantDetMapper companyTenantDetMapper;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //获取当前登录账号对应的TenantId
        String tenantId= TenantContext.getTenant();
        //通过TenantId，查询CompanyTenantDet中对应的记录
        QueryWrapper<CompanyTenantDet> companyTenantDetQueryWrapper = new QueryWrapper<>();
        companyTenantDetQueryWrapper.lambda().eq(CompanyTenantDet::getTenantCode, tenantId);
        CompanyTenantDet companyTenantDet=companyTenantDetMapper.selectOne(companyTenantDetQueryWrapper);
        //通过CompanyTenantDet查询对应的CompanyTenant
        QueryWrapper<CompanyTenant> companyTenantQueryWrapper=new QueryWrapper<>();
        companyTenantQueryWrapper.lambda().eq(CompanyTenant::getId,companyTenantDet.getParentId());
        CompanyTenant companyTenant = companyTenantMapper.selectOne(companyTenantQueryWrapper);
        //如果companyTenant为允许自动结算，则执行
        if(companyTenant.getAutoSettle().equals(YesNoEnum.YES.getCode())){
            //获取没结算和特定顾客的装车单信息
            QueryWrapper<Stack> stackQueryWrapper=new QueryWrapper<>();
            stackQueryWrapper.lambda().eq(Stack::getTenantId, tenantId).eq(Stack::getDelFlag, DelFlagStateEnum.UNDELETE.getCode()).eq(Stack::getSettled, SettleStatusEnum.UNSETTLE.getCode());
            List<Stack> unSettleList = stackMapper.selectList(stackQueryWrapper);
            List<String> stackIdList = CollectionUtil.newArrayList();
            if (CollectionUtil.isNotEmpty(unSettleList)) {
                for (Stack unSettleStack : unSettleList) {
                    String stackId = unSettleStack.getId();
                    stackIdList.add(stackId);
                }
                log.info("执行 SettleAutoJob开始时间{}", DateUtils.gettimestamp());
                //调用settleAuto自动创建结算单
                settleInfoService.settle(stackIdList, BigDecimal.ZERO,"自动结算");
                log.info("执行 SettleAutoJob结束时间{}", DateUtils.gettimestamp());
            }
        }
    }
}

