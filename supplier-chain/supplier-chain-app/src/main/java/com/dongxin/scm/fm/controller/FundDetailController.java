package com.dongxin.scm.fm.controller;

import com.dongxin.scm.fm.entity.FundDetail;
import com.dongxin.scm.fm.service.FundDetailService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Description: 资金明细
 * @Author: jeecg-boot
 * @Date: 2020-12-09
 * @Version: V1.0
 */
@Api(tags = "资金明细")
@RestController
@RequestMapping("/fm/fundDetail")
@Slf4j
public class FundDetailController extends BaseController<FundDetail, FundDetailService> {
}
