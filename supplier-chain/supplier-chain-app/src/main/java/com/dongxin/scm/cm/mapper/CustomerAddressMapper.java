package com.dongxin.scm.cm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.cm.entity.CustomerAddress;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 顾客收货地址信息
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */
public interface CustomerAddressMapper extends BaseMapper<CustomerAddress> {

    boolean deleteByMainId(@Param("mainId") String mainId);

    List<CustomerAddress> selectByMainId(@Param("mainId") String mainId);

}
