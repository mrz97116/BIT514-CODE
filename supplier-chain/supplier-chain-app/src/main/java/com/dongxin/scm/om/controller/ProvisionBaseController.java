package com.dongxin.scm.om.controller;

import com.dongxin.scm.om.entity.ProvisionBase;
import com.dongxin.scm.om.service.ProvisionBaseService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 商务条款维护表
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */
@Api(tags = "商务条款维护表")
@RestController
@RequestMapping("/om/provisionBase")
@Slf4j
public class ProvisionBaseController extends BaseController<ProvisionBase, ProvisionBaseService> {

}
