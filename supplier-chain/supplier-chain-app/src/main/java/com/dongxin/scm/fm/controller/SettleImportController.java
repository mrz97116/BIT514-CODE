package com.dongxin.scm.fm.controller;

import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.fm.entity.SettleImport;
import com.dongxin.scm.fm.service.SettleImportService;
import com.dongxin.scm.sm.entity.ImportStockInfo;
import com.dongxin.scm.sm.service.StockService;
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
 * @Description: 结算单导入
 * @Author: jeecg-boot
 * @Date: 2021-07-26
 * @Version: V1.0
 */
@Api(tags = "结算单导入")
@RestController
@RequestMapping("/fm/settleImport")
@Slf4j
public class SettleImportController extends BaseController<SettleImport, SettleImportService> {

    @Autowired
    private SettleImportService settleImportService;
    @Autowired
    private StockService stockService;

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/settleImportExcel")
    @Lock4j
    protected Result<?> settleImportExcel(HttpServletRequest request, HttpServletResponse response, Class<ImportStockInfo> clazz) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            params.setNeedSave(false);
            try {
                List<SettleImport> list = ExcelImportUtil.importExcel(file.getInputStream(), SettleImport.class, params);
                long start = System.currentTimeMillis();
                int size = settleImportService.importCheck(list);

                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                return Result.OK("文件导入成功！数据行数：" + size);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error("settleImportExcel error", e);
                }
            }
        }
        return Result.error("文件导入失败！");
    }


    @AutoLog("分页列表查询")
    @ApiOperation(
            value = "分页列表查询",
            notes = "分页列表查询"
    )
    @GetMapping({"/list"})
    public Result<?> queryPageList(SettleImport param,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<SettleImport> queryWrapper = QueryGenerator.initQueryWrapper(param, req.getParameterMap());
        Page<SettleImport> page = new Page((long) pageNo, (long) pageSize);
        IPage<SettleImport> pageList = settleImportService.page(page, queryWrapper);
        settleImportService.updatePageList(pageList);
        return Result.OK(pageList);
    }



}
