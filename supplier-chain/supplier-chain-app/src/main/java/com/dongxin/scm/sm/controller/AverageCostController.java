package com.dongxin.scm.sm.controller;

import com.dongxin.scm.sm.entity.AverageCost;
import com.dongxin.scm.sm.service.AverageCostService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: 每日平均成本
 * @Author: jeecg-boot
 * @Date:   2021-08-02
 * @Version: V1.0
 */
@Api(tags="每日平均成本")
@RestController
@RequestMapping("/sm/smAverage")
@Slf4j
public class AverageCostController extends BaseController<AverageCost, AverageCostService> {

}
