package com.dongxin.scm.om.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.om.entity.Order;
import com.dongxin.scm.om.entity.OrderDet;
import com.dongxin.scm.om.entity.Provision;
import com.dongxin.scm.om.service.OrderDetService;
import com.dongxin.scm.om.service.OrderService;
import com.dongxin.scm.om.service.ProvisionService;
import com.dongxin.scm.om.vo.OrderPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 订单主表
 * @Author: jeecg-boot
 * @Date: 2020-11-13
 * @Version: V1.0
 */
@Api(tags = "订单主表")
@RestController
@RequestMapping("/om/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetService orderDetService;
    @Autowired
    private ProvisionService provisionService;
    @Autowired
    private CustomerProfileService customerProfileService;

    /**
     * 分页列表查询
     *
     * @param order
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "订单主表-分页列表查询")
    @ApiOperation(value = "订单主表-分页列表查询", notes = "订单主表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(Order order,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<Order> queryWrapper = QueryGenerator.initQueryWrapper(order, req.getParameterMap());
        Page<Order> page = new Page<Order>(pageNo, pageSize);
        IPage<Order> pageList = orderService.page(page, queryWrapper);
        List<String> customerIdList = new ArrayList<>();

        for (Order record : pageList.getRecords()) {
            customerIdList.add(record.getCustomerId());
        }

        Map<String, String> idAndName = customerProfileService.getNameMap(customerIdList);
        for (Order record : pageList.getRecords()) {
            record.setCustomerId(idAndName.get(record.getCustomerId()));
        }
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param orderPage
     * @return
     */
    @AutoLog(value = "订单主表-添加")
    @ApiOperation(value = "订单主表-添加", notes = "订单主表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody OrderPage orderPage) {
        Order order = new Order();
        BeanUtils.copyProperties(orderPage, order);
        orderService.saveMain(order, orderPage.getOrderDetList(), orderPage.getProvisionList());
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param orderPage
     * @return
     */
    @AutoLog(value = "订单主表-编辑")
    @ApiOperation(value = "订单主表-编辑", notes = "订单主表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody OrderPage orderPage) {
        Order order = new Order();
        BeanUtils.copyProperties(orderPage, order);
        Order orderEntity = orderService.getById(order.getId());
        if (orderEntity == null) {
            return Result.error("未找到对应数据");
        }
        orderService.updateMain(order, orderPage.getOrderDetList(), orderPage.getProvisionList());
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "订单主表-通过id删除")
    @ApiOperation(value = "订单主表-通过id删除", notes = "订单主表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        orderService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "订单主表-批量删除")
    @ApiOperation(value = "订单主表-批量删除", notes = "订单主表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.orderService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "订单主表-通过id查询")
    @ApiOperation(value = "订单主表-通过id查询", notes = "订单主表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(order);

    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "订单明细表通过主表ID查询")
    @ApiOperation(value = "订单明细表主表ID查询", notes = "订单明细表-通主表ID查询")
    @GetMapping(value = "/queryOrderDetByMainId")
    public Result<?> queryOrderDetListByMainId(@RequestParam(name = "id", required = true) String id) {
        QueryWrapper<OrderDet> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OrderDet::getOrderMainId, id);
//       List<OrderDet> orderDetList = orderDetService.selectByMainId(id);
        Page<OrderDet> page = new Page<OrderDet>(1, 10);
        IPage<OrderDet> pageList = orderDetService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "商务条款通过主表ID查询")
    @ApiOperation(value = "商务条款主表ID查询", notes = "商务条款-通主表ID查询")
    @GetMapping(value = "/queryProvisionByMainId")
    public Result<?> queryProvisionListByMainId(@RequestParam(name = "id", required = true) String id) {
        QueryWrapper<Provision> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Provision::getParentId, id);
        Page<Provision> page = new Page<>(1, 10);
        IPage<Provision> pageList = provisionService.page(page, queryWrapper);
//       List<Provision> provisionList = provisionService.selectByMainId(id);
        return Result.OK(pageList);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param order
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Order order) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<Order> queryWrapper = QueryGenerator.initQueryWrapper(order, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //Step.2 获取导出数据
        List<Order> queryList = orderService.list(queryWrapper);
        // 过滤选中数据
        String selections = request.getParameter("selections");
        List<Order> orderList = new ArrayList<Order>();
        if (oConvertUtils.isEmpty(selections)) {
            orderList = queryList;
        } else {
            List<String> selectionList = Arrays.asList(selections.split(","));
            orderList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        }

        // Step.3 组装pageList
        List<OrderPage> pageList = new ArrayList<OrderPage>();
        for (Order main : orderList) {
            OrderPage vo = new OrderPage();
            BeanUtils.copyProperties(main, vo);
            List<OrderDet> orderDetList = orderDetService.selectByMainId(main.getId());
            vo.setOrderDetList(orderDetList);
            List<Provision> provisionList = provisionService.selectByMainId(main.getId());
            vo.setProvisionList(provisionList);
            pageList.add(vo);
        }

        // Step.4 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "订单主表列表");
        mv.addObject(NormalExcelConstants.CLASS, OrderPage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("订单主表数据", "导出人:" + sysUser.getRealname(), "订单主表"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(false);
            try {
                List<OrderPage> list = ExcelImportUtil.importExcel(file.getInputStream(), OrderPage.class, params);
                for (OrderPage page : list) {
                    Order po = new Order();
                    BeanUtils.copyProperties(page, po);
                    orderService.saveMain(po, page.getOrderDetList(), page.getProvisionList());
                }
                return Result.OK("文件导入成功！数据行数:" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.OK("文件导入失败！");
    }

}
