package com.dongxin.scm.cm.controller;

import com.dongxin.scm.cm.entity.CustomerConfiguration;
import com.dongxin.scm.cm.service.CustomerConfigurationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import org.jeecg.config.mybatis.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 顾客合同控量配置表
 * @Author: jeecg-boot
 * @Date: 2021-01-04
 * @Version: V1.0
 */
@Api(tags = "顾客合同控量配置表")
@RestController
@RequestMapping("/cm/customerConfiguration")
@Slf4j
public class CustomerConfigurationController extends BaseController<CustomerConfiguration, CustomerConfigurationService> {

    @Autowired
    private CustomerConfigurationService customerConfigurationService;

    @AutoLog(value = "合同控量-添加")
    @ApiOperation(value = "合同控量-添加", notes = "合同控量-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody CustomerConfiguration customerConfiguration) {

        customerConfigurationService.save(customerConfiguration);
        return Result.ok("添加成功！");
    }
    //判断是否装车单合同控量
    @GetMapping(value = "/queryWhichContractControl")
    public Result<?> queryWhichContractControl(String tenantId){
        //获取登录租户Id
        return Result.ok(customerConfigurationService.queryWhichContractControl(tenantId));
    }
}
