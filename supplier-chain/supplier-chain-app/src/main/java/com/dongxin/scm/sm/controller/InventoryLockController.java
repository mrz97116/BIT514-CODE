package com.dongxin.scm.sm.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.sm.entity.InventoryLock;
import com.dongxin.scm.sm.service.InventoryLockService;
import com.dongxin.scm.sm.service.InventoryService;
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
 * @Description: 库存锁定
 * @Author: jeecg-boot
 * @Date: 2021-06-23
 * @Version: V1.0
 */
@Api(tags = "库存锁定")
@RestController
@RequestMapping("/sm/inventoryLock")
@Slf4j
public class InventoryLockController extends BaseController<InventoryLock, InventoryLockService> {

    @Autowired
    InventoryLockService inventoryLockService;
    @Autowired
    InventoryService inventoryService;

    /**
     * 分页列表查询
     *
     * @param param
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "分页列表查询")
    @ApiOperation(value = "分页列表查询", notes = "分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(InventoryLock param,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {

        QueryWrapper<InventoryLock> queryWrapper = QueryGenerator.initQueryWrapper(param, req.getParameterMap());
        if (StrUtil.isNotBlank(req.getParameter("matNos"))) {
            String matNos = req.getParameter("matNos");
            String[] str = matNos.split("\n");
            queryWrapper.lambda().in(InventoryLock::getMatNo, str);
        }
        Page<InventoryLock> page = new Page<>(pageNo, pageSize);
        IPage<InventoryLock> pageList = inventoryLockService.page(page, queryWrapper);
        return Result.OK(pageList);
    }


    /**
     * 锁定库存
     *
     * @param ids
     * @param remark
     * @return
     */
    @AutoLog(value = "锁定库存")
    @ApiOperation(value = "锁定库存", notes = "锁定库存")
    @RequestMapping(value = "/locking")
    public Result<?> inventoryLocking(@RequestParam(name = "ids") String ids,
                                      @RequestParam(name = "remark") String remark) {

        inventoryLockService.saveLock(ids,remark);
        return Result.OK("锁定成功!");
    }
    /**
     * 批量备注
     *
     * @param ids
     * @param remark
     * @return
     */
    @AutoLog(value = "批量备注")
    @ApiOperation(value = "批量备注", notes = "批量备注")
    @RequestMapping(value = "/bulkEditing")
    public Result<?> bulkEditing(@RequestParam(name = "ids") String ids,
                                      @RequestParam(name = "remark") String remark) {

        inventoryLockService.bulkEditing(ids,remark);
        return Result.OK("修改成功!");
    }

    /**
     * 解锁库存
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "解锁库存")
    @ApiOperation(value = "解锁库存", notes = "解锁库存")
    @RequestMapping(value = "/unlocking")
    public Result<?> inventoryUnlocking(@RequestParam(name = "ids") String ids) {

        inventoryLockService.inventoryUnlocking(ids);
        return Result.OK("解锁成功!");
    }

}
