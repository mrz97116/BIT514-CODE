package com.dongxin.scm.fm.controller;

import com.baomidou.lock.LockInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.common.service.LockService;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.dto.FundAccountDTO;
import com.dongxin.scm.fm.entity.Capital;
import com.dongxin.scm.fm.entity.FundAccount;
import com.dongxin.scm.fm.entity.FundPool;
import com.dongxin.scm.fm.enums.CommonCheckStatusEnum;
import com.dongxin.scm.fm.enums.IncomingTypeEnum;
import com.dongxin.scm.fm.service.*;
import com.dongxin.scm.om.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.mybatis.TenantContext;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 资金账户表
 * @Author: jeecg-boot
 * @Date: 2020-11-23
 * @Version: V1.0
 */
@Api(tags = "资金账户表")
@RestController
@RequestMapping("/fm/fundAccount")
@Slf4j
public class FundAccountController extends BaseController<FundAccount, FundAccountService> {

    @Autowired
    FundAccountService fundAccountService;

    @Autowired
    CustomerProfileService customerProfileService;

    @Autowired
    FundPoolService fundPoolService;

    @Autowired
    FinanceDetailService financeDetailService;

    @Autowired
    FundService fundService;

    @Autowired
    CreditService creditService;

    @Autowired
    LockService lockService;



    @AutoLog(value = "查询授信，可用金额,可开单金额")
    @ApiOperation(value = "查询授信，可用金额,可开单金额", notes = "查询授信，可用金额,可开单金额")
    @GetMapping(value = "/selectAvailableAmount")
    public Result<?> selectAvailableAmount(@RequestParam(name = "id", required = true) String customerId) {
        FundAccountDTO fundAccountDTO = fundService.getFundAccountDTO(customerId);
        return Result.OK(fundAccountDTO);
    }

    @AutoLog(value = "更新授信，可用余额")
    @ApiOperation(value = "更新授信，可用余额", notes = "更新授信，可用余额")
    @PutMapping(value = "/updateCreditAndAvailableAmount")
    public Result<?> updateCreditAndAvailableAmount(@RequestBody FundPool param) {
        if (IncomingTypeEnum.NORMAL.getCode().equals(param.getIncomingType())) {
            param.setCreditAmount(BigDecimal.ZERO);
        }
        LockInfo lockInfo = lockService.lock(Constants.CUSTOMER_LOCK_HEADER + param.getCustomerId());
        if (lockInfo == null) {
            throw new ScmException("请求过于频繁，请稍后重试");
        }
        try {
            FundPool fundPool = fundPoolService.getById(param.getId());


            //判断来款审核是否是已通过
            if (CommonCheckStatusEnum.APPROVE.getCode().equals(fundPool.getStatus())) {
                throw new ScmException("此来款已审核！");
            } else {
                fundPoolService.setApprove(param);
                return Result.OK("审核成功");
            }

        } finally {
            lockService.releaseLock(lockInfo);
        }

    }

    @ApiOperation(value = "退款")
    @GetMapping(value = "refundUpdate")
    public Result<?> updateWarehouse(@RequestParam(name = "id", required = true) String customerId,
                                     @RequestParam(name = "refundAmount", required = true) BigDecimal refundAmount,
                                     @RequestParam(name = "receiptCode", required = true) String receiptCode,
                                     @RequestParam(name = "remarks") String remarks) {

        fundAccountService.refundUpdate(customerId, refundAmount,receiptCode, remarks);

        return Result.OK("操作成功！");
    }

    @AutoLog("分页列表查询")
    @ApiOperation(
            value = "分页列表查询",
            notes = "分页列表查询"
    )
    @GetMapping({"/list"})
    public Result<?> queryPageList(FundAccount param,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {

        Page<Capital> page = new Page<>(pageNo, pageSize);
        page.setRecords(fundAccountService.getCapital(param));
        return Result.OK(page);

    }


    /**
     * @param settleIdList
     * @return
     */
    @AutoLog(value = "删除结算单")
    @ApiOperation(value = "删除结算单", notes = "删除结算单")
    @PostMapping(value = "/deleteSettle")
    public Result<?> deleteSettle(@RequestBody List<String> settleIdList) {
        String tenantId = TenantContext.getTenant();

        LockInfo lockInfo = lockService.lock(Constants.SETTLE_LOCK_HEADER + tenantId);

        if (lockInfo == null) {
            throw new ScmException("请求过于频繁，请稍后重试");
        }

        try {

            for (String settleId : settleIdList) {
                fundAccountService.settleDelete(settleId);
            }
            return Result.OK("删除成功！");
        } finally {
            lockService.releaseLock(lockInfo);

        }
    }


    /**
     * 导出excel
     *
     * @param request
     */
    protected ModelAndView exportXls(HttpServletRequest request, FundAccount object, Class<FundAccount> clazz, String title) {
        // Step.1 组装查询条件
//        QueryWrapper<FundAccount> queryWrapper = QueryGenerator.initQueryWrapper(object, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // Step.2 获取导出数据
//        List<FundAccount> pageList = fundAccountService.list(queryWrapper);
//        List<Capital> result = fundAccountService.getFundAccountList(object);
        List<Capital> result = fundAccountService.getCapitalBalance(object == null ? null : object.getCustomerId());
        List<Capital> exportList = null;

        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            exportList = result.stream().filter(item -> selectionList.contains(item.getCustomerId())).collect(Collectors.toList());
        } else {
            exportList = result;
        }

        fundAccountService.setExcelFundAccount(exportList);
        title = "客户余额";

        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, title); //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.CLASS, Capital.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams(title + "报表", "导出人:" + sysUser.getRealname(), title));
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }

}
