package com.dongxin.scm.fm.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.entity.SettleImport;
import com.dongxin.scm.fm.mapper.SettleImportMapper;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.service.SalesOrderDetService;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.sm.entity.Stock;
import com.dongxin.scm.sm.service.StockService;
import org.jeecg.common.system.base.service.BaseService;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * @Description: 结算单导入
 * @Author: jeecg-boot
 * @Date: 2021-07-26
 * @Version: V1.0
 */
@Service
public class SettleImportService extends BaseService<SettleImportMapper, SettleImport> {

    @Autowired
    private StockService stockService;

    @Autowired
    private SalesOrderService salesOrderService;

    @Autowired
    private SalesOrderDetService salesOrderDetService;


    @Transactional(rollbackFor = Exception.class)
    public int importCheck(List<SettleImport> list) {

        List<String> orderBillNoList = newArrayList();

        List<SettleImport> result = newArrayList();

        for (SettleImport settleImport : list) {
            orderBillNoList.add(settleImport.getSaleBillNo());
        }

        QueryWrapper<SettleImport> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(SettleImport::getSaleBillNo, CollectionUtil.distinct(orderBillNoList));

        List<SettleImport> settleImportInDbs = list(queryWrapper);

        List<String> importedSaleBillNos = settleImportInDbs.stream().map(SettleImport::getSaleBillNo).collect(Collectors.toList());

        List<String> toBeImportSaleBillNos = newArrayList();

        for (SettleImport settleImport : list) {

            //件数不能为空
            if (ObjectUtil.isEmpty(settleImport.getQty())){
                throw new ScmException(StrUtil.format("数量不能为空"));
            }

            //仓库验证
            Stock stock = stockService.getById(settleImport.getStockId());
            if (ObjectUtil.isNull(stock)) {
                throw new ScmException(StrUtil.format("请维护：" + settleImport.getStockId()));
            }

            //已经导入的提单号不再重复导入
            if (!importedSaleBillNos.contains(settleImport.getSaleBillNo())) {
                result.add(settleImport);
                toBeImportSaleBillNos.add(settleImport.getSaleBillNo());
            }
        }
        toBeImportSaleBillNos = CollectionUtil.distinct(toBeImportSaleBillNos);

        List<String> uniQueKeys = salesOrderDetService.getUniqueSaleOrderDet(toBeImportSaleBillNos);

        Map<String, String> orderNoAndCarNo = salesOrderService.getCarNoMap(toBeImportSaleBillNos);

        for (SettleImport settleImport : result) {
            String importCarNo = settleImport.getCarNo().replaceAll(" ", "");

            if (StrUtil.isBlank(orderNoAndCarNo.get(settleImport.getSaleBillNo()))) {
                throw new ScmException("系统中无该单号或该提单无对应车号，请检查" + settleImport.getSaleBillNo());
            }

            String saleOrderCarNo = orderNoAndCarNo.get(settleImport.getSaleBillNo()).replaceAll(" ", "");
            if (!importCarNo.equalsIgnoreCase(saleOrderCarNo)) {
                throw new ScmException(StrUtil.format("导入失败，提单号{}车号{}与原始单据车号{}不匹配，请检查",
                        settleImport.getSaleBillNo(), importCarNo, saleOrderCarNo));
            }
            String thisUniqueKey = settleImport.getSaleBillNo() + settleImport.getCustMatSpecs() + settleImport.getSgSign();

            if (!uniQueKeys.contains(thisUniqueKey)) {
                throw new ScmException(StrUtil.format("单号:{}规格:{}牌号:{}与系统提单不匹配，请检查",
                        settleImport.getSaleBillNo(), settleImport.getCustMatSpecs(), settleImport.getSgSign()));
            }

        }

        if (CollectionUtil.isNotEmpty(result)) {
            saveBatch(result);
        }
        QueryWrapper<SalesOrder> salesOrderQueryWrapper = new QueryWrapper<>();
        salesOrderQueryWrapper.lambda().in(SalesOrder::getSaleBillNo, toBeImportSaleBillNos);

        List<SalesOrder> salesOrderList = salesOrderService.list(salesOrderQueryWrapper);

        for (SalesOrder salesOrder : salesOrderList) {
            salesOrder.setGdImportStackDetail(YesNoEnum.YES.getCode());
        }
        salesOrderService.updateBatchById(salesOrderList);

        return result.size();
    }




    public void updatePageList(IPage<SettleImport> pageList) {
        List<String> stockIdList = newArrayList();
        pageList.getRecords().forEach(i -> stockIdList.add(i.getStockId()));

        Map<String, String> stockNameMap = stockService.getStockHouseIdMap(stockIdList);
        pageList.getRecords().forEach(i -> i.setStockId(stockNameMap.get(i.getStockId())));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection idList) {
        List<String> ids = newArrayList();
        for (Object o : idList) {
            ids.add((String) o);
        }

        List<SettleImport> settleImportList = listByIds(ids);

        List<String> salesOrderNos = newArrayList();

        for (SettleImport settleImport : settleImportList) {
            salesOrderNos.add(settleImport.getSaleBillNo());
        }

        salesOrderNos = CollectionUtil.distinct(salesOrderNos);

        QueryWrapper<SalesOrder> salesOrderQueryWrapper = new QueryWrapper<>();

        salesOrderQueryWrapper.lambda().in(SalesOrder::getSaleBillNo, salesOrderNos);
        List<SalesOrder> salesOrderList = salesOrderService.list(salesOrderQueryWrapper);
        for (SalesOrder salesOrder : salesOrderList) {
            salesOrder.setGdImportStackDetail(YesNoEnum.NO.getCode());
        }
        salesOrderService.updateBatchById(salesOrderList);
        return super.removeByIds(idList);
    }
}
