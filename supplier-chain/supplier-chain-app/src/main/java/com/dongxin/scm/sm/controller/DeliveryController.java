package com.dongxin.scm.sm.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.service.SalesOrderDetService;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.sm.service.DeliveryService;
import com.dongxin.scm.sm.vo.SalesOrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 发货
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */
@Api(tags = "销售单")
@RestController
@RequestMapping("/sm/delivery")
@Slf4j
public class DeliveryController extends JeecgController<SalesOrder, SalesOrderService> {

    @Autowired
    private SalesOrderService salesOrderService;

    @Autowired
    private SalesOrderDetService salesOrderDetService;

    @Autowired
    private DeliveryService deliveryService;


    /*---------------------------------主表处理-begin-----------------------
    /**
     * 列表查询
     * @param salesOrderDetVO
     * @return
     */
    @ApiOperation(value = "销售单查询", notes = "销售单查询")
    @GetMapping(value = "/list")
    public Result<?> queryPage(SalesOrderVO salesOrderVO,
                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                               @RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize) {
        Page<SalesOrderVO> page = new Page<SalesOrderVO>(pageNo, pageSize);
        page.setRecords(deliveryService.queryPage(page, salesOrderVO));
        return Result.ok(page);
    }

    /**
     * 获取库存材料
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "获取库存材料")
    @GetMapping(value = "/matList")
    public Result<?> queryMatList(@RequestParam(name = "id", required = true) String id) {
        return Result.ok(deliveryService.queryMatList(id));
    }


}
