package com.dongxin.scm.sm.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.sm.entity.InventoryTiming;
import com.dongxin.scm.sm.service.InventoryTimingService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: 库存定时表
 * @Author: jeecg-boot
 * @Date:   2021-05-29
 * @Version: V1.0
 */
@Api(tags="库存定时表")
@RestController
@RequestMapping("/sm/inventoryTiming")
@Slf4j
public class InventoryTimingController extends BaseController<InventoryTiming, InventoryTimingService> {

}
