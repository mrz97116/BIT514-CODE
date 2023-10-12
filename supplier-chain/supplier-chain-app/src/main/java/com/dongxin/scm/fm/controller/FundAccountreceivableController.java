package com.dongxin.scm.fm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.cm.service.CustomerProfileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.fm.entity.FundAccountreceivable;
import com.dongxin.scm.fm.service.FundAccountreceivableService;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 应收账款
 * @Author: jeecg-boot
 * @Date: 2021-06-09
 * @Version: V1.0
 */
@Api(tags = "应收账款")
@RestController
@RequestMapping("/fm/fundAccountreceivable")
@Slf4j
public class FundAccountreceivableController extends BaseController<FundAccountreceivable, FundAccountreceivableService> {
    @Autowired
    FundAccountreceivableService service;
    @Autowired
    CustomerProfileService customerProfileService;

    @AutoLog(value = "分页列表查询")
    @ApiOperation(value = "分页列表查询", notes = "分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(FundAccountreceivable param,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<FundAccountreceivable> queryWrapper = QueryGenerator.initQueryWrapper(param, req.getParameterMap());
        Page<FundAccountreceivable> page = new Page<FundAccountreceivable>(pageNo, pageSize);
        IPage<FundAccountreceivable> pageList = service.page(page, queryWrapper);

        List<String> customerIds = newArrayList();
        for (FundAccountreceivable record : pageList.getRecords()) {
            customerIds.add(record.getCustomerId());
        }

        Map<String, String> customerName = customerProfileService.getNameMap(customerIds);
        //1.收集所有的顾客id，去customerProfileService中查询，生成一个map，其中key为customerProfile的id，value为customerProfile的name
        for (FundAccountreceivable record : pageList.getRecords()) {
            //2. 对record的 customerNameText进行赋值，通过map对customerNameText进行赋值
            record.setCustomerId(customerName.get(record.getCustomerId()));
        }
        return Result.OK(pageList);
    }
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FundAccountreceivable fundAccountreceivable) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<FundAccountreceivable> queryWrapper = QueryGenerator.initQueryWrapper(fundAccountreceivable, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //Step.2 获取导出数据
        List<FundAccountreceivable> queryList = service.list(queryWrapper);
        // 过滤选中数据
        String selections = request.getParameter("selections");
        List<FundAccountreceivable> fundAccountreceivableList = null;
        if (oConvertUtils.isEmpty(selections)) {
            fundAccountreceivableList = queryList;
        } else {
            List<String> selectionList = Arrays.asList(selections.split(","));
            fundAccountreceivableList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        }

        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "应收账款列表");
        mv.addObject(NormalExcelConstants.CLASS, FundAccountreceivable.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("应收账款数据", "导出人:" + sysUser.getRealname(), "应收账款"));
        mv.addObject(NormalExcelConstants.DATA_LIST, fundAccountreceivableList);
        return mv;
    }
}
