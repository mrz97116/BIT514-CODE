package com.dongxin.scm.sm.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.entity.MaterialSummary;
import com.dongxin.scm.sm.enums.MaterialSummaryEnum;
import com.dongxin.scm.sm.mapper.MaterialSummaryMapper;
import com.dongxin.scm.sm.vo.OptionVO;
import com.dongxin.scm.utils.BigDecimalUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 材料汇总
 * @Author: jeecg-boot
 * @Date: 2021-04-26
 * @Version: V1.0
 */
@Service
public class MaterialSummaryService extends BaseService<MaterialSummaryMapper, MaterialSummary> {

    @Transactional(rollbackFor = Exception.class)
    public void addImportExcel(List<MaterialSummary> list) {
        if (CollectionUtils.isEmpty(list)) {
            log.error(StrUtil.format("导入信息为空", list));
            throw new SecurityException(StrUtil.format("导入信息为空"));
        }

        List<MaterialSummary> materialSummaryList = newArrayList();

        //把时间换成输入的时间一号
        if(ObjectUtil.isEmpty(list.get(0).getTime())){
            log.error(StrUtil.format("时间为空："+list.get(0).getTime()));
            throw new ScmException(StrUtil.format("时间为空："+list.get(0).getTime()));
        }
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(list.get(0).getTime());
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        Date date = gcLast.getTime();

        Double taxRate = 1.13;

        QueryWrapper<MaterialSummary> materialSummaryQueryWrapper = new QueryWrapper<>();
        materialSummaryQueryWrapper.lambda().eq(MaterialSummary::getTime,date);

        List<MaterialSummary> existsMaterialSummaryList = list(materialSummaryQueryWrapper);
        if(CollectionUtils.isNotEmpty(existsMaterialSummaryList)){
            removeByIds(existsMaterialSummaryList);
        }

        for (MaterialSummary materialSummary : list) {
            String productName = materialSummary.getProductName();

//            //钢材
//            if (productName.equals(MaterialSummaryEnum.TOTAL.getDesc())) {
//                MaterialSummary totalMaterialSummary = newMaterialSummary(MaterialSummaryEnum.TOTAL.getCode()
//                        , materialSummary, taxRate, time);
//
//                materialSummaryList.add(totalMaterialSummary);
//            }

            //螺纹钢
            if (productName.equals(MaterialSummaryEnum.B.getDesc())) {
                MaterialSummary screwThreadSteelMaterialSummary = newMaterialSummary(MaterialSummaryEnum.B.getCode()
                        , materialSummary, taxRate, date);

                materialSummaryList.add(screwThreadSteelMaterialSummary);
            }

//            //中型
//            if (productName.equals(MaterialSummaryEnum.CE.getDesc())) {
//                MaterialSummary midsizeMaterialSummary = newMaterialSummary(MaterialSummaryEnum.CE.getCode()
//                        , materialSummary, taxRate, time);
//
//                materialSummaryList.add(midsizeMaterialSummary);
//            }

            //线型
            if (productName.equals(MaterialSummaryEnum.D.getDesc())) {
                MaterialSummary linearMaterialSummary = newMaterialSummary(MaterialSummaryEnum.D.getCode()
                        , materialSummary, taxRate, date);

                materialSummaryList.add(linearMaterialSummary);
            }

            //热卷
            if (productName.equals(MaterialSummaryEnum.F.getDesc())) {
                MaterialSummary hotRollMaterialSummary = newMaterialSummary(MaterialSummaryEnum.F.getCode()
                        , materialSummary, taxRate, date);

                materialSummaryList.add(hotRollMaterialSummary);
            }

            //板材
            if (productName.equals(MaterialSummaryEnum.H.getDesc())) {
                MaterialSummary boardMaterialSummary = newMaterialSummary(MaterialSummaryEnum.H.getCode()
                        , materialSummary, taxRate, date);

                materialSummaryList.add(boardMaterialSummary);
            }

//            //外购材
//            if (productName.equals(MaterialSummaryEnum.PURCHASED_MATERIALS.getDesc())) {
//                MaterialSummary purchasedMaterialSummary = newMaterialSummary(MaterialSummaryEnum.PURCHASED_MATERIALS.getCode()
//                        , materialSummary, taxRate, time);
//
//                materialSummaryList.add(purchasedMaterialSummary);
//            }
//
//            //外购不锈钢
//            if (productName.equals(MaterialSummaryEnum.PURCHASED_STAINLESS_STEEL.getDesc())) {
//                MaterialSummary purchasedStainlessSteelMaterialSummary = newMaterialSummary(
//                        MaterialSummaryEnum.PURCHASED_STAINLESS_STEEL.getCode(), materialSummary, taxRate, time);
//
//                materialSummaryList.add(purchasedStainlessSteelMaterialSummary);
//            }
//
//            //冷卷
//            if (productName.equals(MaterialSummaryEnum.COLD_ROLL.getDesc())) {
//                MaterialSummary coldRollMaterialSummary = newMaterialSummary(
//                        MaterialSummaryEnum.COLD_ROLL.getCode(), materialSummary, taxRate, time);
//
//                materialSummaryList.add(coldRollMaterialSummary);
//            }
//
//            //冷轧基板
//            if (productName.equals(MaterialSummaryEnum.COLD_ROLLED_SUBSTRATE.getDesc())) {
//                MaterialSummary coldRolledSubstrateMaterialSummary = newMaterialSummary(
//                        MaterialSummaryEnum.COLD_ROLLED_SUBSTRATE.getCode(), materialSummary, taxRate, time);
//
//                materialSummaryList.add(coldRolledSubstrateMaterialSummary);
//            }

        }

        //批量add
        saveBatch(materialSummaryList);

    }

    private MaterialSummary newMaterialSummary(String code, MaterialSummary materialSummary, Double taxRate, Date time) {
        BigDecimal weight = BigDecimal.ZERO;
        BigDecimal inventoryCost = BigDecimal.ZERO;
        //去掉字符串中的逗号
        if (StrUtil.isNotBlank(materialSummary.getWeightString())) {
            String weightString = materialSummary.getWeightString().replaceAll(",", "");
            weight = weight.add(new BigDecimal(weightString));
        }
        if(StrUtil.isNotBlank(materialSummary.getInventoryCostString())){
            String inventoryCostString = materialSummary.getInventoryCostString().replaceAll(",", "");
            inventoryCost = BigDecimalUtils.multiply(taxRate, inventoryCost.add(new BigDecimal(inventoryCostString)));
        }

//        //String转BigDecimal
//        BigDecimal weight = new BigDecimal(weightString);
//        BigDecimal inventoryCost = new BigDecimal(inventoryCostString);

        MaterialSummary newMaterialSummary = new MaterialSummary();
        newMaterialSummary.setProductName(code);
        newMaterialSummary.setWeight(weight);
        newMaterialSummary.setInventoryCost(inventoryCost);
        newMaterialSummary.setTime(time);

        return newMaterialSummary;
    }


    public IPage<MaterialSummary> translate(IPage<MaterialSummary> pageList) {
        Map<String, String> materialSummaryMap = new HashMap<>();
        MaterialSummaryEnum[] materialSummaryEnums = MaterialSummaryEnum.values();
        for (MaterialSummaryEnum materialSummaryEnum : materialSummaryEnums) {
            materialSummaryMap.put(materialSummaryEnum.getCode(), materialSummaryEnum.getDesc());
        }

        for (MaterialSummary materialSummary : pageList.getRecords()) {
            materialSummary.setProductName(materialSummaryMap.get(materialSummary.getProductName()));
        }
        return pageList;
    }

    public List<OptionVO> selectProductName() {
        List<OptionVO> selectProductName = newArrayList();
        MaterialSummaryEnum[] materialSummaryEnums = MaterialSummaryEnum.values();
        for (MaterialSummaryEnum materialSummaryEnum : materialSummaryEnums) {
            OptionVO optionVO = new OptionVO();
            optionVO.setValue(materialSummaryEnum.getCode());
            optionVO.setText(materialSummaryEnum.getDesc());
            selectProductName.add(optionVO);
        }

        return selectProductName;
    }
}
