package com.dongxin.scm.om.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.om.entity.ContractDet;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 合同明细表
 * @Author: jeecg-boot
 * @Date: 2020-12-14
 * @Version: V1.0
 */
public interface ContractDetMapper extends BaseMapper<ContractDet> {

    boolean deleteByMainId(@Param("mainId") String mainId);

    List<ContractDet> selectByMainId(@Param("mainId") String mainId);
}
