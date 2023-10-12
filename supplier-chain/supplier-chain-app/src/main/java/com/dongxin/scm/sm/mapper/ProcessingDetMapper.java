package com.dongxin.scm.sm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.dongxin.scm.sm.entity.ProcessingDet;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 加工详情表
 * @Author: jeecg-boot
 * @Date:   2021-04-19
 * @Version: V1.0
 */
public interface ProcessingDetMapper extends BaseMapper<ProcessingDet> {

   List<ProcessingDet> getDeleteListById(String processingId);

}
