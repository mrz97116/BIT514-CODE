package com.dongxin.scm.fm.service;

import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.common.enums.CommonCheckStatusEnum;
import com.dongxin.scm.fm.enums.CreditTypeStatusEnum;
import com.dongxin.scm.common.service.LockService;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.entity.Credit;
import com.dongxin.scm.fm.entity.CreditAdjust;
import com.dongxin.scm.fm.mapper.CreditAdjustMapper;
import com.dongxin.scm.om.util.Constants;
import com.dongxin.scm.sm.enums.StatusEnum;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description: 授信调整
 * @Author: jeecg-boot
 * @Date: 2022-01-24
 * @Version: V1.0
 */
@Service
public class CreditAdjustService extends BaseService<CreditAdjustMapper, CreditAdjust> {

    @Autowired
    private LockService lockService;
    @Autowired
    private CreditService creditService;
    @Autowired
    private LockTemplate lockTemplate;


    @Transactional(rollbackFor = Exception.class)
    public void creditAdjust(String id, String creditNo, String adjustStatus, BigDecimal adjustAmount, String remark) {

        LockInfo lockInfo = lockService.lock(Constants.CUSTOMER_LOCK_HEADER + id);
        try {

            Credit credit = creditService.getById(id);
            //减少授信，额度不能小于当前授信记录可用金额
            if (adjustStatus.equalsIgnoreCase(CreditTypeStatusEnum.REDUCE.getCode()) && (adjustAmount.compareTo(credit.getAvailAmount()) > 0)) {
                log.error("减少授信失败" + credit.getId() + creditNo);
                throw new ScmException("减少授信，额度不能小于当前授信记录可用金额");
            }

            //生成待审核的调整记录
            CreditAdjust creditAdjust = new CreditAdjust();
            creditAdjust.setCustomerId(credit.getCustomerId())
                    .setAdjustAmount(adjustAmount)
                    .setCreditId(id)
                    .setAdjustType(adjustStatus)
                    .setCreditNo(creditNo)
                    .setRemark(remark)
                    .setStatus(CommonCheckStatusEnum.PENDING_VERIFY.getCode());

            save(creditAdjust);

            //处理原授信金额
            if (adjustStatus.equalsIgnoreCase(CreditTypeStatusEnum.REDUCE.getCode())) {
                credit.setAvailAmount(credit.getAvailAmount().subtract(adjustAmount))
                        .setAmount(credit.getAmount().subtract(adjustAmount));
            }
            creditService.updateById(credit);

        } finally {
            lockService.releaseLock(lockInfo);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void updatePassBatch(List<String> creditIds, String tag) {
        List<CreditAdjust> creditAdjustList = baseMapper.selectBatchIds(creditIds);
        //更新的实体

        for (CreditAdjust creditAdjust : creditAdjustList) {
            LockInfo lockInfo = lockTemplate.lock(creditAdjust.getCustomerId());
            if (lockInfo == null) {
                throw new ScmException("请求过于频繁，请稍后重试");
            }
            try {
                if (!CommonCheckStatusEnum.PENDING_VERIFY.getCode().equals(creditAdjust.getStatus())) {
                    throw new ScmException("勾选项存在已审核项，请重新勾选！");
                }
                CreditAdjust updateCreditAdjust = new CreditAdjust();

                Credit credit = creditService.getById(creditAdjust.getCreditId());

                updateCreditAdjust.setId(creditAdjust.getId());
                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                updateCreditAdjust.setChecker(sysUser.getUsername())
                        .setAdjustAmount(creditAdjust.getAdjustAmount())
                        .setCreditNo(creditAdjust.getCreditNo())
                        .setCheckDate(new Date());

                if (tag.equals(CommonCheckStatusEnum.APPROVE.getCode())) {

                    updateCreditAdjust.setStatus(CommonCheckStatusEnum.APPROVE.getCode())
                            .setAdjustType(creditAdjust.getAdjustType());

                    if (creditAdjust.getAdjustType().equalsIgnoreCase(CreditTypeStatusEnum.ADD.getCode())) {
                        credit.setAmount(credit.getAmount().add(creditAdjust.getAdjustAmount()))
                                .setAvailAmount(credit.getAvailAmount().add(creditAdjust.getAdjustAmount()));
                    }

                } else if (tag.equals(CommonCheckStatusEnum.REJECT.getCode())) {
                    updateCreditAdjust.setStatus(CommonCheckStatusEnum.REJECT.getCode());

                    if (creditAdjust.getAdjustType().equalsIgnoreCase(CreditTypeStatusEnum.REDUCE.getCode())) {
                        credit.setAvailAmount(credit.getAvatlableAmount().add(creditAdjust.getAdjustAmount()))
                                .setAmount(credit.getAmount().add(creditAdjust.getAdjustAmount()));
                    }
                }

                updateById(updateCreditAdjust);
                creditService.updateById(credit);

            } finally {

                lockTemplate.releaseLock(lockInfo);
            }
        }
    }

}
