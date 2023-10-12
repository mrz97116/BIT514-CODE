package com.dongxin.scm.sm.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.mapper.CustomerProfileMapper;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.common.service.LockService;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.dto.FundPoolDTO;
import com.dongxin.scm.fm.entity.FundDetail;
import com.dongxin.scm.fm.entity.FundPool;
import com.dongxin.scm.fm.enums.FundDetailTypeEnum;
import com.dongxin.scm.fm.enums.SettleStatusEnum;
import com.dongxin.scm.fm.service.*;
import com.dongxin.scm.om.util.Constants;
import com.dongxin.scm.sm.entity.EquipmentSupplies;
import com.dongxin.scm.sm.mapper.EquipmentSuppliesMapper;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * @Description: 设备物资网主表
 * @Author: jeecg-boot
 * @Date: 2021-03-03
 * @Version: V1.0
 */
@Service
public class EquipmentSuppliesService extends BaseService<EquipmentSuppliesMapper, EquipmentSupplies> {

    @Autowired
    CustomerProfileService customerProfileService;

    @Resource
    CustomerProfileMapper customerProfileMapper;

    @Autowired
    FundAccountService fundAccountService;

    @Autowired
    FinanceDetailService financeDetailService;

    @Autowired
    private FundService fundService;

    @Autowired
    private FundPoolService fundPoolService;

    @Autowired
    private FundDetailService fundDetailService;

    @Autowired
    private LockTemplate lockTemplate;

    @Autowired
    private LockService lockService;


    @Transactional
    public void subduction() {
        QueryWrapper<EquipmentSupplies> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EquipmentSupplies::getStatus, SettleStatusEnum.UNSETTLE.getCode());
        List<EquipmentSupplies> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            throw new ScmException("无结算数据");
        }
        List<FundPool> updateFundPoolList = newArrayList();
        List<FundDetail> addFundDetailList = newArrayList();
        for (EquipmentSupplies equipmentSupplies : list) {
            QueryWrapper<CustomerProfile> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.lambda().eq(CustomerProfile::getCompanyName, equipmentSupplies.getCustomer());
            CustomerProfile customerProfile = customerProfileMapper.selectOne(queryWrapper1);

            LockInfo lockInfo = lockService.lock(Constants.CUSTOMER_LOCK_HEADER + customerProfile.getId());

            try {
                List<FundPoolDTO> fundPoolDTOList = fundService.autoSelectFundPool(equipmentSupplies.getTotalPrice(), customerProfile.getId(), null);
                for (FundPoolDTO fundPoolDTO : fundPoolDTOList) {
                    FundPool fundPool = fundPoolService.getById(fundPoolDTO.getFundId());
                    fundPool.setAvailAmount(fundPool.getAvailAmount().subtract(fundPoolDTO.getUseAmount()));
                    updateFundPoolList.add(fundPool);

                    addFundDetailList.add(fundDetailService.addFundDetail(fundPoolDTO.getUseAmount()
                            , fundPool.getCustomerId(), equipmentSupplies.getId()
                            , FundDetailTypeEnum.SETTLE, fundPool.getId(), fundPoolDTO.getType()));
                }
            } finally {
                lockTemplate.releaseLock(lockInfo);
            }


        }
        list.forEach(item -> {
            item.setStatus(SettleStatusEnum.SETTLED.getCode());
        });
        updateBatchById(list);
        fundPoolService.updateBatchById(updateFundPoolList);
        fundDetailService.saveBatch(addFundDetailList);
    }

    @Transactional
    public void revocation(List<String> idList) {
        for (String id : idList) {
            EquipmentSupplies equipmentSupplies = getById(id);

            LockInfo lockInfo = lockService.lock(Constants.CUSTOMER_LOCK_HEADER + customerProfileService.getId(equipmentSupplies.getCustomer()));

            try {

                if (SettleStatusEnum.SETTLED.getCode().equals(equipmentSupplies.getStatus())) {
                    List<FundPool> updateFundPoolList = newArrayList();
                    List<FundDetail> deleteFundDetailList = newArrayList();

                    QueryWrapper<FundDetail> fundDetailQueryWrapper = new QueryWrapper<>();
                    fundDetailQueryWrapper.lambda().eq(FundDetail::getOutTradeNo, equipmentSupplies.getId())
                            .eq(FundDetail::getType, FundDetailTypeEnum.SETTLE.getCode());
                    List<FundDetail> fundDetailList = fundDetailService.list(fundDetailQueryWrapper);
                    if (CollectionUtil.isEmpty(fundDetailList)) {
                        throw new ScmException(StrUtil.format("查询不到结算流水。请联系技术人员！" + equipmentSupplies.getId()));
                    }
                    for (FundDetail fundDetail : fundDetailList) {
                        FundPool fundPool = fundPoolService.getById(fundDetail.getFundId());
                        fundPool.setAvailAmount(fundPool.getAvailAmount().add(fundDetail.getAmount()));
                        updateFundPoolList.add(fundPool);
                        deleteFundDetailList.add(fundDetail);
                    }

                    fundPoolService.updateBatchById(updateFundPoolList);
                    fundDetailService.removeByIds(deleteFundDetailList);

                }
                logicDeleteById(id);
            } finally {
                lockTemplate.releaseLock(lockInfo);
            }

        }
    }

}
