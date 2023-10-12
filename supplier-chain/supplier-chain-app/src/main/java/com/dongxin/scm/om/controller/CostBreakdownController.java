package com.dongxin.scm.om.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.entity.SalesOrderDet;
import com.dongxin.scm.om.vo.SalesOrderPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.om.entity.CostBreakdown;
import com.dongxin.scm.om.service.CostBreakdownService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 费用明细表
 * @Author: jeecg-boot
 * @Date: 2021-06-09
 * @Version: V1.0
 */
@Api(tags = "费用明细表")
@RestController
@RequestMapping("/om/costBreakdown")
@Slf4j
public class CostBreakdownController extends BaseController<CostBreakdown, CostBreakdownService> {
    @Autowired
    CostBreakdownService costBreakdownService;

    @AutoLog(value = "费用明细表通过主表ID查询")
    @ApiOperation(value = "费用明细表通过主表ID查询", notes = "费用明细表-通过主表ID查询")
    @GetMapping(value = "/queryCostBreakdownById")
    public org.jeecg.common.api.vo.Result<?> queryCostBreakdownById(@RequestParam(name = "id", required = true) String id,
                                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<CostBreakdown> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CostBreakdown::getSalesOrderId, id);
        Page<CostBreakdown> page = new Page<>(pageNo, pageSize);
        IPage<CostBreakdown> pageList = costBreakdownService.page(page, queryWrapper);
        return Result.OK(pageList);
    }
}
