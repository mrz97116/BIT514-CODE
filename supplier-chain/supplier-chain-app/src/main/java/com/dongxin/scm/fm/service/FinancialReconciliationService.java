package com.dongxin.scm.fm.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.entity.FinancialReconciliation;
import com.dongxin.scm.fm.mapper.FinancialReconciliationMapper;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 财务对账
 * @Author: jeecg-boot
 * @Date: 2021-04-18
 * @Version: V1.0
 */
@Service
public class FinancialReconciliationService extends BaseService<FinancialReconciliationMapper, FinancialReconciliation> {
    @Autowired
    private CustomerProfileService customerProfileService;


    @Transactional(rollbackFor = Exception.class)
    public void financialDataProcessing(List<FinancialReconciliation> list) {
        if(CollectionUtils.isEmpty(list)){
            log.error(StrUtil.format("导入数据为空："+list));
            throw new ScmException(StrUtil.format("导入数据为空："+list));
        }


        List<FinancialReconciliation> financialReconciliationList = new ArrayList();

        //把时间换成输入的时间一号
        if(ObjectUtil.isEmpty(list.get(0).getDate())){
            log.error(StrUtil.format("时间为空："+list.get(0).getDate()));
            throw new ScmException(StrUtil.format("时间为空："+list.get(0).getDate()));
        }
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(list.get(0).getDate());
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        Date date = gcLast.getTime();

        //判断是否导入数据月份是否已存在
        QueryWrapper<FinancialReconciliation> financialReconciliationQueryWrapper = new QueryWrapper<>();
        financialReconciliationQueryWrapper.lambda().eq(FinancialReconciliation::getDate, date);
        List<FinancialReconciliation> financialReconciliationDatabaseList = list(financialReconciliationQueryWrapper);

        //导入月份有数据，先删除数据
        if (CollectionUtils.isNotEmpty(financialReconciliationDatabaseList)) {
            //删除存在的数据
            QueryWrapper<FinancialReconciliation> removeFinancialReconciliationQueryWrapper = new QueryWrapper<>();
            removeFinancialReconciliationQueryWrapper.lambda().eq(FinancialReconciliation::getDate, date);
            remove(removeFinancialReconciliationQueryWrapper);
        }

        int count = 1;
        //把导入的数据add到数据库
        for (FinancialReconciliation financialReconciliation : list) {
            count++;
//            if(financialReconciliation.getCustomerId().equals("合计")){
//                continue;
//            }
            //客户名转换成客户id
            if(StringUtils.isBlank(financialReconciliation.getCustomerId())){
                log.error(StrUtil.format("第"+count+"行数据没有客户名,"+financialReconciliation));
                throw new ScmException(StrUtil.format("第"+count+"条数据没有客户名"));
            }
//            QueryWrapper<CustomerProfile> customerProfileQueryWrapper = new QueryWrapper<>();
//            customerProfileQueryWrapper.lambda().eq(CustomerProfile::getCompanyName, financialReconciliation.getCustomerId());
//            CustomerProfile customerProfile = customerProfileService.getOne(customerProfileQueryWrapper);
            CustomerProfile customerProfile = customerProfileService.getCustomer(financialReconciliation.getCustomerId());
            if (ObjectUtil.isEmpty(customerProfile)) {
                log.error(StrUtil.format("客户信息没有此客户:" + financialReconciliation));
                throw new ScmException(StrUtil.format("第"+count+"行客户信息没有此客户:" + financialReconciliation.getCustomerId()));
            }

            if(StringUtils.isBlank(financialReconciliation.getEndingCreditAccountString())){
//                log.error(StrUtil.format("第"+count+"条数据期末贷方为空:"+financialReconciliation));
//                throw new ScmException(StrUtil.format("第"+count+"行数据期末贷方为空:"+financialReconciliation.getEndingCreditAccountString()));
                financialReconciliation.setEndingCreditAccountString("0");
            }
            //去掉字符串中的逗号
            String endingCreditAccountString = financialReconciliation.getEndingCreditAccountString().replaceAll(",", "");
            //String转BigDecimal
            BigDecimal endingCreditAccountBigDecimal = new BigDecimal(endingCreditAccountString);

            //处理本期发生额借方
            if(StringUtils.isBlank(financialReconciliation.getPaymentAccountString())){
                financialReconciliation.setPaymentAccountString("0");
            }
            String paymentAccountString = financialReconciliation.getPaymentAccountString().replaceAll(",","");
            BigDecimal paymentAccount = new BigDecimal(paymentAccountString);


            //处理本期发生额贷方
            if(StringUtils.isBlank(financialReconciliation.getSettleAccountString())){
                financialReconciliation.setSettleAccountString("0");
            }
            String settleAccountString = financialReconciliation.getSettleAccountString().replaceAll(",","");
            BigDecimal settleAccount = new BigDecimal(settleAccountString);

            //创建实体对象
            FinancialReconciliation financialReconciliationEntity = new FinancialReconciliation();
            financialReconciliationEntity.setCustomerId(customerProfile.getId());
            financialReconciliationEntity.setDate(date);
            financialReconciliationEntity.setEndingCreditAccount(endingCreditAccountBigDecimal);
            financialReconciliationEntity.setPaymentAccount(paymentAccount);
            financialReconciliationEntity.setSettleAccount(settleAccount);

            financialReconciliationList.add(financialReconciliationEntity);
        }

        //add数据
        saveBatch(financialReconciliationList);
    }

    public IPage<FinancialReconciliation> translate(IPage<FinancialReconciliation> pageList) {
        List<String> customerIds = newArrayList();
        for(FinancialReconciliation financialReconciliation : pageList.getRecords()){
            customerIds.add(financialReconciliation.getCustomerId());
        }

        Map<String,String> customerNameMap = customerProfileService.getNameMap(customerIds);
        for(FinancialReconciliation financialReconciliation : pageList.getRecords()){
            financialReconciliation.setCustomerId(customerNameMap.get(financialReconciliation.getCustomerId()));
        }

        return pageList;
    }

    @Transactional(rollbackFor = Exception.class)
    public void importExcelNN(List<FinancialReconciliation> list) {
        if(CollectionUtils.isEmpty(list)){
            log.error(StrUtil.format("导入数据为空："+list));
            throw new ScmException(StrUtil.format("导入数据为空："+list));
        }


        List<FinancialReconciliation> financialReconciliationList = new ArrayList();

        //把时间换成输入的时间一号
        if(ObjectUtil.isEmpty(list.get(0).getDate())){
            log.error(StrUtil.format("时间为空："+list.get(0).getDate()));
            throw new ScmException(StrUtil.format("时间为空："+list.get(0).getDate()));
        }
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(list.get(0).getDate());
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        Date date = gcLast.getTime();

        //判断是否导入数据月份是否已存在
        QueryWrapper<FinancialReconciliation> financialReconciliationQueryWrapper = new QueryWrapper<>();
        financialReconciliationQueryWrapper.lambda().eq(FinancialReconciliation::getDate, date);
        List<FinancialReconciliation> financialReconciliationDatabaseList = list(financialReconciliationQueryWrapper);

        //导入月份有数据，先删除数据
        if (CollectionUtils.isNotEmpty(financialReconciliationDatabaseList)) {
            //删除存在的数据
            QueryWrapper<FinancialReconciliation> removeFinancialReconciliationQueryWrapper = new QueryWrapper<>();
            removeFinancialReconciliationQueryWrapper.lambda().eq(FinancialReconciliation::getDate, date);
            remove(removeFinancialReconciliationQueryWrapper);
        }


        int count = 1;
        //把导入的数据add到数据库
        for (FinancialReconciliation financialReconciliation : list) {
            count++;

            //客户名转换成客户id
            if(StringUtils.isBlank(financialReconciliation.getCustomerId())){
                log.error(StrUtil.format("第"+count+"行数据没有客户名,"+financialReconciliation));
                throw new ScmException(StrUtil.format("第"+count+"条数据没有客户名"));
            }
            String customerName = financialReconciliation.getCustomerId();
            customerName = customerName.substring(customerName.lastIndexOf("_")+1);

            String customerId = customerProfileService.getId(customerName);


            //处理期末余额
            if(StringUtils.isBlank(financialReconciliation.getEndingCreditAccountString())){
                financialReconciliation.setEndingCreditAccountString("0");
            }
            //去掉字符串中的逗号
            String endingCreditAccountString = financialReconciliation.getEndingCreditAccountString().replaceAll(",", "");
            //String转BigDecimal
            BigDecimal endingCreditAccountBigDecimal = new BigDecimal(endingCreditAccountString);


            //处理本期发生额借方
            if(StringUtils.isBlank(financialReconciliation.getPaymentAccountString())){
                financialReconciliation.setPaymentAccountString("0");
            }
            String paymentAccountString = financialReconciliation.getPaymentAccountString().replaceAll(",","");
            BigDecimal paymentAccount = new BigDecimal(paymentAccountString);

            //处理本期发生额贷方
            if(StringUtils.isBlank(financialReconciliation.getSettleAccountString())){
                financialReconciliation.setSettleAccountString("0");
            }
            String settleAccountString = financialReconciliation.getSettleAccountString().replaceAll(",","");
            BigDecimal settleAccount = new BigDecimal(settleAccountString);


            //新增
            FinancialReconciliation addFinancialReconciliation = new FinancialReconciliation();
            addFinancialReconciliation.setCustomerId(customerId);
            addFinancialReconciliation.setDate(date);
            addFinancialReconciliation.setEndingCreditAccount(endingCreditAccountBigDecimal);
            addFinancialReconciliation.setPaymentAccount(paymentAccount);
            addFinancialReconciliation.setSettleAccount(settleAccount);

            financialReconciliationList.add(addFinancialReconciliation);

        }

        saveBatch(financialReconciliationList);

    }
}
