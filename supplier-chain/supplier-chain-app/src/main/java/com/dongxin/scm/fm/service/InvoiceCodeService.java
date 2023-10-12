package com.dongxin.scm.fm.service;

import com.dongxin.scm.fm.entity.InvoiceCode;
import com.dongxin.scm.fm.mapper.InvoiceCodeMapper;
import freemarker.template.utility.NumberUtil;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.stereotype.Service;

/**
 * @Description: 发票码信息表
 * @Author: jeecg-boot
 * @Date: 2020-12-23
 * @Version: V1.0
 */
@Service
public class InvoiceCodeService extends BaseService<InvoiceCodeMapper, InvoiceCode> {

    @Override
    public boolean save(InvoiceCode invoiceCode) {
        //设置直径(外径)止、直径(外径)起，初始值
        if (NumberUtil.isNaN(invoiceCode.getDiameterEnd())) {
            invoiceCode.setDiameterEnd(0d);
        }
        if (NumberUtil.isNaN(invoiceCode.getDiameterStart())) {
            invoiceCode.setDiameterStart(0d);
        }
        return super.save(invoiceCode);
    }

}
