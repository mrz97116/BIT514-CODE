package com.dongxin.scm.cm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.cm.entity.CustomerBank;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 顾客银行卡信息
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */
public interface CustomerBankMapper extends BaseMapper<CustomerBank> {

    boolean deleteByMainId(@Param("mainId") String mainId);

    List<CustomerBank> selectByMainId(@Param("mainId") String mainId);

}
