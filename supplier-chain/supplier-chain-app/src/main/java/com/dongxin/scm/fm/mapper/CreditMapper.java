package com.dongxin.scm.fm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.fm.entity.Credit;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 授信额度表
 * @Author: jeecg-boot
 * @Date: 2020-10-27
 * @Version: V1.0
 */
public interface CreditMapper extends BaseMapper<Credit> {

    Long numberMax(@Param("numSeq") String numSeq);

}
