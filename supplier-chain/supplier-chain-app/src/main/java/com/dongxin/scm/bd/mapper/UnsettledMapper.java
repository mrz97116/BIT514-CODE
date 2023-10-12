package com.dongxin.scm.bd.mapper;

import com.dongxin.scm.bd.entity.Unsettled;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.sm.entity.Inventory;

/**
 * @Description: 未结算量
 * @Author: jeecg-boot
 * @Date: 2021-11-15
 * @Version: V1.0
 */
public interface UnsettledMapper extends BaseMapper<Unsettled> {

    boolean delById(String mainId);

}
