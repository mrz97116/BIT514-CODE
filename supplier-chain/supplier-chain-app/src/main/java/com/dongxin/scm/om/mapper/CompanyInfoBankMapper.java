package com.dongxin.scm.om.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.om.entity.CompanyInfoBank;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 公司银行卡信息
 * @Author: jeecg-boot
 * @Date: 2020-12-14
 * @Version: V1.0
 */
public interface CompanyInfoBankMapper extends BaseMapper<CompanyInfoBank> {

    boolean deleteByMainId(@Param("mainId") String mainId);

    List<CompanyInfoBank> selectByMainId(@Param("mainId") String mainId);
}
