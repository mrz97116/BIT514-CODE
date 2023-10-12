package com.dongxin.scm.sm.controller;

import com.dongxin.scm.sm.entity.Processing;
import com.dongxin.scm.sm.service.ProcessingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 加工管理
 * @Author: jeecg-boot
 * @Date: 2021-02-20
 * @Version: V1.0
 */
@Api(tags = "加工管理")
@RestController
@RequestMapping("/sm/processing")
@Slf4j
public class ProcessingController extends BaseController<Processing, ProcessingService> {


    @Autowired
    private ProcessingService processingService;
    /**
     *   通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "通过id删除")
    @ApiOperation(value="通过id删除", notes="通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name="id",required=true) String id) {
        processingService.delProcessing(id);
        return Result.OK("删除成功!");
    }

}
