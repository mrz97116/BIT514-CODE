package com.dongxin.scm.fm.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.entity.FinanceMatCode;
import com.dongxin.scm.fm.entity.FinanceStockBalance;
import com.dongxin.scm.fm.entity.SettleMatCoding;
import com.dongxin.scm.fm.mapper.FinanceStockBalanceMapper;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;


/**
 * @Description: 财务库存余额
 * @Author: jeecg-boot
 * @Date: 2021-05-18
 * @Version: V1.0
 */
@Service
public class FinanceStockBalanceService extends BaseService<FinanceStockBalanceMapper, FinanceStockBalance> {
    @Autowired
    private FinanceMatCodeService financeMatCodeService;

    @Autowired
    private SettleMatCodingService settleMatCodingService;


    @Transactional(rollbackFor = Exception.class)
    public void addImportData(List<FinanceStockBalance> list) {
        if (CollectionUtil.isEmpty(list)) {
            throw new ScmException(StrUtil.format("导入数据位空"));
        }
        //把时间换成输入的时间一号
        if (ObjectUtil.isEmpty(list.get(0).getTime())) {
            log.error(StrUtil.format("时间为空：" + list.get(0).getTime()));
            throw new ScmException(StrUtil.format("时间为空：" + list.get(0).getTime()));
        }
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(list.get(0).getTime());
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        Date date = gcLast.getTime();

        QueryWrapper<FinanceStockBalance> olpFinanceStockBalanceQueryWrapper = new QueryWrapper<>();
        olpFinanceStockBalanceQueryWrapper.lambda().eq(FinanceStockBalance::getTime, date);
        List<FinanceStockBalance> olpFinanceStockBalanceList = list(olpFinanceStockBalanceQueryWrapper);
        if (CollectionUtil.isNotEmpty(olpFinanceStockBalanceList)) {
            removeByIds(olpFinanceStockBalanceList);
        }

        //去重
        List<FinanceStockBalance> duplicateRemovalFinanceStockBalanceList = list.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getFinanceProductName() + ";" + o.getCategory() + ";" + o.getProductionLine()))), ArrayList::new));

        for (FinanceStockBalance financeStockBalance : duplicateRemovalFinanceStockBalanceList) {
            financeStockBalance.setBeginningInventoryWeight(BigDecimal.ZERO);
            financeStockBalance.setMonthWarehousingWeight(BigDecimal.ZERO);
            financeStockBalance.setMonthSaleWeight(BigDecimal.ZERO);
            financeStockBalance.setEndMonthInventoryWeight(BigDecimal.ZERO);
            int count = 1;
            for (FinanceStockBalance duplicateFinanceStockBalance : list) {
                if (financeStockBalance.getFinanceProductName().equals(duplicateFinanceStockBalance.getFinanceProductName())
                        && financeStockBalance.getCategory().equals(duplicateFinanceStockBalance.getCategory())
                        && financeStockBalance.getProductionLine().equals(duplicateFinanceStockBalance.getProductionLine())) {

                    count++;
                    if (StrUtil.isBlank(duplicateFinanceStockBalance.getFinanceProductName())) {
                        log.error(StrUtil.format("第" + count + "行没有财务品名" + duplicateFinanceStockBalance));
                        throw new ScmException(StrUtil.format("第" + count + "行没有财务品名"));
                    }

                    //去掉字符串中的逗号
                    if (StrUtil.isBlank(duplicateFinanceStockBalance.getBeginningInventoryWeightString())) {
                        duplicateFinanceStockBalance.setBeginningInventoryWeightString("0");
                    }
                    String beginningInventoryWeightString = duplicateFinanceStockBalance.getBeginningInventoryWeightString().replaceAll(",", "");
                    BigDecimal beginningInventoryWeight = new BigDecimal(beginningInventoryWeightString);

                    //本月入库量
                    if (StrUtil.isBlank(duplicateFinanceStockBalance.getMonthWarehousingWeightString())) {
                        duplicateFinanceStockBalance.setMonthWarehousingWeightString("0");
                    }
                    String monthWarehousingWeightString = duplicateFinanceStockBalance.getMonthWarehousingWeightString().replaceAll(",", "");
                    BigDecimal monthWarehousingWeight = new BigDecimal(monthWarehousingWeightString);

                    //本月销量
                    if (StrUtil.isBlank(duplicateFinanceStockBalance.getMonthSaleWeightString())) {
                        duplicateFinanceStockBalance.setMonthSaleWeightString("0");
                    }
                    String monthSaleWeightString = duplicateFinanceStockBalance.getMonthSaleWeightString().replaceAll(",", "");
                    BigDecimal monthSaleWeight = new BigDecimal(monthSaleWeightString);

                    //本月期末库存量
                    if (StrUtil.isBlank(duplicateFinanceStockBalance.getEndMonthInventoryWeightString())) {
                        duplicateFinanceStockBalance.setEndMonthInventoryWeightString("0");
                    }
                    String endMonthInventoryWeightString = duplicateFinanceStockBalance.getEndMonthInventoryWeightString().replaceAll(",", "");
                    BigDecimal endMonthInventory = new BigDecimal(endMonthInventoryWeightString);


                    financeStockBalance.setBeginningInventoryWeight(financeStockBalance.getBeginningInventoryWeight().add(beginningInventoryWeight));
                    financeStockBalance.setMonthWarehousingWeight(financeStockBalance.getMonthWarehousingWeight().add(monthWarehousingWeight));
                    financeStockBalance.setMonthSaleWeight(financeStockBalance.getMonthSaleWeight().add(monthSaleWeight));
                    financeStockBalance.setEndMonthInventoryWeight(financeStockBalance.getEndMonthInventoryWeight().add(endMonthInventory));
                }
            }

            //物料编码
            String financeProductName = financeStockBalance.getFinanceProductName();
            QueryWrapper<FinanceMatCode> financeMatCodeQueryWrapper = new QueryWrapper<>();
            financeMatCodeQueryWrapper.lambda().eq(FinanceMatCode::getName, financeProductName)
                    .eq(FinanceMatCode::getProductionLine, financeStockBalance.getProductionLine());
            List<FinanceMatCode> financeMatCodeList = financeMatCodeService.list(financeMatCodeQueryWrapper);

            if (CollectionUtil.isEmpty(financeMatCodeList)) {
                throw new ScmException(StrUtil.format("查询不到对应的财务物料编码," + financeStockBalance.getFinanceProductName()));
            }
            FinanceMatCode financeMatCode = financeMatCodeList.get(0);
            if (financeMatCodeList.size() > 1) {
                throw new ScmException(StrUtil.format("系统识别到财务物料编码表有两个相同的财务物料名称," + financeMatCode.getName()));
            }


            String steelGrade = "";
            //处理钢号（外购才暂时没有对应的牌号）
            if (!financeStockBalance.getProductionLine().equals("生产线:外购")) {
                QueryWrapper<SettleMatCoding> settleMatCodingQueryWrapper = new QueryWrapper<>();
                settleMatCodingQueryWrapper.lambda().eq(SettleMatCoding::getMatCode, financeMatCode.getMatCode());
                List<SettleMatCoding> settleMatCodingList = settleMatCodingService.list(settleMatCodingQueryWrapper);
                if (CollectionUtil.isEmpty(settleMatCodingList)) {
                    throw new ScmException(StrUtil.format("查询不到对应的结算物料编码,财务物料名:" + financeStockBalance.getFinanceProductName() + ",编码:" + financeStockBalance.getMaterialCode()));
                }
                List<String> steelGradeList = newArrayList();
                for (SettleMatCoding settleMatCoding : settleMatCodingList) {
                    steelGradeList.add(settleMatCoding.getSgSign());
                }

                //去重复牌号
                for (int i = 0; i < steelGradeList.size() - 1; i++) {
                    for (int j = i + 1; j < steelGradeList.size(); j++) {
                        if (steelGradeList.get(i).equals(steelGradeList.get(j))) {
                            steelGradeList.remove(j);
                        }
                    }
                }

                for (String steelGradeString : steelGradeList) {
                    steelGrade = steelGrade + steelGradeString + "、";
                }
                steelGrade = steelGrade.substring(0, steelGrade.length() - 1);
            }


            financeStockBalance.setSteelGrade(steelGrade);
            financeStockBalance.setTime(date);
            financeStockBalance.setMaterialCode(financeMatCode.getMatCode());
        }
        saveBatch(duplicateRemovalFinanceStockBalanceList);

    }

    public void addFinanceStockBalance(FinanceStockBalance financeStockBalance) {
        //出来时间
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(financeStockBalance.getTime());
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        Date date = gcLast.getTime();

        //获取物料编码
        QueryWrapper<FinanceMatCode> financeMatCodeQueryWrapper = new QueryWrapper<>();
        financeMatCodeQueryWrapper.lambda().eq(FinanceMatCode::getName, financeStockBalance.getFinanceProductName());
        List<FinanceMatCode> financeMatCodeList = financeMatCodeService.list(financeMatCodeQueryWrapper);
        if (CollectionUtil.isEmpty(financeMatCodeList)) {
            throw new ScmException(StrUtil.format("查询不到对应的财务物料编码,产品名称:" + financeStockBalance.getFinanceProductName()));
        }
        if (financeMatCodeList.size() > 1) {
            throw new ScmException(StrUtil.format("查询到财务物料编码中有两个相同的产品名称,产品名称:" + financeStockBalance.getFinanceProductName()));
        }


        //处理钢号
        QueryWrapper<SettleMatCoding> settleMatCodingQueryWrapper = new QueryWrapper<>();
        settleMatCodingQueryWrapper.lambda().eq(SettleMatCoding::getMatCode, financeMatCodeList.get(0).getMatCode());
        List<SettleMatCoding> settleMatCodingList = settleMatCodingService.list(settleMatCodingQueryWrapper);
        if (CollectionUtil.isEmpty(settleMatCodingList)) {
            throw new ScmException(StrUtil.format("查询不到对应的结算物料编码,编码:" + financeMatCodeList.get(0).getMatCode()));
        }
        List<String> steelGradeList = newArrayList();
        for (SettleMatCoding settleMatCoding : settleMatCodingList) {
            steelGradeList.add(settleMatCoding.getSgSign());
        }

        //去重复牌号
        for (int i = 0; i < steelGradeList.size() - 1; i++) {
            for (int j = i + 1; j < steelGradeList.size(); j++) {
                if (steelGradeList.get(i).equals(steelGradeList.get(j))) {
                    steelGradeList.remove(j);
                }
            }
        }

        String steelGrade = "";
        for (String steelGradeString : steelGradeList) {
            String sgSign = steelGradeString;
            steelGrade = steelGrade + sgSign + "、";
        }
        steelGrade = steelGrade.substring(0, steelGrade.length() - 1);


        FinanceStockBalance addFinanceStockBalance = new FinanceStockBalance();
        addFinanceStockBalance.setFinanceProductName(financeStockBalance.getFinanceProductName());
        addFinanceStockBalance.setCategory(financeStockBalance.getCategory());
        addFinanceStockBalance.setProductionLine(financeStockBalance.getProductionLine());
        addFinanceStockBalance.setSteelGrade(steelGrade);
        addFinanceStockBalance.setMaterialCode(financeMatCodeList.get(0).getMatCode());
        addFinanceStockBalance.setTime(date);
        addFinanceStockBalance.setBeginningInventoryWeight(financeStockBalance.getBeginningInventoryWeight());
        addFinanceStockBalance.setMonthWarehousingWeight(financeStockBalance.getMonthWarehousingWeight());
        addFinanceStockBalance.setMonthSaleWeight(financeStockBalance.getMonthSaleWeight());
        addFinanceStockBalance.setEndMonthInventoryWeight(financeStockBalance.getEndMonthInventoryWeight());

        save(addFinanceStockBalance);
    }
}
