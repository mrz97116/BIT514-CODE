package com.dongxin.scm.sm.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.sm.entity.UnsettleSummary;
import com.dongxin.scm.sm.service.UnsettleSummaryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: 客户月度未结算量汇总统计
 * @Author: jeecg-boot
 * @Date:   2021-10-14
 * @Version: V1.0
 */
@Api(tags="客户月度未结算量汇总统计")
@RestController
@RequestMapping("/sm/unsettleSummary")
@Slf4j
public class UnsettleSummaryController extends BaseController<UnsettleSummary, UnsettleSummaryService> {

}
