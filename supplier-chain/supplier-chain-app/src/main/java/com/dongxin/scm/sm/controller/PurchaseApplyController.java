package com.dongxin.scm.sm.controller;

import com.dongxin.scm.sm.entity.PurchaseApply;
import com.dongxin.scm.sm.service.PurchaseApplyService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: 采购申请单
 * @Author: jeecg-boot
 * @Date: 2020-10-14
 * @Version: V1.0
 */
@Api(tags = "采购申请单")
@RestController
@RequestMapping("/sm/purchaseApply")
@Slf4j
public class PurchaseApplyController extends BaseController<PurchaseApply, PurchaseApplyService> {

    @Autowired
    PurchaseApplyService purchaseApplyService;

    @PostMapping("/commit")
    public Result<?> commit(@RequestBody List<PurchaseApply> purchaseApplyList) {
        Result<?> result = purchaseApplyService.commit(purchaseApplyList);
        return result;
    }

    @PostMapping("/verify")
    public Result<?> verify(@RequestBody List<PurchaseApply> purchaseApplyList) {
        Result<?> result = purchaseApplyService.verify(purchaseApplyList);
        return result;
    }
}
