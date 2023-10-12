package com.dongxin.scm.bd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.bd.entity.TenantReportDet;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 公司租户id
 * @Author: jeecg-boot
 * @Date: 2021-01-27
 * @Version: V1.0
 */
public interface TenantReportDetMapper extends BaseMapper<TenantReportDet> {

    boolean deleteByMainId(@Param("mainId") String mainId);

    List<TenantReportDet> selectByMainId(@Param("mainId") String mainId);
}
