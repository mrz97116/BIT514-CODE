package com.dongxin.scm.cm.controller;

import cn.hutool.core.util.StrUtil;
import com.dongxin.scm.cm.entity.JxProtocol;
import com.dongxin.scm.cm.service.JxProtocolService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.BaseController;
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
import java.util.stream.Collectors;

/**
 * @Description: 经销年度协议
 * @Author: jeecg-boot
 * @Date: 2020-12-29
 * @Version: V1.0
 */
@Api(tags = "经销年度协议")
@RestController
@RequestMapping("/cm/jxProtocol")
@Slf4j
public class JxProtocolController extends BaseController<JxProtocol, JxProtocolService> {

    @Autowired
    private JxProtocolService jxProtocolService;

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(false);
            try {
                List<JxProtocol> list = ExcelImportUtil.importExcel(file.getInputStream(), JxProtocol.class, params);

                list = list.stream().filter(i -> StrUtil.isNotBlank(i.getAddress())).collect(Collectors.toList());


                List<JxProtocol> listInDb = jxProtocolService.list();

                List<String> namesInDb = listInDb.stream().map(JxProtocol::getBranchCompany).collect(Collectors.toList());

                list = list.stream().filter(i -> !namesInDb.contains(i.getBranchCompany())).collect(Collectors.toList());

                jxProtocolService.saveBatch(list);
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
