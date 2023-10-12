package com.dongxin.scm.fm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.fm.entity.Invoice;

import java.util.List;

/**
 * @Description: 发票信息
 * @Author: jeecg-boot
 * @Date: 2021-02-01
 * @Version: V1.0
 */
public interface InvoiceMapper extends BaseMapper<Invoice> {

    List<Invoice> selectBySettleNo(String settleNo);

}
