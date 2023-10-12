package com.dongxin.scm.fm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.fm.entity.InvoiceDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 发票明细
 * @Author: jeecg-boot
 * @Date: 2021-02-01
 * @Version: V1.0
 */
public interface InvoiceDetailMapper extends BaseMapper<InvoiceDetail> {

    boolean deleteByMainId(@Param("mainid") String mainId);

    List<InvoiceDetail> selectByMainId(@Param("mainid") String mainId);
}
