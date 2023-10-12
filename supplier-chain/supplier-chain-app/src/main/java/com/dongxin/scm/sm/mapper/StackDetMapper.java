package com.dongxin.scm.sm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.sm.entity.StackDet;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 装车单明细表
 * @Author: jeecg-boot
 * @Date: 2020-09-17
 * @Version: V1.0
 */
public interface StackDetMapper extends BaseMapper<StackDet> {

    boolean deleteByMainId(@Param("mainId") String mainId);

    List<StackDet> selectByMainId(@Param("mainId") String mainId);

    List<StackDet> selectUnSettleStackDet(String id);
}
