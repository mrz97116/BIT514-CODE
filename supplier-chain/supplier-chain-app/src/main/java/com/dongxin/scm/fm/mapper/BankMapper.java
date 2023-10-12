package com.dongxin.scm.fm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.fm.entity.Bank;
import com.dongxin.scm.fm.vo.BankCategory;
import org.apache.ibatis.annotations.Param;

import javax.persistence.MapKey;
import java.util.List;
import java.util.Map;

/**
 * @Description: 银行信息汇总表
 * @Author: jeecg-boot
 * @Date: 2020-10-28
 * @Version: V1.0
 */
public interface BankMapper extends BaseMapper<Bank> {

    List<BankCategory> getBankTypeList();
}
