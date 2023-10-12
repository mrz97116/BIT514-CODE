package com.dongxin.scm.om.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.entity.SalesOrderDet;
import com.dongxin.scm.om.enums.WmsTypeEnum;
import com.dongxin.scm.om.service.SalesOrderDetService;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.om.util.Constants;
import com.dongxin.scm.om.util.TenantText;
import com.dongxin.scm.om.vo.CheckCarNoVO;
import com.dongxin.scm.om.vo.SalesOrderPage;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.vo.OptionVO;
import com.dongxin.scm.utils.ScmBeanUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.mybatis.TenantContext;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 销售单
 * @Author: jeecg-boot
 * @Date: 2020-11-23
 * @Version: V1.0
 */
@Api(tags = "销售单")
@RestController
@RequestMapping("/om/salesOrder")
@Slf4j
public class SalesOrderController {

    @Autowired
    private SalesOrderService salesOrderService;
    @Autowired
    private SalesOrderDetService salesOrderDetService;
    @Autowired
    private CustomerProfileService customerProfileService;

    /**
     * 分页列表查询
     *
     * @param salesOrder
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "销售单-分页列表查询")
    @ApiOperation(value = "销售单-分页列表查询", notes = "销售单-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(SalesOrder salesOrder,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<SalesOrder> queryWrapper = QueryGenerator.initQueryWrapper(salesOrder, req.getParameterMap());
        String notSalesMan = req.getParameter("notSalesMan"); //排除业务员
        String notCustomerId = req.getParameter("notCustomerId"); //排除顾客
        queryWrapper.lambda().ne(SalesOrder::getCustomerId,notCustomerId)
                .ne(SalesOrder::getSalesMan,notSalesMan);
        //updateBy liujiazhi 2021.04.29 根据提单明细的材料号查询
        //updateBy zhj 2021.05.27 根据提单明细的仓库、牌号、产品大类查询
        String matNo = req.getParameter("matNo");//材料号
        String stockHouseId = req.getParameter("stockHouseId"); //仓库
        String prodClassCode1 = req.getParameter("prodClassCode1"); //产品大类
        String sgSign = req.getParameter("sgSign"); //牌号
        QueryWrapper<SalesOrderDet> salesOrderQueryWrapper = new QueryWrapper<>();

        if (StrUtil.isNotBlank(stockHouseId) || StrUtil.isNotBlank(sgSign) || StrUtil.isNotBlank(prodClassCode1) || StrUtil.isNotBlank(matNo)) {
            if (StrUtil.isNotBlank(stockHouseId)) {
                salesOrderQueryWrapper.lambda().eq(SalesOrderDet::getStockId, stockHouseId);
            }
            if (StrUtil.isNotBlank(sgSign)) {
                salesOrderQueryWrapper.lambda().eq(SalesOrderDet::getSgSign, sgSign);
            }
            if (StrUtil.isNotBlank(prodClassCode1)) {
                salesOrderQueryWrapper.lambda().eq(SalesOrderDet::getProdClassCode, prodClassCode1);
            }
            if (StrUtil.isNotBlank(matNo)) {
                salesOrderQueryWrapper.lambda().eq(SalesOrderDet::getMatNo, matNo);
            }
            List<SalesOrderDet> salesOrderDetList = salesOrderDetService.list(salesOrderQueryWrapper);
            List<String> ids = salesOrderDetList.stream().map(SalesOrderDet::getParentId).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ids)) {
                queryWrapper.lambda().in(SalesOrder::getId, ids);
            } else {
                queryWrapper.lambda().eq(SalesOrder::getId, null);
            }
        }
        Page<SalesOrder> page = new Page<SalesOrder>(pageNo, pageSize);
        IPage<SalesOrder> pageList = salesOrderService.page(page, queryWrapper);
        salesOrderService.updatePageList(pageList);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param salesOrderPage
     * @return
     */
    @AutoLog(value = "销售单-添加")
    @ApiOperation(value = "销售单-添加", notes = "销售单-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody SalesOrderPage salesOrderPage) {

        SalesOrder salesOrder = new SalesOrder();
        ScmBeanUtils.copyProperties(salesOrderPage, salesOrder);

        //阳蕊明校验是否可开单
        if (Constants.YRM_TENANT_IDS.contains(TenantContext.getTenant())) {
            QueryWrapper<CustomerProfile> customerProfileQueryWrapper = new QueryWrapper<>();
            customerProfileQueryWrapper.lambda().eq(CustomerProfile::getId, salesOrder.getCustomerId());
            CustomerProfile customerProfile = customerProfileService.getOne(customerProfileQueryWrapper);
            if (customerProfile.getCanLadingBill().equalsIgnoreCase(YesNoEnum.NO.getCode())) {
                return Result.error("添加失败！顾客:”" + customerProfile.getCompanyName() + "“可开单状态为不可开单，请检查是否超出最大超期天数");
            }
        }

        salesOrderService.saveMain(salesOrder, salesOrderPage.getSalesOrderDetList(), salesOrder.generateFundIdList());
        return Result.OK("添加成功！");
    }

    /**
     * 提单生成装车单
     *
     * @param salesOrderPage 接收到的页面提单
     * @return
     */
    @AutoLog(value = "提单生成装车单")
    @ApiOperation(value = "提单生成装车单", notes = "提单生成装车单")
    @PutMapping(value = "/edit")
    public Result<?> addStackBySaleOrder(@RequestBody SalesOrderPage salesOrderPage) throws ParseException {
        SalesOrder salesOrder = new SalesOrder();
        BeanUtils.copyProperties(salesOrderPage, salesOrder);
        SalesOrder salesOrderEntity = salesOrderService.getById(salesOrder.getId());
        if (salesOrderEntity == null) {
            return Result.error("未找到对应数据");
        }
        salesOrderService.updateMain(salesOrder, salesOrderPage.getSalesOrderDetList());
        return Result.OK("编辑成功!");
    }

    /**
     * 提单生成装车单
     *
     * @param salesOrderPage
     * @return
     */
    @AutoLog(value = "提单修改")
    @ApiOperation(value = "提单修改", notes = "提单修改")
    @PostMapping(value = "/editSalesOrder")
    public Result<?> editSalesOrder(@RequestBody SalesOrderPage salesOrderPage) {
        SalesOrder salesOrder = new SalesOrder();
        BeanUtils.copyProperties(salesOrderPage, salesOrder);
        SalesOrder salesOrderEntity = salesOrderService.getById(salesOrder.getId());
        if (salesOrderEntity == null) {
            return Result.error("未找到对应数据");
        }
        salesOrderService.editSalesOrder(salesOrder, salesOrderPage.getSalesOrderDetList());
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "销售单-通过id删除")
    @ApiOperation(value = "销售单-通过id删除", notes = "销售单-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        salesOrderService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "提单-批量删除")
    @ApiOperation(value = "提单-批量删除", notes = "提单-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.salesOrderService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "销售单-通过id查询")
    @ApiOperation(value = "销售单-通过id查询", notes = "销售单-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        SalesOrder salesOrder = salesOrderService.getById(id);
        if (salesOrder == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(salesOrder);

    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "销售单明细表通过主表ID查询")
    @ApiOperation(value = "销售单明细表主表ID查询", notes = "销售单明细表-通主表ID查询")
    @GetMapping(value = "/querySalesOrderDetByMainId")
    public Result<?> querySalesOrderDetListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<SalesOrderDet> salesOrderDetList = salesOrderDetService.selectByMainId(id);
        if ("12".equalsIgnoreCase(TenantContext.getTenant())) {
            salesOrderDetList = salesOrderDetService.updateSalesOrderDetList(salesOrderDetList, id);

        }
        if ("2".equalsIgnoreCase(TenantContext.getTenant())) {
            salesOrderDetList = salesOrderDetService.replaceByWmsActual(salesOrderDetList, id);
        }
        return Result.OK(salesOrderDetList);
    }


    /**
     * 通过id查询展示
     *
     * @param id
     * @return
     */
    @AutoLog(value = "销售单明细表通过主表ID查询")
    @ApiOperation(value = "销售单明细表主表ID查询", notes = "销售单明细表-通主表ID查询")
    @GetMapping(value = "/showSalesOrderDetById")
    public Result<?> showSalesOrderDetById(@RequestParam(name = "id", required = true) String id) {
        QueryWrapper<SalesOrderDet> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SalesOrderDet::getParentId, id);
        List<SalesOrderDet> pageList = salesOrderDetService.list(queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "销售单明细表通过主表ID查询")
    @ApiOperation(value = "销售单明细表主表ID查询", notes = "销售单明细表-通主表ID查询")
    @GetMapping(value = "/querySalesOrderDetById")
    public Result<?> queryStackDetByMainId(@RequestParam(name = "id", required = true) String id,
                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<SalesOrderDet> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SalesOrderDet::getParentId, id);
        Page<SalesOrderDet> page = new Page<>(pageNo, pageSize);
        IPage<SalesOrderDet> pageList = salesOrderDetService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "提单明细表-通过id删除")
    @ApiOperation(value = "提单明细表-通过id删除", notes = "提单明细表-通过id删除")
    @DeleteMapping(value = "/deleteSalesOrderDet")
    public Result<?> deleteSalesOrderDet(@RequestParam(name = "id", required = true) String id) {
        salesOrderDetService.delStackDet(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "提单明细表-批量删除")
    @ApiOperation(value = "提单明细表-批量删除", notes = "提单明细表-批量删除")
    @DeleteMapping(value = "/deleteBatchSalesOrderDet")
    public Result<?> deleteBatchSalesOrderDet(@RequestParam(name = "ids", required = true) String ids) {
        salesOrderDetService.delBatchStackDet(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 导出excel
     *
     * @param request
     * @param salesOrder
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SalesOrder salesOrder) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<SalesOrder> queryWrapper = QueryGenerator.initQueryWrapper(salesOrder, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //Step.2 获取导出数据
        List<SalesOrder> queryList = salesOrderService.list(queryWrapper);
        // 过滤选中数据
        String selections = request.getParameter("selections");
        List<SalesOrder> salesOrderList = new ArrayList<SalesOrder>();
        if (oConvertUtils.isEmpty(selections)) {
            salesOrderList = queryList;
        } else {
            List<String> selectionList = Arrays.asList(selections.split(","));
            salesOrderList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        }

        // Step.3 组装pageList
        List<SalesOrderPage> pageList = new ArrayList<SalesOrderPage>();
        for (SalesOrder main : salesOrderList) {
            SalesOrderPage vo = new SalesOrderPage();
            BeanUtils.copyProperties(main, vo);
            List<SalesOrderDet> salesOrderDetList = salesOrderDetService.selectByMainId(main.getId());
            vo.setSalesOrderDetList(salesOrderDetList);
            pageList.add(vo);
        }

        // Step.4 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "销售单列表");
        mv.addObject(NormalExcelConstants.CLASS, SalesOrderPage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("销售单数据", "导出人:" + sysUser.getRealname(), "销售单"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    @GetMapping("/checkCarNo")
    public Result<?> checkCarNo(String id) {
        CheckCarNoVO result = salesOrderService.checkCarNo(id);
        return Result.OK(result);
    }

    //查询登录账号的配置
    @GetMapping("/queryParamsFromBackEnd")
    public Result<?> queryParamsFromBackEnd() {
        Map<String, Object> map = new HashMap<>();
        Boolean queryDeliverExpense = salesOrderDetService.queryDeliverExpense();
        Boolean queryAddPrice = salesOrderDetService.queryAddPrice();
        Boolean queryShowTheoryWeight = salesOrderDetService.queryIfShowTheoryWeight();
        Boolean queryShowTheoryPrice = salesOrderDetService.queryIfShowTheoryPrice();
        Boolean queryShowBasicPrice = salesOrderDetService.queryIfShowBasicPrice();
        Date date = new Date();
        map.put("deliverExpense", queryDeliverExpense);
        map.put("addPrice", queryAddPrice);
        map.put("theoryWeight", queryShowTheoryWeight);
        map.put("theoryPrice", queryShowTheoryPrice);
        map.put("basicPrice", queryShowBasicPrice);
        map.put("currentDate", date);
        return Result.OK(map);
    }

    @GetMapping("/selectPushDestination")
    public Result<?> selectPushDestination() {
        List<OptionVO> typeOptionVO = salesOrderService.selectPushDestination();
        return Result.OK(typeOptionVO);
    }

    @GetMapping("/selectPushCarNo")
    public Result<?> selectPushCarNo() {
        List<OptionVO> typeOptionVO = salesOrderService.selectPushCarNo();
        return Result.OK(typeOptionVO);
    }

    /**
     * 通过id查询
     *
     * @param goodEntrustmentLetterId
     * @return
     */
    @AutoLog(value = "材料表通过提货委托单主表ID查询")
    @ApiOperation(value = "材料表通过提货委托单主表ID查询", notes = "材料表通过提货委托单主表ID查询")
    @GetMapping(value = "/querySalesOrderDetByGoodEntrustmentLetterId")
    public Result<?> queryMatByGoodEntrustmentLetterId(@RequestParam(name = "id", required = true) String goodEntrustmentLetterId) {
        List<SalesOrderDet> salesOrderDetList = salesOrderDetService.selectByGoodEntrustmentLetterId(goodEntrustmentLetterId);
        return Result.OK(salesOrderDetList);
    }


    /**
     * 推送提单到物流园
     */
    @PostMapping("/waitPush")
    public Result<?> waitPush(@RequestBody List<String> salesOrderIdList) {
        salesOrderService.waitPush(salesOrderIdList);
        return Result.OK();
    }

    /**
     * 推送物流园提单取消推送
     */
    @PostMapping("/cancelLogistics")
    public Result<?> cancelLogistics(@RequestBody List<String> salesOrderIdList) {
        String type = WmsTypeEnum.CANCEL_PUSH_ORDER.getCode();
        salesOrderService.cancelLogistics(salesOrderIdList, type);
        return Result.OK();
    }

    /**
     * 推送物流园过户撤销推送
     */
    @PostMapping("/cancelTransfer")
    public Result<?> cancelTransfer(@RequestBody List<String> salesOrderIdList) {
        String type = WmsTypeEnum.CANCEL_PUSH_TRANSFER.getCode();
        salesOrderService.cancelLogistics(salesOrderIdList, type);
        return Result.OK();
    }

    /**
     * 立即推送物流园
     */
    @GetMapping("/pushLogisticsNow")
    public Result<?> pushLogisticsNow(@RequestParam(name = "id", required = true) String id) {
        salesOrderService.pushLogisticsNow(id);
        return Result.OK();
    }

    /**
     * 需要等待物流园开通接口.
     * 过户推送物流园
     */
     @GetMapping("/pushTransfer") public Result<?> pushTransfer(@RequestParam(name = "id", required = true) String id) {
     salesOrderService.pushTransfer(id);
     return Result.OK();
     }


    /**
     * 修改生成发票状态
     */
    @AutoLog(value = "修改生成发票状态")
    @ApiOperation(value = "修改生成发票状态", notes = "修改生成发票状态")
    @RequestMapping(value = "/editCreateInvoiceStatus")
    public Result<?> editCreateInvoiceStatus(@RequestParam(name = "ids", required = true) String ids,
                                             @RequestParam(name = "createInvoiceTime", required = true) String createInvoiceTime) {
        try {//转换日期
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date invoiceTime = null;
            salesOrderService.editCreateInvoiceStatus(ids, dateFormat.parse(createInvoiceTime));
            return Result.OK("修改成功！");

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("修改失败:" + e.getMessage());
        }

    }
    /**记录打印次数*/
    @GetMapping("/printNum")
    public Result<?> printNum(@RequestParam(name = "ids", required = true) String ids) {
        salesOrderService.printNum(ids);
        return Result.OK();
    }


}
