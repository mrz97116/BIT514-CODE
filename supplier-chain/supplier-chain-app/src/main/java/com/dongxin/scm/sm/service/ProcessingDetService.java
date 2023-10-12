package com.dongxin.scm.sm.service;

import cn.hutool.core.util.StrUtil;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.sm.entity.ProcessingDet;
import com.dongxin.scm.sm.mapper.ProcessingDetMapper;
import org.springframework.stereotype.Service;
import org.jeecg.common.system.base.service.BaseService;

import java.util.List;

/**
 * @Description: 加工详情表
 * @Author: jeecg-boot
 * @Date:   2021-04-19
 * @Version: V1.0
 */
@Service
public class ProcessingDetService extends BaseService<ProcessingDetMapper, ProcessingDet> {
    public  List<ProcessingDet> getDeleteListById(String processingId) {
        return baseMapper.getDeleteListById(processingId);
    }
}
