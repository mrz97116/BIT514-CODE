package com.dongxin.scm.sm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.sm.entity.InventoryAllot;
import com.dongxin.scm.sm.service.InventoryAllotService;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 货物调拨表
 * @Author: jeecg-boot
 * @Date: 2021-08-08
 * @Version: V1.0
 */
@Api(tags = "货物调拨表")
@RestController
@RequestMapping("/sm/inventoryAllot")
@Slf4j
public class InventoryAllotController extends BaseController<InventoryAllot, InventoryAllotService> {
    @Autowired
    private InventoryAllotService inventoryAllotService;


    /**
     * 分页列表查询
     *
     * @param inventoryAllot
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "调拨信息-分页列表查询")
    @ApiOperation(value = "调拨信息-分页列表查询", notes = "调拨信息-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(InventoryAllot inventoryAllot,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<InventoryAllot> queryWrapper = QueryGenerator.initQueryWrapper(inventoryAllot, req.getParameterMap());
        queryWrapper.lambda().eq(InventoryAllot::getDelFlag,0);
        Page<InventoryAllot> page = new Page<InventoryAllot>(pageNo, pageSize);
        IPage<InventoryAllot> pageList = inventoryAllotService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 通过id删除，逻辑删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "调拨单信息-通过id删除")
    @ApiOperation(value = "调拨单信息-通过id删除", notes = "调拨单信息-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        inventoryAllotService.logicalDelete(id);
        return Result.ok("删除成功!");
    }


}
