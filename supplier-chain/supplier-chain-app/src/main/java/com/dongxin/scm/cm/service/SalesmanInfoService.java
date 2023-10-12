package com.dongxin.scm.cm.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.cm.entity.SalesmanInfo;
import com.dongxin.scm.cm.mapper.SalesmanInfoMapper;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 业务员信息管理
 * @Author: jeecg-boot
 * @Date: 2020-11-30
 * @Version: V1.0
 */
@Service
public class SalesmanInfoService extends BaseService<SalesmanInfoMapper, SalesmanInfo> {

    public Map<String, String> getNameMap(List<String> salesManIds) {
        Map<String, String> name = new HashMap<>();
        if (CollectionUtil.isEmpty(salesManIds)) {
            return name;
        }
        QueryWrapper<SalesmanInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(SalesmanInfo::getId, salesManIds);
        List<SalesmanInfo> salesmanInfoList = list(queryWrapper);
        for (SalesmanInfo salesmanInfo : salesmanInfoList) {
            name.put(salesmanInfo.getId(), salesmanInfo.getName());
        }
        return name;
    }

    public Map<String, BigDecimal> getSalesmanMap() {
        List<SalesmanInfo> salesmanInfoList = list();
        if (CollectionUtils.isEmpty(salesmanInfoList)) {
            return null;
        }
        Map<String, BigDecimal> salesmanMap = new HashMap<>();
        BigDecimal salesAccount = BigDecimal.ZERO;
        for (SalesmanInfo salesmanInfo : salesmanInfoList) {
            salesmanMap.put(salesmanInfo.getName(), salesAccount);
        }
        return salesmanMap;
    }
}
