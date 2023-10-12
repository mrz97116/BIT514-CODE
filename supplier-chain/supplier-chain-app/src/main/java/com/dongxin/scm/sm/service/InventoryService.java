package com.dongxin.scm.sm.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.annotation.Lock4j;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.bd.mapper.CompanyTenantMapper;
import com.dongxin.scm.bd.vo.CompanyTenantVo;
import com.dongxin.scm.common.service.DataLogService;
import com.dongxin.scm.enums.ProdClassCodeEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.sm.dto.InventoryAllotDTO;
import com.dongxin.scm.sm.dto.InventoryDto;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.entity.InventoryAllot;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.entity.PrepareCustomer;
import com.dongxin.scm.sm.enums.CutdealEnum;
import com.dongxin.scm.sm.enums.WtMethodEnum;
import com.dongxin.scm.sm.mapper.InventoryMapper;
import com.dongxin.scm.sm.mapper.SmMaterialsMapper;
import com.dongxin.scm.utils.ScmNumberUtils;
import me.zhyd.oauth.utils.StringUtils;
import org.jeecg.common.system.base.service.BaseService;
import org.jeecg.config.mybatis.TenantContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 库存信息
 * @Author: jeecg-boot
 * @Date: 2020-12-03
 * @Version: V1.0
 */
@Service
public class InventoryService extends BaseService<InventoryMapper, Inventory> {
    @Autowired
    private DataLogService dataLogService;

    @Autowired
    private InventoryAllotService inventoryAllotService;

    @Resource
    private SmMaterialsMapper smMaterialsMapper;

    @Resource
    private CompanyTenantMapper companyTenantMapper;

    @Autowired
    private PrepareCustomerService prepareCustomerService;

    @Lock4j
    @Transactional(rollbackFor = Exception.class)
    public void deductionAvailable(Inventory inventory, double qty, double weight) {
        inventory.setAvailableWeight(inventory.getAvailableWeight() - weight);
        inventory.setAvailableQty(inventory.getAvailableQty() - qty);
        updateById(inventory);
    }

    @Lock4j
    @Transactional(rollbackFor = Exception.class)
    public void deductionPractical(Inventory inventory, double qty, double weight) {
        inventory.setWeight(inventory.getWeight() - weight);
        inventory.setMatNum(inventory.getMatNum() - qty);
        updateById(inventory);
    }

    @Lock4j
    @Transactional(rollbackFor = Exception.class)
    public void deductionAvailableById(String id, double qty, double weight) {
        Inventory inventory = getById(id);
        inventory.setAvailableWeight(inventory.getAvailableWeight() - weight);
        inventory.setAvailableQty(inventory.getAvailableQty() - qty);
        updateById(inventory);
    }

    @Lock4j
    @Transactional(rollbackFor = Exception.class)
    public void deductionPracticalById(String id, double qty, double weight) {
        Inventory inventory = getById(id);
        inventory.setWeight(inventory.getWeight() - weight);
        inventory.setMatNum(inventory.getMatNum() - qty);
        updateById(inventory);
    }

    @Lock4j
    @Transactional(rollbackFor = Exception.class)
    public void newly(Mat mat, int count, String stockLocation) {
        CompanyTenantVo companyTenantVo = companyTenantMapper.queryPaymentConfiguration(Integer.valueOf(TenantContext.getTenant()));
        String cutdealFlag = companyTenantVo.getCutdealConfiguration();
        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();

        //获取单重
        double matOrderWeight = 0.0;
        if (ObjectUtil.isNotNull(mat.getPieceWeight())) {
            matOrderWeight = mat.getPieceWeight();
        }


        Inventory inventory;
        //棒线无材料号查询
        if (ProdClassCodeEnum.B.getValue().equals(mat.getProdClassCode())
                || ProdClassCodeEnum.D.getValue().equals(mat.getProdClassCode())
                || ProdClassCodeEnum.H.getValue().equals(mat.getProdClassCode())
                || ProdClassCodeEnum.C.getValue().equals(mat.getProdClassCode())
                || ProdClassCodeEnum.Z.getValue().equals(mat.getProdClassCode())
        ) {
            queryWrapper.lambda().eq(Inventory::getProdClassCode, mat.getProdClassCode())
                    .eq(Inventory::getStockHouseId, mat.getStockHouseId())
                    .eq(Inventory::getSgSign, mat.getSgSign())
                    .eq(Inventory::getProdCname, mat.getProdCname())
                    .eq(Inventory::getProdCnameOther, mat.getProdCnameOther())
                    .eq(Inventory::getMatThick, mat.getMatThick())
                    .eq(Inventory::getWtMode, mat.getWtMode())
                    .gt(Inventory::getWeight, 0);
            if (ObjectUtil.isNull(mat.getMatLen())) {
                queryWrapper.lambda().isNull(Inventory::getMatLen);
            } else {
                queryWrapper.lambda().eq(Inventory::getMatLen, mat.getMatLen());
            }


            if (mat.getMatWidth() != null) {
                queryWrapper.lambda().eq(Inventory::getMatWidth, mat.getMatWidth());
            }
        } else {
            if (StrUtil.isBlank(mat.getMatNo())) {
                throw new ScmException(StrUtil.format("第{}条明细：请输入材料号！（产品大类为棒材、线材、中厚板材的材料除外。）", count));
            }

            //检查材料号是否重复入库
            queryWrapper.lambda().eq(Inventory::getMatNo, mat.getMatNo());
            //Inventory库存表查询

        }
        List<Inventory> inventoryList = list(queryWrapper);
        if (ProdClassCodeEnum.G.getValue().equals(mat.getProdClassCode())
                || ProdClassCodeEnum.F.getValue().equals(mat.getProdClassCode())
        ) {
            queryWrapper.lambda().gt(Inventory::getWeight, 0);
            inventoryList = list(queryWrapper);
            if (CollectionUtil.isNotEmpty(inventoryList) && StrUtil.isBlank(mat.getPlatingWeight())) {
                throw new ScmException(StrUtil.format("第{}条明细：材料号重复！", count));
            }

        }
        if (CollectionUtil.isNotEmpty(inventoryList)) {
            inventory = inventoryList.get(0);
            /**阳蕊明中板无材料号的物料入库库存合并
             * 阳蕊明中板有材料号的物料入库库存不合并
             * updateBy liujiuzhi 2021.6.3**/
            if (CutdealEnum.NO.getCode().equals(cutdealFlag) && StringUtils.isNotEmpty(mat.getMatNo()) && (ProdClassCodeEnum.H.getValue().equals(mat.getProdClassCode()))) {
                inventory = null;
            }
            if ("车".equals(mat.getUnit())) {
                inventory = null;
            }
            if ("块".equals(mat.getUnit())) {
                inventory = null;
            }
            if (TenantContext.getTenant().equals("12") && ProdClassCodeEnum.H.getValue().equals(mat.getProdClassCode())) {
                inventory = null;
            }
        } else {
            inventory = null;
        }


        //无库存信息 更新库存
        if (ObjectUtil.isNull(inventory)) {

            Inventory initEntity = new Inventory();
            initEntity.setMatLen(mat.getMatLen());
            initEntity.setMatThick(mat.getMatThick());
            initEntity.setMatWidth(mat.getMatWidth());
            initEntity.setWeight(mat.getMatNetWt());
            initEntity.setProdClassCode(mat.getProdClassCode());
            initEntity.setProdCname(mat.getProdCname());
            initEntity.setUnit(mat.getUnit());
            initEntity.setProdCnameOther(mat.getProdCnameOther());
            initEntity.setSgSign(mat.getSgSign());
            initEntity.setOldProdCname(mat.getOldProdCname());
            initEntity.setMatNum(mat.getMatNum().doubleValue());
            initEntity.setGoodsNo(mat.getGoodsNo());
            if ("12".equals(TenantContext.getTenant())) {
                if (("H".equals(mat.getProdClassCode()))) {
                    initEntity.setCarNo(mat.getCarNo());
                }
                initEntity.setRemark(mat.getRemarks());
                initEntity.setStockTime(mat.getStockTime());
            }
            initEntity.setPlatingWeight(mat.getPlatingWeight());
            initEntity.setSurfaceTreatment(mat.getSurfaceTreatment());
            if (!(ProdClassCodeEnum.B.getValue().equals(mat.getProdClassCode())
                    || ProdClassCodeEnum.D.getValue().equals(mat.getProdClassCode())
                    || ProdClassCodeEnum.C.getValue().equals(mat.getProdClassCode()))) {
                //如果不是棒材、线材、型材，则更新库存中的船运管理相关信息
                initEntity.setShippingManagementNo(mat.getShippingManagementNo());
                initEntity.setRemark(mat.getRemarks());
            }
            if (StringUtils.isNotEmpty(mat.getStockLocation())) {
                initEntity.setStockLocation(mat.getStockLocation()); // addBy liujiazhi 2021/05/14  库存库位
            } else {
                initEntity.setStockLocation(stockLocation);
            }
            if (StringUtils.isNotEmpty(mat.getCakeNo())) { //addBy liujiazhi 2021/05/27 库存块号
                initEntity.setCakeNo(mat.getCakeNo());
            } else {
                initEntity.setCakeNo(mat.getMatNo());
            }

            //入库 棒材 线材 型材 时，若计重方式为理计，单重取sm_material表里对应数据的单重
            if ((ProdClassCodeEnum.B.getValue().equals(mat.getProdClassCode())
                    || ProdClassCodeEnum.D.getValue().equals(mat.getProdClassCode())
                    || ProdClassCodeEnum.C.getValue().equals(mat.getProdClassCode()))
                    && WtMethodEnum.THEORY.getValue().equals(mat.getWtMode())) {
                initEntity.setMatNo("");
                double materialsWeight = smMaterialsMapper.queryMaterialsWeight(mat);
                //判断是否从materials表中查到数据
                if (!new Double(0.0).equals(materialsWeight)) {
                    matOrderWeight = materialsWeight;
                }
            } else {
                initEntity.setMatNo(mat.getMatNo());
            }
            initEntity.setCustMatSpecs(mat.getCustMatSpecs());
            if (ProdClassCodeEnum.F.getValue().equals(mat.getProdClassCode()) ||
                    ProdClassCodeEnum.G.getValue().equals(mat.getProdClassCode())) {
                initEntity.setCustMatSpecs(mat.getMatThick() + "*" + mat.getMatWidth() + "*C");
            }
            initEntity.setMatKind(mat.getMatKind());
            initEntity.setAvailableQty(mat.getMatNum().doubleValue());
            initEntity.setAvailableWeight(mat.getMatNetWt());
            initEntity.setStockHouseId(mat.getStockHouseId());
            initEntity.setPieceWeight(ScmNumberUtils.setScale(matOrderWeight));
            //对 中板 设置单重  单重=理论厚度*钢板宽度*钢板长度*7.85/1000000000
            BigDecimal matThick = BigDecimal.valueOf(ScmNumberUtils.ifNullZero(mat.getMatThick()));
            BigDecimal matWidth = BigDecimal.valueOf(ScmNumberUtils.ifNullZero(mat.getMatWidth()));
            BigDecimal matLen = BigDecimal.valueOf(ScmNumberUtils.ifNullZero(mat.getMatLen()));
            BigDecimal formula = BigDecimal.valueOf(7.85).divide(BigDecimal.valueOf(1000000000));
            if (ProdClassCodeEnum.H.getValue().equals(mat.getProdClassCode())) {
                initEntity.setPieceWeight(matThick.multiply(matWidth).multiply(matLen).multiply(formula)
                        .setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
            }
            initEntity.setWtMode(mat.getWtMode());

//            物料编码
            if (StrUtil.isNotBlank(mat.getMatCode())) {
                initEntity.setMatCode(mat.getMatCode());
            }
            save(initEntity);
            //材料关联库存
            mat.setInventoryId(initEntity.getId());
        } else {
            inventory.setWeight(inventory.getWeight() + mat.getMatNetWt());
            inventory.setStockHouseId(mat.getStockHouseId());
            inventory.setMatNum(inventory.getMatNum() + mat.getMatNum());
            inventory.setAvailableWeight(inventory.getAvailableWeight() + mat.getMatNetWt());
            inventory.setAvailableQty(inventory.getAvailableQty() + mat.getMatNum());
            inventory.setSurfaceTreatment(mat.getSurfaceTreatment());
            inventory.setPlatingWeight(mat.getPlatingWeight());
            //物料编码
            if (StrUtil.isNotBlank(mat.getMatCode())) {
                inventory.setMatCode(mat.getMatCode());
            }
//            inventory.setPieceWeight(matOrderWeight);
            if (StringUtils.isNotEmpty(mat.getStockLocation())) {
                inventory.setStockLocation(mat.getStockLocation()); // addBy liujiazhi 2021/05/14  库存库位
            } else {
                inventory.setStockLocation(stockLocation);
            }
            if (StringUtils.isNotEmpty(mat.getCakeNo())) { //addBy liujiazhi 2021/05/27 库存块号
                inventory.setCakeNo(mat.getCakeNo());
            } else {
                inventory.setCakeNo(mat.getMatNo());
            }
            updateById(inventory);
            //材料关联库存
            mat.setInventoryId(inventory.getId());
        }
    }

    public Inventory getInventory(InventoryDto inventoryDto) {
        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
        if (ProdClassCodeEnum.B.getValue().equals(inventoryDto.getProdClassCode())
                || ProdClassCodeEnum.D.getValue().equals(inventoryDto.getProdClassCode())
                || ProdClassCodeEnum.H.getValue().equals(inventoryDto.getProdClassCode())
                || ProdClassCodeEnum.C.getValue().equals(inventoryDto.getProdClassCode())
                || ProdClassCodeEnum.Z.getValue().equals(inventoryDto.getProdClassCode())
        ) {
            queryWrapper.lambda().eq(Inventory::getProdClassCode, inventoryDto.getProdClassCode())
                    .eq(Inventory::getStockHouseId, inventoryDto.getStockHouseId())
                    .eq(Inventory::getSgSign, inventoryDto.getSgSign())
                    .eq(Inventory::getProdCname, inventoryDto.getProdCname())
                    .eq(Inventory::getProdCnameOther, inventoryDto.getProdCnameOther())
                    .eq(Inventory::getMatLen, inventoryDto.getMatLen())
                    .eq(Inventory::getMatWidth, inventoryDto.getMatWidth())
                    .eq(Inventory::getMatThick, inventoryDto.getMatThick());
        } else {
            queryWrapper.lambda().eq(Inventory::getMatNo, inventoryDto.getMatNo());
        }
        return getBaseMapper().selectOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Inventory entity) {
        if (entity.getMatThick() <= 0 && !StrUtil.equals(entity.getSgSign().trim(), "混")) {
            if (!StrUtil.equals(entity.getProdCname(), "热轧槽钢")) {
                throw new ScmException("材料厚度必须大于0!");
            }
        }
        Boolean flag = super.save(entity);
        String tableName = Inventory.class.getAnnotation(TableName.class).value();
        String dataContent = JSONObject.toJSONString(entity);
        dataLogService.dataLog(tableName, entity.getId(), dataContent);
        return flag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(Inventory entity) {
        if (entity.getMatThick() <= 0 && !StrUtil.equals(entity.getSgSign().trim(), "混")) {
            if (!StrUtil.equals(entity.getProdCname(), "热轧槽钢")) {
                throw new ScmException("材料厚度必须大于0!");
            }
        }
        boolean flag = super.updateById(entity);
        String tableName = Inventory.class.getAnnotation(TableName.class).value();
        String dataContent = JSONObject.toJSONString(entity);
        dataLogService.dataLog(tableName, entity.getId(), dataContent);
        return flag;
    }


    public Inventory getByMatNo(String matNo) {
        if (StrUtil.isBlank(matNo)) {
            throw new ScmException("参数错误,材料号为空");
        }
        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Inventory::getMatNo, matNo).gt(Inventory::getMatNum, 0);

        return getOne(queryWrapper);
    }

    public void setDelFlagFalseByMatNo(String matNo) {
        baseMapper.setDelFlagFalseByMatNo(matNo);
    }

    public Inventory getByMatNoIgnoreDelFlag(String matNo) {
        if (StrUtil.isBlank(matNo)) {
            throw new ScmException("参数错误,材料号为空");
        }
        return baseMapper.getInventoryByMatNoIgnoreDelFlag(matNo);
    }

    public void warehouseWarrantSubduction(Mat mat) {
        Inventory inventory = getById(mat.getInventoryId());
        BigDecimal inventoryWeight = BigDecimal.valueOf(inventory.getWeight());
        BigDecimal inventoryMatNum = BigDecimal.valueOf(inventory.getMatNum());
        BigDecimal inventoryAvailableWeight = BigDecimal.valueOf(inventory.getAvailableWeight());
        BigDecimal inventoryAvailableQty = BigDecimal.valueOf(inventory.getAvailableQty());

        BigDecimal matWeight = BigDecimal.valueOf(mat.getMatNetWt());
        BigDecimal matQty = BigDecimal.valueOf(mat.getMatNum());
        if (inventoryWeight.compareTo(matWeight) < 0) {
            log.error(StrUtil.format("退库数量大于库存数" + mat));
            throw new ScmException("退库数量大于库存数，退库数量:" + matWeight + ",库存数:" + inventoryWeight);
        }
        if (inventoryMatNum.compareTo(matQty) < 0) {
            log.error(StrUtil.format("退库件数大于库存件数" + mat));
            throw new ScmException("退库件数大于库存件数，退库件数:" + matQty + ",库存件数:" + inventoryMatNum);
        }
        if (inventoryAvailableWeight.compareTo(matWeight) < 0) {
            log.error(StrUtil.format("退库数量大于库存可售数" + mat));
            throw new ScmException("退库数量大于库存可售数，退库数量:" + matWeight + ",库存可售数:" + inventoryAvailableWeight);
        }
        if (inventoryAvailableQty.compareTo(matQty) < 0) {
            log.error(StrUtil.format("退库件数大于库存可售件数" + mat));
            throw new ScmException("退库件数大于库存可售件数，退库件数:" + matQty + ",库存可售件数:" + inventoryAvailableQty);
        }
        deductionAvailable(inventory, mat.getMatNum(), mat.getMatNetWt());
        deductionPractical(inventory, mat.getMatNum(), mat.getMatNetWt());
    }

    @Lock4j
    @Transactional(rollbackFor = Exception.class)
    public boolean allotStock(InventoryAllotDTO inventoryAllotDTO) {

        //取调拨数allotNumber、库存数maxMatNum、库存量maxMatWeight、可售数availableNum、可售量availableWeight
        BigDecimal allotNumber = BigDecimal.valueOf(inventoryAllotDTO.getAllotNumber());
        BigDecimal maxMatNum = BigDecimal.valueOf(inventoryAllotDTO.getList().get(0).getMatNum());
        BigDecimal maxMatWeight = BigDecimal.valueOf(inventoryAllotDTO.getList().get(0).getWeight());
        BigDecimal availableNum = BigDecimal.valueOf(inventoryAllotDTO.getList().get(0).getAvailableQty());
        BigDecimal availableWeight = BigDecimal.valueOf(inventoryAllotDTO.getList().get(0).getAvailableWeight());
        BigDecimal allotWeight = BigDecimal.valueOf(inventoryAllotDTO.getAllotWeight());
        BigDecimal zero = BigDecimal.ZERO;

        //判断有效性
        if (allotNumber.compareTo(zero) == -1) {
            throw new ScmException("调拨数无效");
        }

        if (maxMatWeight.compareTo(zero) == 0 && availableWeight.compareTo(zero) == 0) {
            throw new ScmException("库存量、调拨量均为0");
        }
        if (inventoryAllotDTO.getWarehouse().equals(inventoryAllotDTO.getList().get(0).getStockHouseId())) {
            throw new ScmException("不能选择原仓库");
        }

        if (allotNumber.compareTo(maxMatNum) == 1) {
            throw new ScmException("调拨数:" + allotNumber + "大于库存数:" + maxMatNum);
        }
        if (maxMatWeight.compareTo(availableWeight) == 1) {
            if (allotWeight.compareTo(maxMatWeight) == 1) {
                throw new ScmException("合计重量不能大于实际库存量");
            }

        }

        if (maxMatWeight.compareTo(availableWeight) == -1) {
            if (allotWeight.compareTo(maxMatWeight) == 1) {
                throw new ScmException("合计重量不能大于实际可售量");
            }
        }

        if (allotWeight.compareTo(zero) == 0) {
            throw new ScmException("请输入有效重量");
        }

        if (allotNumber.compareTo(availableNum) > 0) {
            throw new ScmException(StrUtil.format("挑拨件数不能大于可售件数。挑拨件数：" + allotNumber + ",可售件数：" + availableNum));
        }

        if (allotWeight.compareTo(allotWeight) > 0) {
            throw new ScmException(StrUtil.format("挑拨重量不能大于可售重量。挑拨重量：" + allotWeight + ",可售重量：" + availableWeight));
        }


        //差额marginNumber
        BigDecimal marginNumber = maxMatNum.subtract(allotNumber);

        boolean materialIdentifyCode = materialIdentify(inventoryAllotDTO.getList().get(0).getProdClassCode());
        String stockHouseId = stockCheck(inventoryAllotDTO.getList().get(0), inventoryAllotDTO.getWarehouse());

        //根据差额判断物资是整体移动还是部分调拨
        if (marginNumber.compareTo(zero) == 0) {
            //材料整体移动
            if (materialIdentifyCode) {
                //线材、棒材、型材、中厚板材材料进行 整体调拨
                if (stockHouseId.equals("")) {
                    //目标仓库无记录，新增目标，原ID逻辑删除
                    Inventory inventory = new Inventory();
                    BeanUtils.copyProperties(inventoryAllotDTO.getList().get(0), inventory);
                    //新增一条记录
                    inventory.setId(null);
                    inventory.setLockQty(null);
                    inventory.setLockWeight(null);
                    inventory.setCreateTime(new Date());
                    inventory.setStockHouseId(inventoryAllotDTO.getWarehouse());
                    inventory.setStockLocation(inventoryAllotDTO.getStockLocation());
                    inventory.setWeight(allotWeight.doubleValue());
                    inventory.setAvailableWeight(allotWeight.doubleValue());
                    inventory.setRemark(inventoryAllotDTO.getRemarks());
                    boolean res1 = save(inventory);

                    //保存调拨单
                    boolean res2 = inventoryAllotService.save(inventoryAllotDTO, inventoryAllotDTO.getList().get(0).getId(), inventory.getId());
                    logicDeleteWithLockNum(inventoryAllotDTO.getList().get(0), allotNumber.doubleValue(), allotWeight.doubleValue());
                    //logicDeleteById(inventoryAllotDTO.getList().get(0).getId());

                    return res1 && res2;

                } else {
                    //目标仓库有记录，目标ID更新计控量，原ID逻辑删除
                    Inventory inventory = getById(stockHouseId);
                    //获取计控量
                    BigDecimal inventory_availableQty = BigDecimal.valueOf(inventory.getAvailableQty());
                    BigDecimal inventory_availableWeight = BigDecimal.valueOf(inventory.getAvailableWeight());
                    BigDecimal inventory_MatNum = BigDecimal.valueOf(inventory.getMatNum());
                    BigDecimal inventory_Weight = BigDecimal.valueOf(inventory.getWeight());

                    inventory.setCreateTime(new Date());
                    inventory.setAvailableQty(inventory_availableQty.add(availableNum).doubleValue());
                    inventory.setAvailableWeight(inventory_availableWeight.add(allotWeight).doubleValue());
                    inventory.setMatNum(inventory_MatNum.add(allotNumber).doubleValue());
                    inventory.setWeight(inventory_Weight.add(allotWeight).doubleValue());
                    inventory.setRemark(inventoryAllotDTO.getRemarks());

                    updateById(inventory);

                    //保存调拨单
                    boolean res2 = inventoryAllotService.save(inventoryAllotDTO, inventoryAllotDTO.getList().get(0).getId(), stockHouseId);
                    //logicDeleteById(inventoryAllotDTO.getList().get(0).getId());
                    logicDeleteWithLockNum(inventoryAllotDTO.getList().get(0), allotNumber.doubleValue(), allotWeight.doubleValue());
                    return res2;

                }

            }
            if (!materialIdentifyCode) {
                //非 线材、棒材、型材、中厚板材材料进行 整体调拨
                //新增目标，原ID逻辑删除
                Inventory inventory = new Inventory();
                BeanUtils.copyProperties(inventoryAllotDTO.getList().get(0), inventory);
                //新增一条记录
                inventory.setId(null);
                inventory.setLockQty(null);
                inventory.setLockWeight(null);
                inventory.setCreateTime(new Date());
                inventory.setStockHouseId(inventoryAllotDTO.getWarehouse());
                inventory.setStockLocation(inventoryAllotDTO.getStockLocation());
                inventory.setRemark(inventoryAllotDTO.getRemarks());
                inventory.setWeight(allotWeight.doubleValue());
                inventory.setAvailableWeight(allotWeight.doubleValue());
                boolean res1 = save(inventory);

                //保存调拨单
                boolean res2 = inventoryAllotService.save(inventoryAllotDTO, inventoryAllotDTO.getList().get(0).getId(), inventory.getId());
                logicDeleteWithLockNum(inventoryAllotDTO.getList().get(0), allotNumber.doubleValue(), allotWeight.doubleValue());
                //logicDeleteById(inventoryAllotDTO.getList().get(0).getId());

                return res1 && res2;

            }

        } else {
            //部分调拨
            if (materialIdentifyCode) {
                //线材、棒材、型材、中厚板材 部分调拨
                if (stockHouseId.equals("")) {
                    //目标仓库无记录，新增目标，更新原ID
                    Inventory inventory = new Inventory();
                    BeanUtils.copyProperties(inventoryAllotDTO.getList().get(0), inventory);
                    inventoryAllotDTO.getList().get(0).setAvailableQty(availableNum.subtract(allotNumber).doubleValue());
                    inventoryAllotDTO.getList().get(0).setAvailableWeight(availableWeight.subtract(allotWeight).doubleValue());
                    inventoryAllotDTO.getList().get(0).setMatNum(marginNumber.doubleValue());
                    inventoryAllotDTO.getList().get(0).setWeight(maxMatWeight.subtract(allotWeight).doubleValue());
                    boolean res1 = updateById(inventoryAllotDTO.getList().get(0));
                    //新增一条记录，抹去锁定数、锁定量
                    inventory.setId(null);
                    inventory.setLockQty(null);
                    inventory.setLockWeight(null);
                    inventory.setCreateBy(inventory.getUpdateBy());
                    inventory.setCreateTime(inventory.getUpdateTime());
                    //赋值可售数、可售量、库存数、库存量
                    inventory.setAvailableQty(allotNumber.doubleValue());
                    inventory.setAvailableWeight(allotWeight.doubleValue());
                    inventory.setMatNum(allotNumber.doubleValue());
                    inventory.setWeight(allotWeight.doubleValue());
                    inventory.setStockHouseId(inventoryAllotDTO.getWarehouse());
                    inventory.setStockLocation(inventoryAllotDTO.getStockLocation());
                    inventory.setRemark(inventoryAllotDTO.getRemarks());
                    boolean res2 = save(inventory);

                    //保存调拨单
                    boolean res3 = inventoryAllotService.save(inventoryAllotDTO, inventoryAllotDTO.getList().get(0).getId(), inventory.getId());
                    return res1 && res2 && res3;

                } else {
                    //目标仓库有记录，更新目标，更新原ID
                    Inventory inventory = getById(stockHouseId);
                    //获取计控量
                    BigDecimal inventory_availableQty = BigDecimal.valueOf(inventory.getAvailableQty());
                    BigDecimal inventory_availableWeight = BigDecimal.valueOf(inventory.getAvailableWeight());
                    BigDecimal inventory_MatNum = BigDecimal.valueOf(inventory.getMatNum());
                    BigDecimal inventory_Weight = BigDecimal.valueOf(inventory.getWeight());

                    inventory.setUpdateTime(new Date());
                    inventory.setAvailableQty(inventory_availableQty.add(allotNumber).doubleValue());
                    inventory.setAvailableWeight(inventory_availableWeight.add(allotWeight).doubleValue());
                    inventory.setMatNum(inventory_MatNum.add(allotNumber).doubleValue());
                    inventory.setWeight(inventory_Weight.add(allotWeight).doubleValue());
                    inventory.setRemark(inventoryAllotDTO.getRemarks());

                    updateById(inventory);

                    //更新原ID记录
                    inventoryAllotDTO.getList().get(0).setAvailableQty(availableNum.subtract(allotNumber).doubleValue());
                    inventoryAllotDTO.getList().get(0).setAvailableWeight(availableWeight.subtract(allotWeight).doubleValue());
                    inventoryAllotDTO.getList().get(0).setMatNum(marginNumber.doubleValue());
                    inventoryAllotDTO.getList().get(0).setWeight(maxMatWeight.subtract(allotWeight).doubleValue());
                    boolean res1 = updateById(inventoryAllotDTO.getList().get(0));

                    //保存调拨单
                    boolean res2 = inventoryAllotService.save(inventoryAllotDTO, inventoryAllotDTO.getList().get(0).getId(), stockHouseId);
                    return res1 && res2;


                }


            }
            if (!materialIdentifyCode) {
                //非线材、棒材、型材、中厚板材 部分调拨，
                //新增目标，原ID记录更新
                Inventory inventory = new Inventory();
                BeanUtils.copyProperties(inventoryAllotDTO.getList().get(0), inventory);
                inventoryAllotDTO.getList().get(0).setAvailableQty(availableNum.subtract(allotNumber).doubleValue());
                inventoryAllotDTO.getList().get(0).setAvailableWeight(availableWeight.subtract(allotWeight).doubleValue());
                inventoryAllotDTO.getList().get(0).setMatNum(marginNumber.doubleValue());
                inventoryAllotDTO.getList().get(0).setWeight(maxMatWeight.subtract(allotWeight).doubleValue());
                boolean res1 = updateById(inventoryAllotDTO.getList().get(0));
                //新增一条记录，抹去锁定数、锁定量
                inventory.setId(null);
                inventory.setLockQty(null);
                inventory.setLockWeight(null);
                inventory.setCreateBy(inventory.getUpdateBy());
                inventory.setCreateTime(inventory.getUpdateTime());
                //赋值可售数、可售量、库存数、库存量
                inventory.setAvailableQty(allotNumber.doubleValue());
                inventory.setAvailableWeight(allotWeight.doubleValue());
                inventory.setMatNum(allotNumber.doubleValue());
                inventory.setWeight(allotWeight.doubleValue());
                inventory.setStockHouseId(inventoryAllotDTO.getWarehouse());
                inventory.setStockLocation(inventoryAllotDTO.getStockLocation());
                inventory.setRemark(inventoryAllotDTO.getRemarks());

                boolean res2 = save(inventory);

                //保存调拨单
                boolean res3 = inventoryAllotService.save(inventoryAllotDTO, inventoryAllotDTO.getList().get(0).getId(), inventory.getId());

                return res1 && res2 && res3;

            }
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean recoverAllot(InventoryAllot inventoryAllot) {
        boolean checkDeletePermitionResult = checkDeletePermition(inventoryAllot, inventoryAllot.getAllotNumberSum(), inventoryAllot.getAllotWeightSum());

        if (checkDeletePermitionResult) {
            //查询出调拨方、接收方的记录，但是调拨方的del_flag可能是1也可能是0
            Inventory inventory_allot = baseMapper.getInventoryDataDelFlagById(inventoryAllot.getAllotId());
            Inventory inventory_receive = getById(inventoryAllot.getReceiveId());
            //判断材料
            boolean materialIdentifyCode = materialIdentify(inventory_receive.getProdClassCode());
            //检查调拨方ID是否处于逻辑删除状态
            boolean idDelFlag = checkIdDelFlag(inventoryAllot.getAllotId());

            if (!idDelFlag) {

                //整体调拨，调拨方记录del_flag == 1
                if (materialIdentifyCode) {

                    //线材、棒材
                    //处理调拨方A_ID 和 接收方 B_ID 数值
                    allotAllotValueExchange(inventory_allot, inventory_receive, inventoryAllot.getAllotNumberSum(), inventoryAllot.getAllotWeightSum());

                } else {
                    //非 线材、棒材
                    logicDeleteById(inventory_receive.getId());
                    baseMapper.setDelFlagFalseById(inventory_allot.getId(), inventory_allot.getStockHouseId());

                }


            } else {

                //部分调拨
                if (materialIdentifyCode) {

                    //线材、棒材、型材、中厚板材
                    partialAllotValueExchange(inventory_allot, inventory_receive, inventoryAllot.getAllotNumberSum(), inventoryAllot.getAllotWeightSum());

                } else {
                    //非 线材、棒材

                    BigDecimal allotNumberSum = BigDecimal.valueOf(inventoryAllot.getAllotNumberSum());
                    BigDecimal allotWeightSum = BigDecimal.valueOf(inventoryAllot.getAllotWeightSum());
                    //取调拨方记录数据
                    BigDecimal allotMatNum = BigDecimal.valueOf(inventory_allot.getMatNum());
                    BigDecimal allotMatWeight = BigDecimal.valueOf(inventory_allot.getWeight());
                    BigDecimal allotAvailableQty = BigDecimal.valueOf(inventory_allot.getAvailableQty());
                    BigDecimal allotAvailableWeight = BigDecimal.valueOf(inventory_allot.getAvailableWeight());

                    //A记录加法

                    inventory_allot.setMatNum(allotMatNum.add(allotNumberSum).doubleValue());
                    inventory_allot.setWeight(allotMatWeight.add(allotWeightSum).doubleValue());
                    inventory_allot.setAvailableQty(allotAvailableQty.add(allotNumberSum).doubleValue());
                    inventory_allot.setAvailableWeight(allotAvailableWeight.add(allotWeightSum).doubleValue());

                    updateById(inventory_allot);

                    logicDeleteById(inventory_receive.getId());


                }

            }


        } else {
            throw new ScmException("接收仓库存数据已变更，禁止删除");
        }

        return true;

    }

    public boolean materialIdentify(String prodClassCode) {

        if (ProdClassCodeEnum.B.getValue().equals(prodClassCode)
                || ProdClassCodeEnum.D.getValue().equals(prodClassCode)
                || ProdClassCodeEnum.H.getValue().equals(prodClassCode)
                || ProdClassCodeEnum.C.getValue().equals(prodClassCode)
        ) {
            //材料产品大类为棒材、线材、型材、中厚板材
            return true;

        } else {
            //材料产品大类不为棒材、线材、型材、中厚板材
            return false;

        }
    }


    public String stockCheck(Inventory inventory, String stockHouseId) {
        //查找目标仓库是否已存在同材料记录
        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Inventory::getProdClassCode, inventory.getProdClassCode())
                .eq(Inventory::getStockHouseId, stockHouseId)
                .eq(Inventory::getMatLen, inventory.getMatLen())
                .eq(Inventory::getMatThick, inventory.getMatThick())
                .eq(Inventory::getMatWidth, inventory.getMatWidth())
                .eq(Inventory::getCustMatSpecs, inventory.getCustMatSpecs())
                .eq(Inventory::getWtMode, inventory.getWtMode())
                .eq(Inventory::getPieceWeight, inventory.getPieceWeight())
                .eq(Inventory::getProdCname, inventory.getProdCname())
                .eq(Inventory::getProdCnameOther, inventory.getProdCnameOther());

        List<Inventory> inventoryList = list(queryWrapper);
        if (CollectionUtil.isEmpty(inventoryList)) {
            return "";
        } else {
            return inventoryList.get(0).getId();
        }
    }


    public boolean checkIdDelFlag(String id) {
        //查询ID的删除状态,true 未删除，false 已删除
        QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Inventory::getId, id);
        List<Inventory> inventoryList1 = list(queryWrapper);

        if (CollectionUtil.isEmpty(inventoryList1)) {
            List<Inventory> inventoryList2 = baseMapper.getInventoryDelFlagById(id);
            if (CollectionUtil.isEmpty(inventoryList2)) {
                throw new ScmException("原记录丢失");

            } else {
                return false;
            }

        } else {
            return true;
        }


    }

    public boolean checkDeletePermition(InventoryAllot inventoryAllot, Double allot_num, Double allot_weight) {
        //校验接收仓现计控数据是否大于等于原调拨参数
        Inventory inventoryReceive = getById(inventoryAllot.getReceiveId());
        BigDecimal matNum = BigDecimal.valueOf(inventoryReceive.getMatNum());
        BigDecimal matWeight = BigDecimal.valueOf(inventoryReceive.getWeight());
        BigDecimal availableQty = BigDecimal.valueOf(inventoryReceive.getAvailableQty());
        BigDecimal availableWeight = BigDecimal.valueOf(inventoryReceive.getAvailableWeight());
        BigDecimal allotNum = BigDecimal.valueOf(allot_num);
        BigDecimal allotWeight = BigDecimal.valueOf(allot_weight);

        if (matNum.compareTo(allotNum) == -1
                || availableQty.compareTo(allotNum) == -1
                || availableWeight.compareTo(allotWeight) == -1
                || matWeight.compareTo(allotWeight) == -1) {

            return false;

        }

        return true;

    }

    public void allotAllotValueExchange(Inventory inventoryAllot, Inventory inventoryReceive, Double allotNumber, Double allotWeight) {
        //交换调拨方、接收方计控量数据
        BigDecimal allotNumberSum = BigDecimal.valueOf(allotNumber);
        BigDecimal allotWeightSum = BigDecimal.valueOf(allotWeight);

        //取接收方记录数据
        BigDecimal receiveMatNum = BigDecimal.valueOf(inventoryReceive.getMatNum());
        BigDecimal receiveMatWeight = BigDecimal.valueOf(inventoryReceive.getWeight());
        BigDecimal receiveAvailableQty = BigDecimal.valueOf(inventoryReceive.getAvailableQty());
        BigDecimal receiveAvailableWeight = BigDecimal.valueOf(inventoryReceive.getAvailableWeight());

        String stockCheckCode = stockCheck(inventoryReceive, inventoryAllot.getStockHouseId());
        if ("".equals(stockCheckCode)) {
            baseMapper.setDelFlagFalseById(inventoryAllot.getId(), inventoryAllot.getStockHouseId());

        } else {
            //同仓库已存在同类型材料记录，需合并
            Inventory inventory_Exist = getById(stockCheckCode);

            BigDecimal existMatNum = BigDecimal.valueOf(inventory_Exist.getMatNum());
            BigDecimal existMatWeight = BigDecimal.valueOf(inventory_Exist.getWeight());
            BigDecimal existAvailableQty = BigDecimal.valueOf(inventory_Exist.getAvailableQty());
            BigDecimal existAvailableWeight = BigDecimal.valueOf(inventory_Exist.getAvailableWeight());

            inventory_Exist.setMatNum(existMatNum.add(allotNumberSum).doubleValue());
            inventory_Exist.setWeight(existMatWeight.add(allotWeightSum).doubleValue());
            inventory_Exist.setAvailableQty(existAvailableQty.add(allotNumberSum).doubleValue());
            inventory_Exist.setAvailableWeight(existAvailableWeight.add(allotWeightSum).doubleValue());

            updateById(inventory_Exist);


        }


        if (receiveAvailableQty.subtract(allotNumberSum).compareTo(BigDecimal.ZERO) == 0
                && receiveAvailableWeight.subtract(allotWeightSum).compareTo(BigDecimal.ZERO) == 0
                && receiveMatNum.subtract(allotNumberSum).compareTo(BigDecimal.ZERO) == 0
                && receiveMatWeight.subtract(allotWeightSum).compareTo(BigDecimal.ZERO) == 0
        ) {
            //当接收仓减量后全为0时，直接逻辑删除该记录
            logicDeleteById(inventoryReceive.getId());


        } else {
            //当接收仓减量后不全为0时，更新接收仓数值
            inventoryReceive.setMatNum(receiveMatNum.subtract(allotNumberSum).doubleValue());
            inventoryReceive.setWeight(receiveMatWeight.subtract(allotWeightSum).doubleValue());
            inventoryReceive.setAvailableQty(receiveAvailableQty.subtract(allotNumberSum).doubleValue());
            inventoryReceive.setAvailableWeight(receiveAvailableWeight.subtract(allotWeightSum).doubleValue());

            updateById(inventoryReceive);

        }


    }

    public void partialAllotValueExchange(Inventory inventoryAllot, Inventory inventoryReceive, Double allotNumber, Double allotWeight) {
        //交换调拨方、接收方计控量数据
        BigDecimal allotNumberSum = BigDecimal.valueOf(allotNumber);
        BigDecimal allotWeightSum = BigDecimal.valueOf(allotWeight);
        //取调拨方记录数据
        BigDecimal allotMatNum = BigDecimal.valueOf(inventoryAllot.getMatNum());
        BigDecimal allotMatWeight = BigDecimal.valueOf(inventoryAllot.getWeight());
        BigDecimal allotAvailableQty = BigDecimal.valueOf(inventoryAllot.getAvailableQty());
        BigDecimal allotAvailableWeight = BigDecimal.valueOf(inventoryAllot.getAvailableWeight());

        //取接收方记录数据
        BigDecimal receiveMatNum = BigDecimal.valueOf(inventoryReceive.getMatNum());
        BigDecimal receiveMatWeight = BigDecimal.valueOf(inventoryReceive.getWeight());
        BigDecimal receiveAvailableQty = BigDecimal.valueOf(inventoryReceive.getAvailableQty());
        BigDecimal receiveAvailableWeight = BigDecimal.valueOf(inventoryReceive.getAvailableWeight());

        //A记录恢复

        inventoryAllot.setMatNum(allotMatNum.add(allotNumberSum).doubleValue());
        inventoryAllot.setWeight(allotMatWeight.add(allotWeightSum).doubleValue());
        inventoryAllot.setAvailableQty(allotAvailableQty.add(allotNumberSum).doubleValue());
        inventoryAllot.setAvailableWeight(allotAvailableWeight.add(allotWeightSum).doubleValue());

        updateById(inventoryAllot);


        if (receiveAvailableQty.subtract(allotNumberSum).compareTo(BigDecimal.ZERO) == 0
                && receiveAvailableWeight.subtract(allotWeightSum).compareTo(BigDecimal.ZERO) == 0
                && receiveMatNum.subtract(allotNumberSum).compareTo(BigDecimal.ZERO) == 0
                && receiveMatWeight.subtract(allotWeightSum).compareTo(BigDecimal.ZERO) == 0
        ) {
            //当接收仓减量后全为0时，直接逻辑删除该记录
            logicDeleteById(inventoryReceive.getId());

        } else {
            //当接收仓减量后不全为0时，更新接收仓数值
            inventoryReceive.setMatNum(receiveMatNum.subtract(allotNumberSum).doubleValue());
            inventoryReceive.setWeight(receiveMatWeight.subtract(allotWeightSum).doubleValue());
            inventoryReceive.setAvailableQty(receiveAvailableQty.subtract(allotNumberSum).doubleValue());
            inventoryReceive.setAvailableWeight(receiveAvailableWeight.subtract(allotWeightSum).doubleValue());

            updateById(inventoryReceive);
        }


    }

    public void logicDeleteWithLockNum(Inventory inventory, Double allot_number, Double allot_weight) {
        //根据锁定量判断是直接逻辑删除还是减法
        if (inventory.getLockQty() != null) {
            BigDecimal lockQty = BigDecimal.valueOf(inventory.getLockQty());

            BigDecimal allotNumberSum = BigDecimal.valueOf(allot_number);
            BigDecimal allotWeightSum = BigDecimal.valueOf(allot_weight);

            if (lockQty.compareTo(BigDecimal.ZERO) > 0) {
                //存在锁定量，不允许直接逻辑删除
                BigDecimal allotMatNum = BigDecimal.valueOf(inventory.getMatNum());
                BigDecimal allotMatWeight = BigDecimal.valueOf(inventory.getWeight());
                BigDecimal allotAvailableQty = BigDecimal.valueOf(inventory.getAvailableQty());
                BigDecimal allotAvailableWeight = BigDecimal.valueOf(inventory.getAvailableWeight());
                inventory.setMatNum(allotMatNum.subtract(allotNumberSum).doubleValue());
                inventory.setWeight(allotMatWeight.subtract(allotWeightSum).doubleValue());
                inventory.setAvailableQty(allotAvailableQty.subtract(allotNumberSum).doubleValue());
                inventory.setAvailableWeight(allotAvailableWeight.subtract(allotWeightSum).doubleValue());
                updateById(inventory);
            } else {
                //不存在锁定量，直接逻辑删除
                logicDeleteById(inventory.getId());
            }

        } else {
            logicDeleteById(inventory.getId());
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void autoPrepareGoods() {
        //待分货客户
        List<PrepareCustomer> prepareCustomerList = prepareCustomerService.list();

        if (CollectionUtil.isEmpty(prepareCustomerList)) {
            throw new ScmException("无待分货客户");
        } else {

            QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();

            List<String> customerIds = prepareCustomerList.stream().map(PrepareCustomer::getCustomerId).collect(Collectors.toList());

            queryWrapper.lambda().eq(Inventory::getProdClassCode, ProdClassCodeEnum.G.getValue())
                    .gt(Inventory::getAvailableQty, 0d);

            List<Inventory> inventoryList = list(queryWrapper);

            Map<String, List<Inventory>> map = inventoryList.stream()
                    .collect(Collectors.groupingBy(Inventory::geneKindStr));

            map.forEach((k, v) -> {
                //随机乱序
                Collections.shuffle(customerIds);
                for (int i = 0; i < v.size(); i++) {
                    String customerId = customerIds.get(i % customerIds.size());

                    v.get(i).setPrepareCustomerId(customerId);
                }
            });

            updateBatchById(inventoryList);


        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updatePrepareCustomer(String inventoryIds, String customerId) {

        List<String> inventoryIdList = Arrays.asList(inventoryIds.split(","));

        if (CollectionUtil.isNotEmpty(inventoryIdList)) {

            QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();

            queryWrapper.lambda().in(Inventory::getId, inventoryIdList);

            List<Inventory> inventoryList = list(queryWrapper);

            for (Inventory inventory : inventoryList) {

                if (inventory.getAvailableQty() <= 0) {
                    throw new ScmException(StrUtil.format("分货失败，{}无可销售量",inventory.getMatNo()));
                }
                inventory.setPrepareCustomerId(customerId);
            }

            updateBatchById(inventoryList);
        }


    }


}
