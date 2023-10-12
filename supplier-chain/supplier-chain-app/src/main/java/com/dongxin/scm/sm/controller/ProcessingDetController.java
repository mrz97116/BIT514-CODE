package com.dongxin.scm.sm.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.sm.entity.ProcessingDet;
import com.dongxin.scm.sm.service.ProcessingDetService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: 加工详情表
 * @Author: jeecg-boot
 * @Date:   2021-04-19
 * @Version: V1.0
 */
@Api(tags="加工详情表")
@RestController
@RequestMapping("/sm/processingDet")
@Slf4j
public class ProcessingDetController extends BaseController<ProcessingDet, ProcessingDetService> {

}
