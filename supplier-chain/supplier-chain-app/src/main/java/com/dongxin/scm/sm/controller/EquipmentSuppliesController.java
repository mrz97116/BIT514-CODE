package com.dongxin.scm.sm.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.sm.entity.EquipmentSupplies;
import com.dongxin.scm.sm.service.EquipmentSuppliesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 设备物资网主表
 * @Author: jeecg-boot
 * @Date: 2021-03-03
 * @Version: V1.0
 */
@Api(tags = "设备物资网主表")
@RestController
@RequestMapping("/sm/equipmentSupplies")
@Slf4j
public class EquipmentSuppliesController extends BaseController<EquipmentSupplies, EquipmentSuppliesService> {

    @Autowired
    EquipmentSuppliesService service;

    /**
     * 消减款项
     *
     * @return
     */
    @AutoLog(value = "消减款项")
    @ApiOperation(value = "消减款项", notes = "消减款项")
    @GetMapping(value = "/subduction")
    public Result<?> subduction() {
        service.subduction();
        return Result.ok();
    }

    /**
     * 红冲
     * @param idList
     * @return
     */
    @AutoLog(value = "红冲")
    @ApiOperation(value = "红冲", notes = "红冲")
    @PostMapping(value = "/revocation")
    public Result<?> revocation(@RequestBody List<String> idList) {
        service.revocation(idList);
        return Result.ok();
    }
}
