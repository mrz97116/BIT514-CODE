package com.dongxin.scm.cm.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.cm.entity.CustomerConfiguration;
import com.dongxin.scm.cm.mapper.CustomerConfigurationMapper;
import com.dongxin.scm.exception.ScmException;
import org.jeecg.common.system.base.service.BaseService;
import org.jeecg.config.mybatis.TenantContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 顾客合同控量配置表
 * @Author: jeecg-boot
 * @Date: 2021-01-04
 * @Version: V1.0
 */
@Service
public class CustomerConfigurationService extends BaseService<CustomerConfigurationMapper, CustomerConfiguration> {
    @Resource
    private CustomerConfigurationMapper customerConfigurationMapper;

    @Override
    public boolean save(CustomerConfiguration customerConfiguration) {
        //校验当前租户
        QueryWrapper<CustomerConfiguration> customerConfigWrapper = new QueryWrapper<>();
        customerConfigWrapper.lambda().eq(CustomerConfiguration::getTenantId, customerConfiguration.getTenantId());
        CustomerConfiguration customerConfig = getOne(customerConfigWrapper);
        if (ObjectUtil.isNotNull(customerConfig)) {
            throw new ScmException(StrUtil.format("当前租户为: {}，已配置合同控量！", customerConfiguration.getTenantId()));
        }
        return super.save(customerConfiguration);
    }
    //    判断租户是否装车单合同控量
    public List<String> queryWhichContractControl(String tenantId) {
        QueryWrapper<CustomerConfiguration> customerConfigurationQueryWrapper = new QueryWrapper<>();
        customerConfigurationQueryWrapper.lambda().eq(CustomerConfiguration::getTenantId, tenantId);
        CustomerConfiguration customerConfiguration = customerConfigurationMapper.selectOne(customerConfigurationQueryWrapper);
        //如果StackContractConfiguration为1，则为装车单合同控量
        if (ObjectUtil.isNotNull(customerConfiguration)) {
            List<String> contractControlList = new ArrayList<>();
            if(ObjectUtil.isNotNull(customerConfiguration.getFlag())){
                String salesContractConfiguration = customerConfiguration.getFlag();
                contractControlList.add(salesContractConfiguration);
            } else {
                throw new ScmException("当前租户没有配置销售单合同控量");
            }
            if(ObjectUtil.isNotNull(customerConfiguration.getStackContractConfiguration())){
                String stackContractConfiguration = customerConfiguration.getStackContractConfiguration();
                contractControlList.add(stackContractConfiguration);
            } else {
                throw new ScmException("当前租户没有配置装车单合同控量");
            }
            customerConfigurationQueryWrapper.clear();
            return contractControlList;
        }else{
            throw new ScmException("当前租户没有配置合同控量");
        }
    }
}
