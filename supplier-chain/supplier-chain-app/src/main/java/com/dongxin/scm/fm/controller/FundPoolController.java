package com.dongxin.scm.fm.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.entity.FundPool;
import com.dongxin.scm.fm.enums.CommonCheckStatusEnum;
import com.dongxin.scm.fm.enums.IncomeMethodTypeEnum;
import com.dongxin.scm.fm.enums.PaymentMethodEnum;
import com.dongxin.scm.fm.service.BankService;
import com.dongxin.scm.fm.service.FundPoolService;
import com.dongxin.scm.fm.vo.AcceptanceBillVO;
import com.dongxin.scm.fm.vo.AcceptanceImport;
import com.dongxin.scm.utils.DateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;


/**
 * @Description: 来款资金表
 * @Author: jeecg-boot
 * @Date: 2020-10-27
 * @Version: V1.0
 */
@Api(tags = "来款资金表")
@RestController
@RequestMapping("/fm/fundPool")
@Slf4j
public class FundPoolController extends BaseController<FundPool, FundPoolService> {

    @Autowired
    private FundPoolService fundPoolService;
    @Autowired
    private CustomerProfileService customerProfileService;
    @Autowired
    private BankService bankService;

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
    @Override
    public Result<?> queryPageList(FundPool param,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        IPage<FundPool> pageList;
        String term = req.getParameter("term");
        QueryWrapper<FundPool> queryWrapper = new QueryWrapper<>();
        List<FundPool> fundPoolList = newArrayList();
        Page<FundPool> page = new Page<>(pageNo, pageSize);

        if (StrUtil.isNotEmpty(term)) {
            queryWrapper.lambda().eq(FundPool::getPaymentMethod,PaymentMethodEnum.ACCEPTANCE_BILL.getCode());
            if (term.equalsIgnoreCase("0")) {
                queryWrapper.lambda().eq(FundPool::getTicketDate,DateUtils.getToday(new Date()));
            } else if (term.equalsIgnoreCase("3")) {
                queryWrapper.lambda().between(FundPool::getTicketDate,DateUtils.getDaysBeforeAndAfterTheCurrentTime(1),DateUtils.getDaysBeforeAndAfterTheCurrentTime(105));
            } else if (term.equalsIgnoreCase("4")) {
                queryWrapper.lambda().between(FundPool::getTicketDate,DateUtils.getDaysBeforeAndAfterTheCurrentTime(105),DateUtils.getDaysBeforeAndAfterTheCurrentTime(135));
            } else if (term.equalsIgnoreCase("5")) {
                queryWrapper.lambda().between(FundPool::getTicketDate,DateUtils.getDaysBeforeAndAfterTheCurrentTime(135),DateUtils.getDaysBeforeAndAfterTheCurrentTime(165));
            } else if (term.equalsIgnoreCase("6")) {
                queryWrapper.lambda().between(FundPool::getTicketDate,DateUtils.getDaysBeforeAndAfterTheCurrentTime(165),DateUtils.getDaysBeforeAndAfterTheCurrentTime(195));
            }

            fundPoolList = fundPoolService.list(queryWrapper);
            pageList = page.setRecords(fundPoolList);

        } else if (StringUtils.isNotEmpty(param.getAlias())) {
            List<String> customerId = customerProfileService.selectAlias(param.getAlias());
            if (CollectionUtils.isEmpty(customerId)) {
                throw new ScmException("没有此助记码");
            }
            queryWrapper.lambda().in(FundPool::getCustomerId, customerId);
            pageList = page.setRecords(fundPoolList);
        } else {
            queryWrapper = QueryGenerator.initQueryWrapper(param, req.getParameterMap());
            page = new Page<FundPool>(pageNo, pageSize);
            pageList = fundPoolService.page(page, queryWrapper);
        }


        //获取顾客Id,汇款银行Id,承兑银行Id
        List<String> customerIds = newArrayList();
        List<String> paymentBankIds = newArrayList();
        List<String> acceptanceBankIds = newArrayList();
        for (FundPool record : pageList.getRecords()) {
            customerIds.add(record.getCustomerId());
            paymentBankIds.add(record.getPaymentBank());
            acceptanceBankIds.add(record.getAcceptanceBank());
        }

        //返回顾客姓名,汇款银行名,承兑银行名
        Map<String, String> customerName = customerProfileService.getNameMap(customerIds);
        Map<String, String> paymentBankNameMap = bankService.getBankNameMap(paymentBankIds);
        Map<String, String> acceptanceBankNameMap = bankService.getBankNameMap(acceptanceBankIds);
        for (FundPool record : pageList.getRecords()) {
            record.setCustomerNameText(customerName.get(record.getCustomerId()));
            record.setPaymentBankName(paymentBankNameMap.get(record.getPaymentBank()));
            record.setAcceptanceBankName(acceptanceBankNameMap.get(record.getAcceptanceBank()));
            record.setGapDays(DateUtils.gapDays(new Date(), record.getTicketDate()));

            record.geneTerm();
        }
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param param
     * @return
     */
    @AutoLog(value = "添加")
    @ApiOperation(value = "添加", notes = "添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody FundPool param) {
        if (param.getIncomingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ScmException("来款金额不能小于等于0");
        }

        //进账方式为正常来款
        if (param.getIncomeMethod().equals(IncomeMethodTypeEnum.NORMAL_PAYMENT.getCode())
                || param.getIncomeMethod().equals(IncomeMethodTypeEnum.SALES_RETURN.getCode())) {
            //初始化审核状态
            param.setStatus(CommonCheckStatusEnum.PENDING_VERIFY.getCode());
        }

        //进账方式为红冲情况
        if (param.getIncomeMethod().equals(IncomeMethodTypeEnum.RED_SCOUR.getCode())) {
            //初始化审核状态
            param.setStatus(CommonCheckStatusEnum.APPROVE.getCode());
        }
        param.setAvailAmount(BigDecimal.ZERO);

        fundPoolService.checkAcceptBillInSixMonth(param);

        //初始化可用金额，结算金额，预用金额
        fundPoolService.save(param);
        return Result.OK("添加成功！");
    }

    /**
     * 通过customerId查询
     *
     * @param customerId
     * @return
     */
    @AutoLog(value = "通过id查询")
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping(value = "/queryByCustomerId")
    public Result<?> queryByCustomerId(@RequestParam(name = "customerId", required = true) String customerId,
                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        FundPool fundPool = new FundPool();
        fundPool.setCustomerId(customerId);
        QueryWrapper<FundPool> queryWrapper = QueryGenerator.initQueryWrapper(fundPool, null);
        Page<FundPool> page = new Page<FundPool>(pageNo, pageSize);
        IPage<FundPool> pageList = fundPoolService.page(page, queryWrapper);

        return Result.OK(pageList);
    }

    /**
     * 导入
     *
     * @param request
     * @param response
     * @param clazz
     * @return
     */
    @RequestMapping(value = "/importExcel")
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response, Class<FundPool> clazz) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            params.setNeedSave(false);

            try {
                List<FundPool> list = ExcelImportUtil.importExcel(file.getInputStream(), clazz, params);
                List<FundPool> fundPoolList = fundPoolService.checkFile(list);
                fundPoolService.saveBatch(fundPoolList);
                return Result.OK("文件导入成功！数据行数：" + list.size());
            } catch (Exception var23) {
                log.error(var23.getMessage(), var23);
                return Result.error("文件导入失败:" + var23.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException var22) {
                    var22.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    /**
     * 银承导入
     *
     * @param request
     * @param response
     * @param clazz
     * @return
     */
    @RequestMapping(value = "/importExcelHK")
    public Result<?> importExcelHK(HttpServletRequest request, HttpServletResponse
            response, Class<FundPool> clazz) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            params.setNeedSave(false);

            try {
                List<AcceptanceImport> list = ExcelImportUtil.importExcel(file.getInputStream(), AcceptanceImport.class, params);
                List<FundPool> fundPoolList = fundPoolService.importExcelHK(list);
                fundPoolService.saveBatch(fundPoolList);
                return Result.OK("文件导入成功！数据行数：" + list.size());
            } catch (Exception var23) {
                log.error(var23.getMessage(), var23);
                return Result.error("文件导入失败:" + var23.getMessage() + "请核对数据是否有误");
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException var22) {
                    var22.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    /**
     * 批量审核
     *
     * @param fundPoolIdList
     * @return
     */
    @AutoLog(value = "批量审核")
    @ApiOperation(value = "批量审核", notes = "批量审核")
    @PostMapping(value = "/batchAudit")
    public Result<?> add(@RequestBody List<String> fundPoolIdList) {
        fundPoolService.batchAudit(fundPoolIdList);
        return Result.OK("审核成功！");
    }


    /**
     * 编辑
     *
     * @param fundPool
     * @return
     */
    @AutoLog(value = "编辑")
    @ApiOperation(value = "编辑", notes = "编辑")
    @PostMapping(value = "/editFundPool")
    public Result<?> editFundPool(@RequestBody FundPool fundPool) {
        fundPoolService.editFundPool(fundPool);
        return Result.ok("编辑成功");
    }

    /**
     * 导入CH
     *
     * @param request
     * @param response
     * @param clazz
     * @return
     */
    @RequestMapping(value = "/importExcelCH")
    public Result<?> importExcelCH(HttpServletRequest request, HttpServletResponse
            response, Class<FundPool> clazz) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            params.setNeedSave(false);

            try {
                List<AcceptanceBillVO> list = ExcelImportUtil.importExcel(file.getInputStream(), AcceptanceBillVO.class, params);
                List<FundPool> fundPoolList = fundPoolService.importExcelCH(list);

                for (FundPool fundPool : fundPoolList) {
                    fundPoolService.checkAcceptBillInSixMonth(fundPool);
                }
                this.fundPoolService.saveBatch(fundPoolList);
                return Result.OK("文件导入成功！数据行数：" + list.size());
            } catch (Exception var23) {
                log.error(var23.getMessage(), var23);
                return Result.error("文件导入失败:" + var23.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException var22) {
                    var22.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败！");
    }


    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "通过id删除")
    @ApiOperation(value = "通过id删除", notes = "通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        fundPoolService.deleteFundPool(id);
        return Result.OK("删除成功!");
    }

    /**
     * 编辑
     *
     * @param param
     * @return
     */
    @AutoLog(value = "编辑")
    @ApiOperation(value = "编辑", notes = "编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody FundPool param) {
        fundPoolService.updateFundPool(param);
        return Result.OK("编辑成功!");
    }


    /**
     * 导出excel
     *
     * @param request
     */
    protected ModelAndView exportXls(HttpServletRequest request, FundPool object, Class<FundPool> clazz, String
            title) {
        // Step.1 组装查询条件
        QueryWrapper<FundPool> queryWrapper = QueryGenerator.initQueryWrapper(object, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // Step.2 获取导出数据
        List<FundPool> pageList = fundPoolService.list(queryWrapper);
        List<FundPool> exportList = null;

        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        } else {
            exportList = pageList;
        }

        fundPoolService.setFundPool(exportList);

        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, title); //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.CLASS, clazz);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams(title + "报表", "导出人:" + sysUser.getRealname(), title));
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }

    @GetMapping(value = "/selectFundPool")
    public Result<?> selectFundPool(String id, String paymentMethod) {
        List<FundPool> fundPoolList = fundPoolService.selectFundPool(id, paymentMethod);
        return Result.OK(fundPoolList);
    }
}
