package com.dongxin.scm.sm.controller;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.system.query.QueryGenerator;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

import org.jeecg.common.util.oConvertUtils;
import com.dongxin.scm.sm.entity.GetActualDeliveryDet;
import com.dongxin.scm.sm.entity.GetActualDelivery;
import com.dongxin.scm.sm.service.GetActualDeliveryService;
import com.dongxin.scm.sm.service.GetActualDeliveryDetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 获取装车实际
 * @Author: jeecg-boot
 * @Date: 2021-09-22
 * @Version: V1.0
 */
@Api(tags = "获取装车实际")
@RestController
@RequestMapping("/sm/getActualDelivery")
@Slf4j
public class GetActualDeliveryController extends JeecgController<GetActualDelivery, GetActualDeliveryService> {

    @Autowired
    private GetActualDeliveryService getActualDeliveryService;

    @Autowired
    private GetActualDeliveryDetService getActualDeliveryDetService;


    /*---------------------------------主表处理-begin-------------------------------------*/

    /**
     * 分页列表查询
     *
     * @param getActualDelivery
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "获取装车实际-分页列表查询")
    @ApiOperation(value = "获取装车实际-分页列表查询", notes = "获取装车实际-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(GetActualDelivery getActualDelivery,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<GetActualDelivery> queryWrapper = QueryGenerator.initQueryWrapper(getActualDelivery, req.getParameterMap());

        String length = req.getParameter("length");
        String width = req.getParameter("width");
        String thick = req.getParameter("thick");
        String steelGradeName = req.getParameter("steelGradeName");
        String materialNo = req.getParameter("materialNo");
        String productName = req.getParameter("productName");

        QueryWrapper<GetActualDeliveryDet> getActualDeliveryDetQueryWrapper = new QueryWrapper<>();

        if (StrUtil.isNotBlank(length)){
            getActualDeliveryDetQueryWrapper.lambda().eq(GetActualDeliveryDet::getLength,length);
        }
        if (StrUtil.isNotBlank(width)){
            getActualDeliveryDetQueryWrapper.lambda().eq(GetActualDeliveryDet::getWidth,width);
        }
        if (StrUtil.isNotBlank(thick)){
            getActualDeliveryDetQueryWrapper.lambda().eq(GetActualDeliveryDet::getThick,thick);
        }
        if (StrUtil.isNotBlank(steelGradeName)){
            getActualDeliveryDetQueryWrapper.lambda().eq(GetActualDeliveryDet::getSteelGradeName,steelGradeName);
        }
        if (StrUtil.isNotBlank(materialNo)){
            getActualDeliveryDetQueryWrapper.lambda().eq(GetActualDeliveryDet::getMaterialNo,materialNo);
        }
        if (StrUtil.isNotBlank(productName)){
            getActualDeliveryDetQueryWrapper.lambda().eq(GetActualDeliveryDet::getProductName,productName);
        }

        List<GetActualDeliveryDet> getActualDeliveryDetList = getActualDeliveryDetService.list(getActualDeliveryDetQueryWrapper);
        List<String> ids = getActualDeliveryDetList.stream().map(GetActualDeliveryDet::getGetActualDeliveryId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(ids)) {
            queryWrapper.lambda().in(GetActualDelivery::getId, ids);
        } else {
            queryWrapper.lambda().eq(GetActualDelivery::getId, null);
        }
        Page<GetActualDelivery> page = new Page<GetActualDelivery>(pageNo, pageSize);
        IPage<GetActualDelivery> pageList = getActualDeliveryService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param getActualDelivery
     * @return
     */
    @AutoLog(value = "获取装车实际-添加")
    @ApiOperation(value = "获取装车实际-添加", notes = "获取装车实际-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody GetActualDelivery getActualDelivery) {
        getActualDeliveryService.save(getActualDelivery);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param getActualDelivery
     * @return
     */
    @AutoLog(value = "获取装车实际-编辑")
    @ApiOperation(value = "获取装车实际-编辑", notes = "获取装车实际-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody GetActualDelivery getActualDelivery) {
        getActualDeliveryService.updateById(getActualDelivery);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "获取装车实际-通过id删除")
    @ApiOperation(value = "获取装车实际-通过id删除", notes = "获取装车实际-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        getActualDeliveryService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "获取装车实际-批量删除")
    @ApiOperation(value = "获取装车实际-批量删除", notes = "获取装车实际-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.getActualDeliveryService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 导出
     *
     * @return
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, GetActualDelivery getActualDelivery) {
        return super.exportXls(request, getActualDelivery, GetActualDelivery.class, "获取装车实际");
    }

    /**
     * 导入
     *
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, GetActualDelivery.class);
    }
    /*---------------------------------主表处理-end-------------------------------------*/


    /*--------------------------------子表处理-获取装车实际明细-begin----------------------------------------------*/

    /**
     * 通过主表ID查询
     *
     * @return
     */
    @AutoLog(value = "获取装车实际明细-通过主表ID查询")
    @ApiOperation(value = "获取装车实际明细-通过主表ID查询", notes = "获取装车实际明细-通过主表ID查询")
    @GetMapping(value = "/listGetActualDeliveryDetByMainId")
    public Result<?> listGetActualDeliveryDetByMainId(GetActualDeliveryDet getActualDeliveryDet,
                                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      HttpServletRequest req) {
        QueryWrapper<GetActualDeliveryDet> queryWrapper = QueryGenerator.initQueryWrapper(getActualDeliveryDet, req.getParameterMap());
        Page<GetActualDeliveryDet> page = new Page<GetActualDeliveryDet>(pageNo, pageSize);
        IPage<GetActualDeliveryDet> pageList = getActualDeliveryDetService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param getActualDeliveryDet
     * @return
     */
    @AutoLog(value = "获取装车实际明细-添加")
    @ApiOperation(value = "获取装车实际明细-添加", notes = "获取装车实际明细-添加")
    @PostMapping(value = "/addGetActualDeliveryDet")
    public Result<?> addGetActualDeliveryDet(@RequestBody GetActualDeliveryDet getActualDeliveryDet) {
        getActualDeliveryDetService.save(getActualDeliveryDet);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param getActualDeliveryDet
     * @return
     */
    @AutoLog(value = "获取装车实际明细-编辑")
    @ApiOperation(value = "获取装车实际明细-编辑", notes = "获取装车实际明细-编辑")
    @PutMapping(value = "/editGetActualDeliveryDet")
    public Result<?> editGetActualDeliveryDet(@RequestBody GetActualDeliveryDet getActualDeliveryDet) {
        getActualDeliveryDetService.updateById(getActualDeliveryDet);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "获取装车实际明细-通过id删除")
    @ApiOperation(value = "获取装车实际明细-通过id删除", notes = "获取装车实际明细-通过id删除")
    @DeleteMapping(value = "/deleteGetActualDeliveryDet")
    public Result<?> deleteGetActualDeliveryDet(@RequestParam(name = "id", required = true) String id) {
        getActualDeliveryDetService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "获取装车实际明细-批量删除")
    @ApiOperation(value = "获取装车实际明细-批量删除", notes = "获取装车实际明细-批量删除")
    @DeleteMapping(value = "/deleteBatchGetActualDeliveryDet")
    public Result<?> deleteBatchGetActualDeliveryDet(@RequestParam(name = "ids", required = true) String ids) {
        this.getActualDeliveryDetService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 导出
     *
     * @return
     */
    @RequestMapping(value = "/exportGetActualDeliveryDet")
    public ModelAndView exportGetActualDeliveryDet(HttpServletRequest request, GetActualDeliveryDet getActualDeliveryDet) {
        // Step.1 组装查询条件
        QueryWrapper<GetActualDeliveryDet> queryWrapper = QueryGenerator.initQueryWrapper(getActualDeliveryDet, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // Step.2 获取导出数据
        List<GetActualDeliveryDet> pageList = getActualDeliveryDetService.list(queryWrapper);
        List<GetActualDeliveryDet> exportList = null;

        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        } else {
            exportList = pageList;
        }

        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "获取装车实际明细"); //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.CLASS, GetActualDeliveryDet.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("获取装车实际明细报表", "导出人:" + sysUser.getRealname(), "获取装车实际明细"));
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }

    /**
     * 导入
     *
     * @return
     */
    @RequestMapping(value = "/importGetActualDeliveryDet/{mainId}")
    public Result<?> importGetActualDeliveryDet(HttpServletRequest request, HttpServletResponse response, @PathVariable("mainId") String mainId) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(false);
            try {
                List<GetActualDeliveryDet> list = ExcelImportUtil.importExcel(file.getInputStream(), GetActualDeliveryDet.class, params);
                for (GetActualDeliveryDet temp : list) {
                    temp.setGetActualDeliveryId(mainId);
                }
                long start = System.currentTimeMillis();
                getActualDeliveryDetService.saveBatch(list);
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                return Result.OK("文件导入成功！数据行数：" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    /*--------------------------------子表处理-获取装车实际明细-end----------------------------------------------*/


    @AutoLog(value = "获取装车实际")
    @ApiOperation(value = "获取装车实际", notes = "获取装车实际")
    @PostMapping(value = "/getActualDelivery")
    public Result<?> getActualDelivery(@RequestBody com.dongxin.scm.wms.condition.GetActualDelivery getActualDelivery) {

        getActualDeliveryService.addGetActualDelivery(getActualDelivery);
        return Result.OK();
    }

    @AutoLog(value = "获取过户实际")
    @ApiOperation(value = "获取过户实际", notes = "获取过户实际")
    @PostMapping(value = "/getTransferDelivery")
    public Result<?> getTransferDelivery(@RequestBody com.dongxin.scm.wms.condition.GetTransferDelivery getTransferDelivery) {

        getActualDeliveryService.addGetTransferDelivery(getTransferDelivery);
        return Result.OK();
    }


    //确认装车
    @GetMapping(value = "/stack")
    public Result<?> stack(String id) {
        String stackStatus = "";
        stackStatus = getActualDeliveryService.confirmStack(id,stackStatus);

        return Result.OK(stackStatus);
    }

    //取消装车确认
    @GetMapping(value = "/noStack")
    public Result<?> noStack(String id) {
        getActualDeliveryService.noStack(id);

        return Result.OK();
    }

}
