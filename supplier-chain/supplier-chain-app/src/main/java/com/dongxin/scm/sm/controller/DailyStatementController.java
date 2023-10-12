package com.dongxin.scm.sm.controller;

import com.dongxin.scm.sm.entity.DailyStatement;
import com.dongxin.scm.sm.service.DailyStatementService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 日报表
 * @Author: jeecg-boot
 * @Date: 2020-12-28
 * @Version: V1.0
 */
@Api(tags = "日报表")
@RestController
@RequestMapping("/sm/dailyStatement")
@Slf4j
public class DailyStatementController extends BaseController<DailyStatement, DailyStatementService> {

}
