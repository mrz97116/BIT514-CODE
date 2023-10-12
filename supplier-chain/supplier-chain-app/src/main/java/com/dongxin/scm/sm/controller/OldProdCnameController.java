package com.dongxin.scm.sm.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.sm.entity.OldProdCname;
import com.dongxin.scm.sm.service.OldProdCnameService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: 旧系统产品名称
 * @Author: jeecg-boot
 * @Date:   2021-03-02
 * @Version: V1.0
 */
@Api(tags="旧系统产品名称")
@RestController
@RequestMapping("/sm/oldProdCname")
@Slf4j
public class OldProdCnameController extends BaseController<OldProdCname, OldProdCnameService> {

}
