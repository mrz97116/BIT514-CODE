package com.dongxin.scm.fm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.fm.entity.RefundRecords;
import com.dongxin.scm.fm.service.RefundRecordsService;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 退款记录表
 * @Author: jeecg-boot
 * @Date: 2020-11-27
 * @Version: V1.0
 */
@Api(tags = "退款记录表")
@RestController
@RequestMapping("/fm/refundRecords")
@Slf4j
public class RefundRecordsController extends BaseController<RefundRecords, RefundRecordsService> {


    @Autowired
    RefundRecordsService refundRecordsService;

    @Autowired
    CustomerProfileService customerProfileService;

    @AutoLog("分页列表查询")
    @ApiOperation(
            value = "分页列表查询",
            notes = "分页列表查询"
    )
    @GetMapping({"/list"})
    public Result<?> queryPageList(RefundRecords param,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<RefundRecords> queryWrapper = QueryGenerator.initQueryWrapper(param, req.getParameterMap());
        Page<RefundRecords> page = new Page((long) pageNo, (long) pageSize);
        IPage<RefundRecords> pageList = this.refundRecordsService.page(page, queryWrapper);

        List<String> customerIds = newArrayList();
        for (RefundRecords record : pageList.getRecords()) {
            customerIds.add(record.getCustomerId());
        }

        Map<String, String> customerName = customerProfileService.getNameMap(customerIds);
        for (RefundRecords record : pageList.getRecords()) {
            record.setCustomerId(customerName.get(record.getCustomerId()));
        }

        return Result.OK(pageList);
    }

    @AutoLog(value = "修改审批状态")
    @ApiOperation(value = "修改审批状态", notes = "修改审批状态")
    @GetMapping(value = "/passBatch")
    public Result<?> passBatch(@RequestParam(name = "ids") String ids, @RequestParam(name = "tag") String tag) {
        List<String> refundRecordsIds = Arrays.asList(ids.split(","));
        refundRecordsService.updatePassBatch(refundRecordsIds, tag);
        return Result.ok("审核成功");
    }

}
