package com.dongxin.scm.bd.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.dongxin.scm.bd.vo.CompanyTenantVo;
import com.dongxin.scm.sm.vo.OptionVO;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import com.dongxin.scm.bd.entity.CompanyTenantDet;
import com.dongxin.scm.bd.entity.CompanyTenant;
import com.dongxin.scm.bd.vo.CompanyTenantPage;
import com.dongxin.scm.bd.service.CompanyTenantService;
import com.dongxin.scm.bd.service.CompanyTenantDetService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

/**
 * @Description: 公司租户表
 * @Author: jeecg-boot
 * @Date: 2021-02-26
 * @Version: V1.0
 */
@Api(tags = "公司租户表")
@RestController
@RequestMapping("/bd/companyTenant")
@Slf4j
public class CompanyTenantController {
    @Autowired
    private CompanyTenantService companyTenantService;
    @Autowired
    private CompanyTenantDetService companyTenantDetService;

    /**
     * 分页列表查询
     *
     * @param companyTenant
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "公司租户表-分页列表查询")
    @ApiOperation(value = "公司租户表-分页列表查询", notes = "公司租户表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(CompanyTenant companyTenant,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        List<CompanyTenantVo> companyTenantVoList = companyTenantService.select();
        Page<CompanyTenantVo> page = new Page<CompanyTenantVo>(pageNo, pageSize);
        IPage<CompanyTenantVo> pageList = page.setRecords(companyTenantVoList);
        companyTenantService.updateCompanyTenant(pageList);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param companyTenantVo
     * @return
     */
    @AutoLog(value = "公司租户表-添加")
    @ApiOperation(value = "公司租户表-添加", notes = "公司租户表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody CompanyTenantVo companyTenantVo) {
        CompanyTenant companyTenant = new CompanyTenant();
        BeanUtils.copyProperties(companyTenantVo, companyTenant);
        companyTenantService.saveMain(companyTenant, companyTenantVo);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param companyTenantVo
     * @return
     */
    @AutoLog(value = "公司租户表-编辑")
    @ApiOperation(value = "公司租户表-编辑", notes = "公司租户表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody CompanyTenantVo companyTenantVo) {
        CompanyTenant companyTenant = new CompanyTenant();
        BeanUtils.copyProperties(companyTenantVo, companyTenant);
        CompanyTenant companyTenantEntity = companyTenantService.getById(companyTenant.getId());
        if (companyTenantEntity == null) {
            return Result.error("未找到对应数据");
        }
        companyTenantService.updateMain(companyTenant, companyTenantVo);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "公司租户表-通过id删除")
    @ApiOperation(value = "公司租户表-通过id删除", notes = "公司租户表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        companyTenantService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "公司租户表-批量删除")
    @ApiOperation(value = "公司租户表-批量删除", notes = "公司租户表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.companyTenantService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "公司租户表-通过id查询")
    @ApiOperation(value = "公司租户表-通过id查询", notes = "公司租户表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        CompanyTenant companyTenant = companyTenantService.getById(id);
        if (companyTenant == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(companyTenant);

    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "公司租户明细表通过主表ID查询")
    @ApiOperation(value = "公司租户明细表主表ID查询", notes = "公司租户明细表-通主表ID查询")
    @GetMapping(value = "/queryCompanyTenantDetByMainId")
    public Result<?> queryCompanyTenantDetListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<CompanyTenantDet> companyTenantDetList = companyTenantDetService.selectByMainId(id);
        return Result.OK(companyTenantDetList);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param companyTenant
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, CompanyTenant companyTenant) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<CompanyTenant> queryWrapper = QueryGenerator.initQueryWrapper(companyTenant, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //Step.2 获取导出数据
        List<CompanyTenant> queryList = companyTenantService.list(queryWrapper);
        // 过滤选中数据
        String selections = request.getParameter("selections");
        List<CompanyTenant> companyTenantList = new ArrayList<CompanyTenant>();
        if (oConvertUtils.isEmpty(selections)) {
            companyTenantList = queryList;
        } else {
            List<String> selectionList = Arrays.asList(selections.split(","));
            companyTenantList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        }

        // Step.3 组装pageList
        List<CompanyTenantPage> pageList = new ArrayList<CompanyTenantPage>();
        for (CompanyTenant main : companyTenantList) {
            CompanyTenantPage vo = new CompanyTenantPage();
            BeanUtils.copyProperties(main, vo);
            List<CompanyTenantDet> companyTenantDetList = companyTenantDetService.selectByMainId(main.getId());
            vo.setCompanyTenantDetList(companyTenantDetList);
            pageList.add(vo);
        }

        // Step.4 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "公司租户表列表");
        mv.addObject(NormalExcelConstants.CLASS, CompanyTenantPage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("公司租户表数据", "导出人:" + sysUser.getRealname(), "公司租户表"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }


    @GetMapping(value = "/selectSalesDetTheoryWeight")
    public Result<?> selectSalesDetTheoryWeight() {
        List<OptionVO> typeOptionVO = companyTenantService.selectAutomaticallyAdjustWeightAndQuantity();
        return Result.OK(typeOptionVO);
    }

}
