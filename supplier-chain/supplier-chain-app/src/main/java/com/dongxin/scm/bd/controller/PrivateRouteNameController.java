package com.dongxin.scm.bd.controller;

import com.dongxin.scm.bd.entity.PrivateRouteName;
import com.dongxin.scm.bd.service.PrivateRouteNameService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 铁路专用线
 * @Author: jeecg-boot
 * @Date: 2020-11-19
 * @Version: V1.0
 */
@Api(tags = "铁路专用线")
@RestController
@RequestMapping("/bd/privateRouteName")
@Slf4j
public class PrivateRouteNameController extends BaseController<PrivateRouteName, PrivateRouteNameService> {

}
