package com.dongxin.scm.fm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.fm.entity.Credit;
import com.dongxin.scm.fm.service.CreditAdjustService;
import com.dongxin.scm.fm.service.CreditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: 授信额度表
 * @Author: jeecg-boot
 * @Date: 2020-10-27
 * @Version: V1.0
 */
@Api(tags = "授信额度表")
@RestController
@RequestMapping("/fm/credit")
@Slf4j
public class CreditController extends BaseController<Credit, CreditService> {

    @Autowired
    CreditService creditService;
    @Autowired
    CreditAdjustService creditAdjustService;

    @Autowired
    private CustomerProfileService customerProfileService;

    @AutoLog(value = "修改审批状态")
    @ApiOperation(value = "修改审批状态", notes = "修改审批状态")
    @GetMapping(value = "/passBatch")
    public Result<?> passBatch(@RequestParam(name = "ids") String ids, @RequestParam(name = "tag") String tag) {
        List<String> creditIds = Arrays.asList(ids.split(","));
        creditService.updatePassBatch(creditIds, tag);
        return Result.ok("审核成功");
    }

    @AutoLog(value = "调整授信额度")
    @ApiOperation(value = "调整授信额度", notes = "调整授信额度")
    @GetMapping(value = "/creditAdjust")
    public Result<?> creditAdjust(@RequestParam(name = "id") String id,@RequestParam(name = "creditNo") String creditNo,
                                  @RequestParam(name = "adjustStatus") String adjustStatus, @RequestParam(name = "adjustAmount") BigDecimal adjustAmount,
                                  @RequestParam(name = "remark") String remark) {
        creditAdjustService.creditAdjust(id,creditNo, adjustStatus, adjustAmount,remark);
        return Result.ok("审核成功");

    }

    /**
     * 分页列表查询
     *
     * @param credit
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "订单主表-分页列表查询")
    @ApiOperation(value = "订单主表-分页列表查询", notes = "订单主表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(Credit credit,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<Credit> queryWrapper = QueryGenerator.initQueryWrapper(credit, req.getParameterMap());
        Page<Credit> page = new Page<>(pageNo, pageSize);
        IPage<Credit> pageList = creditService.page(page, queryWrapper);
        List<String> customerIdList = new ArrayList<>();

        for (Credit record : pageList.getRecords()) {
            customerIdList.add(record.getCustomerId());
        }

        Map<String, String> idAndName = customerProfileService.getNameMap(customerIdList);
        for (Credit record : pageList.getRecords()) {
            record.setCustomerText(idAndName.get(record.getCustomerId()));
        }
        return Result.ok(pageList);
    }


}
