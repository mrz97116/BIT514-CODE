package com.dongxin.scm.fm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.fm.entity.FinanceDetail;
import com.dongxin.scm.fm.entity.FundDetail;
import com.dongxin.scm.fm.service.FinanceDetailService;
import com.dongxin.scm.fm.service.FundDetailService;
import com.dongxin.scm.sm.vo.OptionVO;
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
import java.util.List;

/**
 * @Description: 财务明细
 * @Author: jeecg-boot
 * @Date: 2021-04-06
 * @Version: V1.0
 */
@Api(tags = "财务明细")
@RestController
@RequestMapping("/fm/financeDetail")
@Slf4j
public class FinanceDetailController extends BaseController<FinanceDetail, FinanceDetailService> {

    @Autowired
    private FinanceDetailService financeDetailService;

    @Autowired
    private FundDetailService fundDetailService;


    /**
     * 分页列表查询
     *
     * @param param
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "分页列表查询")
    @ApiOperation(value = "分页列表查询", notes = "分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(FinanceDetail param,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        FundDetail fundDetail = new FundDetail().setCustomerId(param.getCustomerId()).setType(param.getType());
        QueryWrapper<FundDetail> queryWrapper = QueryGenerator.initQueryWrapper(fundDetail, req.getParameterMap()).orderByDesc("create_time");
        Page<FundDetail> page = new Page<FundDetail>(pageNo, pageSize);
        IPage<FundDetail> pageList = fundDetailService.page(page, queryWrapper);
        IPage<FundDetail> fundDetailPage = fundDetailService.translate(pageList);

        return Result.OK(fundDetailPage);
    }


    @AutoLog(value = "查询类型")
    @ApiOperation(value = "查询类型", notes = "查询类型")
    @GetMapping(value = "/selectType")
    public Result<?> selectType() {
        List<OptionVO> typeOptionVO = financeDetailService.selectType();
        return Result.ok(typeOptionVO);
    }

}
