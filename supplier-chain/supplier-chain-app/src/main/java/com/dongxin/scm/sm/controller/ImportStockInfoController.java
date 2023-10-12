package com.dongxin.scm.sm.controller;

import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.sm.entity.ImportStockInfo;
import com.dongxin.scm.sm.service.ImportStockInfoService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Description: 导入入库信息
 * @Author: jeecg-boot
 * @Date: 2021-07-21
 * @Version: V1.0
 */
@Api(tags = "导入入库信息")
@RestController
@RequestMapping("/sm/importStockInfo")
@Slf4j
public class ImportStockInfoController extends BaseController<ImportStockInfo, ImportStockInfoService> {

    @Autowired
    private ImportStockInfoService importStockInfoService;

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @Lock4j
    protected Result<?> importExcel(HttpServletRequest request, HttpServletResponse response, Class<ImportStockInfo> clazz) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            params.setNeedSave(false);
            try {
                List<ImportStockInfo> list = ExcelImportUtil.importExcel(file.getInputStream(), ImportStockInfo.class, params);
                long start = System.currentTimeMillis();
                importStockInfoService.importCheck(list);
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                return Result.OK("文件导入成功！数据行数：" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return Result.error("文件导入失败！");
    }


    /**
     *
     * @param importStockInfoIdList
     * @return
     */
    @AutoLog(value = "广东入库")
    @ApiOperation(value = "广东入库", notes = "广东入库")
    @PostMapping(value = "/warehousing")
    public Result<?> copyAdd(@RequestBody List<String> importStockInfoIdList) {
//        importStockInfoService.warehousing(importStockInfoIdList);
        return Result.OK("添加成功！");
    }

    @AutoLog(value = "查询入库状态")
    @ApiOperation(value = "查询入库状态", notes = "查询入库状态")
    @GetMapping(value = "/storageStatus")
    public Result<?> selectType() {
        List<OptionVO> typeOptionVO = importStockInfoService.selectStorageStatus();
        return Result.ok(typeOptionVO);
    }

    @AutoLog("分页列表查询")
    @ApiOperation(value = "分页列表查询", notes = "分页列表查询")
    @GetMapping({"/list"})
    public Result<?> queryPageList(ImportStockInfo param, @RequestParam(name = "pageNo",defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize",defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<ImportStockInfo> queryWrapper = QueryGenerator.initQueryWrapper(param, req.getParameterMap());
        Page<ImportStockInfo> page = new Page((long)pageNo, (long)pageSize);
        IPage<ImportStockInfo> pageList = importStockInfoService.page(page, queryWrapper);
        importStockInfoService.updateImportStockInfoSelectDate(pageList);
        return Result.OK(pageList);
    }
}
