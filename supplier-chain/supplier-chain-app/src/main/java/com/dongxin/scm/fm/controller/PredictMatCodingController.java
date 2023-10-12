package com.dongxin.scm.fm.controller;

import com.dongxin.scm.fm.entity.PredictMatCoding;
import com.dongxin.scm.fm.service.PredictMatCodingService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 预测报表物料编号
 * @Author: jeecg-boot
 * @Date:   2021-12-27
 * @Version: V1.0
 */
@Api(tags="预测报表物料编号")
@RestController
@RequestMapping("/fm/predictMatCoding")
@Slf4j
public class PredictMatCodingController extends BaseController<PredictMatCoding, PredictMatCodingService> {

}
