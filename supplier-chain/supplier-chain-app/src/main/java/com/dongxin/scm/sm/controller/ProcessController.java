package com.dongxin.scm.sm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.enums.ProdClassCodeEnum;
import com.dongxin.scm.sm.dto.ProcessingDTO;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.service.InventoryService;
import com.dongxin.scm.sm.service.ProcessService;
import com.dongxin.scm.sm.service.ProcessingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static cn.hutool.core.collection.CollUtil.newArrayList;

/**
 * @Description: 物料信息表
 * @Author: jeecg-boot
 * @Date: 2020-11-18
 * @Version: V1.0
 */
@Api(tags = "物料信息表")
@RestController
@RequestMapping("/sm/process")
@Slf4j
public class ProcessController {

    @Autowired
    ProcessService processService;

    @Autowired
    ProcessingService processingService;

    @Autowired
    InventoryService inventoryService;

    @AutoLog("分页列表查询")
    @ApiOperation(
            value = "分页列表查询",
            notes = "分页列表查询"
    )
    @GetMapping({"/list"})
    public Result<?> queryPageList(Inventory inventory, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<Inventory> queryWrapper = QueryGenerator.initQueryWrapper(inventory, req.getParameterMap());
        List<String> prodClassCodes = newArrayList(ProdClassCodeEnum.F.getValue()
                , ProdClassCodeEnum.G.getValue());
        queryWrapper.lambda().in(Inventory::getProdClassCode, prodClassCodes)
                .gt(Inventory::getAvailableQty, 0);
        Page<Inventory> page = new Page((long) pageNo, (long) pageSize);
        IPage<Inventory> pageList = inventoryService.page(page, queryWrapper);

        return Result.OK(pageList);
    }

    /**
     * @param inventoryIdList
     * @return
     */
    @AutoLog(value = "添加")
    @ApiOperation(value = "添加", notes = "添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody List<String> inventoryIdList) {
        processingService.processMat(inventoryIdList);
        return Result.OK("添加成功！");
    }

    //    Yrm加工
    @AutoLog(value = "Yrm添加")
    @ApiOperation(value = "Yrm添加", notes = "Yrm添加")
    @PostMapping(value = "/addYrm")
    public Result<?> addYrm(@RequestBody ProcessingDTO processingDTO) {
        processingService.processMatYrm(processingDTO.getInventoryId(), processingDTO.getProcessDtoList(), processingDTO.getCustomerId(), processingDTO.getSalesManId(), processingDTO.getRemarks());
        return Result.OK("添加成功！");
    }

    @AutoLog("编辑")
    @ApiOperation(
            value = "编辑",
            notes = "编辑"
    )
    @PutMapping({"/edit"})
    public Result<?> edit(@RequestBody Mat mat) {
        processService.updateById(mat);
        return Result.OK("编辑成功!");
    }

    @AutoLog("通过id删除")
    @ApiOperation(
            value = "通过id删除",
            notes = "通过id删除"
    )
    @DeleteMapping({"/delete"})
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        processService.removeById(id);
        return Result.OK("删除成功!");
    }

    @AutoLog("批量删除")
    @ApiOperation(
            value = "批量删除",
            notes = "批量删除"
    )
    @DeleteMapping({"/deleteBatch"})
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        processService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    @AutoLog("通过id查询")
    @ApiOperation(
            value = "通过id查询",
            notes = "通过id查询"
    )
    @GetMapping({"/queryById"})
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        Mat mat = processService.getById(id);
        return mat == null ? Result.error("未找到对应数据") : Result.OK(mat);
    }

}
