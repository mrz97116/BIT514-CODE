package com.dongxin.scm.fm.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.fm.entity.SettleMatCoding;
import com.dongxin.scm.fm.service.SettleMatCodingService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

 /**
 * @Description: 结算单报表关联产品名称、牌号和规格显示对应的物料编号、生产线、品种
 * @Author: jeecg-boot
 * @Date:   2021-04-18
 * @Version: V1.0
 */
@Api(tags="结算单报表关联产品名称、牌号和规格显示对应的物料编号、生产线、品种")
@RestController
@RequestMapping("/fm/settleMatCoding")
@Slf4j
public class SettleMatCodingController extends BaseController<SettleMatCoding, SettleMatCodingService> {

}
