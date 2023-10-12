package com.dongxin.scm.sm.controller;

import com.dongxin.scm.sm.entity.Stock;
import com.dongxin.scm.sm.enums.StockActiveEnum;
import com.dongxin.scm.sm.service.StockService;
import com.dongxin.scm.sm.vo.StockVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 仓库信息表
 * @Author: jeecg-boot
 * @Date: 2020-09-17
 * @Version: V1.0
 */
@Api(tags = "仓库信息表")
@RestController
@RequestMapping("/sm/stock")
@Slf4j
public class StockController extends BaseController<Stock, StockService> {

    @Autowired
    private StockService stockService;

    /**
     * 添加
     *
     * @param stock
     * @return
     */
    @AutoLog(value = "装车单主表-添加")
    @ApiOperation(value = "装车单主表-添加", notes = "装车单主表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Stock stock) {
        stockService.addStock(stock);
        return Result.ok("添加成功！");
    }

    /**
     * 批量启用
     *
     * @param stockVO
     * @return
     */
    @ApiOperation(value = "批量启用")
    @PostMapping(value = "/enable")
    public Result<?> enable(@RequestBody StockVO stockVO) {
        stockService.enable(stockVO.getIds(), StockActiveEnum.ENABLE.getCode());
        return Result.ok("启用成功!");
    }


    /**
     * 批量禁用
     *
     * @param stockVO
     * @return
     */
    @ApiOperation(value = "批量禁用")
    @PostMapping(value = "/disable")
    public Result<?> disable(@RequestBody StockVO stockVO) {
        stockService.enable(stockVO.getIds(), StockActiveEnum.DISABLE.getCode());
        return Result.ok("禁用成功!");
    }

    @ApiOperation(value = "查询仓库信息")
    @GetMapping(value = "/queryWarehouse")
    public Result<?> queryWarehouse() {
        return Result.ok(stockService.queryWarehouse());
    }


}
