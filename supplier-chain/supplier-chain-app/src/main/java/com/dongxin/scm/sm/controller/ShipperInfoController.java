package com.dongxin.scm.sm.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.sm.entity.ShipperInfo;
import com.dongxin.scm.sm.service.ShipperInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: 装货人信息
 * @Author: jeecg-boot
 * @Date:   2021-05-12
 * @Version: V1.0
 */
@Api(tags="装货人信息")
@RestController
@RequestMapping("/sm/shipperInfo")
@Slf4j
public class ShipperInfoController extends BaseController<ShipperInfo, ShipperInfoService> {

}
