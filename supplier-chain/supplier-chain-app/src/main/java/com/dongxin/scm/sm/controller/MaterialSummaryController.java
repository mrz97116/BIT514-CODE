package com.dongxin.scm.sm.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.sm.entity.MaterialSummary;
import com.dongxin.scm.sm.service.MaterialSummaryService;
import com.dongxin.scm.sm.vo.OptionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description: 材料汇总
 * @Author: jeecg-boot
 * @Date: 2021-04-26
 * @Version: V1.0
 */
@Api(tags = "材料汇总")
@RestController
@RequestMapping("/sm/materialSummary")
@Slf4j
public class MaterialSummaryController extends BaseController<MaterialSummary, MaterialSummaryService> {


    @Autowired
    MaterialSummaryService materialSummaryService;

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    protected Result<?> importExcel(HttpServletRequest request, HttpServletResponse response, Class<MaterialSummary> clazz) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            params.setNeedSave(false);
            try {
                List<MaterialSummary> list = ExcelImportUtil.importExcel(file.getInputStream(), clazz, params);
                //update-begin-author:taoyan date:20190528 for:批量插入数据
                long start = System.currentTimeMillis();
                materialSummaryService.addImportExcel(list);
//                materialSummaryService.saveBatch(list);
                //400条 saveBatch消耗时间1592毫秒  循环插入消耗时间1947毫秒
                //1200条  saveBatch消耗时间3687毫秒 循环插入消耗时间5212毫秒
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                //update-end-author:taoyan date:20190528 for:批量插入数据
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
    @ApiOperation(value="分页列表查询", notes="分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(MaterialSummary param,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<MaterialSummary> queryWrapper = QueryGenerator.initQueryWrapper(param, req.getParameterMap());
        Page<MaterialSummary> page = new Page<MaterialSummary>(pageNo, pageSize);
        IPage<MaterialSummary> pageList = materialSummaryService.page(page, queryWrapper);
        IPage<MaterialSummary> materialSummaryIPageList = materialSummaryService.translate(pageList);
        return Result.OK(materialSummaryIPageList);
    }



    @AutoLog(value = "查询产品大类")
    @ApiOperation(value = "查询产品大类", notes = "查询产品大类")
    @GetMapping(value = "/selectProductName")
    public Result<?> selectProductName() {
        List<OptionVO> selectProductName = materialSummaryService.selectProductName();
        return Result.OK(selectProductName);
    }



}
