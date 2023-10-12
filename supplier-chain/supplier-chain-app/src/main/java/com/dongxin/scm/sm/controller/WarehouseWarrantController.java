package com.dongxin.scm.sm.controller;

import cn.hutool.core.util.ObjectUtil;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.sm.dto.WarehouseWarrantDTO;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.entity.WarehouseWarrant;
import com.dongxin.scm.sm.service.InventoryService;
import com.dongxin.scm.sm.service.MatService;
import com.dongxin.scm.sm.service.WarehouseWarrantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 入库单
 * @Author: jeecg-boot
 * @Date: 2021-04-06
 * @Version: V1.0
 */
@Api(tags = "入库单")
@RestController
@RequestMapping("/sm/warehouseWarrant")
@Slf4j
public class WarehouseWarrantController extends BaseController<WarehouseWarrant, WarehouseWarrantService> {

    @Autowired
    private WarehouseWarrantService warehouseWarrantService;
    @Autowired
    private MatService matService;
    @Autowired
    private InventoryService inventoryService;

    /**
     * 入库单整单删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "入库单整单删除")
    @ApiOperation(value = "入库单整单删除", notes = "入库单整单删除")
    @GetMapping(value = "/removeAll")
    public Result<?> removeAll(@RequestParam(name = "id", required = true) String id) {
        warehouseWarrantService.subduction(id, true);
        return Result.OK();
    }

    /**
     * 入库单整单删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "入库单明细删除")
    @ApiOperation(value = "入库单明细删除", notes = "入库单明细删除")
    @PostMapping(value = "/removeDetail")
    public Result<?> removeDetail(@RequestBody List<String> ids) {
        warehouseWarrantService.detailSubduction(ids);
        return Result.OK();
    }
    /**
     *  编辑仓库和来源地
     *  @param warehouseWarrantDTO
     *  @return
     */
    @AutoLog(value = "编辑")
    @ApiOperation(value = "编辑", notes = "编辑")
    @PutMapping(value = "/editWarehouseWarrant")
    public Result<?> editWarehouseWarrant(@RequestBody WarehouseWarrantDTO warehouseWarrantDTO) {
        WarehouseWarrant warehouseWarrant = new WarehouseWarrant();
        BeanUtils.copyProperties(warehouseWarrantDTO, warehouseWarrant);
        //查询数据库中与warehouseWarrant同id的旧数据
        WarehouseWarrant oldWarehouseWarrant = warehouseWarrantService.getById(warehouseWarrant.getId());
        //如果旧数据的仓库id与新数据的仓库id相同，则直接在旧数据上更新数据
        if (ObjectUtil.isNotNull(oldWarehouseWarrant)){
            if(oldWarehouseWarrant.getStockId().equals(warehouseWarrant.getStockId())) {
                warehouseWarrantService.newUpdateById(warehouseWarrant,warehouseWarrantDTO.getMatList());
            } else {
                //反之，执行更新修改仓库id
                warehouseWarrantService.subductionAsStockChanges(warehouseWarrant,warehouseWarrantDTO.getMatList());
            }
        } else {
            throw new ScmException("未找到相应入库信息");
        }

        return Result.OK("编辑成功");
    }
    /**
     * 通过id查询
     *
     * @param warehouseWarrantId
     * @return
     */
    @AutoLog(value = "入库单明细表通过主表ID查询")
    @ApiOperation(value = "入库单明细表通过主表ID查询", notes = "入库单明细表通过主表ID查询")
    @GetMapping(value = "/queryMatByWarehouseWarrantId")
    public Result<?> queryMatByWarehouseWarrantId(@RequestParam(name = "id", required = true) String warehouseWarrantId) {
        List<Mat> matList = matService.selectByWarehouseWarrantId(warehouseWarrantId);
        return Result.OK(matList);
    }
}
