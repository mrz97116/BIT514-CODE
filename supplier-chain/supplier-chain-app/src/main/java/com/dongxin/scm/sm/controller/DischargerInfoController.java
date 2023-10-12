package com.dongxin.scm.sm.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.sm.entity.DischargerInfo;
import com.dongxin.scm.sm.service.DischargerInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: 卸货人信息
 * @Author: jeecg-boot
 * @Date:   2021-05-12
 * @Version: V1.0
 */
@Api(tags="卸货人信息")
@RestController
@RequestMapping("/sm/dischargerInfo")
@Slf4j
public class DischargerInfoController extends BaseController<DischargerInfo, DischargerInfoService> {

}
