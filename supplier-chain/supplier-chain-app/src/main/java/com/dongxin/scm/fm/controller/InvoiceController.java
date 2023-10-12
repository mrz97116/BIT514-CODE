package com.dongxin.scm.fm.controller;

import com.baomidou.lock.LockInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.common.service.LockService;
import com.dongxin.scm.fm.entity.Invoice;
import com.dongxin.scm.fm.entity.InvoiceDetail;
import com.dongxin.scm.fm.entity.SettleInfo;
import com.dongxin.scm.fm.enums.InvoiceStatusEnum;
import com.dongxin.scm.fm.enums.StatementStateEnum;
import com.dongxin.scm.fm.service.InvoiceDetailService;
import com.dongxin.scm.fm.service.InvoiceService;
import com.dongxin.scm.fm.service.SettleInfoService;
import com.dongxin.scm.fm.vo.InvoicePage;
import com.dongxin.scm.om.util.Constants;
import com.dongxin.scm.sm.entity.Inventory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.mybatis.TenantContext;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 发票信息
 * @Author: jeecg-boot
 * @Date: 2021-02-01
 * @Version: V1.0
 */
@Api(tags = "发票信息")
@RestController
@RequestMapping("/fm/invoice")
@Slf4j
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private InvoiceDetailService invoiceDetailService;
    @Autowired
    private SettleInfoService settleInfoService;


    /**
     * 分页列表查询
     *
     * @param invoice
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "发票信息-分页列表查询")
    @ApiOperation(value = "发票信息-分页列表查询", notes = "发票信息-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(Invoice invoice,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {

        //系统默认按照createTime降序，客户要求按照结算单单号进行排序，所以修改排序列
        Map<String, String[]> parameterMap = new HashMap<>();
        req.getParameterMap().forEach(parameterMap::put);
        QueryWrapper<Invoice> invoiceQueryWrapper = QueryGenerator.initQueryWrapper(invoice, parameterMap);
//        if (TenantContext.getTenant().equals("2")) {
            invoiceQueryWrapper.lambda().orderByDesc(Invoice::getHdorderno);
//        }

//        QueryWrapper<Invoice> queryWrapper = QueryGenerator.initQueryWrapper(invoice, req.getParameterMap());
        Page<Invoice> page = new Page<Invoice>(pageNo, pageSize);
        IPage<Invoice> pageList = invoiceService.page(page, invoiceQueryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param invoicePage
     * @return
     */
    @AutoLog(value = "发票信息-添加")
    @ApiOperation(value = "发票信息-添加", notes = "发票信息-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody InvoicePage invoicePage) {
        Invoice invoice = new Invoice();
        BeanUtils.copyProperties(invoicePage, invoice);
        invoiceService.saveMain(invoice, invoicePage.getInvoiceDetailList());
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param invoicePage
     * @return
     */
    @AutoLog(value = "发票信息-编辑")
    @ApiOperation(value = "发票信息-编辑", notes = "发票信息-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody InvoicePage invoicePage) {
        Invoice invoice = new Invoice();
        BeanUtils.copyProperties(invoicePage, invoice);
        Invoice invoiceEntity = invoiceService.getById(invoice.getId());
        if (invoiceEntity == null) {
            return Result.error("未找到对应数据");
        }
        invoiceService.updateMain(invoice, invoicePage.getInvoiceDetailList());
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "发票信息-通过id删除")
    @ApiOperation(value = "发票信息-通过id删除", notes = "发票信息-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        invoiceService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "发票信息-批量删除")
    @ApiOperation(value = "发票信息-批量删除", notes = "发票信息-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.invoiceService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "发票信息-通过id查询")
    @ApiOperation(value = "发票信息-通过id查询", notes = "发票信息-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        Invoice invoice = invoiceService.getById(id);
        if (invoice == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(invoice);

    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "发票明细通过主表ID查询")
    @ApiOperation(value = "发票明细主表ID查询", notes = "发票明细-通主表ID查询")
    @GetMapping(value = "/queryInvoiceDetailByMainId")
    public Result<?> queryInvoiceDetailListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<InvoiceDetail> invoiceDetailList = invoiceDetailService.selectByMainId(id);
        return Result.OK(invoiceDetailList);
    }

    /**
     * 发票申请
     *
     * @param ids
     * @return
     */
    @PostMapping(value = "/invoiceApply")
    public Result<?> invoiceApply(@RequestBody List<String> ids) {

        invoiceService.invoiceApply(ids);
        return Result.OK("发票数据已生成！");

    }

    /**
     * 一键生成发票申请
     *
     * @return
     */
    @GetMapping(value = "/invoiceApplyForOne")
    public Result<?> invoiceApplyForOne() {

        invoiceService.invoiceApplyForOne();
        return Result.OK("发票数据已生成！");

    }

    @PostMapping(value = "/invoiceMerge")
    public Result<?> invoiceMerge(@RequestBody List<String> ids){
        invoiceService.invoiceMerge(ids);
        return Result.OK("发票数据已生成！");
    }

    /**
     * 导出excel
     *
     * @param request
     * @param invoice
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Invoice invoice) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<Invoice> queryWrapper = QueryGenerator.initQueryWrapper(invoice, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //Step.2 获取导出数据
        List<Invoice> queryList = invoiceService.list(queryWrapper);
        // 过滤选中数据
        String selections = request.getParameter("selections");
        List<Invoice> invoiceList = new ArrayList<Invoice>();
        if (oConvertUtils.isEmpty(selections)) {
            invoiceList = queryList;
        } else {
            List<String> selectionList = Arrays.asList(selections.split(","));
            invoiceList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        }

        // Step.3 组装pageList
        List<InvoicePage> pageList = new ArrayList<InvoicePage>();
        for (Invoice main : invoiceList) {
            InvoicePage vo = new InvoicePage();
            BeanUtils.copyProperties(main, vo);
            List<InvoiceDetail> invoiceDetailList = invoiceDetailService.selectByMainId(main.getId());
            vo.setInvoiceDetailList(invoiceDetailList);
            pageList.add(vo);
        }

        // Step.4 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "发票信息列表");
        mv.addObject(NormalExcelConstants.CLASS, InvoicePage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("发票信息数据", "导出人:" + sysUser.getRealname(), "发票信息"));
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
                List<InvoicePage> list = ExcelImportUtil.importExcel(file.getInputStream(), InvoicePage.class, params);
                for (InvoicePage page : list) {
                    Invoice po = new Invoice();
                    BeanUtils.copyProperties(page, po);
                    invoiceService.saveMain(po, page.getInvoiceDetailList());
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
