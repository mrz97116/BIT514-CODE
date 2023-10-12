package com.dongxin.scm.fm.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dongxin.scm.fm.entity.Bank;
import com.dongxin.scm.fm.mapper.BankMapper;
import com.dongxin.scm.fm.vo.BankCategory;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 银行信息汇总表
 * @Author: jeecg-boot
 * @Date: 2020-10-28
 * @Version: V1.0
 */
@Service
public class BankService extends BaseService<BankMapper, Bank> {

    public Map<String, String> getBankNameMap(List<String> ids) {
        Map<String, String> map = new HashMap<>();
        if (CollectionUtil.isEmpty(ids)) {
            return map;
        }
        QueryWrapper<Bank> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(Bank::getId, ids);
        List<Bank> bankList = list(queryWrapper);
        for (Bank bankListTmp : bankList) {
            map.put(bankListTmp.getId(), bankListTmp.getBankName());
        }
        return map;
    }

    public IPage<Bank> getBankType(IPage<Bank> pageList) {
        List<BankCategory> getBankCategoryList = baseMapper.getBankTypeList();
        Map<String,String> getBankTypeMap = new HashMap<>();
        for (BankCategory bankCategory : getBankCategoryList){
            getBankTypeMap.put(bankCategory.getType(),bankCategory.getTypeName());
        }
        for (Bank bank : pageList.getRecords()){
            bank.setTapeName(getBankTypeMap.get(bank.getTypeId()));
        }
        return pageList;
    }
}
