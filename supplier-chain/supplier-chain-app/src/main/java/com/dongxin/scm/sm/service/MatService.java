package com.dongxin.scm.sm.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.LockInfo;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.bd.entity.CompanyTenant;
import com.dongxin.scm.bd.entity.CompanyTenantDet;
import com.dongxin.scm.bd.service.CompanyTenantDetService;
import com.dongxin.scm.bd.service.CompanyTenantService;
import com.dongxin.scm.common.enums.CommonCheckStatusEnum;
import com.dongxin.scm.common.service.DataLogService;
import com.dongxin.scm.common.service.LockService;
import com.dongxin.scm.enums.AddTypeEnum;
import com.dongxin.scm.enums.ProdClassCodeEnum;
import com.dongxin.scm.enums.SerialNoEnum;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.entity.Credit;
import com.dongxin.scm.om.service.OrderMatService;
import com.dongxin.scm.sm.entity.*;
import com.dongxin.scm.sm.enums.*;
import com.dongxin.scm.sm.mapper.MatMapper;
import com.dongxin.scm.sm.mapper.SmMaterialsMapper;
import com.dongxin.scm.sm.mapper.StockMapper;
import com.dongxin.scm.sm.vo.MatVo;
import com.dongxin.scm.utils.ScmNumberUtils;
import com.dongxin.scm.utils.SerialNoUtil;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.base.service.BaseService;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.config.mybatis.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;
import static org.hibernate.validator.internal.util.CollectionHelper.newHashMap;

/**
 * @Description: 物料信息表
 * @Author: jeecg-boot
 * @Date: 2020-09-17
 * @Version: V1.0
 */
@Service
public class MatService extends BaseService<MatMapper, Mat> {
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private OrderMatService orderMatService;
    @Autowired
    private MatService matService;
    @Resource
    private MatMapper matMapper;
    @Autowired
    private OldProdCnameService oldProdCnameService;
    @Autowired
    private DataLogService dataLogService;
    @Autowired
    private WarehouseWarrantService warehouseWarrantService;
    @Autowired
    private CompanyTenantDetService companyTenantDetService;
    @Autowired
    private CompanyTenantService companyTenantService;
    @Autowired
    private ImportStockInfoService importStockInfoService;
    @Autowired
    private ShippingManagementService shippingManagementService;
    @Autowired
    private LockService lockService;
    @Resource
    private SmMaterialsMapper smMaterialsMapper;
    @Resource
    private StockMapper stockMapper;

    //查询材料表
    public List<Mat> queryMatList(Mat mat) {
        QueryWrapper<Mat> queryWrapper = QueryGenerator.initQueryWrapper(mat, null);
        queryWrapper.lambda().isNotNull(Mat::getMatNo);
        return list(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public String updateWarehouse(List<Mat> mats, String stockHouseId, String remarks, String dischargerName, Date warehouseTime) {
        if (StrUtil.isBlank(stockHouseId)) {
            throw new ScmException("仓库信息为空，请选择仓库");
        }

        Stock stock = stockMapper.selectById(stockHouseId);

        if (stock == null) {
            throw new ScmException("不存在的仓库");
        }

        List<Mat> updateMatList = CollectionUtil.newArrayList();
        int count = 1;

        List<String> matNOList = newArrayList();
        for (Mat mat : mats) {
            mat.setUnit("件");
            BigDecimal matThick = BigDecimal.valueOf(mat.getMatThick());
            BigDecimal matWidth = BigDecimal.valueOf(mat.getMatWidth());
            BigDecimal matLen = BigDecimal.valueOf(mat.getMatLen());
            BigDecimal formula = BigDecimal.valueOf(7.85).divide(BigDecimal.valueOf(1000000000));
            Mat matAdd = new Mat();
            matAdd.setId(mat.getId());
            matAdd.setStockHouseId(stockHouseId);  //仓库id
            matAdd.setStockLocation(mat.getStockLocation()); //库存位置
            matAdd.setWtMode(mat.getWtMode());
            matAdd.setStockType(StockTypeEnum.ON_STOCK.getCode());  //库存类型(已入库)
            matAdd.setPurchasePrice(mat.getPurchasePrice());
            matAdd.setCostPrice(mat.getCostPrice());
            matAdd.setRemarks(remarks);
            matAdd.setDischargerName(dischargerName);
            matAdd.setUnit(mat.getUnit());
            matAdd.setGoodsNo(mat.getGoodsNo());
            //对 中厚板材 设置单重  单重=理论厚度*钢板宽度*钢板长度*7.85/1000000000
            if (ProdClassCodeEnum.H.getValue().equals(mat.getProdClassCode()) || (TenantContext.getTenant().equals("12") && mat.getProdCname().equalsIgnoreCase("热轧钢板"))) {
                BigDecimal pieceWeight = matThick.multiply(matWidth).multiply(matLen).multiply(formula).setScale(3, BigDecimal.ROUND_HALF_UP);
                matAdd.setPieceWeight(pieceWeight.doubleValue());
                matAdd.setMatTheoryWt(pieceWeight.doubleValue());
                mat.setPieceWeight(pieceWeight.doubleValue());
            }
            mat.setStockHouseId(stockHouseId);


            if (StrUtil.isNotBlank(remarks)) {
                mat.setRemarks(mat.getRemarks() + remarks);
            }
            inventoryService.newly(mat, count++, mat.getStockLocation());

            matAdd.setInventoryId(mat.getInventoryId());
            matAdd.setProcessingIdentification(ProcessingEnum.NO_PROCESSING.getCode());

            matNOList.add(mat.getMatNo());

            updateMatList.add(matAdd);

        }

        //修改导入入库信息状态
        QueryWrapper<ImportStockInfo> importStockInfoQueryWrapper = new QueryWrapper<>();
        importStockInfoQueryWrapper.lambda().in(ImportStockInfo::getMatNo, matNOList);
        List<ImportStockInfo> importStockInfoList = importStockInfoService.list(importStockInfoQueryWrapper);
        if (CollectionUtil.isNotEmpty(importStockInfoList)) {
            for (ImportStockInfo importStockInfo : importStockInfoList) {
                importStockInfo.setStorageStatus(StorageStatusEnum.STORED.getCode());
            }
            importStockInfoService.updateBatchById(importStockInfoList);
        }

        //设置入库时间

        if (ObjectUtil.isEmpty(warehouseTime) && !(TenantContext.getTenant().equals("5")
                || TenantContext.getTenant().equals("6") || TenantContext.getTenant().equals("7")
                || TenantContext.getTenant().equals("8") || TenantContext.getTenant().equals("13"))) {

            warehouseTime = new Date();
        }

        Date date = warehouseTime;
        String EntryNo = SerialNoUtil.getSerialNo(SerialNoEnum.STOCK_NO);
        updateMatList.forEach(u -> u.setStockNo(EntryNo).setStorageTime(date));

        updateBatchById(updateMatList);

        List<String> matIds = updateMatList.stream().map(Mat::getId).collect(Collectors.toList());

        //增加入库单
        return warehouseWarrant(matIds);

    }

    /**
     * 提交入库
     *
     * @param mats
     * @param warehouse
     * @param remarks
     * @param tenantId
     */
    @Transactional(rollbackFor = Exception.class)
    public void readyWarehouse(List<Mat> mats, String warehouse, String remarks, Integer tenantId) {

        //验证仓库信息
        if (StrUtil.isBlank(warehouse)) {
            throw new ScmException("仓库信息为空，请选择仓库");
        }

        int count = 0;
        for (Mat mat : mats) {
            count++;

            //验证是否到货
//            if (mat.getArrivalStatus().equals(ArrivalStatusEnum.PENDING.getCode())){
//                throw new ScmException(StrUtil.format("第{}条数据到货状态待确定",count));
//            }

            ImportStockInfo importStockInfo = new ImportStockInfo();
            importStockInfo.setMatNo(mat.getMatNo());
            importStockInfo.setProdCnameOther(mat.getProdCname());
            importStockInfo.setStorageStatus(mat.getWtMode());
            importStockInfo.setSurfaces(mat.getSgSign());
            importStockInfo.setStandards(mat.getCustMatSpecs());
            importStockInfo.setGoodsNo(mat.getStockLocation());
            importStockInfo.setCopyCodeNetWeight(BigDecimal.valueOf(mat.getMatNetWt()));
            importStockInfo.setQty(BigDecimal.valueOf(mat.getMatNum()));
            importStockInfo.setStockId(warehouse);
            importStockInfo.setRemarks(remarks);
            importStockInfo.setPreparationTime(new Date());
            importStockInfo.setStorageStatus(StorageStatusEnum.NOT_STORED.getCode());

            //更改 库存类型
            mat.setStockType(StockTypeEnum.ON_READY.getCode());
            updateById(mat);

            //材料号重复校验
            QueryWrapper<ImportStockInfo> importStockInfoQueryWrapper = new QueryWrapper<>();
            importStockInfoQueryWrapper.lambda().eq(ImportStockInfo::getMatNo, importStockInfo.getMatNo());
            ImportStockInfo importStockInfoInDb = importStockInfoQueryWrapper.getEntity();

            if (ObjectUtil.isNotEmpty(importStockInfoInDb)) {
                log.error(StrUtil.format("第{}条系统已有材料号为:{}的材料", count, importStockInfo.getMatNo()));
                throw new ScmException(StrUtil.format("第{}条系统已有材料号为:{}的材料", count, importStockInfo.getMatNo()));
            }
            importStockInfoService.save(importStockInfo);

        }
    }


    public Map<String, Mat> getMap(List<String> matNos) {

        Map<String, Mat> result = newHashMap();
        QueryWrapper<Mat> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(Mat::getMatNo, matNos);

        List<Mat> mats = list(queryWrapper);

        for (Mat mat : mats) {
            result.put(mat.getMatNo(), mat);
        }
        return result;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean saveMat(Mat mat, int count) {
        if (AddTypeEnum.MACHINING_ADD.getValue().equals(mat.getAddType())) {
            if (BeanUtil.isEmpty(mat.getMatNo())) {
                throw new ScmException("加工新增时，材料号不能为空");
            }
        }
        if (mat.getMatThick() <= 0 && !StrUtil.equals(mat.getSgSign().trim(), "混")) {
            if (!StrUtil.equals(mat.getProdCname(), "热轧槽钢")) {
                throw new ScmException("材料厚度必须大于0!");
            }
        }

        //岑海手动入库柳钢装车时间不为空
        if (TenantContext.getTenant().equals("2")) {
            if (ObjectUtil.isNull(mat.getCarLoadingTime())
                    && BigDecimal.valueOf(mat.getMatNetWt()).compareTo(BigDecimal.ZERO) < 0) {
                throw new ScmException(StrUtil.format("第" + count + "条红冲材料请输入柳钢装车时间。"));
            }
            if (ObjectUtil.isNull(mat.getCarLoadingTime())) {
                mat.setCarLoadingTime(new Date());
            }
        }


        //入库新增，材料号重复校验，物料物料信息表
        matService.checkMatNo(mat, count);

        //旧系统产品中文名赋值(岑海需求)
        if (TenantContext.getTenant().equals("2")) {
            oldProdCnameService.queryProdName(mat, count);

        } else {
            mat.setOldProdCname(mat.getProdCnameOther());
            if (mat.getProdCname().equalsIgnoreCase("高碳钢盘条(高线)") && TenantContext.getTenant().equals("2")){
                mat.setOldProdCname("高碳钢盘条");
            }
        }

        if ((TenantContext.getTenant().equals("5") || TenantContext.getTenant().equals("6")
                || TenantContext.getTenant().equals("7") || TenantContext.getTenant().equals("8")
                || TenantContext.getTenant().equals("13")) && ProdClassCodeEnum.H.getValue().equals(mat.getProdClassCode())) {

            mat.setCakeNo(matService.getCakeNo(mat.getMatNo()));
        }
        inventoryService.newly(mat, count, null);
        orderMatService.addOrderMat(mat);
        return save(mat);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(Mat entity) {
        if (entity.getMatThick() <= 0 && !StrUtil.equals(entity.getSgSign().trim(), "混")) {
            if (!StrUtil.equals(entity.getProdCname(), "热轧槽钢")) {
                throw new ScmException("材料厚度必须大于0!");
            }
        }
        boolean flag = super.updateById(entity);
        String tableName = Mat.class.getAnnotation(TableName.class).value();
        String dataContent = JSONObject.toJSONString(entity);
        dataLogService.dataLog(tableName, entity.getId(), dataContent);
        return flag;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<Mat> list) {
        boolean flag = super.saveBatch(list);
        String tableName = Mat.class.getAnnotation(TableName.class).value();
        for (Mat mat : list) {
            String dataContent = JSONObject.toJSONString(mat);
            dataLogService.dataLog(tableName, mat.getId(), dataContent);
        }
        return flag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Mat entity) {
        boolean flag = super.save(entity);
        String tableName = Mat.class.getAnnotation(TableName.class).value();
        String dataContent = JSONObject.toJSONString(entity);
        dataLogService.dataLog(tableName, entity.getId(), dataContent);
        return flag;
    }

    //入库新增
    @Transactional(rollbackFor = Exception.class)
    public void saveMain(MatVo matVo) {
        List<Mat> matList = matVo.getMatDetList();
        //入库单号
        String stockNo = SerialNoUtil.getSerialNo(SerialNoEnum.STOCK_NO);

        List<String> matIds = CollectionUtil.newArrayList();
        if (matList != null && matList.size() > 0) {

            //count记录循环次数
            int count = 1;
            for (Mat mat : matList) {
                if (mat.getMatThick() <= 0 && !StrUtil.equals(mat.getSgSign().trim(), "混")) {
                    if (!StrUtil.equals(mat.getProdCname(), "热轧槽钢")) {
                        throw new ScmException("材料厚度必须大于0!");
                    }
                }
                if (ObjectUtil.isEmpty(mat.getMatNum())) {
                    throw new ScmException("材料件数不能为空！");
                }
                //设置 仓库、库位、库存类型、车号、新增类型（默认新增（手动入库）)、入库单号、入库时间
                BigDecimal matThick = BigDecimal.valueOf(ScmNumberUtils.ifNullZero(mat.getMatThick()));
                BigDecimal matWidth = BigDecimal.valueOf(ScmNumberUtils.ifNullZero(mat.getMatWidth()));
                BigDecimal matLen = BigDecimal.valueOf(ScmNumberUtils.ifNullZero(mat.getMatLen()));
                if (ProdClassCodeEnum.H.getValue().equals(mat.getProdClassCode())) {
                    BigDecimal formula = BigDecimal.valueOf(7.85).divide(BigDecimal.valueOf(1000000000));
                    mat.setPieceWeight(matThick.multiply(matWidth).multiply(matLen).multiply(formula)
                            .setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
                }

                mat.setId(null).setStockHouseId(matVo.getStockHouseId())
                        .setStockType(matVo.getStockType())
                        .setRemarks(matVo.getRemarks())
                        .setCarNo(mat.getCarNo())
                        .setAddType(AddTypeEnum.ADD.getValue())
                        .setStockNo(stockNo)
                        .setCustMatSpecs(mat.generateCustMatSpecs())
                        .setCarLoadingTime(mat.getCarLoadingTime())
                        .setDischargerName(matVo.getDischargerName())
                        .setProcessingIdentification(ProcessingEnum.NO_PROCESSING.getCode());

                if (ObjectUtil.isNotNull(matVo.getStockTime())) {
                    mat.setStorageTime(matVo.getStockTime());
                } else if (TenantContext.getTenant().equals("5") || TenantContext.getTenant().equals("6")
                        || TenantContext.getTenant().equals("7") || TenantContext.getTenant().equals("8")
                        || TenantContext.getTenant().equals("13")) {
                    mat.setStorageTime(mat.getCarLoadingTime());
                } else {
                    Date date = new Date();
                    mat.setStorageTime(date);
                }
                mat.setTransportWay(TransportWayEnum.CARTAGE.getCode());
                saveMat(mat, count++);
                matIds.add(mat.getId());
            }
            //增加入库单
            warehouseWarrant(matIds);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int importMatBatch(List<Mat> list) {
        List<Mat> matList = CollectionUtil.newArrayList();
        List<Mat> duplicatedMats = CollectionUtil.newArrayList();
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
            mat.setStockType(StockTypeEnum.ON_ROUTE.getCode());
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

            if (ObjectUtil.isNull(mat.getMatKind()) && ObjectUtil.isNotNull(mat.getProdClassCode())) {
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
        if (CollectionUtil.isEmpty(matList)) {
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
                for (Mat mat : matList
                ) {
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
                if (item.getProdClassCode().equals("B")) {
                    item.setWtMode("1");
                }
                if (TenantContext.getTenant().equals("5") || TenantContext.getTenant().equals("6") || TenantContext.getTenant().equals("7") || TenantContext.getTenant().equals("8") || TenantContext.getTenant().equals("13")) {
                    item.setMatLen(NumberUtil.div(item.getMatLen(), Double.valueOf(1000.0)));
                }
            });
            matService.saveBatch(distinctMatList);
        } else {
            this.saveBatch(matList);
        }
        return duplicatedMats.size();
    }

    //物料信息表导入新增
    @Transactional(rollbackFor = Exception.class)
    public void checkMatNo(Mat mat, int count) {
        //入库新增，材料号重复校验，物料物料信息表
        if (!(ProdClassCodeEnum.B.getValue().equals(mat.getProdClassCode())
                || ProdClassCodeEnum.D.getValue().equals(mat.getProdClassCode())
                || ProdClassCodeEnum.H.getValue().equals(mat.getProdClassCode())
                || ProdClassCodeEnum.C.getValue().equals(mat.getProdClassCode())
                || ProdClassCodeEnum.Z.getValue().equals(mat.getProdClassCode())
        )
        ) {
            if (StrUtil.isBlank(mat.getMatNo())) {
                throw new ScmException(StrUtil.format("第{}条明细：请输入材料号！（产品大类为棒材、线材、中厚板材的材料除外。）", count));
            }
            QueryWrapper<Mat> matQueryWrapper = new QueryWrapper<>();
            matQueryWrapper.lambda().eq(Mat::getMatNo, mat.getMatNo());
            List<Mat> matList = list(matQueryWrapper);
            if (CollectionUtil.isNotEmpty(matList)) {
                throw new ScmException(StrUtil.format("第{}条数据，材料号重复！材料号：{}", count, mat.getMatNo()));
            }
        }

        //阳蕊明柳钢装车时间校验必填
        if (ObjectUtil.isNull(mat.getCarLoadingTime()) && (TenantContext.getTenant().equals("5")
                || TenantContext.getTenant().equals("6") || TenantContext.getTenant().equals("7")
                || TenantContext.getTenant().equals("8") || TenantContext.getTenant().equals("13"))) {
            throw new ScmException(StrUtil.format("第{}条数据请输入柳钢装车时间。", count));

        }
    }

    public String warehouseWarrant(List<String> ids) {
        List<Mat> matList = matService.listByIds(ids);
        WarehouseWarrant warehouseWarrant = new WarehouseWarrant();
        //数量合计
        int sumQty = matList.stream().map(e -> {
            if (ObjectUtil.isNull(e.getMatNum())) {
                return 0;
            }
            return e.getMatNum();
        }).reduce(0, Integer::sum);

        //重量合计
        Double sumWeight = matList.stream().map(e -> {
            if (ObjectUtil.isNull(e.getMatNetWt())) {
                return 0d;
            }
            return e.getMatNetWt();
        }).reduce(0d, Double::sum);

        warehouseWarrant.setQty(sumQty)
                .setWeight(sumWeight)
                .setStockNo(matList.get(0).getStockNo())
                .setStockId(matList.get(0).getStockHouseId())
                .setRemark(matList.get(0).getRemarks());
        if (ObjectUtil.isEmpty(matList.get(0).getStorageTime())) {
            warehouseWarrant.setStockTime(matList.get(0).getCarLoadingTime());
            for (Mat mat : matList){
                mat.setStorageTime(mat.getCarLoadingTime());
            }
        } else {
            warehouseWarrant.setStockTime(matList.get(0).getStorageTime());
        }

        warehouseWarrantService.save(warehouseWarrant);

        matList.forEach(item -> item.setWarehouseWarrantId(warehouseWarrant.getId()));
        matService.updateBatchById(matList);

        return warehouseWarrant.getId();
    }

    /**
     * 依据入库单id查询入库材料信息
     *
     * @param warehouseWarrantId
     * @return
     */
    public List<Mat> queryListByWarehouseWarrantId(String warehouseWarrantId) {
        QueryWrapper<Mat> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Mat::getWarehouseWarrantId, warehouseWarrantId);
        return list(queryWrapper);
    }

    public String getCakeNo(String matNo) {
        if (StrUtil.isBlank(matNo)) {
            return "";
        }
        return matNo.substring(matNo.length() - 6, matNo.length() - 2);
    }

    /**
     * 入库管理 导入数据校验 收获公司 字段
     */
    public void checkConsignUserName(String companyName, int count) {
        //通过当前登录账户的租户id找到所属公司
        Integer tenantId = Integer.valueOf(TenantContext.getTenant());
        String name = matMapper.queryCompanyNameByTenantId(tenantId);
        //判断表格里的公司是否与当前登录账户所属公司一致，一致则满足收货公司校验，跳出该方法
        if (name.equals(companyName)) {
            return;
        }
        //通过当前登录账户的租户id查找公司信息对照表中对应的入库收货单位校验配置项
        QueryWrapper<CompanyTenantDet> companyTenantDetQueryWrapper = new QueryWrapper<>();
        companyTenantDetQueryWrapper.lambda().eq(CompanyTenantDet::getTenantCode, tenantId);
        CompanyTenantDet companyTenantDet = companyTenantDetService.getOne(companyTenantDetQueryWrapper);
        if (ObjectUtil.isNotNull(companyTenantDet)) {
            CompanyTenant companyTenant = companyTenantService.getById(companyTenantDet.getParentId());
            //如果入库收货单位校验配置项为否，则跳出该方法
            if (companyTenant.getStorageConsigneeUnitCheck().equals(YesNoEnum.NO.getCode())) {
                return;
            }
        }
        //如果公司名称不一致，且入库收货单位校验配置项为“是”，则报错
        throw new ScmException(StrUtil.format("第{}条数据，收货公司名称与当前登录的公司不符合！收货公司名称：{}，登录的公司：{}", count, companyName, name));
    }

    /**
     * 依据库存id查询入库材料信息
     *
     * @param id
     * @return
     */
    public List<Mat> selectLastCarLoadingTime(String id, Date date) {
        List<Mat> matList = baseMapper.selectLastCarLoadingTime(id, date);
        return matList;
    }

    /**
     * 每日（入库）成本价
     */
    public BigDecimal selectAveragePrice(Integer tenantId, String prodClassCode, Date date) {
        BigDecimal averagePrice = baseMapper.selectAveragePrice(tenantId, prodClassCode, date);
        System.out.println(averagePrice);
        return averagePrice;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateArrivalStatus(List<String> creditIds, String status) {
        List<Mat> mats = baseMapper.selectBatchIds(creditIds);
        for (Mat mat : mats) {
            LockInfo lockInfo = lockService.lock(mat.getProdCnameOther());
            if (lockInfo == null) {
                throw new ScmException("请求过于频繁，请稍后重试");
            }
            try {
                Mat updateMat = new Mat();
                updateMat.setId(mat.getId());
                if (status.equals(ArrivalStatusEnum.ARRIVED.getCode())) {
                    updateMat.setArrivalStatus(ArrivalStatusEnum.ARRIVED.getCode());
                } else if (status.equals(ArrivalStatusEnum.PENDING.getCode())) {
                    //判断是否已经分货，已经分货则需撤销分货后方可执行取消到货
                    if (mat.getAllotStatus().equals(AllotStatusEnum.ALLOTED.getCode())) {
                        throw new ScmException("该货物已分货，请撤销分货后重试");
                    }
                    updateMat.setArrivalStatus(ArrivalStatusEnum.PENDING.getCode());
                } else {
                    throw new ScmException("审核状态错误");
                }
                LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
                updateMat.setArrivalChecker(sysUser.getUsername());
                updateMat.setArrivalCheckDate(new Date());
                updateMat.setMatThick(mat.getMatThick());
                updateMat.setSgSign(mat.getSgSign());
                updateMat.setAllotStatus(AllotStatusEnum.NONALLOT.getCode());//确认到货后，给分货状态默认赋值 未分货
                updateById(updateMat);

                //授信需增加可用金额字段 **

            } finally {

                lockService.releaseLock(lockInfo);
            }
        }
    }

    public List<Mat> selectByWarehouseWarrantId(String warehouseWarrantId) {
        return matMapper.selectByWarehouseWarrantId(warehouseWarrantId);
    }

    /**
     * 根据库存id查柳钢装车时间
     *
     * @param inventoryId
     * @return
     */
    public Date selectCarLodingTime(String inventoryId) {
        Date carLodingTime = baseMapper.selectCarLodingTime(inventoryId);
        return carLodingTime;
    }

    public Mat getByMatNo(String matNo) {
        QueryWrapper<Mat> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Mat::getMatNo, matNo);
        return getOne(queryWrapper);
    }

    public List<Mat> getByMatNo(List<String> matNos) {
        if (CollectionUtil.isEmpty(matNos)) {
            throw new ScmException("参数为空，请检查材料号信息");
        }
        QueryWrapper<Mat> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(Mat::getMatNo, matNos);
        return list(queryWrapper);
    }
}
