package com.dongxin.scm.fm.controller;

import com.dongxin.scm.fm.entity.AcceptanceBank;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.fm.entity.PredictPrice;
import com.dongxin.scm.fm.service.PredictPriceService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description: 预测单价表
 * @Author: jeecg-boot
 * @Date:   2021-12-08
 * @Version: V1.0
 */
@Api(tags="预测单价表")
@RestController
@RequestMapping("/fm/fmPredictPrice")
@Slf4j
public class PredictPriceController extends BaseController<PredictPrice, PredictPriceService> {


 @Autowired
 PredictPriceService predictPriceService;

 @RequestMapping(value = "/importExcel")
 public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response, Class<PredictPrice> clazz) {
  MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
  Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
  for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
   MultipartFile file = entity.getValue();
   ImportParams params = new ImportParams();
   params.setTitleRows(0);
   params.setHeadRows(1);
   params.setNeedSave(false);
   try {
    List<PredictPrice> list = ExcelImportUtil.importExcel(file.getInputStream(), clazz, params);
    predictPriceService.saveBatch(list);
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


}
