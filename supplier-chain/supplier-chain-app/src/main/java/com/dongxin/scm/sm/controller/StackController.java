package com.dongxin.scm.sm.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.cm.service.SalesmanInfoService;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.sm.entity.Stack;
import com.dongxin.scm.sm.entity.StackDet;
import com.dongxin.scm.sm.enums.StatusEnum;
import com.dongxin.scm.sm.service.StackDetService;
import com.dongxin.scm.sm.service.StackService;
import com.dongxin.scm.sm.service.StockService;
import com.dongxin.scm.sm.vo.*;
import freemarker.template.utility.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.newArrayList;

/**
 * @Description: 装车单主表
 * @Author: jeecg-boot
 * @Date: 2020-11-24
 * @Version: V1.0
 */
@Api(tags = "装车单主表")
@RestController
@RequestMapping("/sm/stack")
@Slf4j
public class StackController {
    @Autowired
    private StackService stackService;
    @Autowired
    private StackDetService stackDetService;
    @Autowired
    private CustomerProfileService customerProfileService;
    @Autowired
    private StockService stockService;
    @Autowired
    private SalesmanInfoService salesmanInfoService;


    /**
     * 分页列表查询
     *
     * @param stack
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "装车单主表-分页列表查询")
    @ApiOperation(value = "装车单主表-分页列表查询", notes = "装车单主表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(Stack stack,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<Stack> queryWrapper = QueryGenerator.initQueryWrapper(stack, req.getParameterMap());
        //updateBy liujiazhi 2021.04.27 装车单页面增添牌号和品名查询
        String sgSign = req.getParameter("sgSign"); //牌号
        String prodCname = req.getParameter("prodCname");//品名
        String prodClassCode = req.getParameter("prodClassCode1");//产品大类
        QueryWrapper<StackDet> stackDetQueryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(sgSign)) {
            stackDetQueryWrapper.lambda().eq(StackDet::getSgSign, sgSign);
        }
        if (StrUtil.isNotBlank(prodCname)) {
            stackDetQueryWrapper.lambda().eq(StackDet::getProdCname, prodCname);
        }
        if (StrUtil.isNotBlank(prodClassCode)) {
            stackDetQueryWrapper.lambda().eq(StackDet::getProdClassCode, prodClassCode);
        }
        List<StackDet> stackDetList = stackDetService.list(stackDetQueryWrapper);
        List<String> ids = stackDetList.stream().map(StackDet::getStackId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(ids)) {
            queryWrapper.lambda().in(Stack::getId, ids);
        } else {
            queryWrapper.lambda().eq(Stack::getId, null);
        }

        Page<Stack> page = new Page<Stack>(pageNo, pageSize);
        IPage<Stack> pageList = stackService.page(page, queryWrapper);
        return Result.OK(pageList);
    }


    /**
     * 发货生成装车单
     *
     * @param salesOrderDetVo
     * @return
     */
    @ApiOperation(value = "销售单发货生成装车单")
    @PostMapping(value = "/sendOutGoods")
    public Result<?> sendOutGoods(@RequestBody SalesOrderDetVO salesOrderDetVo) {
        stackService.sendOutGoods(salesOrderDetVo);
        return Result.ok("发出成功");
    }

    /**
     * 按量发货生成装车单
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "销售单发货生成装车单")
    @GetMapping(value = "/sendMeasured")
    public Result<?> sendMeasured(@RequestParam String id) {
        stackService.sendMeasured(id);
        return Result.ok("发出成功");
    }

    /**
     * 分页列表查询装车单明细信息
     *
     * @param stackDetailVO
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "装车单主表和明细数据分页查询")
    @ApiOperation(value = "装车单主表和明细数据分页查询", notes = "装车单主表和明细数据分页查询")
    @GetMapping(value = "/listStackDetail")
    public Result<?> queryPageListStackDetail(StackDetailVO stackDetailVO,
                                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                              HttpServletRequest req) {

//        if (StrUtil.isBlank(stackDetailVO.getCustomerId())) {
//            throw new ScmException("请选择顾客");
//        }
//        if(ObjectUtils.isEmpty(stackDetailVO)){
//            throw new ScmException("请输入搜索内容");
//        }
        Page<SelectSettlenInfoVO> page = new Page<>(pageNo, pageSize);

        QueryWrapper<StackDet> stackDetQueryWrapper = new QueryWrapper<>();
        QueryWrapper<Stack> stackQueryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(stackDetailVO.getProdCode())) {
            stackDetQueryWrapper.lambda().eq(StackDet::getProdClassCode, stackDetailVO.getProdCode());
        }
        List<StackDet> stackDetList = stackDetService.list(stackDetQueryWrapper);
        List<String> detIds = stackDetList.stream().map(StackDet::getStackId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(detIds)) {
            stackQueryWrapper.lambda().in(Stack::getId, detIds);
        } else {
            stackQueryWrapper.lambda().eq(Stack::getId, null);
        }
        List<Stack> stackList = stackService.list(stackQueryWrapper);
        List<String> stackIds = stackList.stream().map(Stack::getId).collect(Collectors.toList());
        String ids = String.join(",", stackIds);
        if (StrUtil.isEmpty(ids)){
            log.error("产品大类;" + stackDetailVO.getProdCode());
            throw new ScmException(StrUtil.format("查询不到对应的装车单,请检查是否选择错误"));
        }
        page.setRecords(stackService.pageListStack(stackDetailVO, ids));

        List<String> customerIds = newArrayList();
        List<String> salesManIds = newArrayList();
        List<String> createByIds = newArrayList();
        for (SelectSettlenInfoVO record : page.getRecords()) {
            customerIds.add(record.getCustomerId());
            salesManIds.add(record.getSalesMan());
            createByIds.add(record.getCreateBy());
        }

        Map<String, String> customerName = customerProfileService.getNameMap(customerIds);
        Map<String, String> salesManName = salesmanInfoService.getNameMap(salesManIds);
        Map<String, String> createByMap = stackService.listCreateBy(createByIds);
        for (SelectSettlenInfoVO record : page.getRecords()) {
            record.setCustomerId(customerName.get(record.getCustomerId()));
            record.setSalesMan(salesManName.get(record.getSalesMan()));
            record.setCreateBy(createByMap.get(record.getCreateBy()));
        }

        return Result.ok(page);
    }

    /**
     * 编辑
     *
     * @param stackPage
     * @return
     */
    @AutoLog(value = "装车单主表-编辑")
    @ApiOperation(value = "装车单主表-编辑", notes = "装车单主表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StackPage stackPage) {
        Stack stackEntity = stackService.getById(stackPage.getId());
        if (stackEntity == null) {
            return Result.error("未找到对应数据");
        }
        if (stackEntity.getStatus().equals(StatusEnum.WRITE_OFF.getCode())) {
            stackEntity.setStackPageUpdate(YesNoEnum.YES.getCode());
        } else {
            stackEntity.setStackPageUpdate(YesNoEnum.NO.getCode());
        }
        BeanUtils.copyProperties(stackPage, stackEntity);

        stackService.updateMain(stackEntity, stackPage.getStackDetList());
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "装车单主表-通过id删除")
    @ApiOperation(value = "装车单主表-通过id删除", notes = "装车单主表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        stackService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "装车单主表-批量删除")
    @ApiOperation(value = "装车单主表-批量删除", notes = "装车单主表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.stackService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "装车单主表-通过id查询")
    @ApiOperation(value = "装车单主表-通过id查询", notes = "装车单主表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        Stack stack = stackService.getById(id);
        if (stack == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(stack);

    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "装车单明细表通过主表ID查询")
    @ApiOperation(value = "装车单明细表主表ID查询", notes = "装车单明细表-通主表ID查询")
    @GetMapping(value = "/queryStackDetByMainId")
    public Result<?> queryStackDetListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<StackDet> stackDetList = stackDetService.selectByMainId(id);
        return Result.OK(stackDetList);
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "装车单明细表通过主表ID查询")
    @ApiOperation(value = "装车单明细表主表ID查询", notes = "装车单明细表-通主表ID查询")
    @GetMapping(value = "/queryStackDetByStackId")
    public Result<?> queryStackDetByMainId(@RequestParam(name = "id", required = true) String id,
                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<StackDet> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(StackDet::getStackId, id);
        Page<StackDet> page = new Page<StackDet>(pageNo, pageSize);
        IPage<StackDet> pageList = stackDetService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "装车单明细表-通过id删除")
    @ApiOperation(value = "装车单明细表-通过id删除", notes = "装车单明细表-通过id删除")
    @DeleteMapping(value = "/deleteStackDet")
    public Result<?> deleteStackDet(@RequestParam(name = "id", required = true) String id) {
        stackDetService.delStackDet(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "装车单明细表-批量删除")
    @ApiOperation(value = "装车单明细表-批量删除", notes = "装车单明细表-批量删除")
    @DeleteMapping(value = "/deleteBatchStackDet")
    public Result<?> deleteBatchStackDet(@RequestParam(name = "ids", required = true) String ids) {
        stackDetService.delBatchStackDet(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 导出excel
     *
     * @param request
     * @param stack
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Stack stack) {
        // Step.1 组装查询条件查询数据
        QueryWrapper<Stack> queryWrapper = QueryGenerator.initQueryWrapper(stack, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //Step.2 获取导出数据
        List<Stack> queryList = stackService.list(queryWrapper);
        // 过滤选中数据
        String selections = request.getParameter("selections");
        List<Stack> stackList = new ArrayList<Stack>();
        if (oConvertUtils.isEmpty(selections)) {
            stackList = queryList;
        } else {
            List<String> selectionList = Arrays.asList(selections.split(","));
            stackList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        }

        // Step.3 组装pageList
        List<StackPage> pageList = new ArrayList<StackPage>();
        for (Stack main : stackList) {
            StackPage vo = new StackPage();
            BeanUtils.copyProperties(main, vo);
            List<StackDet> stackDetList = stackDetService.selectByMainId(main.getId());
            vo.setStackDetList(stackDetList);
            pageList.add(vo);
        }

        // Step.4 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "装车单主表列表");
        mv.addObject(NormalExcelConstants.CLASS, StackPage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("装车单主表数据", "导出人:" + sysUser.getRealname(), "装车单主表"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }


    @AutoLog(value = "查询装车单明细")
    @ApiOperation(value = "查询装车单明细", notes = "查询装车单明细")
    @GetMapping(value = "/settleStackDetail")
    public Result<?> settleStackDetail(@RequestParam(name = "stackIds") String stackIds) {
        List<String> stackIdList = Arrays.asList(stackIds.split(","));
        List<StackDetailSettle> stackDetailSettleList = stackDetService.settleStackDetail(stackIdList);

        List<String> createBy = newArrayList();
        for (StackDetailSettle stackDetailSettle : stackDetailSettleList) {
            createBy.add(stackDetailSettle.getCreateBy());
        }

        Map<String, String> createByMap = stackService.listCreateBy(createBy);
        for (StackDetailSettle stackDetailSettle : stackDetailSettleList) {
            stackDetailSettle.setCreateBy(createByMap.get(stackDetailSettle.getCreateBy()));
        }


        return Result.ok(stackDetailSettleList);
    }

    @AutoLog(value = "查询有销售单的客户")
    @ApiOperation(value = "查询有销售单的客户", notes = "查询有销售单的客户")
    @GetMapping(value = "/selectUnsettleCustomerId")
    public Result<?> selectUnsettleCustomerId() {
        List<OptionVO> selectUnsettleCustomerId = stackService.selectUnsettleCustomerId();
        return Result.ok(selectUnsettleCustomerId);
    }

    @GetMapping(value = "/selectStackDet")
    public Result<?> selectStackDet(String id) {
        List<StackDet> stackDetList = stackDetService.selectByStackId(id);
        return Result.OK(stackDetList);
    }

    @GetMapping(value = "/stackWriteOff")
    public Result<?> stackWriteOff(String id) throws ParseException {
        stackService.stackWriteOffAlone(id);
        return Result.OK("冲红成功");
    }

    @GetMapping(value = "/stackWriteOffReviewer")
    public Result<?> stackWriteOffReviewer(String id) throws ParseException {
        stackService.stackWriteOffReviewer(id);
        return Result.OK("冲红复核成功");
    }

    @GetMapping(value = "/stackWriteOffGiveUp")
    public Result<?> stackWriteOffGiveUp(String id) throws ParseException {
        stackService.stackWriteOffGiveUp(id);
        return Result.OK("已取消冲红");
    }
}
