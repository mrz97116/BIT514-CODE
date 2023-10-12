package com.dongxin.scm.sm.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import com.dongxin.scm.sm.entity.EquipmentSuppliesDet;
import com.dongxin.scm.sm.service.EquipmentSuppliesDetService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.stream.Collectors;

/**
 * @Description: 设备物资明细表
 * @Author: jeecg-boot
 * @Date: 2021-03-02
 * @Version: V1.0
 */
@Api(tags = "设备物资明细表")
@RestController
@RequestMapping("/sm/equipmentSuppliesDet")
@Slf4j
public class EquipmentSuppliesDetController extends BaseController<EquipmentSuppliesDet, EquipmentSuppliesDetService> {

    @Autowired
    private EquipmentSuppliesDetService service;
    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "设备物资明细表通过主表ID查询")
    @ApiOperation(value = "设备物资明细表主表ID查询", notes = "设备物资明细表-通主表ID查询")
    @GetMapping(value = "/queryEquipmentSuppliesDetByMainId")
    public Result<?> queryEquipmentSuppliesDetListByMainId(@RequestParam(name = "id", required = true) String id) {
        QueryWrapper<EquipmentSuppliesDet> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(EquipmentSuppliesDet::getParentId, id);
        Page<EquipmentSuppliesDet> page = new Page<EquipmentSuppliesDet>(1, 10);
        IPage<EquipmentSuppliesDet> pageList = service.page(page, queryWrapper);
        return Result.OK(pageList);
    }



    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(1);
            params.setHeadRows(1);
            params.setNeedSave(false);
            try {
                List<EquipmentSuppliesDet> list = ExcelImportUtil.importExcel(file.getInputStream(), EquipmentSuppliesDet.class, params);
                service.importExcel(list);
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
