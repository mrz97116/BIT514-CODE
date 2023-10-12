package com.dongxin.scm.cm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.cm.entity.Attorney;

/**
 * @Description: 委托书
 * @Author: jeecg-boot
 * @Date: 2020-10-20
 * @Version: V1.0
 */
public interface AttorneyMapper extends BaseMapper<Attorney> {
    Long attorneyMaxNum(String numSeq);

}
