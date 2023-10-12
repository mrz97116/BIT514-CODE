package com.dongxin.scm.fm.controller;

import com.dongxin.scm.fm.entity.InvoiceCode;
import com.dongxin.scm.fm.service.InvoiceCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 发票码信息表
 * @Author: jeecg-boot
 * @Date: 2020-12-23
 * @Version: V1.0
 */
@Api(tags = "发票码信息表")
@RestController
@RequestMapping("/fm/invoiceCode")
@Slf4j
public class InvoiceCodeController extends BaseController<InvoiceCode, InvoiceCodeService> {

    @Autowired
    InvoiceCodeService invoiceCodeService;

    /**
     * 添加
     *
     * @param invoiceCode
     * @return
     */
    @AutoLog(value = "添加")
    @ApiOperation(value = "添加", notes = "添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody InvoiceCode invoiceCode) {
        invoiceCodeService.save(invoiceCode);
        return Result.OK("添加成功！");
    }


}
