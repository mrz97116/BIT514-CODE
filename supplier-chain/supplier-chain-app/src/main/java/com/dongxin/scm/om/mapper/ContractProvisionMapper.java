package com.dongxin.scm.om.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.om.entity.ContractProvision;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 合同商务条款
 * @Author: jeecg-boot
 * @Date: 2020-12-14
 * @Version: V1.0
 */
public interface ContractProvisionMapper extends BaseMapper<ContractProvision> {

    boolean deleteByMainId(@Param("mainId") String mainId);

    List<ContractProvision> selectByMainId(@Param("mainId") String mainId);
}
