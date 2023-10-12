package com.dongxin.scm.fm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.fm.entity.Bank;
import com.dongxin.scm.fm.service.BankService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 银行信息汇总表
 * @Author: jeecg-boot
 * @Date: 2020-10-28
 * @Version: V1.0
 */
@Api(tags = "银行信息汇总表")
@RestController
@RequestMapping("/fm/bank")
@Slf4j
public class BankController extends BaseController<Bank, BankService> {

    @Autowired
    BankService bankService;


    @AutoLog("分页列表查询")
    @ApiOperation(
            value = "分页列表查询",
            notes = "分页列表查询"
    )
    @GetMapping({"/list"})
    public Result<?> queryPageList(Bank param,
                                   @RequestParam(name = "pageNo",defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<Bank> queryWrapper = QueryGenerator.initQueryWrapper(param, req.getParameterMap());
        Page<Bank> page = new Page((long)pageNo, (long)pageSize);
        IPage<Bank> pageList = this.bankService.page(page, queryWrapper);

        IPage<Bank> BankList = bankService.getBankType(pageList);
        return Result.OK(BankList);
    }

    /**
     *  编辑
     *
     * @param param
     * @return
     */
    @AutoLog(value = "编辑")
    @ApiOperation(value="编辑", notes="编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody Bank param) {
        bankService.updateById(param);
        return Result.OK("编辑成功!");
    }

}
