package com.dongxin.scm.om.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.om.entity.CompanyInfo;
import com.dongxin.scm.om.entity.CompanyInfoBank;
import com.dongxin.scm.om.service.CompanyInfoBankService;
import com.dongxin.scm.om.service.CompanyInfoService;
import com.dongxin.scm.om.vo.CompanyInfoPage;
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
 * @Description: 公司信息
 * @Author: jeecg-boot
 * @Date: 2020-12-14
 * @Version: V1.0
 */
@Api(tags = "公司信息")
@RestController
@RequestMapping("/om/companyInfo")
@Slf4j
public class CompanyInfoController {
    @Autowired
    private CompanyInfoService companyInfoService;
    @Autowired
    private CompanyInfoBankService companyInfoBankService;

    /**
     * 分页列表查询
     *
     * @param companyInfo
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "公司信息-分页列表查询")
    @ApiOperation(value = "公司信息-分页列表查询", notes = "公司信息-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(CompanyInfo companyInfo,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<CompanyInfo> queryWrapper = QueryGenerator.initQueryWrapper(companyInfo, req.getParameterMap());
        Page<CompanyInfo> page = new Page<CompanyInfo>(pageNo, pageSize);
        IPage<CompanyInfo> pageList = companyInfoService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param companyInfoPage
     * @return
     */
    @AutoLog(value = "公司信息-添加")
    @ApiOperation(value = "公司信息-添加", notes = "公司信息-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody CompanyInfoPage companyInfoPage) {
        CompanyInfo companyInfo = new CompanyInfo();
        BeanUtils.copyProperties(companyInfoPage, companyInfo);
        companyInfoService.saveMain(companyInfo, companyInfoPage.getCompanyInfoBankList());
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param companyInfoPage
     * @return
     */
    @AutoLog(value = "公司信息-编辑")
    @ApiOperation(value = "公司信息-编辑", notes = "公司信息-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody CompanyInfoPage companyInfoPage) {
        CompanyInfo companyInfo = new CompanyInfo();
        BeanUtils.copyProperties(companyInfoPage, companyInfo);
        CompanyInfo companyInfoEntity = companyInfoService.getById(companyInfo.getId());
        if (companyInfoEntity == null) {
            return Result.error("未找到对应数据");
        }
        companyInfoService.updateMain(companyInfo, companyInfoPage.getCompanyInfoBankList());
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "公司信息-通过id删除")
    @ApiOperation(value = "公司信息-通过id删除", notes = "公司信息-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        companyInfoService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "公司信息-批量删除")
    @ApiOperation(value = "公司信息-批量删除", notes = "公司信息-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.companyInfoService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "公司信息-通过id查询")
    @ApiOperation(value = "公司信息-通过id查询", notes = "公司信息-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        CompanyInfo companyInfo = companyInfoService.getById(id);
        if (companyInfo == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(companyInfo);

    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "公司银行卡信息通过主表ID查询")
    @ApiOperation(value = "公司银行卡信息主表ID查询", notes = "公司银行卡信息-通主表ID查询")
    @GetMapping(value = "/queryCompanyInfoBankByMainId")
    public Result<?> queryCompanyInfoBankListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<CompanyInfoBank> companyInfoBankList = companyInfoBankService.selectByMainId(id);
        return Result.OK(companyInfoBankList);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param companyInfo
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CompanyInfo companyInfo) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<CompanyInfo> queryWrapper = QueryGenerator.initQueryWrapper(companyInfo, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //Step.2 获取导出数据
        List<CompanyInfo> queryList = companyInfoService.list(queryWrapper);
        // 过滤选中数据
        String selections = request.getParameter("selections");
        List<CompanyInfo> companyInfoList = new ArrayList<CompanyInfo>();
        if (oConvertUtils.isEmpty(selections)) {
            companyInfoList = queryList;
        } else {
            List<String> selectionList = Arrays.asList(selections.split(","));
            companyInfoList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        }

        // Step.3 组装pageList
        List<CompanyInfoPage> pageList = new ArrayList<CompanyInfoPage>();
        for (CompanyInfo main : companyInfoList) {
            CompanyInfoPage vo = new CompanyInfoPage();
            BeanUtils.copyProperties(main, vo);
            List<CompanyInfoBank> companyInfoBankList = companyInfoBankService.selectByMainId(main.getId());
            vo.setCompanyInfoBankList(companyInfoBankList);
            pageList.add(vo);
        }

        // Step.4 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "公司信息列表");
        mv.addObject(NormalExcelConstants.CLASS, CompanyInfoPage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("公司信息数据", "导出人:" + sysUser.getRealname(), "公司信息"));
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
                List<CompanyInfoPage> list = ExcelImportUtil.importExcel(file.getInputStream(), CompanyInfoPage.class, params);
                for (CompanyInfoPage page : list) {
                    CompanyInfo po = new CompanyInfo();
                    BeanUtils.copyProperties(page, po);
                    companyInfoService.saveMain(po, page.getCompanyInfoBankList());
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
