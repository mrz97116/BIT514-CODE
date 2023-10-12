package com.dongxin.scm.bd.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.bd.entity.TenantReport;
import com.dongxin.scm.bd.entity.TenantReportDet;
import com.dongxin.scm.bd.service.TenantReportDetService;
import com.dongxin.scm.bd.service.TenantReportService;
import com.dongxin.scm.bd.vo.TenantReportPage;
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
 * @Description: 公司报表路径
 * @Author: jeecg-boot
 * @Date: 2021-01-27
 * @Version: V1.0
 */
@Api(tags = "公司报表路径")
@RestController
@RequestMapping("/bd/tenantReport")
@Slf4j
public class TenantReportController {
    @Autowired
    private TenantReportService tenantReportService;
    @Autowired
    private TenantReportDetService tenantReportDetService;

    /**
     * 查询公司报表路径
     */
    @GetMapping(value = "/queryCompanyReportName")
    public Result<?> queryCompanyReportName(Integer tenantId) {
        String urlName = tenantReportService.queryCompanyReportName(tenantId);
        return Result.ok(urlName);
    }

    /**
     * 分页列表查询
     *
     * @param tenantReport
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "公司报表路径-分页列表查询")
    @ApiOperation(value = "公司报表路径-分页列表查询", notes = "公司报表路径-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(TenantReport tenantReport,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<TenantReport> queryWrapper = QueryGenerator.initQueryWrapper(tenantReport, req.getParameterMap());
        Page<TenantReport> page = new Page<TenantReport>(pageNo, pageSize);
        IPage<TenantReport> pageList = tenantReportService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param tenantReportPage
     * @return
     */
    @AutoLog(value = "公司报表路径-添加")
    @ApiOperation(value = "公司报表路径-添加", notes = "公司报表路径-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody TenantReportPage tenantReportPage) {
        TenantReport tenantReport = new TenantReport();
        BeanUtils.copyProperties(tenantReportPage, tenantReport);
        tenantReportService.saveMain(tenantReport, tenantReportPage.getTenantReportDetList());
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param tenantReportPage
     * @return
     */
    @AutoLog(value = "公司报表路径-编辑")
    @ApiOperation(value = "公司报表路径-编辑", notes = "公司报表路径-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody TenantReportPage tenantReportPage) {
        TenantReport tenantReport = new TenantReport();
        BeanUtils.copyProperties(tenantReportPage, tenantReport);
        TenantReport tenantReportEntity = tenantReportService.getById(tenantReport.getId());
        if (tenantReportEntity == null) {
            return Result.error("未找到对应数据");
        }
        tenantReportService.updateMain(tenantReport, tenantReportPage.getTenantReportDetList());
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "公司报表路径-通过id删除")
    @ApiOperation(value = "公司报表路径-通过id删除", notes = "公司报表路径-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        tenantReportService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "公司报表路径-批量删除")
    @ApiOperation(value = "公司报表路径-批量删除", notes = "公司报表路径-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.tenantReportService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "公司报表路径-通过id查询")
    @ApiOperation(value = "公司报表路径-通过id查询", notes = "公司报表路径-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        TenantReport tenantReport = tenantReportService.getById(id);
        if (tenantReport == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(tenantReport);

    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "公司租户id通过主表ID查询")
    @ApiOperation(value = "公司租户id主表ID查询", notes = "公司租户id-通主表ID查询")
    @GetMapping(value = "/queryTenantReportDetByMainId")
    public Result<?> queryTenantReportDetListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<TenantReportDet> tenantReportDetList = tenantReportDetService.selectByMainId(id);
        return Result.OK(tenantReportDetList);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param tenantReport
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TenantReport tenantReport) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<TenantReport> queryWrapper = QueryGenerator.initQueryWrapper(tenantReport, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //Step.2 获取导出数据
        List<TenantReport> queryList = tenantReportService.list(queryWrapper);
        // 过滤选中数据
        String selections = request.getParameter("selections");
        List<TenantReport> tenantReportList = new ArrayList<TenantReport>();
        if (oConvertUtils.isEmpty(selections)) {
            tenantReportList = queryList;
        } else {
            List<String> selectionList = Arrays.asList(selections.split(","));
            tenantReportList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        }

        // Step.3 组装pageList
        List<TenantReportPage> pageList = new ArrayList<TenantReportPage>();
        for (TenantReport main : tenantReportList) {
            TenantReportPage vo = new TenantReportPage();
            BeanUtils.copyProperties(main, vo);
            List<TenantReportDet> tenantReportDetList = tenantReportDetService.selectByMainId(main.getId());
            vo.setTenantReportDetList(tenantReportDetList);
            pageList.add(vo);
        }

        // Step.4 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "公司报表路径列表");
        mv.addObject(NormalExcelConstants.CLASS, TenantReportPage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("公司报表路径数据", "导出人:" + sysUser.getRealname(), "公司报表路径"));
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
                List<TenantReportPage> list = ExcelImportUtil.importExcel(file.getInputStream(), TenantReportPage.class, params);
                for (TenantReportPage page : list) {
                    TenantReport po = new TenantReport();
                    BeanUtils.copyProperties(page, po);
                    tenantReportService.saveMain(po, page.getTenantReportDetList());
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
