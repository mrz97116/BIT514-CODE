package com.dongxin.scm.sm.controller;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.sm.dto.InventoryAllotDTO;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.service.InventoryLockService;
import com.dongxin.scm.sm.service.InventoryService;
import com.dongxin.scm.sm.service.MatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.mybatis.TenantContext;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 库存信息
 * @Author: jeecg-boot
 * @Date: 2020-12-03
 * @Version: V1.0
 */
@Api(tags = "库存信息")
@RestController
@RequestMapping("/sm/inventory")
@Slf4j
public class InventoryController extends BaseController<Inventory, InventoryService> {

    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private InventoryLockService inventoryLockService;
    @Autowired
    private MatService matService;


    @Override
    @AutoLog("分页列表查询")
    @ApiOperation(
            value = "分页列表查询",
            notes = "分页列表查询"
    )
    @GetMapping({"/list"})
    public Result<?> queryPageList(Inventory param,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        //系统默认按照createTime降序排，客户要求按照名称 厚，宽，长进行排序，所以修改默认排序列
        Map<String, String[]> parameterMap = new HashMap<>();
        req.getParameterMap().forEach(parameterMap::put);
        parameterMap.put("column", new String[]{"prodCname"});
        QueryWrapper<Inventory> queryWrapper = QueryGenerator.initQueryWrapper(param, parameterMap);

        String tenantId = TenantContext.getTenant();
        if ("2".equalsIgnoreCase(tenantId) || "11".equalsIgnoreCase(tenantId)) {
            queryWrapper.lambda()
                    .gt(Inventory::getAvailableWeight,0)
                    .ge(Inventory::getAvailableQty,0)
                    .or()
                    .gt(Inventory::getLockQty, 0)
                    .eq(Inventory::getMatNum, 0)
                    .eq(Inventory::getAvailableQty, 0)
                    .orderByAsc(Inventory::getMatThick, Inventory::getMatWidth, Inventory::getMatLen)
                    .orderByDesc(Inventory::getAvailableWeight);
        } else {
            queryWrapper.lambda()
                    .gt(Inventory::getMatNum, 0)
                    .orderByAsc(Inventory::getMatThick, Inventory::getMatWidth, Inventory::getMatLen);
        }

        if (StrUtil.isNotBlank(req.getParameter("matNos"))) {
            String matNos = req.getParameter("matNos");
            String[] str = matNos.split("\n");
            queryWrapper.lambda().in(Inventory::getMatNo, str);
        }
        if (StrUtil.isNotBlank(req.getParameter("stockLocations"))) {
            String matNos = req.getParameter("stockLocations");
            String[] str = matNos.split("\n");
            queryWrapper.lambda().in(Inventory::getStockLocation, str);
        }

        if ("12".equalsIgnoreCase(tenantId)) {
            queryWrapper.lambda().gt(Inventory::getAvailableWeight, 0);
        }

        Page<Inventory> page = new Page((long) pageNo, (long) pageSize);
        IPage<Inventory> pageList = inventoryService.page(page, queryWrapper);
        List<Inventory> records = pageList.getRecords();
        //上海：获取柳钢装车时间，计算在库时长
        for(Inventory inventory:records){
            Date carLodingDate = matService.selectCarLodingTime(inventory.getId());
            if (carLodingDate == null) {
                carLodingDate = new Date();
            }
            Date currentDate = new Date();
            long longInStockDays = DateUtil.between(carLodingDate, currentDate, DateUnit.DAY);
            Integer inStockDays = new Integer((int) longInStockDays);
            inventory.setInStockDays(inStockDays);
        }
        pageList.setRecords(records);
        return Result.OK(pageList);
    }

    /**
     * 获取对象ID
     *
     * @return
     */
    public String getId(Inventory item) {
        try {
            return PropertyUtils.getProperty(item, "id").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 导出excel
     *
     * @param request
     */
    @AutoLog(value = "导出")
    @ApiOperation(value = "导出", notes = "导出")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Inventory inventory) {
        // Step.1 组装查询条件
        QueryWrapper<Inventory> queryWrapper = QueryGenerator.initQueryWrapper(inventory, request.getParameterMap());
        queryWrapper.lambda()
                .ne(Inventory::getAvailableWeight, 0d)
                .ne(Inventory::getAvailableQty, 0d)
                .ne(Inventory::getMatNum, 0d);
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // Step.2 获取导出数据
        List<Inventory> pageList = inventoryService.list(queryWrapper);
        List<Inventory> exportList = null;

        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            exportList = pageList.stream().filter(item -> selectionList.contains(getId(item))).collect(Collectors.toList());
        } else {
            exportList = pageList;
        }

        // Step.3 AutoPoi 导出Excel
        String title = "库存物料信息表";
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, title); //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.CLASS, Inventory.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams(title, "导出人:" + sysUser.getRealname(), title));
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }

    /**
     * 在仓货物调拨
     */
    @AutoLog(value = "调拨")
    @ApiOperation(value = "调拨", notes = "调拨")
    @RequestMapping(value = "/allot")
    public Result<?> Allot(@RequestBody InventoryAllotDTO inventoryAllotDTO) {
        if (inventoryService.allotStock(inventoryAllotDTO)) {
            return Result.ok("调拨成功");
        } else return Result.error("调拨失败");


    }

    /**
     * 现货自动分配
     */
    @AutoLog(value = "广东分公司自动分货")
    @ApiOperation(value = "广东分公司自动分货", notes = "广东分公司自动分货")
    @RequestMapping(value = "/autoPrepareGoods")
    public Result<?> autoPrepareGoods() {
        inventoryService.autoPrepareGoods();
        return Result.ok("分货成功");

    }

    /**
     * 修改分货
     * @param inventoryIds
     * @param customerId
     * @return
     */
    @RequestMapping(value = "/updatePrepareCustomer")
    public Result<?> updatePrepareCustomer(@RequestParam(name = "inventoryIds") String inventoryIds,
                                           @RequestParam(name = "customerId") String customerId) {
        inventoryService.updatePrepareCustomer(inventoryIds, customerId);
        return Result.ok("分货成功");

    }


}
