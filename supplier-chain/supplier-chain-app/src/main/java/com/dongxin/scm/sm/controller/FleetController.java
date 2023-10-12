package com.dongxin.scm.sm.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.om.vo.OptionVO;
import com.dongxin.scm.om.vo.SalesOrderPage;
import com.dongxin.scm.sm.entity.GetActualDelivery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.sm.entity.Fleet;
import org.jeecg.common.api.vo.Result;
import com.dongxin.scm.sm.service.FleetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 车队管理
 * @Author: jeecg-boot
 * @Date: 2021-06-29
 * @Version: V1.0
 */
@Api(tags = "车队管理")
@RestController
@RequestMapping("/fleet/smFleet")
@Slf4j
public class FleetController extends BaseController<Fleet, FleetService> {
    @Autowired
    private FleetService fleetService;

    //查询车队名称
    @GetMapping("/fleetList")
    public Result<?> queryFleet() {
        List<OptionVO> fleetList = fleetService.queryFleet();
        return Result.ok(fleetList);
    }

    /**
     * 添加
     *
     * @param fleet
     * @return
     */
    @AutoLog(value = "添加")
    @ApiOperation(value = "添加", notes = "添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Fleet fleet) {
        QueryWrapper<Fleet> fleetQueryWrapper = new QueryWrapper<>();
        fleetQueryWrapper.lambda().eq(Fleet::getFleetName, fleet.getFleetName());

        Fleet fleetInDb = fleetService.getOne(fleetQueryWrapper);
        if (ObjectUtil.isNotEmpty(fleetInDb)) {
            return Result.error("添加失败，车队名称重复！");
        } else {
            fleetService.save(fleet);
            return Result.OK("添加成功！");
        }
    }
    /**
     * 编辑
     *
     * @param fleet 编辑
     * @return
     */
    @AutoLog(value = "编辑")
    @ApiOperation(value = "编辑", notes = "编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody Fleet fleet){
        QueryWrapper<Fleet> fleetQueryWrapper = new QueryWrapper<>();
        fleetQueryWrapper.lambda().eq(Fleet::getFleetName, fleet.getFleetName());

        Fleet fleetInDb = fleetService.getOne(fleetQueryWrapper);
        if (ObjectUtil.isNotEmpty(fleetInDb)) {
            return Result.error("修改失败，车队名称重复！");
        } else {
            fleetService.updateById(fleet);
            return Result.OK("修改成功！");
        }
    }
}
