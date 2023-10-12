package com.dongxin.scm.fm.controller;

import com.dongxin.scm.fm.entity.CustomerFoundSummary;
import com.dongxin.scm.fm.service.CustomerFoundSummaryService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: 客户资金情况汇总表
 * @Author: jeecg-boot
 * @Date:   2021-04-06
 * @Version: V1.0
 */
@Api(tags="客户资金情况汇总表")
@RestController
@RequestMapping("/fm/customerFoundSummary")
@Slf4j
public class CustomerFoundSummaryController extends BaseController<CustomerFoundSummary, CustomerFoundSummaryService> {

}
