package com.dongxin.scm.sm.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.enums.ProdClassCodeEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.om.util.Constants;
import com.dongxin.scm.sm.dto.MatIdDTO;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.enums.ArrivalStatusEnum;
import com.dongxin.scm.sm.enums.StockTypeEnum;
import com.dongxin.scm.sm.enums.TransportWayEnum;
import com.dongxin.scm.sm.mapper.SmMaterialsMapper;
import com.dongxin.scm.sm.service.MatService;
import com.dongxin.scm.sm.service.OldProdCnameService;
import com.dongxin.scm.sm.service.StockService;
import com.dongxin.scm.sm.vo.MatVo;
import com.dongxin.scm.utils.ScmNumberUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.BaseController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.mybatis.TenantContext;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 物料信息表
 * @Author: jeecg-boot
 * @Date: 2020-09-17
 * @Version: V1.0
 */
@Api(tags = "物料信息表")
@RestController
@RequestMapping("/sm/mat")
@Slf4j
public class MatController extends BaseController<Mat, MatService> {

    @Autowired
    private MatService matService;

    @Autowired
    private StockService stockService;

    @Autowired
    private LockTemplate lockTemplate;

    @Autowired
    OldProdCnameService oldProdCnameService;

    @Resource
    SmMaterialsMapper smMaterialsMapper;

    @AutoLog("分页列表查询")
    @ApiOperation(
            value = "分页列表查询",
            notes = "分页列表查询"
    )
    @GetMapping({"/list"})
    public Result<?> queryPageList(Mat param,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<Mat> queryWrapper = QueryGenerator.initQueryWrapper(param, req.getParameterMap());
        Page<Mat> page = new Page<>(pageNo, pageSize);
        IPage<Mat> pageList = matService.page(page, queryWrapper);

        //显示仓库名
        List<String> stockHouseIds = newArrayList();
        for (Mat record : pageList.getRecords()) {
            stockHouseIds.add(record.getStockHouseId());
        }
        Map<String, String> houseId = stockService.getStockHouseIdMap(stockHouseIds);
        for (Mat record : pageList.getRecords()) {
            record.setStockHouseId(houseId.get(record.getStockHouseId()));
        }

        return Result.OK(pageList);
    }


    /**
     * 分页列表查询
     *
     * @param mat
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "材料-分页列表查询")
    @ApiOperation(value = "材料表-分页列表查询", notes = "材料主档表-分页列表查询")
    @GetMapping(value = "/noWarehouseList")
    public Result<?> queryNoWarehouseList(Mat mat,
                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                          HttpServletRequest req) {
        QueryWrapper<Mat> queryWrapper = QueryGenerator.initQueryWrapper(mat, req.getParameterMap());
        queryWrapper.lambda().isNull(Mat::getStockHouseId);
        if (StrUtil.isNotBlank(req.getParameter("matNos"))) {
            String matNos = req.getParameter("matNos");
            String[] str = matNos.split("\n");
            queryWrapper.lambda().in(Mat::getMatNo, str);
        }

        Page<Mat> page = new Page<>(pageNo, pageSize);
        IPage<Mat> pageList = matService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    @ApiOperation(value = "修改仓库")
    @PostMapping(value = "/updateWarehouse")
    public Result<?> updateWarehouse(@RequestBody MatIdDTO matIdDTO) {
        String tenantId = TenantContext.getTenant();

        //用入库字段作为锁头，同一租户只有一个线程会被执行
        LockInfo lockInfo = lockTemplate.lock(Constants.ADD_MAT_LOCK_HEADER + tenantId);

        if (lockInfo == null) {
            throw new ScmException("请求过于频繁，请稍后重试");
        }
        Integer formatTenantId = Integer.valueOf(tenantId);
        try {
            matService.updateWarehouse(matIdDTO.getList(), matIdDTO.getWarehouse(), matIdDTO.getRemarks(),
                    matIdDTO.getDischargerName(), matIdDTO.getWarehouseTime());
            return Result.ok("入库成功");
        } finally {
            lockTemplate.releaseLock(lockInfo);
        }

    }

    @ApiOperation(value = "提交入库")
    @PostMapping(value = "/readyWarehouse")
    public Result<?> readyWarehouse(@RequestBody MatIdDTO matIdDTO) {
        String tenantId = TenantContext.getTenant();

        //用入库字段作为锁头，同一租户只有一个线程会被执行
        LockInfo lockInfo = lockTemplate.lock(Constants.ADD_MAT_LOCK_HEADER + tenantId);

        if (lockInfo == null) {
            throw new ScmException("请求过于频繁，请稍后重试");
        }
        Integer formatTenantId = Integer.valueOf(tenantId);
        try {
            matService.readyWarehouse(matIdDTO.getList(), matIdDTO.getWarehouse(), matIdDTO.getRemarks(), formatTenantId);
            return Result.ok("入库成功");
        } finally {
            lockTemplate.releaseLock(lockInfo);
        }

    }

    /**
     * 添加
     *
     * @param mat
     * @return
     */
    @AutoLog(value = "物料信息-添加")
    @ApiOperation(value = "物料信息-添加", notes = "物料信息-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Mat mat) {
        if (ObjectUtil.isNull(mat.getMatNetWt())) {
            throw new ScmException("请选择材料重量！！");
        }
        mat.setAvailableWeight(mat.getMatNetWt());
        matService.save(mat);
        return Result.ok("添加成功！");
    }

    /**
     * 入库新增
     *
     * @param matVo
     * @return
     */
    @AutoLog(value = "入库新增")
    @ApiOperation(value = "入库新增", notes = "入库新增")
    @PostMapping(value = "/addMat")
    public Result<?> add(@RequestBody MatVo matVo) {

        String tenantId = TenantContext.getTenant();

        //用入库字段作为锁头，同一租户只有一个线程会被执行
        LockInfo lockInfo = lockTemplate.lock(Constants.ADD_MAT_LOCK_HEADER + tenantId);

        if (lockInfo == null) {
            throw new ScmException("请求过于频繁，请稍后重试");
        }
        try {

            matService.saveMain(matVo);
            return Result.ok("添加成功！");
        } finally {

            lockTemplate.releaseLock(lockInfo);
        }
    }

    /**
     * 获取对象ID
     *
     * @return
     */
    public String getId(Mat item) {
        try {
            return PropertyUtils.getProperty(item, "id").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 入库明细查询
     */
    @AutoLog(value = "入库明细查询")
    @ApiOperation(value = "入库明细查询", notes = "入库明细查询")
    @GetMapping(value = "/queryMatByWarehouseWarrantId")
    public Result<?> queryMatByWarehouseWarrantId(@RequestParam(name = "warehouseWarrantId") String warehouseWarrantId) {
        List<Mat> matList = matService.queryListByWarehouseWarrantId(warehouseWarrantId);
        return Result.OK(matList);
    }

    /**
     * 导出excel
     *
     * @param request
     */
    @AutoLog(value = "导出")
    @ApiOperation(value = "导出", notes = "导出")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Mat mat) {
        // Step.1 组装查询条件
        QueryWrapper<Mat> queryWrapper = QueryGenerator.initQueryWrapper(mat, request.getParameterMap());

        // Step.2 获取导出数据
        List<Mat> pageList = matService.list(queryWrapper);
        List<Mat> exportList = null;

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
//        mv.addObject(NormalExcelConstants.FILE_NAME, title); //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.CLASS, Mat.class);
        ExportParams exportParams = new ExportParams();
        exportParams.setAddIndex(false);
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }


    /**
     * 导入
     *
     * @return
     */
    @SneakyThrows
    @AutoLog(value = "导入")
    @ApiOperation(value = "导入", notes = "导入")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {

        //用入库字段作为锁头，同一租户只有一个线程会被执行
        LockInfo lockInfo = lockTemplate.lock(Constants.ADD_MAT_BATCH_LOCK_HEADER + TenantContext.getTenant());

        if (lockInfo == null) {
            throw new ScmException("请求过于频繁，请稍后重试");
        }
        try {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(0);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<Mat> list = ExcelImportUtil.importExcel(file.getInputStream(), Mat.class, params);
                List<Mat> matList = CollectionUtil.newArrayList();
                List<Mat> duplicatedMats = CollectionUtil.newArrayList();
                long start = System.currentTimeMillis();
                int count = 1;
                //设置默认库存类型，在途
                for (Mat mat : list) {
                    count++;
                    //新增材料 材料号重复则不做处理
                    QueryWrapper<Mat> matQueryWrapper = new QueryWrapper<>();
                    matQueryWrapper.lambda().eq(Mat::getMatNo, mat.getMatNo());
                    List<Mat> mats = matService.list(matQueryWrapper);
                    if (CollectionUtil.isNotEmpty(mats)) {
                        duplicatedMats.addAll(mats);
                        continue;
                    }
                    //广东材料号不能为空
                    if (TenantContext.getTenant().equals("12") && StrUtil.isBlank(mat.getMatNo())){
                        throw new ScmException(StrUtil.format("第{}条数据材料号为空。", count));
                    }
                    mat.setStockType(StockTypeEnum.ON_ROUTE.getCode());
                    mat.setTransportWay(TransportWayEnum.CARTAGE.getCode());
                    mat.setArrivalStatus(ArrivalStatusEnum.PENDING.getCode());
                    if (ObjectUtil.isNull(mat.getMatNum())
                            || mat.getProdClassCode().equals(ProdClassCodeEnum.B.getValue())
                            || mat.getProdClassCode().equals(ProdClassCodeEnum.D.getValue())
                    ) {
                        mat.setMatNum(1);
                    }
                    mat.setCountNumber(mat.getMatNum());
                    //收货公司校验
                    if (StrUtil.isNotEmpty(mat.getConsignUserName())) {
                        matService.checkConsignUserName(mat.getConsignUserName(), count);
                    }
                    //阳蕊明公司需要存块号
                    //yrm 柳钢装车时间必填
                    //出厂时刻转柳钢装车时间
                    mat.setCarLoadingTime(DateUtil.parse(mat.getDeliveryTime()));

                    if (ObjectUtil.isNull(mat.getMatKind())) {
                        switch (mat.getProdClassCode()) {
                            case "A":
                                mat.setMatKind("SM");
                                break;
                            case "B":
                            case "C":
                            case "D":
                            case "E":
                                mat.setMatKind("BW");
                                break;
                            case "F":
                                mat.setMatKind("HR");
                                break;
                            case "G":
                                mat.setMatKind("CR");
                                break;
                            case "H":
                                mat.setMatKind("HP");
                                break;
                            case "I":
                                mat.setMatKind("IR");
                                break;
                        }
                    }

                    mat.setCustMatSpecs(mat.generateCustMatSpecs());

                    //重量 = 净重 + 磅差
                    double weight = NumberUtil.add(mat.getMatNetWt(), Double.valueOf(ScmNumberUtils.ifNullZero(mat.getMatDiscrepWt())));
                    mat.setMatNetWt(weight);
                    mat.setAvailableWeight(weight);

                    //旧系统产品中文名赋值(岑海需求)
                    if (TenantContext.getTenant().equals("2")) {
                        oldProdCnameService.queryProdName(mat, count);
                    } else {
                        mat.setOldProdCname(mat.getProdCnameOther());
                    }

                    if (TenantContext.getTenant().equals("5") || TenantContext.getTenant().equals("6") || TenantContext.getTenant().equals("7") || TenantContext.getTenant().equals("8") || TenantContext.getTenant().equals("13")) {
                        if (ProdClassCodeEnum.H.getValue().equals(mat.getProdClassCode())) {
                            mat.setCakeNo(matService.getCakeNo(mat.getMatNo()));
                        }
                        if (ProdClassCodeEnum.B.getValue().equals(mat.getProdClassCode())) {
                            mat.setOldProdCname("螺纹钢");
                            mat.setProdCnameOther("螺纹钢");
                        }
                        if (ObjectUtil.isNull(mat.getCarLoadingTime())) {
                            throw new ScmException(StrUtil.format("第{}条数据请输入柳钢装车时间。", count));
                        }
                    }
                    matList.add(mat);
                }
                if(CollectionUtil.isEmpty(matList)){
                    throw new ScmException("材料重复入库！");
                }

                if ("B".equals(matList.get(0).getProdClassCode()) || "D".equals(matList.get(0).getProdClassCode())) {
                    matList.forEach(i -> {
                        if (!i.getProdClassCode().equals("B")) {
                            throw new ScmException("导入的材料类型须一致");
                        }
                    });
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    List<Mat> distinctMatList = matList.stream()
                            .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> StrUtil.format("{}-{}-{}-{}-{}-{}", o.getProdCname(), o.getProdCnameOther(), o.getCustMatSpecs(), o.getSgSign(), o.getCarNo(), simpleDateFormat.format(o.getCarLoadingTime()))))), ArrayList::new));
                    distinctMatList.forEach(item -> {
                        double matNetWt = 0.0;
                        int num = 0;
                        double matDiscrepWt = 0.0;
                        double matActWt = 0.0;
                        for (Mat mat : matList) {
                            if (item.getProdCnameOther().equals(mat.getProdCnameOther())
                                    && item.getProdCname().equals(mat.getProdCname())
                                    && item.getCustMatSpecs().equals(mat.getCustMatSpecs())
                                    && item.getSgSign().equals(mat.getSgSign())
                                    && item.getCarNo().equals(mat.getCarNo())
                                    && simpleDateFormat.format(item.getCarLoadingTime()).equals(simpleDateFormat.format(mat.getCarLoadingTime()))
                            ) {
                                if ("B".equals(matList.get(0).getProdClassCode())) {
                                    matNetWt = mat.getMatLen().compareTo(0.0) > 0 ? NumberUtil.add(Double.valueOf(smMaterialsMapper.queryMaterialsWeight(mat)), Double.valueOf(matNetWt)) : NumberUtil.add(NumberUtil.add(mat.getMatNetWt(), Double.valueOf(ScmNumberUtils.ifNullZero(mat.getMatDiscrepWt()))), matNetWt);
                                } else {
                                    matNetWt = NumberUtil.add(NumberUtil.add(mat.getMatNetWt(), Double.valueOf(ScmNumberUtils.ifNullZero(mat.getMatDiscrepWt()))), matNetWt);
                                }
                                num += mat.getMatNum();
                                matDiscrepWt = NumberUtil.add(mat.getMatDiscrepWt(), Double.valueOf(matDiscrepWt));
                                matActWt = NumberUtil.add(mat.getMatActWt(), Double.valueOf(matActWt));
                            }
                        }
                        item.setMatNetWt(matNetWt);
                        item.setMatNum(num);
                        item.setMatDiscrepWt(matDiscrepWt);
                        item.setMatActWt(matActWt);
                        if(item.getProdClassCode().equals("B")) {
                            item.setWtMode("1");
                        }
                        if (TenantContext.getTenant().equals("5") || TenantContext.getTenant().equals("6") || TenantContext.getTenant().equals("7") || TenantContext.getTenant().equals("8") || TenantContext.getTenant().equals("13")) {
                            item.setMatLen(NumberUtil.div(item.getMatLen(), Double.valueOf(1000.0)));
                        }
                    });
                    matService.saveBatch(distinctMatList);
                } else {
                    matService.saveBatch(matList);
                }


                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
                //update-end-author:taoyan date:20190528 for:批量插入数据
                return Result.ok("导入数据行数：" + list.size() + "，导入成功数：" + (list.size() - duplicatedMats.size()) + ",材料号重复数：" + duplicatedMats.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败：" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败！");
        } finally {
            lockTemplate.releaseLock(lockInfo);
        }
    }
    @AutoLog(value = "到货状态更新")
    @ApiOperation(value = "到货状态更新", notes = "到货状态更新")
    @GetMapping("/updateArrivalStatus")
    public Result<?> updateArrivalStatus(@RequestParam(name = "ids") String ids, @RequestParam(name = "status") String status){
        List<String> creditIds = Arrays.asList(ids.split(","));
        matService.updateArrivalStatus(creditIds, status);
        return Result.ok("审核成功");
    }
}
