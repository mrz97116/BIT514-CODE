package com.dongxin.scm.cm.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.mapper.CustomerProfileMapper;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.service.FundAccountService;
import com.dongxin.scm.fm.service.FundDetailService;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.om.util.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 客户基础信息
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */

@Service
@Component
public class CustomerProfileService extends BaseService<CustomerProfileMapper, CustomerProfile> {

    @Autowired
    private CustomerBankService customerBankService;
    @Autowired
    private CustomerAddressService customerAddressService;
    @Autowired
    private FundDetailService fundDetailService;
    @Autowired
    private CustomerRateService customerRateService;
    @Autowired
    private SalesOrderService salesOrderService;

    public List<String> selectAlias(String alias) {
        QueryWrapper<CustomerProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CustomerProfile::getAlias, alias);
        List<CustomerProfile> customerProfileList = list(queryWrapper);
        List<String> customerIdList = newArrayList();
        for (CustomerProfile customerProfile : customerProfileList) {
            customerIdList.add(customerProfile.getId());
        }
        return customerIdList;
    }

    public Map<String, String> getNameMap(List<String> ids) {
        Map<String, String> map = new HashMap<>();
        if (CollectionUtils.isEmpty(ids)) {
            return map;
        }
        QueryWrapper<CustomerProfile> queryWrapper = new QueryWrapper<>();
        //塞条件:id在ids里
        queryWrapper.lambda().in(CustomerProfile::getId, ids);
        //根据ids的id记录去查询CustomerProfile的所有记录
        List<CustomerProfile> customerProfileList = list(queryWrapper);
        for (CustomerProfile customerProfileTmp : customerProfileList) {
            map.put(customerProfileTmp.getId(), customerProfileTmp.getCompanyName());
        }
        //最终返回 id,CompanyName的map集合
        return map;
    }

    //删除无流水账户信息及其银行卡信息、收货地址信息
    @Transactional(rollbackFor = Exception.class)
    public void delMain(String id) {
        CustomerProfile customerProfile = getById(id);
        if (!fundDetailService.hasFundDetail(id) && customerProfile.getDelFlag() == 0) {
            customerBankService.deleteBatchByMainId(id);
            customerAddressService.deleteBatchByMainId(id);
//            fundAccountService.deleteBatchByMainId(id);
            customerRateService.deleteBatchByMainId(id);
            logicDeleteById(id);
        } else {
            throw new ScmException("此客户存在资金流水！禁止删除!");
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            customerBankService.deleteBatchByMainId(id.toString());
            customerAddressService.deleteBatchByMainId(id.toString());
            logicDeleteById(id);
        }
    }


    //加锁
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(CustomerProfile customerProfile) {
        CustomerProfile searchCustomerProfile = new CustomerProfile();
        searchCustomerProfile.setCompanyName(customerProfile.getCompanyName());
        QueryWrapper<CustomerProfile> queryWrapper = new QueryWrapper<>();

        if (ObjectUtil.isNotNull(customerProfile.getTenantId())) {
            queryWrapper.lambda().eq(CustomerProfile::getCompanyName, customerProfile.getCompanyName())
                    .eq(CustomerProfile::getTenantId, customerProfile.getTenantId());
        } else {
            queryWrapper.lambda().eq(CustomerProfile::getCompanyName, customerProfile.getCompanyName());
        }

        List<CustomerProfile> customerProfileList = list(queryWrapper);
        if (CollectionUtils.isNotEmpty(customerProfileList)) {
            throw new ScmException("顾客名称重复，请确认");
        }


        return super.save(customerProfile);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(CustomerProfile customerProfile) {
        CustomerProfile searchCustomerProfile = new CustomerProfile();
        searchCustomerProfile.setCompanyName(customerProfile.getCompanyName());//查询数据库是否存在该公司名

        QueryWrapper<CustomerProfile> queryWrapper = new QueryWrapper<>();

        queryWrapper.lambda().eq(CustomerProfile::getCompanyName, customerProfile.getCompanyName())
                .ne(CustomerProfile::getId, customerProfile.getId());

        List<CustomerProfile> customerProfileList = list(queryWrapper);

        if (CollectionUtils.isNotEmpty(customerProfileList)) {
            throw new ScmException("顾客名称重复，请确认");
        }

        super.updateById(customerProfile);
        salesOrderService.updateYRMCanLadingBill(customerProfile.getId());
        return true;
    }

    public List<CustomerProfile> getName() {
        return baseMapper.getName();
    }


    public void checkName(String name) {
        QueryWrapper<CustomerProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CustomerProfile::getCompanyName, name);
        CustomerProfile customerProfile = getOne(queryWrapper);
        if (ObjectUtil.isNull(customerProfile)) {
            throw new ScmException(StrUtil.format("{} 未在系统维护数据客户数据", name));
        }
    }

    public String getOneName(String customerId) {
        CustomerProfile customerProfile = getById(customerId);
        return customerProfile.getCompanyName();
    }

    public String getIdByName(String name, Integer tenantID) {
        QueryWrapper<CustomerProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CustomerProfile::getCompanyName, name).eq(CustomerProfile::getTenantId, tenantID);
        CustomerProfile customerProfile = getOne(queryWrapper);
        if (ObjectUtil.isNull(customerProfile)) {
            throw new ScmException(StrUtil.format("{} 未在系统维护数据客户数据", name));
        }
        return customerProfile.getId();
    }

    public String getId(String name) {
        QueryWrapper<CustomerProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CustomerProfile::getCompanyName, name);
        CustomerProfile customerProfile = getOne(queryWrapper);
        if (ObjectUtil.isNull(customerProfile)) {
            throw new ScmException(StrUtil.format("{} 未在系统维护客户数据", name));
        }
        return customerProfile.getId();
    }

    public CustomerProfile getCustomerId(String companyName) {
        QueryWrapper<CustomerProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CustomerProfile::getCompanyName, companyName);
        return getOne(queryWrapper);
    }

    public CustomerProfile getCustomer(String customerId) {
        QueryWrapper<CustomerProfile> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CustomerProfile::getId, customerId);
        return getOne(queryWrapper);
    }

    public List<CustomerProfile> getCustomerProfileList(String customerId) {
        return baseMapper.getCustomerProfileList(customerId);
    }

    public Map<String, CustomerProfile> getCustomerInfoMap(List<String> ids) {
        Map<String, CustomerProfile> result = new HashMap<>();

        if (CollectionUtils.isEmpty(ids)) {
            return result;
        }

        List<CustomerProfile> customerProfiles = listByIds(ids);

        for (CustomerProfile customerProfile : customerProfiles) {
            result.put(customerProfile.getId(), customerProfile);
        }
        return result;
    }

    //将阳蕊明客户全部更新为可开单
    public void updateYRMCanLadingBill() {
        UpdateWrapper<CustomerProfile> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(CustomerProfile::getCanLadingBill, YesNoEnum.YES.getCode())
                .in(CustomerProfile::getTenantId, Constants.YRM_TENANT_IDS);
        update(updateWrapper);
    }

    //将阳蕊明客户全部更新为可开单
    public void updateCanNoLadingBill(List<String> customerIds) {

        UpdateWrapper<CustomerProfile> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(CustomerProfile::getCanLadingBill, YesNoEnum.NO.getCode())
                .in(CustomerProfile::getId, customerIds);
        update(updateWrapper);
    }



}
