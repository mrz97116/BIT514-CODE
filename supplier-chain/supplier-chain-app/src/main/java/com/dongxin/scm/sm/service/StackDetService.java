package com.dongxin.scm.sm.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.cm.service.SalesmanInfoService;
import com.dongxin.scm.sm.entity.Stack;
import com.dongxin.scm.sm.entity.StackDet;
import com.dongxin.scm.sm.mapper.StackDetMapper;
import com.dongxin.scm.sm.vo.StackDetailSettle;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 装车单明细表
 * @Author: jeecg-boot
 * @Date: 2020-11-24
 * @Version: V1.0
 */
@Service
public class StackDetService extends BaseService<StackDetMapper, StackDet> {

    @Resource
    private StackDetMapper stackDetMapper;

    @Autowired
    private StackService stackService;

    @Autowired
    private CustomerProfileService customerProfileService;

    @Autowired
    private SalesmanInfoService salesmanInfoService;

    public List<StackDet> selectByMainId(String mainId) {
        return stackDetMapper.selectByMainId(mainId);
    }

    public List<StackDet> selectByStackId(String stackId) {
        QueryWrapper<StackDet> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(StackDet::getStackId, stackId);
        return list(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delMain(String id) {
        stackDetMapper.deleteByMainId(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            stackDetMapper.deleteByMainId(id.toString());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delStackDet(String id) {
        stackDetMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delBatchStackDet(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            stackDetMapper.deleteById(id.toString());
        }
    }

    public List<StackDetailSettle> settleStackDetail(List<String> stackIds) {
        QueryWrapper<StackDet> stackDetQueryWrapper = new QueryWrapper<>();
        stackDetQueryWrapper.lambda().in(StackDet::getStackId, stackIds);
        List<StackDet> stackDetList = list(stackDetQueryWrapper);
        List<Stack> stackList = stackService.listByIds(stackIds);

        List<StackDetailSettle> stackDetailSettleList = newArrayList();
        for (StackDet stackDet : stackDetList) {
             for (Stack stack : stackList) {
                if (stack.getId().equals(stackDet.getStackId())) {
                    StackDetailSettle stackDetailSettle = new StackDetailSettle();
                    BeanUtil.copyProperties(stackDet, stackDetailSettle);
                    stackDetailSettle.setCustomerId(stack.getCustomerId());
                    stackDetailSettle.setSalesMan(stack.getSalesMan());
                    stackDetailSettle.setStackingNo(stack.getStackingNo());
                    stackDetailSettleList.add(stackDetailSettle);
                    break;
                }
            }
        }


        List<String> customerIds = newArrayList();
        List<String> salesManIds = newArrayList();
        for (StackDetailSettle record : stackDetailSettleList) {
            customerIds.add(record.getCustomerId());
            salesManIds.add(record.getSalesMan());
        }

        Map<String, String> customerName = customerProfileService.getNameMap(customerIds);
        Map<String, String> salesManName = salesmanInfoService.getNameMap(salesManIds);
        for (StackDetailSettle record : stackDetailSettleList) {
            record.setCustomerId(customerName.get(record.getCustomerId()));
            record.setSalesMan(salesManName.get(record.getSalesMan()));
        }
        return stackDetailSettleList;
    }

    public List<StackDet> selectUnSettleStackDet(String id) {
        List<StackDet> stackDetList = baseMapper.selectUnSettleStackDet(id);
        return stackDetList;
    }
}
