package com.dongxin.scm.sm.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.sm.entity.Receiving;
import com.dongxin.scm.sm.service.ReceivingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description: 收料人信息
 * @Author: jeecg-boot
 * @Date: 2021-01-13
 * @Version: V1.0
 */
@Api(tags = "收料人信息")
@RestController
@RequestMapping("/sm/receiving")
@Slf4j
public class ReceivingController extends BaseController<Receiving, ReceivingService> {
    @Autowired
    ReceivingService receivingService;

    /**
     * 添加
     *
     * @param receiving
     * @return
     */
    @AutoLog(value = "收料人信息-添加")
    @ApiOperation(value = "收料人信息-添加", notes = "收料人信息-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Receiving receiving) {
        receivingService.saveReceiving(receiving);
        return Result.OK("添加成功！");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */

    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response, Class<Receiving> clazz) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            params.setNeedSave(false);
            try {
                List<Receiving> list = ExcelImportUtil.importExcel(file.getInputStream(), clazz, params);

                //update-begin-author:taoyan date:20190528 for:批量插入数据
                long start = System.currentTimeMillis();
                receivingService.saveBatch(list);
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
}
