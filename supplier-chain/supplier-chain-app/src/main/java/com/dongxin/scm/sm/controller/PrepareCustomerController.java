package com.dongxin.scm.sm.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.sm.entity.PrepareCustomer;
import com.dongxin.scm.sm.service.PrepareCustomerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: 分货客户
 * @Author: jeecg-boot
 * @Date:   2021-12-08
 * @Version: V1.0
 */
@Api(tags="分货客户")
@RestController
@RequestMapping("/sm/prepareCustomer")
@Slf4j
public class PrepareCustomerController extends BaseController<PrepareCustomer, PrepareCustomerService> {

}
