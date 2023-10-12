package com.dongxin.scm.bd.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.bd.entity.CompanyTenantDet;
import com.dongxin.scm.bd.mapper.CompanyTenantDetMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 * @Description: 公司租户明细表
 * @Author: jeecg-boot
 * @Date: 2021-02-26
 * @Version: V1.0
 */
@Service
public class CompanyTenantDetService extends BaseService<CompanyTenantDetMapper, CompanyTenantDet> {

    @Resource
    private CompanyTenantDetMapper companyTenantDetMapper;

    public List<CompanyTenantDet> selectByMainId(String mainId) {
        return companyTenantDetMapper.selectByMainId(mainId);
    }

    public List<Integer> queryTenantIds(Integer tenantId) {
        //通过登录用户的租户id查询表中是否存在记录
        QueryWrapper<CompanyTenantDet> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(CompanyTenantDet::getTenantCode, tenantId);
        CompanyTenantDet companyTenantDet = companyTenantDetMapper.selectOne(wrapper);
        //存储公司下所有租户id
        List<Integer> tenantIdS = new ArrayList<>();
        //存在记录
        if (ObjectUtil.isNotEmpty(companyTenantDet)) {
            //查询公司下所有租户id
            QueryWrapper<CompanyTenantDet> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(CompanyTenantDet::getTenantCode, companyTenantDet.getParentId());
            List<CompanyTenantDet> tenantDets = companyTenantDetMapper.selectList(queryWrapper);
            if (CollectionUtil.isNotEmpty(tenantDets)) {
                //将租户id存到 tenantIdS
                for (CompanyTenantDet det : tenantDets) {
                    tenantIdS.add(det.getTenantCode());
                }
            }
        }
        return tenantIdS;
    }
}
