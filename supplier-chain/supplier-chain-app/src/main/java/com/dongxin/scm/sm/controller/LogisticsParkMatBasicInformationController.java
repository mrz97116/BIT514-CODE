package com.dongxin.scm.sm.controller;

import com.dongxin.scm.sm.entity.LogisticsParkMatBasicInformation;
import com.dongxin.scm.sm.service.LogisticsParkMatBasicInformationService;
import com.dongxin.scm.trustedthird.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 物流园材料基础信息
 * @Author: jeecg-boot
 * @Date: 2021-09-16
 * @Version: V1.0
 */
@Api(tags = "物流园材料基础信息")
@RestController
@RequestMapping("/sm/logisticsParkMatBasicInformation")
@Slf4j
public class LogisticsParkMatBasicInformationController extends BaseController<LogisticsParkMatBasicInformation, LogisticsParkMatBasicInformationService> {

    @Autowired
    private LogisticsParkMatBasicInformationService logisticsParkMatBasicInformationService;

    /**
     *
     * @param steelMillsName
     * @return
     */
    @GetMapping(value = "/getLogisticsParkMatBasicInformation")
    public Result<?> getLogisticsParkMatBasicInformation(@RequestParam(name = "steelMillsName") String steelMillsName) {
        logisticsParkMatBasicInformationService.getLogisticsParkMatBasicInformation(steelMillsName);
        return Result.ok();
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
    public org.jeecg.common.api.vo.Result<?> edit(@RequestBody LogisticsParkMatBasicInformation param) {
        logisticsParkMatBasicInformationService.checkUpdateContent(param);
        logisticsParkMatBasicInformationService.updateById(param);
        return org.jeecg.common.api.vo.Result.OK("编辑成功!");
    }

}
