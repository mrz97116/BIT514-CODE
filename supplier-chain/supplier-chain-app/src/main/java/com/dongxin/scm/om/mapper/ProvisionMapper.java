package com.dongxin.scm.om.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.om.entity.Provision;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 商务条款
 * @Author: jeecg-boot
 * @Date: 2020-11-13
 * @Version: V1.0
 */
public interface ProvisionMapper extends BaseMapper<Provision> {

    boolean deleteByMainId(@Param("mainId") String mainId);

    List<Provision> selectByMainId(@Param("mainId") String mainId);
}
