package com.dongxin.scm.wms.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.wms.entity.OrderDetMappingProdcode;
import com.dongxin.scm.wms.service.OrderDetMappingProdcodeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: 提单明细物流园产品关系映射表
 * @Author: jeecg-boot
 * @Date:   2021-11-09
 * @Version: V1.0
 */
@Api(tags="提单明细物流园产品关系映射表")
@RestController
@RequestMapping("/wms/orderDetMappingProdcode")
@Slf4j
public class OrderDetMappingProdcodeController extends BaseController<OrderDetMappingProdcode, OrderDetMappingProdcodeService> {

}
