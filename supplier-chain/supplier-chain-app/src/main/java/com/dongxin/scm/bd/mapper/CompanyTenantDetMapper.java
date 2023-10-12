package com.dongxin.scm.bd.mapper;

import java.util.List;
import com.dongxin.scm.bd.entity.CompanyTenantDet;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 公司租户明细表
 * @Author: jeecg-boot
 * @Date:   2021-02-26
 * @Version: V1.0
 */
public interface CompanyTenantDetMapper extends BaseMapper<CompanyTenantDet> {

	public boolean deleteByMainId(@Param("mainId") String mainId);
    
	public List<CompanyTenantDet> selectByMainId(@Param("mainId") String mainId);
}
