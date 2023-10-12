package com.dongxin.scm.sm.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.sm.entity.Stock;
import com.dongxin.scm.sm.mapper.StockMapper;
import com.dongxin.scm.sm.vo.OptionVO;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.system.base.service.BaseService;
import org.jeecg.common.system.query.QueryGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 仓库信息表
 * @Author: jeecg-boot
 * @Date: 2020-09-17
 * @Version: V1.0
 */
@Service
public class StockService extends BaseService<StockMapper, Stock> {


    @Transactional(rollbackFor = Exception.class)
    public void addStock(Stock stock) {
        Stock stockNew = new Stock();
        stockNew.setName(stock.getName());
        QueryWrapper<Stock> queryWrapper = QueryGenerator.initQueryWrapper(stockNew, null);
        List<Stock> stockList = list(queryWrapper);
        if (CollectionUtils.isNotEmpty(stockList)) {
            throw new ScmException("库区已存在！");
        }
        save(stock);
    }

    /*
    启用/禁用
     */
    @Transactional(rollbackFor = Exception.class)
    public void enable(List<String> ids, String enable) {
        List<Stock> stockList = listByIds(ids);
        for (Stock stock : stockList) {
            stock.setActive(enable);
        }
        updateBatchById(stockList);
    }

    public List<OptionVO> queryWarehouse() {
        return baseMapper.queryWarehouse();
    }
    /*获取到stock实体类的map集合*/


    public Map<String, String> getStockHouseIdMap(List<String> ids) {
        Map<String, String> map = new HashMap<>();
        if (CollectionUtils.isEmpty(ids)) {
            return map;
        }
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(Stock::getId, ids);
        List<Stock> stockList = list(queryWrapper);
        for (Stock stockListTmp : stockList) {
            map.put(stockListTmp.getId(), stockListTmp.getName());
        }

        return map;
    }

    /**
     * 查询所有的仓库和对应id的映射关系
     * @return
     */
    public Map<String, String> getAllStockNameAndId() {
        Map<String, String> map = new HashMap<>();
        List<Stock> stockList = list();
        for (Stock stockListTmp : stockList) {
            map.put(stockListTmp.getName(), stockListTmp.getId());
        }

        return map;
    }

    public Stock getByName(String stockName) {
        QueryWrapper<Stock> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Stock::getName, stockName);

        return getOne(queryWrapper);
    }
}
