package com.dongxin.scm.sm.controller;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.entity.SalesOrderDet;
import com.dongxin.scm.sm.dto.GoodEntrustmentLetterDTO;
import com.dongxin.scm.sm.entity.ShippingManagement;
import com.dongxin.scm.sm.service.ShippingManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.formula.functions.T;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.sm.entity.GoodEntrustmentLetter;
import com.dongxin.scm.sm.service.GoodEntrustmentLetterService;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 提货委托管理
 * @Author: jeecg-boot
 * @Date: 2021-11-03
 * @Version: V1.0
 */
@Api(tags = "提货委托管理")
@RestController
@RequestMapping("/sm/goodEntrustmentLetter")
@Slf4j
public class GoodEntrustmentLetterController extends BaseController<GoodEntrustmentLetter, GoodEntrustmentLetterService> {
    @Autowired
    private GoodEntrustmentLetterService goodEntrustmentLetterService;

    @Autowired
    private ShippingManagementService shippingManagementService;

    @AutoLog(value = "提货委托单-添加")
    @ApiOperation(value = "提货委托单-添加", notes = "提货委托单-添加")
    @PostMapping(value = "/addGoodEntrustmentLetter")
    public Result<?> addGoodEntrustmentLetter(@RequestBody GoodEntrustmentLetterDTO goodEntrustmentLetterDTO) {
        goodEntrustmentLetterService.saveMain(goodEntrustmentLetterDTO);
        return Result.OK("添加成功！");
    }

    @AutoLog("通过id删除")
    @ApiOperation(
            value = "通过id删除",
            notes = "通过id删除"
    )
    @DeleteMapping({"/delete"})
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        goodEntrustmentLetterService.clearConnect(id);
        return Result.OK("删除成功!");
    }

    @AutoLog("批量删除")
    @ApiOperation(
            value = "批量删除",
            notes = "批量删除"
    )
    @DeleteMapping({"/deleteBatch"})
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        List<String> batchIds = Arrays.asList(ids.split(","));
        batchIds.forEach(this::delete);
        return Result.OK("批量删除成功!");
    }

    @AutoLog("编辑")
    @ApiOperation(
            value = "编辑",
            notes = "编辑"
    )
    @PutMapping({"/editGoodEntrustmentLetter"})
    public Result<?> editGoodEntrustmentLetter(@RequestBody GoodEntrustmentLetterDTO goodEntrustmentLetterDTO) {
//        goodEntrustmentLetterService.updateByGoodEntrustmentLetter(goodEntrustmentLetterDTO,goodEntrustmentLetterDTO.getSalesOrderDetList());
        return Result.OK("编辑成功!");
    }

    @AutoLog("提货委托单分页查询")
    @ApiOperation(
            value = "提货委托单分页列表查询",
            notes = "提货委托单分页列表查询"
    )
    @GetMapping({"/list"})
    public Result<?> queryPageList(GoodEntrustmentLetter goodEntrustmentLetter,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<GoodEntrustmentLetter> queryWrapper = QueryGenerator.initQueryWrapper(goodEntrustmentLetter, req.getParameterMap());
        String prodClassCode1 = req.getParameter("prodClassCode1"); //产品大类

        QueryWrapper<ShippingManagement> shippingManagementQueryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(prodClassCode1)) {
            shippingManagementQueryWrapper.lambda().eq(ShippingManagement::getProdClassCode, prodClassCode1);
        }
        List<ShippingManagement> shippingManagementList = shippingManagementService.list(shippingManagementQueryWrapper);
        List<String> ids = shippingManagementList.stream().map(ShippingManagement::getDeliveryCommissionId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(ids)) {
            queryWrapper.lambda().in(GoodEntrustmentLetter::getId, ids);
        } else {
            queryWrapper.lambda().eq(GoodEntrustmentLetter::getId, null);
        }
        Page<GoodEntrustmentLetter> page = new Page<GoodEntrustmentLetter>(pageNo, pageSize);
        IPage<GoodEntrustmentLetter> pageList = goodEntrustmentLetterService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    @AutoLog("查询提货委托明细")
    @ApiOperation(
            value = "查询提货委托明细",
            notes = "查询提货委托明细"
    )
    @GetMapping({"/getDetails"})
    public Result<?> getDetails(@RequestParam(name = "id") String id) {

        QueryWrapper<ShippingManagement> queryWrapper = new QueryWrapper<>();

        queryWrapper.lambda().eq(ShippingManagement::getDeliveryCommissionId, id);

        List<ShippingManagement> result = shippingManagementService.list(queryWrapper);
        return Result.OK(result);
    }

    @AutoLog("提货委托单分页查询")
    @ApiOperation(
            value = "提货委托单分页列表查询",
            notes = "提货委托单分页列表查询"
    )
    @PutMapping("/arrivalCheck")
    public Result<?> arrivalCheck(@RequestBody GoodEntrustmentLetterDTO goodEntrustmentLetterDTO) {
        goodEntrustmentLetterService.arrivalCheck(goodEntrustmentLetterDTO);
        return Result.ok("审核成功");
    }
    /**
     * 批量添加备注
     * @param ids
     * @param remark
     * @return
     */
    @RequestMapping(value = "/addRemarks")
    public Result<?> batchAddRemarks(@RequestParam(name = "ids") String ids,
                                     @RequestParam(name = "remark") String remark) {
        goodEntrustmentLetterService.addRemarks(ids, remark);
        return Result.ok("添加备注成功");

    }
}
