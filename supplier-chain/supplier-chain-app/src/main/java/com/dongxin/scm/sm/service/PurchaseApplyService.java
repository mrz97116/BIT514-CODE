package com.dongxin.scm.sm.service;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.dongxin.scm.sm.entity.PurchaseApply;
import com.dongxin.scm.sm.enums.ApplyStatusEnum;
import com.dongxin.scm.sm.mapper.PurchaseApplyMapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 采购申请单
 * @Author: jeecg-boot
 * @Date: 2020-10-14
 * @Version: V1.0
 */
@Service
public class PurchaseApplyService extends BaseService<PurchaseApplyMapper, PurchaseApply> {

    public Result<?> commit(List<PurchaseApply> purchaseApplyList) {
        Result<?> result = new Result();
        for (PurchaseApply purchaseApply : purchaseApplyList) {
            String status = purchaseApply.getStatus();
            if (ApplyStatusEnum.INIT.getCode().equals(status)) {
                UpdateWrapper<PurchaseApply> wrapper = new UpdateWrapper<>();
                wrapper.lambda().set(PurchaseApply::getStatus, ApplyStatusEnum.COMMITTED.getCode())
                        .eq(PurchaseApply::getId, purchaseApply.getId());
                update(wrapper);
                result.setSuccess(true);
                result.setMessage("更改成功");
            } else {
                result.setSuccess(false);
                result.setMessage("状态为新增时可提交!");
            }
        }
        return result;
    }

    public Result<?> verify(List<PurchaseApply> purchaseApplyList) {
        Result<?> result = new Result();
        for (PurchaseApply purchaseApply : purchaseApplyList) {
            String status = purchaseApply.getStatus();
            if (ApplyStatusEnum.COMMITTED.getCode().equals(status)) {
                UpdateWrapper<PurchaseApply> wrapper = new UpdateWrapper<>();
                wrapper.lambda().set(PurchaseApply::getStatus, ApplyStatusEnum.VERIFIED.getCode())
                        .eq(PurchaseApply::getId, purchaseApply.getId());
                update(wrapper);
                result.setSuccess(true);
                result.setMessage("更改成功");
            } else {
                result.setSuccess(false);
                result.setMessage("审核时无法更改状态!");
            }
        }
        return result;
    }
}
