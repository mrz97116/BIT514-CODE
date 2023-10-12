package com.dongxin.scm.cm.controller;

import com.dongxin.scm.cm.entity.SalesmanInfo;
import com.dongxin.scm.cm.service.SalesmanInfoService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 业务员信息管理
 * @Author: jeecg-boot
 * @Date: 2020-11-30
 * @Version: V1.0
 */
@Api(tags = "业务员信息管理")
@RestController
@RequestMapping("/cm/salesmanInfo")
@Slf4j
public class SalesmanInfoController extends BaseController<SalesmanInfo, SalesmanInfoService> {

}
