package com.dongxin.scm.sm.controller;

import com.dongxin.scm.sm.entity.SmMaterials;
import com.dongxin.scm.sm.service.SmMaterialsService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 建筑材单重表
 * @Author: jeecg-boot
 * @Date: 2021-02-02
 * @Version: V1.0
 */
@Api(tags = "建筑材单重表")
@RestController
@RequestMapping("/sm/smMaterials")
@Slf4j
public class SmMaterialsController extends BaseController<SmMaterials, SmMaterialsService> {

}
