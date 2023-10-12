package com.dongxin.scm.bd.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * @Description: 租户id
 * @Autor: liujiazhi
 * Date:2021/3/23
 * @Version: V1.0
 */
public interface TenantMapper {

    public String getCompanyName(@Param("tenant_id") String tenant_id);

}
