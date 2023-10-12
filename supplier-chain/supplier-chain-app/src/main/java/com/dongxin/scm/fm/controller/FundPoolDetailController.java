package com.dongxin.scm.fm.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.fm.entity.FundPoolDetail;
import com.dongxin.scm.fm.service.FundPoolDetailService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: 记录来款资金使用明细
 * @Author: jeecg-boot
 * @Date:   2021-05-13
 * @Version: V1.0
 */
@Api(tags="记录来款资金使用明细")
@RestController
@RequestMapping("/fm/fundPoolDetail")
@Slf4j
public class FundPoolDetailController extends BaseController<FundPoolDetail, FundPoolDetailService> {

}
