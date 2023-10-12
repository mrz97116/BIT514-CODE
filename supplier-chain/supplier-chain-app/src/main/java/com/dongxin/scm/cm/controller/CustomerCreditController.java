package com.dongxin.scm.cm.controller;

import com.dongxin.scm.cm.entity.CustomerCredit;
import com.dongxin.scm.cm.service.CustomerCreditService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 授信额度表
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */
@Api(tags = "授信额度表")
@RestController
@RequestMapping("/cm/customerCredit")
@Slf4j
public class CustomerCreditController extends BaseController<CustomerCredit, CustomerCreditService> {

}
