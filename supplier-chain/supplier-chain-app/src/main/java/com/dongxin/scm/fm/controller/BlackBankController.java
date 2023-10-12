package com.dongxin.scm.fm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.fm.entity.Bank;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.vo.SalesOrderPage;
import com.dongxin.scm.utils.ScmBeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.fm.entity.BlackBank;
import com.dongxin.scm.fm.service.BlackBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 银行黑名单
 * @Author: jeecg-boot
 * @Date: 2021-12-31
 * @Version: V1.0
 */
@Api(tags = "银行黑名单")
@RestController
@RequestMapping("/fm/blackBank")
@Slf4j
public class BlackBankController extends BaseController<BlackBank, BlackBankService> {
    @Autowired
    private BlackBankService blackBankService;

    /**
     * 添加
     *
     * @param blackBank
     * @return
     */
    @AutoLog(value = "销售单-添加")
    @ApiOperation(value = "销售单-添加", notes = "销售单-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody BlackBank blackBank) {
        blackBankService.addBank(blackBank);

        return Result.OK("添加成功！");
    }


}
