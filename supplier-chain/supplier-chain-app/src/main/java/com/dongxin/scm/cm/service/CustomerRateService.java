package com.dongxin.scm.cm.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.cm.entity.CustomerRate;
import com.dongxin.scm.cm.mapper.CustomerRateMapper;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.entity.FundAccount;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @Description: 顾客星级评分
 * @Author: jeecg-boot
 * @Date: 2020-12-11
 * @Version: V1.0
 */
@Service
public class CustomerRateService extends BaseService<CustomerRateMapper, CustomerRate> {

    public List<CustomerRate> selectByMainId(String mainId) {
        return baseMapper.selectByMainId(mainId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(CustomerRate customerRate) {

/*
        //格式化日期
        String date = DateUtil.format(customerRate.getRateTime(), "yyyy年MM月");
        //构造customerRateWrapper，通过顾客id,评分月份查询实体类
        CustomerRate customerRateWrapper = new CustomerRate();
        customerRateWrapper.setCustomerId(customerRate.getCustomerId());
        customerRateWrapper.setRateTime(customerRate.getRateTime());
        QueryWrapper<CustomerRate> queryWrapper = QueryGenerator.initQueryWrapper(customerRateWrapper, null);
        CustomerRate customerRateSelect = baseMapper.selectOne(queryWrapper);

        //判断评分月份是否已存在评分
        if (ObjectUtil.isNotNull(customerRateSelect)) {
            throw new ScmException(StrUtil.format("{}已有评分", date));

        //评分月份校验
        } else if (customerRate.getRateTime().after(nowDate)) {
            throw new ScmException(StrUtil.format("评分月份不能超过当前月份"));
        } else {
            return super.save(customerRate);
        }
*/
        Date nowDate = DateUtil.date(System.currentTimeMillis());
        if (customerRate.getRateTime().after(nowDate)) {
            throw new ScmException(StrUtil.format("评分时间不能超过当前时间"));
        } else {
            return super.save(customerRate);
        }
    }

    /**
     * 通过客户id删除账户星级
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchByMainId(String customerId){
        QueryWrapper<CustomerRate> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CustomerRate::getCustomerId, customerId);
        List<CustomerRate> customerRateList = list(queryWrapper);
        for(CustomerRate customerRate:customerRateList){
            logicDeleteById(customerRate.getId());
        }
    }

}
