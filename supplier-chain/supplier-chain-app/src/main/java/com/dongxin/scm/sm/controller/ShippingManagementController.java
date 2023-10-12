package com.dongxin.scm.sm.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.om.entity.SalesOrderDet;
import com.dongxin.scm.sm.dto.ShippingManagementDTO;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.entity.ShippingManagement;
import com.dongxin.scm.sm.enums.ShippingMatStatusEnum;
import com.dongxin.scm.sm.service.MatService;
import com.dongxin.scm.sm.service.PrepareOrderService;
import com.dongxin.scm.sm.service.ShippingManagementService;
import com.dongxin.scm.sm.vo.OptionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.shiro.SecurityUtils;
import org.checkerframework.checker.units.qual.A;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 船运管理
 * @Author: jeecg-boot
 * @Date: 2021-07-20
 * @Version: V1.0
 */
@Api(tags = "船运管理")
@RestController
@RequestMapping("/sm/shippingManagement")
@Slf4j
public class ShippingManagementController extends BaseController<ShippingManagement, ShippingManagementService> {
    @Autowired
    private MatService matService;
    @Autowired
    private ShippingManagementService shippingManagementService;

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
    public Result<?> queryPageList(ShippingManagement param,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {

        QueryWrapper<ShippingManagement> queryWrapper = QueryGenerator.initQueryWrapper(param, req.getParameterMap());
        if (StrUtil.isNotBlank(req.getParameter("matNos"))) {
            String matNos = req.getParameter("matNos");
            String[] str = matNos.split("\n");
            queryWrapper.lambda().in(ShippingManagement::getMatNo, str);
        }
        if (StrUtil.isNotBlank(req.getParameter("matThicks"))) {
            String matThicks = req.getParameter("matThicks");
            String[] str = matThicks.split(",");
            queryWrapper.lambda().in(ShippingManagement::getMatThick, str);
        }
        queryWrapper.lambda().eq(ShippingManagement::getMatStatus, ShippingMatStatusEnum.ON_BOARD.getCode());
        Page<ShippingManagement> page = new Page<>(pageNo, pageSize);
        IPage<ShippingManagement> pageList = shippingManagementService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 获取对象ID
     *
     * @return
     */
    public String getId(ShippingManagement item) {
        try {
            return PropertyUtils.getProperty(item, "id").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 导出excel
     *
     * @param request
     */
    @AutoLog(value = "导出")
    @ApiOperation(value = "导出", notes = "导出")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, ShippingManagement shippingManagement) {
        // Step.1 组装查询条件
        QueryWrapper<ShippingManagement> queryWrapper = QueryGenerator.initQueryWrapper(shippingManagement, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // Step.2 获取导出数据
        List<ShippingManagement> pageList = shippingManagementService.list(queryWrapper);
        List<ShippingManagement> exportList = null;

        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            exportList = pageList.stream().filter(item -> selectionList.contains(getId(item))).collect(Collectors.toList());
        } else {
            exportList = pageList;
        }

        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "船运单管理");
        mv.addObject(NormalExcelConstants.CLASS, ShippingManagement.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("船运单管理", "导出人:" + sysUser.getRealname(), "船运单管理"));
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }

    /**
     * 导入
     *
     * @return
     */
    //sneakythrows作用是为了用try{}catch{}捕捉异常，添加之后会在代码编译时自动捕获异常
    @SneakyThrows
    @AutoLog(value = "导入")
    @ApiOperation(value = "导入", notes = "导入")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        //MultipartHttpServletRequest提供额外的方法去处理request请求中的各种功能，可以处理上传的文件
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        //获取request中的多个文件，并返回map集合
        Map<String, MultipartFile> fileMap = multipartHttpServletRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();//获取上传文件对象
            ImportParams importParams = new ImportParams();//导入参数设置
            importParams.setTitleRows(0);//没有标题头
            importParams.setHeadRows(1);//字段头
            importParams.setNeedSave(false);//是否需要保存上传的Excel,默认为false
            try {
                //ExcelImportUtil.importExcel：Excel 导入，数据源IO流,不返回校验结果
                //excel中的ShippingManagement对象集合
                List<ShippingManagement> importShippingManagementList = ExcelImportUtil.importExcel(file.getInputStream(), ShippingManagement.class, importParams);

                importShippingManagementList = importShippingManagementList.stream().filter(i -> StrUtil.isNotBlank(i.getMatNo())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(importShippingManagementList)) {
                    String dispatchShipBillNo = importShippingManagementList.get(0).getDispatchShipBillNo();
                    String shipBillNo = importShippingManagementList.get(0).getShipNo();

                    for (ShippingManagement shippingManagement : importShippingManagementList) {
                        shippingManagement.setDispatchShipBillNo(dispatchShipBillNo);
                        shippingManagement.setShipNo(shipBillNo);
                        shippingManagement.setMatStatus(ShippingMatStatusEnum.ON_BOARD.getCode());
                    }
                }

                //ShippingManagement表中，船单号、材料号重复的ShippingManagement对象集合
                List<ShippingManagement> shippingManagementList = CollectionUtil.newArrayList();
                int importShippingManagementIndex = 0;
                List<String> dumplicateMatNos = newArrayList();
                for (ShippingManagement importShippingManagement : importShippingManagementList) {
                    QueryWrapper<ShippingManagement> shippingManagementQueryWrapper = new QueryWrapper<>();
                    importShippingManagementIndex++;
                    //新增船单，材料号、规格、尺寸重复则报错不处理，提示哪条数据重复
                    shippingManagementQueryWrapper.lambda()
                            .eq(ShippingManagement::getMatNo, importShippingManagement.getMatNo());
                    ShippingManagement one = shippingManagementService.getOne(shippingManagementQueryWrapper);
                    //材料号重复则不做处理，记录重复数据，并跳出foreach循环
                    if (ObjectUtil.isNotNull(one)) {
                        dumplicateMatNos.add(importShippingManagement.getMatNo());
                        continue;
                    }
                    //匹配材料表中的材料号
                    QueryWrapper<Mat> matQueryWrapper = new QueryWrapper<>();
                    matQueryWrapper.lambda().eq(Mat::getMatNo, importShippingManagement.getMatNo());
                    Mat mat = matService.getOne(matQueryWrapper);
                    //如果可以匹配到材料，将船运管理单的物料状态设为即将入库，并记录对应材料id编码
                    if (ObjectUtil.isNotNull(mat)) {
                        //将船运管理单中的船号更新到对应材料中，并更改材料的库存状态为虚拟状态
                        shippingManagementService.updateMatByShippingManagement(mat, importShippingManagement);
                        //将材料表中的id更新到对应船运管理单中，并设置物料状态为即将入库
                        ShippingManagement updatedShippingManagement = shippingManagementService.updateShippingManagementByMat(mat, importShippingManagement);

                        shippingManagementList.add(updatedShippingManagement);
                    } else {
                        return Result.error(StrUtil.format("第{}条数据找不到相应物料信息，文件导入失败！", importShippingManagementIndex));
                    }
                    shippingManagementQueryWrapper.clear();
                }
                shippingManagementService.saveBatch(shippingManagementList);
                String result = "导入数据行数：" + importShippingManagementList.size()
                        + "，导入成功数：" + shippingManagementList.size();
                if (CollectionUtil.isNotEmpty(dumplicateMatNos)) {
                    result = result +  "重复材料:" + String.join(",", dumplicateMatNos);
                }
                return Result.ok(result);

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败：" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error("船运单导入失败", e);
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    @ApiOperation(value = "批量添加备注")
    @PostMapping(value = "/batchAddRemarks")
    public Result<?> batchAddRemarks(@RequestBody ShippingManagementDTO shippingManagementDTO) {
        shippingManagementService.updateBatchRemarks(shippingManagementDTO.getList(), shippingManagementDTO.getRemark());
        return Result.ok("添加成功");
    }

    @GetMapping(value = "/queryPrepareOrderDetById")
    public Result<?> queryPrepareOrderDetById(@RequestParam(name = "id") String id) {
        List<ShippingManagement> list = shippingManagementService.queryPrepareOrderDetById(id);
        return Result.OK(list);
    }

    @GetMapping(value = "/delConnectFromPrepareOrder")
    public Result<?> delConnectFromPrepareOrder(@RequestParam(name = "id") String id) {
        shippingManagementService.delConnectFromPrepareOrder(id);
        return Result.OK("删除成功");
    }

    @GetMapping(value = "/batchDelConnectFromPrepareOrder")
    public Result<?> batchDelConnectFromPrepareOrder(@RequestParam(name = "ids") String ids) {
        List<String>  shippingManagementIds = Arrays.asList(ids.split(","));
        shippingManagementService.delConnectFromPrepareOrder(shippingManagementIds);
        return Result.OK("删除成功");
    }

    @GetMapping(value = "getThicks")
    public Result<?> getThicks(@RequestParam(name = "prodClassCode") String prodClassCode) {
        List<OptionVO> list = shippingManagementService.getThicks(prodClassCode);
        return Result.OK(list);
    }

    /**
     * 批量添加备注
     * @param ids
     * @param remark
     * @return
     */
    @RequestMapping(value = "/updatePrepareCustomer")
    public Result<?> batchAddRemarks(@RequestParam(name = "ids") String ids,
                                           @RequestParam(name = "remark") String remark) {
        shippingManagementService.batchAddRemarks(ids, remark);
        return Result.ok("添加备注成功");

    }
    /**
     * 通过提货委托单id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "船运单材料通过提货委托单ID查询")
    @ApiOperation(value = "船运单材料通过提货委托单ID查询", notes = "船运单材料通过提货委托单ID查询")
    @GetMapping(value = "/queryShippingManagementDetById")
    public Result<?> queryShippingManagementDetById(@RequestParam(name = "id", required = true) String id,
                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        QueryWrapper<ShippingManagement> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ShippingManagement::getDeliveryCommissionId, id);
        Page<ShippingManagement> page = new Page<>(pageNo, pageSize);
        IPage<ShippingManagement> pageList = shippingManagementService.page(page, queryWrapper);
        return Result.OK(pageList);
    }
}

