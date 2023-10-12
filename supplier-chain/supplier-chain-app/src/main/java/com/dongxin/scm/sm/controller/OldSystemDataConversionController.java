package com.dongxin.scm.sm.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.sm.entity.OldSystemDataConversion;
import com.dongxin.scm.sm.service.OldSystemDataConversionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: 旧系统数据转换
 * @Author: jeecg-boot
 * @Date:   2021-03-04
 * @Version: V1.0
 */
@Api(tags="旧系统数据转换")
@RestController
@RequestMapping("/sm/oldSystemDataConversion")
@Slf4j
public class OldSystemDataConversionController extends BaseController<OldSystemDataConversion, OldSystemDataConversionService> {

}
