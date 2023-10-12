package com.dongxin.scm.sm.controller;

import cn.hutool.core.util.StrUtil;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.sm.dto.PrepareOrderDTO;
import com.dongxin.scm.sm.dto.ShippingManagementDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.sm.entity.PrepareOrder;
import com.dongxin.scm.sm.service.PrepareOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 预开单信息表
 * @Author: jeecg-boot
 * @Date: 2021-10-31
 * @Version: V1.0
 */
@Api(tags = "预开单信息表")
@RestController
@RequestMapping("/sm/prepareOrder")
@Slf4j
public class PrepareOrderController extends BaseController<PrepareOrder, PrepareOrderService> {

    @Autowired
    PrepareOrderService prepareOrderService;


    @AutoLog(value = "预开单")
    @ApiOperation(value = "预开单", notes = "预开单")
    @PostMapping(value = "/prepareOrder")
    public Result<?> prepareOrder(@RequestBody ShippingManagementDTO shippingManagementDTO) {

        if (StrUtil.isBlank(shippingManagementDTO.getPrepareOrderNo())) {
            if (StrUtil.isBlank(shippingManagementDTO.getCustomerId())) {
                throw new ScmException("请选择客户");
            }

            if (StrUtil.isBlank(shippingManagementDTO.getDestination())) {
                throw new ScmException("请选择仓库");
            }
        }
        prepareOrderService.prepareOrder(shippingManagementDTO);
        return Result.OK();
    }

    @AutoLog(value = "预开单材料入库")
    @ApiOperation(value = "预开单材料入库", notes = "预开单材料入库")
    @PostMapping(value = "/putInStorageBatch")
    public Result<?> putInStorageBatch(@RequestBody PrepareOrderDTO prepareOrderDTO) {
        List<String> ids = prepareOrderDTO.getIds();
        prepareOrderService.putInStorageBatch(ids);
        return Result.OK();
    }

    @AutoLog(value = "预开单材料取消入库")
    @GetMapping(value = "/cancelStorage")
    public Result<?> putInStorageBatch(@RequestParam(name = "prepareOrderId") String prepareOrderId) {
        prepareOrderService.cancelStorage(prepareOrderId);
        return Result.OK();
    }

    /**
     * 通过预开单id查询返回生成提单必备的数据
     *
     * @param prepareOrderId
     * @return
     */
    @AutoLog(value = "通过预开单id查询返回生成提单必备的数据")
    @ApiOperation(value = "通过预开单id查询返回生成提单必备的数据", notes = "通过预开单id查询返回生成提单必备的数据")
    @GetMapping(value = "/querySalesOrderDetByPrepareId")
    public Result<?> querySalesOrderDetByPrepareId(@RequestParam(name = "id", required = true) String prepareOrderId) {

        return Result.OK(prepareOrderService.generatePrepareOrderVO(prepareOrderId));
    }

    /**
     * 修改分货
     * @param prepareOrderIds
     * @param amount
     * @return
     */
    @RequestMapping(value = "/updatePreparePrice")
    public Result<?> updatePrepareOrderPrice(@RequestParam(name = "prepareOrderIds") String prepareOrderIds,
                                           @RequestParam(name = "amount") BigDecimal amount) {
        prepareOrderService.updatePrepareOrderPrice(prepareOrderIds, amount);
        return Result.ok("修改分货成功");

    }

}
