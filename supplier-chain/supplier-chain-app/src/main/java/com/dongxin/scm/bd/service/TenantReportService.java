package com.dongxin.scm.bd.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.bd.entity.TenantReport;
import com.dongxin.scm.bd.entity.TenantReportDet;
import com.dongxin.scm.bd.mapper.TenantReportDetMapper;
import com.dongxin.scm.bd.mapper.TenantReportMapper;
import org.jeecg.common.system.base.service.BaseService;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 公司报表路径
 * @Author: jeecg-boot
 * @Date: 2021-01-27
 * @Version: V1.0
 */
@Service
public class TenantReportService extends BaseService<TenantReportMapper, TenantReport> {

    @Resource
    private TenantReportMapper tenantReportMapper;
    @Resource
    private TenantReportDetMapper tenantReportDetMapper;

    public String queryCompanyReportName(Integer tenantId) {
        TenantReportDet rpDet = new TenantReportDet();
        rpDet.setTenantCode(tenantId);
        QueryWrapper<TenantReportDet> queryWrapper = QueryGenerator.initQueryWrapper(rpDet, null);
        TenantReportDet rp = tenantReportDetMapper.selectOne(queryWrapper);
        if (ObjectUtil.isEmpty(rp)) {
            return "empty";
        }
        TenantReport rpUrl = getById(rp.getParentId());
        return rpUrl.getReportName();
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveMain(TenantReport tenantReport, List<TenantReportDet> tenantReportDetList) {
        tenantReportMapper.insert(tenantReport);
        if (tenantReportDetList != null && tenantReportDetList.size() > 0) {
            for (TenantReportDet entity : tenantReportDetList) {
                //外键设置
                entity.setParentId(tenantReport.getId());
                tenantReportDetMapper.insert(entity);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMain(TenantReport tenantReport, List<TenantReportDet> tenantReportDetList) {
        tenantReportMapper.updateById(tenantReport);

        //1.先删除子表数据
        tenantReportDetMapper.deleteByMainId(tenantReport.getId());

        //2.子表数据重新插入
        if (tenantReportDetList != null && tenantReportDetList.size() > 0) {
            for (TenantReportDet entity : tenantReportDetList) {
                //外键设置
                entity.setParentId(tenantReport.getId());
                tenantReportDetMapper.insert(entity);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delMain(String id) {
        tenantReportDetMapper.deleteByMainId(id);
        tenantReportMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            tenantReportDetMapper.deleteByMainId(id.toString());
            tenantReportMapper.deleteById(id);
        }
    }

}
