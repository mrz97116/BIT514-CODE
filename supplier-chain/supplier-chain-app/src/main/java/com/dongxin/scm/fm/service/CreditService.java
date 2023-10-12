package com.dongxin.scm.fm.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.common.enums.CommonCheckStatusEnum;
import com.dongxin.scm.enums.SerialNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.dto.CreditDTO;
import com.dongxin.scm.fm.entity.Credit;
import com.dongxin.scm.fm.mapper.CreditMapper;
import com.dongxin.scm.utils.SerialNoUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.base.service.BaseService;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 授信额度表
 * @Author: jeecg-boot
 * @Date: 2020-10-27
 * @Version: V1.0
 */
@Service
public class CreditService extends BaseService<CreditMapper, Credit> {

    @Autowired
    private FundAccountService fundAccountService;

    @Autowired
    private LockTemplate lockTemplate;

    //批量审批通过
    @Transactional(rollbackFor = Exception.class)
    public void updatePassBatch(List<String> creditIds, String tag) {
        List<Credit> credits = baseMapper.selectBatchIds(creditIds);
        //更新的实体

        for (Credit credit : credits) {

            LockInfo lockInfo = lockTemplate.lock(credit.getCustomerId());

            if (lockInfo == null) {
                throw new ScmException("请求过于频繁，请稍后重试");
            }
            try {

                if (!CommonCheckStatusEnum.PENDING_VERIFY.getCode().equals(credit.getStatus())) {
                    throw new ScmException("勾选项存在已审核项，请重新勾选！");
                }
                Credit updateCredit = new Credit();
                updateCredit.setId(credit.getId());
                if (tag.equals(CommonCheckStatusEnum.APPROVE.getCode())) {
                    updateCredit.setStatus(CommonCheckStatusEnum.APPROVE.getCode());
                    updateCredit.setAvailAmount(credit.getAmount());
                } else if (tag.equals(CommonCheckStatusEnum.REJECT.getCode())) {
                    updateCredit.setStatus(CommonCheckStatusEnum.REJECT.getCode());
                } else {
                    throw new ScmException("审核状态错误");
                }
                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                updateCredit.setChecker(sysUser.getUsername());
                updateCredit.setCheckDate(new Date());
                updateById(updateCredit);

                //授信需增加可用金额字段 **

            } finally {

                lockTemplate.releaseLock(lockInfo);
            }
        }


    }

    @Override
    public boolean save(Credit credit) {
        if (credit.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            log.error(StrUtil.format("{}增加授信额度失败", credit.getAmount()));
            throw new ScmException(StrUtil.format("{}增加授信额度失败", credit.getAmount()));
        }
        credit.setCreditNo(SerialNoUtil.getSerialNo(SerialNoEnum.CREDIT_NO));
        credit.setAvatlableAmount(credit.getAmount());
        credit.setStatus(CommonCheckStatusEnum.PENDING_VERIFY.getCode());
        return super.save(credit);
    }

    public void addCredit(Credit credit){
        super.save(credit);
    }


    /**
     * 获取客户当前授信可用金额可授信总金额
     *
     * @param customerId
     * @return
     */
    public CreditDTO getCreditAvailAmountAndTotalCreditAmount(String customerId) {
        QueryWrapper<Credit> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Credit::getCustomerId, customerId)
                .eq(Credit::getStatus, CommonCheckStatusEnum.APPROVE.getCode());


        List<Credit> creditList = list(queryWrapper);
        BigDecimal totalAmount = BigDecimal.ZERO;

        BigDecimal totalAvailAmount = BigDecimal.ZERO;

        for (Credit credit : creditList) {
            totalAmount = totalAmount.add(credit.getAmount());
            totalAvailAmount = totalAvailAmount.add(credit.getAvailAmount());
        }

        CreditDTO result = new CreditDTO();

        result.setTotalCreditAmount(totalAmount);

        result.setTotalAvailAmount(totalAvailAmount);

        return result;

    }


}
