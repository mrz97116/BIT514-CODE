package com.dongxin.scm.kingdee.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.kingdee.entity.KingdeeBasicInfo;
import com.dongxin.scm.kingdee.entity.KingdeeTenantInfo;
import com.dongxin.scm.kingdee.mapper.KingdeeBasicInfoMapper;
import com.dongxin.scm.sm.entity.Stock;
import com.dongxin.scm.sm.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.system.entity.SysTenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 金蝶基础数据表
 * @Author: jeecg-boot
 * @Date:   2021-03-26
 * @Version: V1.0
 */
@Service
@Slf4j
public class KingdeeBasicInfoService extends BaseService<KingdeeBasicInfoMapper, KingdeeBasicInfo> {



    @Autowired
    private CustomerProfileService customerProfileService;

    @Autowired
    private StockService stockService;

    public void syncBasicInfo(Date startTime, Date endTime) {
        log.info("===================syncBasicInfo start============");
        this.syncInsertStockInfo(startTime, endTime);
//        this.syncUpdateStockInfo(startTime, endTime);
        this.syncInsertCustomerInfo(startTime, endTime);
//        this.syncUpdateCustomerInfo(startTime, endTime);
        log.info("===================syncBasicInfo end==============");
    }


    /**
     * 根据时间段同步客户信息
     * @param startTime
     * @param endTime
     */
    private void syncInsertCustomerInfo(Date startTime, Date endTime) {

        //查询所有createTime在时间段内的数据
        QueryWrapper<CustomerProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(CustomerProfile::getCreateTime, startTime, endTime);
        List<CustomerProfile> customerInsertList = customerProfileService.list(queryWrapper);

        if (CollectionUtil.isNotEmpty(customerInsertList)) {
            List<KingdeeBasicInfo> kingdeeBasicCustomerInsertList = newArrayList();
            for (CustomerProfile customerProfile : customerInsertList) {
                KingdeeBasicInfo basicInfo = new KingdeeBasicInfo();
                basicInfo.setId(customerProfile.getId());
                basicInfo.setCreateTime(customerProfile.getCreateTime());
                basicInfo.setUpdateTime(customerProfile.getUpdateTime());
                basicInfo.setCode(customerProfile.getId());
                basicInfo.setName(customerProfile.getCompanyName());
                basicInfo.setType("4");
                basicInfo.setTenantId(customerProfile.getTenantId());
                kingdeeBasicCustomerInsertList.add(basicInfo);
            }
            //带id一起批量保存
            saveBatch(kingdeeBasicCustomerInsertList);

        }

    }


    //批量更新
    private void syncUpdateCustomerInfo(Date startTime, Date endTime) {

        //查询所有updateTime在时间段内的数据
        QueryWrapper<CustomerProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(CustomerProfile::getUpdateTime, startTime, endTime);
        List<CustomerProfile> customerUpdateList = customerProfileService.list(queryWrapper);

        if (CollectionUtil.isNotEmpty(customerUpdateList)) {
            List<KingdeeBasicInfo> kingdeeBasicCustomerUpdateList = newArrayList();
            for (CustomerProfile customerProfile : customerUpdateList) {
                KingdeeBasicInfo basicInfo = new KingdeeBasicInfo();
                basicInfo.setId(customerProfile.getId());
                basicInfo.setCreateTime(customerProfile.getCreateTime());
                basicInfo.setUpdateTime(customerProfile.getUpdateTime());
                basicInfo.setCode(customerProfile.getId());
                basicInfo.setName(customerProfile.getCompanyName());
                basicInfo.setType("4");
                basicInfo.setTenantId(customerProfile.getTenantId());
                kingdeeBasicCustomerUpdateList.add(basicInfo);
            }
            //有id，批量更新
            saveOrUpdateBatch(kingdeeBasicCustomerUpdateList);
        }

    }

    /**
     * 根据时间段同步仓库信息
     * @param startTime
     * @param endTime
     */
    private void syncInsertStockInfo(Date startTime, Date endTime) {

        //查询所有createTime在时间段内的数据
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(Stock::getCreateTime, startTime, endTime);
        List<Stock> stockInsertList = stockService.list(queryWrapper);

        if (CollectionUtil.isNotEmpty(stockInsertList)) {
            List<KingdeeBasicInfo> kingdeeBasicStockInsertList = newArrayList();
            for (Stock stock : stockInsertList) {
                KingdeeBasicInfo basicInfo = new KingdeeBasicInfo();
                basicInfo.setId(stock.getId());
                basicInfo.setCreateTime(stock.getCreateTime());
                basicInfo.setUpdateTime(stock.getUpdateTime());
                basicInfo.setCode(stock.getId());
                basicInfo.setName(stock.getName());
                basicInfo.setType("5");
                basicInfo.setTenantId(stock.getTenantId());
                kingdeeBasicStockInsertList.add(basicInfo);
            }
            //带id一起批量保存
            saveBatch(kingdeeBasicStockInsertList);
        }

    }


    /**
     * 根据时间段同步仓库信息
     * @param startTime
     * @param endTime
     */
    private void syncUpdateStockInfo(Date startTime, Date endTime) {

        //查询所有createTime在时间段内的数据
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().between(Stock::getUpdateTime, startTime, endTime);
        List<Stock> stockUpdateList = stockService.list(queryWrapper);

        if (CollectionUtil.isNotEmpty(stockUpdateList)) {
            List<KingdeeBasicInfo> kingdeeBasicStockUpdateList = newArrayList();
            for (Stock stock : stockUpdateList) {
                KingdeeBasicInfo basicInfo = new KingdeeBasicInfo();
                basicInfo.setId(stock.getId());
                basicInfo.setCreateTime(stock.getCreateTime());
                basicInfo.setUpdateTime(stock.getUpdateTime());
                basicInfo.setCode(stock.getId());
                basicInfo.setName(stock.getName());
                basicInfo.setType("5");
                basicInfo.setTenantId(stock.getTenantId());
                kingdeeBasicStockUpdateList.add(basicInfo);
            }
            //带id一起批量保存
            saveOrUpdateBatch(kingdeeBasicStockUpdateList);
        }

    }


}
