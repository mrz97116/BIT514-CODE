package com.dongxin.scm.cm.controller;

import com.dongxin.scm.cm.entity.UserContrast;
import com.dongxin.scm.cm.service.UserContrastService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 经销用户对照表
 * @Author: jeecg-boot
 * @Date: 2020-11-02
 * @Version: V1.0
 */
@Api(tags = "经销用户对照表")
@RestController
@RequestMapping("/cm/userContrast")
@Slf4j
public class UserContrastController extends BaseController<UserContrast, UserContrastService> {

}
