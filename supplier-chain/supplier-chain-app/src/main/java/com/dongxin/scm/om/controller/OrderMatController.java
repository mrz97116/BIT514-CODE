package com.dongxin.scm.om.controller;

import com.dongxin.scm.om.entity.OrderMat;
import com.dongxin.scm.om.service.OrderMatService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 订单物料信息表
 * @Author: jeecg-boot
 * @Date: 2020-12-01
 * @Version: V1.0
 */
@Api(tags = "订单物料信息表")
@RestController
@RequestMapping("/om/orderMat")
@Slf4j
public class OrderMatController extends BaseController<OrderMat, OrderMatService> {

}
