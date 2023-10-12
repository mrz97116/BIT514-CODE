package com.dongxin.scm.bd.service;

import com.dongxin.scm.bd.entity.TenantReportDet;
import com.dongxin.scm.bd.mapper.TenantReportDetMapper;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 公司租户id
 * @Author: jeecg-boot
 * @Date: 2021-01-27
 * @Version: V1.0
 */
@Service
public class TenantReportDetService extends BaseService<TenantReportDetMapper, TenantReportDet> {

    @Autowired
    private TenantReportDetMapper tenantReportDetMapper;

    public List<TenantReportDet> selectByMainId(String mainId) {
        return tenantReportDetMapper.selectByMainId(mainId);
    }
}
