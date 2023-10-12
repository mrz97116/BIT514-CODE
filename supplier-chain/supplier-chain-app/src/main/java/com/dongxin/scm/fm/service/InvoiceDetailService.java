package com.dongxin.scm.fm.service;

import com.dongxin.scm.fm.entity.InvoiceDetail;
import com.dongxin.scm.fm.mapper.InvoiceDetailMapper;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 发票明细
 * @Author: jeecg-boot
 * @Date: 2021-02-01
 * @Version: V1.0
 */
@Service
public class InvoiceDetailService extends BaseService<InvoiceDetailMapper, InvoiceDetail> {

    @Autowired
    private InvoiceDetailMapper invoiceDetailMapper;

    public List<InvoiceDetail> selectByMainId(String mainId) {
        return invoiceDetailMapper.selectByMainId(mainId);
    }

    public void deleteByMainId(String id) {
        //发票明细逻辑删除
        InvoiceDetail invoiceDetail = invoiceDetailMapper.selectByMainId(id).get(0);
        logicDeleteById(invoiceDetail);

    }
}
