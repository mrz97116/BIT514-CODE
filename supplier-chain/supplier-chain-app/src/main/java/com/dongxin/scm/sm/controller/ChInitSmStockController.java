package com.dongxin.scm.sm.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.sm.entity.ChInitSmStock;
import com.dongxin.scm.sm.service.ChInitSmStockService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: 岑海库存表
 * @Author: jeecg-boot
 * @Date:   2021-03-09
 * @Version: V1.0
 */
@Api(tags="岑海库存表")
@RestController
@RequestMapping("/sm/chInitSmStock")
@Slf4j
public class ChInitSmStockController extends BaseController<ChInitSmStock, ChInitSmStockService> {

}
