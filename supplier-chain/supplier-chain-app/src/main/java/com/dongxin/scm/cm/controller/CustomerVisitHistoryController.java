package com.dongxin.scm.cm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.cm.entity.CustomerVisitHistory;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.cm.service.CustomerVisitHistoryService;
import com.dongxin.scm.cm.service.SalesmanInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 客户拜访记录表
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */
@Api(tags = "客户拜访记录表")
@RestController
@RequestMapping("/cm/customerVisitHistory")
@Slf4j
public class CustomerVisitHistoryController extends BaseController<CustomerVisitHistory, CustomerVisitHistoryService> {

    @Autowired
    private CustomerVisitHistoryService customerVisitHistoryService;

    @Autowired
    private CustomerProfileService customerProfileService;

    @Autowired
    private SalesmanInfoService salesmanInfoService;

    @AutoLog(value = "分页列表查询")
    @ApiOperation(value = "分页列表查询", notes = "分页列表查询")
    @GetMapping(value = "/list")
    @Override
    public Result<?> queryPageList(CustomerVisitHistory param,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<CustomerVisitHistory> queryWrapper = QueryGenerator.initQueryWrapper(param, req.getParameterMap());
        Page<CustomerVisitHistory> page = new Page<CustomerVisitHistory>(pageNo, pageSize);
        IPage<CustomerVisitHistory> pageList = customerVisitHistoryService.page(page, queryWrapper);

        List<String> customerIds = newArrayList();
        List<String> salesmanIds = newArrayList();
        for (CustomerVisitHistory record : pageList.getRecords()) {
            customerIds.add(record.getCustomerId());
            salesmanIds.add(record.getSalesmanName());
        }

        Map<String, String> customerName = customerProfileService.getNameMap(customerIds);
        Map<String, String> salesmanName = salesmanInfoService.getNameMap(salesmanIds);
        //1.收集所有的顾客id，去customerProfileService中查询，生成一个map，其中key为customerProfile的id，value为customerProfile的name
        for (CustomerVisitHistory record : pageList.getRecords()) {
            //2. 对record的 customerNameText进行赋值，通过map对customerNameText进行赋值
            record.setCustomerNameText(customerName.get(record.getCustomerId()));
            record.setSalesmanName(salesmanName.get(record.getSalesmanName()));
        }


        return Result.OK(pageList);
    }

}
