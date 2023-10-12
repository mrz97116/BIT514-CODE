package com.dongxin.scm.trustedthird.controller;

import com.dongxin.scm.trustedthird.service.TrustedThirdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.config.mybatis.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "可信数据")
@RestController
@RequestMapping("/trustedThird/trustedThird")
@Slf4j
public class TrustedThirdController {

    @Autowired
    TrustedThirdService trustedThirdService;

    /**
     * 推送签收单至可信数据
     *
     * @param saleBillNo
     * @return
     */
    @AutoLog(value = "推送签收单至可信数据")
    @ApiOperation(value = "推送签收单至可信数据", notes = "推送签收单至可信数据")
    @GetMapping(value = "/send")
    public Result<?> add(@RequestParam(name = "saleBillNo") String saleBillNo ) throws Exception {
        String tenantId = TenantContext.getTenant();
        trustedThirdService.signInOrderSendPdf(saleBillNo, tenantId);
        return Result.OK("添加成功！");
    }

    /**
     * 查看作准文件
     *
     * @param saleBillNo
     * @return
     */
    @AutoLog(value = "查看作准文件")
    @ApiOperation(value = "查看作准文件", notes = "查看作准文件")
    @GetMapping(value = "/viewTrustedThird")
    public Result<?> viewTrustedThird(@RequestParam(name = "saleBillNo") String saleBillNo ) throws Exception {
         String fileUrl =  trustedThirdService.viewTrustedThird(saleBillNo);
        return Result.OK(fileUrl);
    }

}


