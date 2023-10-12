package com.dongxin.scm.sm.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.sm.entity.OldSystemDataConversion;
import com.dongxin.scm.sm.mapper.OldSystemDataConversionMapper;
import org.springframework.stereotype.Service;
import org.jeecg.common.system.base.service.BaseService;

import java.util.List;

/**
 * @Description: 旧系统数据转换
 * @Author: jeecg-boot
 * @Date:   2021-03-04
 * @Version: V1.0
 */
@Service
public class OldSystemDataConversionService extends BaseService<OldSystemDataConversionMapper, OldSystemDataConversion> {

    @Override
    public boolean save(OldSystemDataConversion entity) {
        QueryWrapper<OldSystemDataConversion> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(OldSystemDataConversion::getProdClassCode,entity.getProdClassCode())
                .eq(OldSystemDataConversion::getProdCname,entity.getProdCname())
                .eq(OldSystemDataConversion::getSgSign,entity.getSgSign());
        List<OldSystemDataConversion> list = list(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            throw new ScmException("已维护相同的数据");
        }
        return super.save(entity);
    }
}
