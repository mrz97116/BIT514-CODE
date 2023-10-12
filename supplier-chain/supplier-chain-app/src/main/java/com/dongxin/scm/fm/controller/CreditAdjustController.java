package com.dongxin.scm.fm.controller;

import com.dongxin.scm.fm.service.CreditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.fm.entity.CreditAdjust;
import com.dongxin.scm.fm.service.CreditAdjustService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 授信调整
 * @Author: jeecg-boot
 * @Date:   2022-01-24
 * @Version: V1.0
 */
@Api(tags="授信调整")
@RestController
@RequestMapping("/fm/creditAdjust")
@Slf4j
public class CreditAdjustController extends BaseController<CreditAdjust, CreditAdjustService> {

    @Autowired
    CreditService creditService;
    @Autowired
    CreditAdjustService creditAdjustService;

    @Override
    @RequestMapping({"/exportXls"})
    public ModelAndView exportXls(HttpServletRequest request, CreditAdjust param) {
        return super.exportXls(request, param, this.currentModelClass(), "授信调整");
    }

    @AutoLog(value = "修改审批状态")
    @ApiOperation(value = "修改审批状态", notes = "修改审批状态")
    @GetMapping(value = "/passBatch")
    public Result<?> passBatch(@RequestParam(name = "ids") String ids, @RequestParam(name = "tag") String tag) {
        List<String> creditIds = Arrays.asList(ids.split(","));
        creditAdjustService.updatePassBatch(creditIds, tag);
        return Result.ok("审核成功");
    }
}
