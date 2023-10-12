package com.dongxin.scm.sm.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.dongxin.scm.sm.entity.Receiving;
import com.dongxin.scm.sm.mapper.ReceivingMapper;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 收料人信息
 * @Author: jeecg-boot
 * @Date: 2021-01-13
 * @Version: V1.0
 */
@Service
public class ReceivingService extends BaseService<ReceivingMapper, Receiving> {

    public Receiving queryReceiving(String name) {
        QueryWrapper<Receiving> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Receiving::getReceivingName, name);
        List<Receiving> receivingList = list(queryWrapper);
        if (CollectionUtil.isEmpty(receivingList)) {
            return new Receiving();
        }
        return receivingList.get(0);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveReceiving(Receiving receiving){
        //复制新增会将旧的数据传过来，此处清空。
        receiving.setId(null);
        receiving.setCreateBy(null);
        receiving.setCreateTime(null);
        receiving.setUpdateBy(null);
        receiving.setUpdateTime(null);
        save(receiving);
    }

}
