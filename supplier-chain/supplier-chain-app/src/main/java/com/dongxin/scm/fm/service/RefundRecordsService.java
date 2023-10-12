package com.dongxin.scm.fm.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.common.enums.CommonCheckStatusEnum;
import com.dongxin.scm.common.service.LockService;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.entity.FundDetail;
import com.dongxin.scm.fm.entity.FundPool;
import com.dongxin.scm.fm.entity.RefundRecords;
import com.dongxin.scm.fm.enums.FundDetailTypeEnum;
import com.dongxin.scm.fm.mapper.RefundRecordsMapper;
import com.dongxin.scm.om.util.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * @Description: 退款记录表
 * @Author: jeecg-boot
 * @Date: 2020-11-25
 * @Version: V1.0
 */
@Service
public class RefundRecordsService extends BaseService<RefundRecordsMapper, RefundRecords> {

    @Autowired
    private FundAccountService fundAccountService;
    @Autowired
    private FundDetailService fundDetailService;
    @Autowired
    private LockTemplate lockTemplate;
    @Autowired
    private FinanceDetailService financeDetailService;
    @Autowired
    private FundPoolService fundPoolService;
    @Autowired
    private LockService lockService;


    public BigDecimal getUnVerifiedRefundAmount(String customerId) {
        QueryWrapper<RefundRecords> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(RefundRecords::getCustomerId, customerId)
                .eq(RefundRecords::getStatus, CommonCheckStatusEnum.PENDING_VERIFY.getCode());

        List<RefundRecords> refundRecords = list(queryWrapper);

        return refundRecords.stream().map(RefundRecords::getRefundAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String addRefundInfo(String id, BigDecimal refundAmount) {
        RefundRecords refundRecords = new RefundRecords();
        refundRecords.setCustomerId(id);
        refundRecords.setRefundAmount(refundAmount);
        refundRecords.setRefundDate(new Date());
        refundRecords.setStatus(CommonCheckStatusEnum.PENDING_VERIFY.getCode());
        save(refundRecords);
        return refundRecords.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void updatePassBatch(List<String> refundRecordsIds, String tag) {
        List<RefundRecords> refundRecordsList = baseMapper.selectBatchIds(refundRecordsIds);
        if (CollectionUtils.isEmpty(refundRecordsList)) {
            throw new ScmException("审核失败");
        }


        List<RefundRecords> updateRefundRecords = newArrayList();
        List<FundDetail> deleteFundDetailList = newArrayList();
        List<FundPool> updateFundPoolList = newArrayList();
        for (RefundRecords refundRecord : refundRecordsList) {

            LockInfo lockInfo = lockService.lock(Constants.CUSTOMER_LOCK_HEADER + refundRecord.getCustomerId());

            if (lockInfo == null) {
                throw new ScmException("请求过于频繁，请稍后重试");
            }
            try {

                if (!CommonCheckStatusEnum.PENDING_VERIFY.getCode().equals(refundRecord.getStatus())) {
                    throw new ScmException("勾选项存在已审核项，请重新勾选！");
                }
                if (CommonCheckStatusEnum.APPROVE.getCode().equals(tag)) {
                    refundRecord.setStatus(CommonCheckStatusEnum.APPROVE.getCode());
                    refundRecord.setApproveDate(new Date());
                    updateRefundRecords.add(refundRecord);
                }
                if (CommonCheckStatusEnum.REJECT.getCode().equals(tag)) {
                    refundRecord.setStatus(CommonCheckStatusEnum.REJECT.getCode());
                    refundRecord.setApproveDate(new Date());
                    updateRefundRecords.add(refundRecord);
                    QueryWrapper<FundDetail> fundDetailQueryWrapper = new QueryWrapper<>();
                    fundDetailQueryWrapper.lambda().eq(FundDetail::getOutTradeNo, refundRecord.getId())
                            .eq(FundDetail::getType, FundDetailTypeEnum.REFUND.getCode());
                    List<FundDetail> fundDetailList = fundDetailService.list(fundDetailQueryWrapper);
                    if (CollectionUtils.isEmpty(fundDetailList)) {
                        throw new ScmException(StrUtil.format("查询不到对应的资金流水，请联系技术人员处理。" + refundRecord.getId()));
                    }

                    for (FundDetail fundDetail : fundDetailList) {
                        FundPool fundPool = fundPoolService.getById(fundDetail.getFundId());
                        fundPool.setAvailAmount(fundPool.getAvailAmount().add(fundDetail.getAmount()));
                        updateFundPoolList.add(fundPool);
                        deleteFundDetailList.add(fundDetail);
                    }

                }
            } finally {

                lockTemplate.releaseLock(lockInfo);
            }
        }

        updateBatchById(updateRefundRecords);
        if (CollectionUtils.isNotEmpty(deleteFundDetailList)) {
            fundDetailService.removeByIds(deleteFundDetailList);
        }
        if (CollectionUtils.isNotEmpty(updateFundPoolList)) {
            fundPoolService.updateBatchById(updateFundPoolList);
        }

    }
}
