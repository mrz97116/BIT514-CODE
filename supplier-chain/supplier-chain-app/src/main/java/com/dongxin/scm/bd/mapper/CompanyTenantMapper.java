package com.dongxin.scm.bd.mapper;

import java.util.List;

import com.dongxin.scm.bd.vo.CompanyTenantVo;
import org.apache.ibatis.annotations.Param;
import com.dongxin.scm.bd.entity.CompanyTenant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 公司租户表
 * @Author: jeecg-boot
 * @Date:   2021-02-26
 * @Version: V1.0
 */
public interface CompanyTenantMapper extends BaseMapper<CompanyTenant> {

    List<CompanyTenantVo> querySys_companyInfo();

    /**根据租户id查询控款标识*/
    CompanyTenantVo queryPaymentConfiguration(@Param("tenant_Code") Integer tenant_Code);

}
