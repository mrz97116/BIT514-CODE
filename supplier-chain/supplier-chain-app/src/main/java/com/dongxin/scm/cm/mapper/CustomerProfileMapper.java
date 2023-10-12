package com.dongxin.scm.cm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.om.vo.OptionVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 客户基础信息
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */
public interface CustomerProfileMapper extends BaseMapper<CustomerProfile> {

    List<OptionVO> queryCustomer();

    List<CustomerProfile> getName();

    List<CustomerProfile> getCustomerProfileList(@Param("customerId") String customerId);


}
