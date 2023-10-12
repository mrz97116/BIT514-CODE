package com.dongxin.scm.fm.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dongxin.scm.cm.entity.CustomerBank;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.mapper.CustomerBankMapper;
import com.dongxin.scm.cm.mapper.CustomerProfileMapper;
import com.dongxin.scm.common.service.LockService;
import com.dongxin.scm.enums.ProdClassCodeEnum;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.entity.Invoice;
import com.dongxin.scm.fm.entity.InvoiceDetail;
import com.dongxin.scm.fm.entity.SettleInfo;
import com.dongxin.scm.fm.enums.InvoiceStatusEnum;
import com.dongxin.scm.fm.enums.SettleStatusEnum;
import com.dongxin.scm.fm.enums.StatementStateEnum;
import com.dongxin.scm.fm.mapper.InvoiceCodeMapper;
import com.dongxin.scm.fm.mapper.InvoiceDetailMapper;
import com.dongxin.scm.fm.mapper.InvoiceMapper;
import com.dongxin.scm.fm.mapper.SettleInfoMapper;
import com.dongxin.scm.om.util.Constants;
import com.dongxin.scm.sm.entity.Mat;
import com.dongxin.scm.sm.entity.SmMaterials;
import com.dongxin.scm.sm.entity.Stack;
import com.dongxin.scm.sm.entity.StackDet;
import com.dongxin.scm.sm.enums.StatusEnum;
import com.dongxin.scm.sm.mapper.StackDetMapper;
import com.dongxin.scm.sm.mapper.StackMapper;
import com.dongxin.scm.sm.service.SmMaterialsService;
import com.dongxin.scm.utils.BigDecimalUtils;
import lombok.val;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.base.service.BaseService;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.config.mybatis.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 发票信息
 * @Author: jeecg-boot
 * @Date: 2021-02-01
 * @Version: V1.0
 */
@Service
public class InvoiceService extends BaseService<InvoiceMapper, Invoice> {

    @Resource
    private InvoiceMapper invoiceMapper;
    @Resource
    private InvoiceDetailMapper invoiceDetailMapper;
    @Resource
    private StackMapper stackMapper;
    @Resource
    private StackDetMapper stackDetMapper;
    @Resource
    private CustomerProfileMapper customerProfileMapper;
    @Resource
    private CustomerBankMapper customerBankMapper;
    @Resource
    private SettleInfoMapper settleInfoMapper;
    @Autowired
    private SettleInfoService settleInfoService;
    @Autowired
    private InvoiceDetailService invoiceDetailService;
    @Autowired
    private LockService lockService;
    @Autowired
    private SmMaterialsService smMaterialsService;

    @Transactional(rollbackFor = Exception.class)
    public void saveMain(Invoice invoice, List<InvoiceDetail> invoiceDetailList) {
        invoiceMapper.insert(invoice);
        if (invoiceDetailList != null && invoiceDetailList.size() > 0) {
            for (InvoiceDetail entity : invoiceDetailList) {
                //外键设置
                entity.setMainid(invoice.getId());
                invoiceDetailMapper.insert(entity);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMain(Invoice invoice, List<InvoiceDetail> invoiceDetailList) {
        invoiceMapper.updateById(invoice);

        //1.先删除子表数据
        invoiceDetailMapper.deleteByMainId(invoice.getId());

        //2.子表数据重新插入
        if (invoiceDetailList != null && invoiceDetailList.size() > 0) {
            for (InvoiceDetail entity : invoiceDetailList) {
                //外键设置
                entity.setMainid(invoice.getId());
                invoiceDetailMapper.insert(entity);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delMain(String id) {
        Invoice invoice = getById(id);
        QueryWrapper<SettleInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(SettleInfo::getSettleNo, Arrays.asList(invoice.getHdorderno().split(",")));
        List<SettleInfo> settleInfos = settleInfoService.list(queryWrapper);
        settleInfos.forEach(item->{
            item.setInvoiceStatus(InvoiceStatusEnum.NO.getCode());
        });
        settleInfoService.updateBatchById(settleInfos);
        //发票明细逻辑删除
        invoiceDetailService.deleteByMainId(id);
        invoiceMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            //批量逻辑删除
            Invoice invoice = getById(id);
            QueryWrapper<SettleInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(SettleInfo::getSettleNo, Arrays.asList(invoice.getHdorderno().split(",")));
            List<SettleInfo> list = settleInfoService.list(queryWrapper);
            list.forEach(item->{
                item.setInvoiceStatus(InvoiceStatusEnum.NO.getCode());
            });
            settleInfoService.updateBatchById(list);
            invoiceDetailService.deleteByMainId(id.toString());
            invoiceMapper.deleteById(id);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void invoiceApply(List<String> ids) {
        LockInfo lockInfo = lockService.lock(Constants.INVOICE_APPLY + TenantContext.getTenant());

        try {
            for (String id : ids) {
                generateInvoice(id);
            }

        } finally {
            lockService.releaseLock(lockInfo);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void invoiceApplyForOne() {
        LockInfo lockInfo = lockService.lock(Constants.INVOICE_APPLY + TenantContext.getTenant());

        try {
            QueryWrapper<SettleInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(SettleInfo::getInvoiceStatus, InvoiceStatusEnum.NO.getCode());
            List<SettleInfo> list = settleInfoService.list(queryWrapper);
            List<String> ids = list.stream().map(SettleInfo::getStackId).collect(Collectors.toList());

            for (String id : ids) {
                generateInvoice(id);
            }
        } finally {
            lockService.releaseLock(lockInfo);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    public void invoiceMerge(List<String> ids) {
        LockInfo lockInfo = lockService.lock(Constants.INVOICE_APPLY + TenantContext.getTenant());

        try {
            QueryWrapper<SettleInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().in(SettleInfo::getStackId, ids);

            List<SettleInfo> settleInfos = settleInfoService.list(queryWrapper);

            Map<Object, Long> mapGroup = settleInfos.stream().collect(Collectors.groupingBy(settleInfo -> settleInfo.getCustomer(), Collectors.counting()));
            if (mapGroup.size()>1) {
                throw new ScmException("只能合并同一家客户的结算单！！");
            }
            Invoice invoice = new Invoice();
            //发票重复生成校验
            settleInfos.forEach(item -> {
                if (InvoiceStatusEnum.YES.getCode().equals(item.getInvoiceStatus())) {
                    throw new ScmException(StrUtil.format("结算单: {} 已生成发票申请", item.getSettleNo()));
                }
                item.setInvoiceStatus(InvoiceStatusEnum.YES.getCode());
            });
            CustomerProfile customerProfile = customerProfileMapper.selectById(settleInfos.get(0).getCustomer());
            List<CustomerBank> customerBankList = customerBankMapper.selectByMainId(settleInfos.get(0).getCustomer());
            if (CollectionUtil.isEmpty(customerBankList)) {
                throw new ScmException(StrUtil.format("{} 银行信息为空,请到客户信息管理维护银行卡信息！", customerProfile.getCompanyName()));
            }
            if (ObjectUtil.isNull(customerProfile)) {
                throw new ScmException("查询不到客户信息");
            }
            if (StrUtil.isBlank(customerProfile.getTaxNo())) {
                throw new ScmException(StrUtil.format("{} 税号为空！", customerProfile.getCompanyName()));
            }
            CustomerBank customerBank = customerBankList.get(0);
            String mobile = customerProfile.getLandLineNo();
            if (StrUtil.isBlank(mobile)) {
                mobile = customerProfile.getMobile();
            }
            if (StrUtil.isBlank(mobile)) {
                mobile = " ";
            }
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            //发票主表赋值
            invoice.setChecker1(sysUser.getRealname());
            invoice.setCustomerId(customerProfile.getId());
            //发票地址电话显示格式 地址 + " " + 手机号
            invoice.setAddresstel(StrUtil.format("{} {}", customerProfile.getAddress(), mobile));
            invoice.setBankinfo(StrUtil.format("{} {}", customerBank.getBankBranch(), customerBank.getBankCardNo()));
            invoice.setTaxnum(customerProfile.getTaxNo());
            invoice.setLasttime(new DateTime());
            //有开票名称用开票名称，没有就用客户名称
            if (StringUtils.isNotBlank(customerProfile.getBillingName())) {
                invoice.setReceiverName(customerProfile.getBillingName());
            } else {
                invoice.setReceiverName(customerProfile.getCompanyName());
            }
            String settleNo = StrUtil.join(",", settleInfos.stream().map(SettleInfo::getSettleNo).collect(Collectors.toList()).toArray());
            String tenantId = TenantContext.getTenant();
            if ("9".equalsIgnoreCase(tenantId)) {
                invoice.setNotes("SH," + settleNo);
                invoice.setHdorderno("SH," + settleNo);

            } else {
                invoice.setNotes(settleNo);
                invoice.setHdorderno(settleNo);
            }
            save(invoice);

            QueryWrapper<StackDet> stackDetQueryWrapper = new QueryWrapper<>();
            stackDetQueryWrapper.lambda().in(StackDet::getStackId, ids);
            List<StackDet> stackDets = stackDetMapper.selectList(stackDetQueryWrapper);


            settleInfoService.updateBatchById(settleInfos);
            List<InvoiceDetail> invoiceDetList = newArrayList();
            List<StackDet> distinctStackDetList = stackDets.stream()
                    .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> StrUtil.format("{}-{}-{}-{}-{}-{}-{}", o.getProdCname(), o.getProdCnameOther(), o.getMatThick(), o.getSgSign(), o.getMatWidth(), o.getMatLen(), o.getPrice())))), ArrayList::new));
            for (StackDet distinctStackDet : distinctStackDetList) {
                InvoiceDetail invoiceDet = new InvoiceDetail();
                invoiceDet.setMainid(invoice.getId());
                invoiceDet.setWarename(distinctStackDet.getOldProdCname());
                String matCustStr = convertDoubleToString(distinctStackDet.getMatThick());
                if (distinctStackDet.getProdClassCode().equalsIgnoreCase(ProdClassCodeEnum.G.getValue())
                        || distinctStackDet.getProdClassCode().equalsIgnoreCase(ProdClassCodeEnum.F.getValue())) {
                    String matWidthStr = convertDoubleToString(distinctStackDet.getMatWidth());
                    matCustStr = matCustStr + " * " + matWidthStr.replaceAll(",", "");
                }

                BigDecimal num = BigDecimal.ZERO;
                BigDecimal discountTotalAmount = BigDecimal.ZERO;
                for (StackDet stackDet : stackDets) {
                    if (stackDet.getProdCname().equals(distinctStackDet.getProdCname())
                            && stackDet.getProdCnameOther().equals(distinctStackDet.getProdCnameOther())
                            && stackDet.getSgSign().equals(distinctStackDet.getSgSign())
                            && stackDet.getMatThick().compareTo(distinctStackDet.getMatThick()) == 0
                            && stackDet.getMatWidth().compareTo(distinctStackDet.getMatWidth()) == 0
                            && stackDet.getMatLen().compareTo(distinctStackDet.getMatLen()) == 0
                            && stackDet.getPrice().compareTo(distinctStackDet.getPrice()) == 0
                    ) {

                        discountTotalAmount = discountTotalAmount.add(stackDet.getDiscountTotalAmount());
                        num = num.add("B".equals(stackDet.getProdClassCode()) ? stackDet.getTheoryWeight() : BigDecimal.valueOf(stackDet.getWeight()));
                    }
                }
                BigDecimal discount = settleInfos.stream().map(SettleInfo::getDiscount).reduce(BigDecimal.ZERO, BigDecimal::add);
                invoiceDet.setWarespec(matCustStr);
                invoiceDet.setWareunit("吨");
                invoiceDet.setNum(num);
                invoiceDet.setUnitpricewithtax(distinctStackDet.getPrice());
                invoiceDet.setUnitpricewithouttax(null);
                invoiceDet.setPayment(discountTotalAmount);
                invoiceDet.setSaletax(BigDecimal.valueOf(0.13));
                invoiceDet.setTax(discountTotalAmount.divide(BigDecimal.valueOf(1.13), 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(0.13)));
                invoiceDet.setDiscount(discount);
                invoiceDetList.add(invoiceDet);
            }

            invoiceDetailService.saveBatch(invoiceDetList);
        } finally {
            lockService.releaseLock(lockInfo);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void generateInvoice(String stackId) {
        Invoice invoice = new Invoice();
        Stack stack = stackMapper.selectById(stackId);
        SettleInfo settleInfo = settleInfoMapper.selectById(stack.getSettledId());
        //发票重复生成校验
        if (InvoiceStatusEnum.YES.getCode().equals(settleInfo.getInvoiceStatus())) {
            throw new ScmException(StrUtil.format("结算单: {} 已生成发票申请", settleInfo.getSettleNo()));
        }
        //校验已红冲的结算单
//        if (settleInfo.getStatementState().equals(StatementStateEnum.WRITE_OFF.getCode())) {
//            log.error(StrUtil.format("结算单已红冲,结算单编号:" + settleInfo));
//            throw new ScmException(StrUtil.format("结算单已红冲，结算单编号:" + settleInfo.getSettleNo()));
//        }

        //客户信息、银行卡信息校验
        CustomerProfile customerProfile = customerProfileMapper.selectById(stack.getCustomerId());
        List<CustomerBank> customerBankList = customerBankMapper.selectByMainId(stack.getCustomerId());
        if (CollectionUtil.isEmpty(customerBankList)) {
            throw new ScmException(StrUtil.format("{} 银行信息为空,请到客户信息管理维护银行卡信息！", customerProfile.getCompanyName()));
        }
        if (ObjectUtil.isNull(customerProfile)) {
            throw new ScmException("查询不到客户信息");
        }
        if (StrUtil.isBlank(customerProfile.getTaxNo())) {
            throw new ScmException(StrUtil.format("{} 税号为空！", customerProfile.getCompanyName()));
        }
        CustomerBank customerBank = customerBankList.get(0);
        String mobile = customerProfile.getLandLineNo();
        if (StrUtil.isBlank(mobile)) {
            mobile = customerProfile.getMobile();
        }
        if (StrUtil.isBlank(mobile)) {
            mobile = " ";
        }
        List<StackDet> stackDetList = stackDetMapper.selectByMainId(stackId);
        //获取当前用户
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //发票主表赋值
        invoice.setChecker1(sysUser.getRealname());
        invoice.setCustomerId(customerProfile.getId());
        //发票地址电话显示格式 地址 + " " + 手机号
        invoice.setAddresstel(StrUtil.format("{} {}", customerProfile.getAddress(), mobile));
        invoice.setBankinfo(StrUtil.format("{} {}", customerBank.getBankBranch(), customerBank.getBankCardNo()));
        invoice.setTaxnum(customerProfile.getTaxNo());
        //有开票名称用开票名称，没有就用客户名称
        if (StringUtils.isNotBlank(customerProfile.getBillingName())) {
            invoice.setReceiverName(customerProfile.getBillingName());
        } else {
            invoice.setReceiverName(customerProfile.getCompanyName());
        }

        invoice.setHdorderno(settleInfo.getSettleNo());
        String tenantId = TenantContext.getTenant();

        //上海公司和南宁公司用同一个开票机，防止单号重复，上海公司加上前缀
        if ("9".equalsIgnoreCase(tenantId)) {
            invoice.setNotes("SH-" + settleInfo.getSettleNo());
            invoice.setHdorderno("SH-" + settleInfo.getSettleNo());

        } else {
            invoice.setNotes(settleInfo.getSettleNo());
            invoice.setHdorderno(settleInfo.getSettleNo());
        }
        invoice.setInvoiceMakeMoney(settleInfo.getSettleAmount());
        invoice.setInvoiceno("");
        invoice.setYn(YesNoEnum.NO.getCode());
        invoice.setLasttime(new DateTime());
        getBaseMapper().insert(invoice);


        settleInfo.setInvoiceStatus(InvoiceStatusEnum.YES.getCode());

        QueryWrapper<SettleInfo> settleInfoQueryWrapper = new QueryWrapper<>();
        settleInfoQueryWrapper.lambda().eq(SettleInfo::getStackingNo, settleInfo.getStackingNo());
        List<SettleInfo> settleInfoList = settleInfoService.list(settleInfoQueryWrapper);
        if (settleInfoList.size() > 1) {
            settleInfo.setStatementState(StatementStateEnum.WRITE_OFF.getCode());
        }
        settleInfoService.updateById(settleInfo);

        List<InvoiceDetail> invoiceDetList = newArrayList();
        if ("12".equals(TenantContext.getTenant())) {
            List<StackDet> distinctStackDetList = stackDetList.stream()
                    .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> StrUtil.format("{}-{}-{}-{}-{}-{}", o.getProdCname(), o.getProdCnameOther(), o.getMatThick(), o.getSgSign(), o.getMatWidth(), o.getMatLen())))), ArrayList::new));
            for (StackDet distinctStackDet : distinctStackDetList) {
                InvoiceDetail invoiceDet = new InvoiceDetail();
                invoiceDet.setMainid(invoice.getId());
                invoiceDet.setWarename(distinctStackDet.getOldProdCname());
                String matCustStr = convertDoubleToString(distinctStackDet.getMatThick());
                if (distinctStackDet.getProdClassCode().equalsIgnoreCase(ProdClassCodeEnum.G.getValue())
                        || distinctStackDet.getProdClassCode().equalsIgnoreCase(ProdClassCodeEnum.F.getValue())) {
                    String matWidthStr = convertDoubleToString(distinctStackDet.getMatWidth());
                    matCustStr = matCustStr + " * " + matWidthStr.replaceAll(",", "");
                }

//            BigDecimal afterDiscountPrice = stackDet.getDiscountPrice() == null ? stackDet.getPrice() : stackDet.getDiscountPrice();
//            BigDecimal payment = BigDecimalUtils.multiply(stackDet.getWeight(), afterDiscountPrice);
                BigDecimal num = BigDecimal.ZERO;
                BigDecimal discountTotalAmount = BigDecimal.ZERO;
                for (StackDet stackDet : stackDetList) {
                    if (stackDet.getProdCname().equals(distinctStackDet.getProdCname())
                            && stackDet.getProdCnameOther().equals(distinctStackDet.getProdCnameOther())
                            && stackDet.getSgSign().equals(distinctStackDet.getSgSign())
                            && stackDet.getMatThick().compareTo(distinctStackDet.getMatThick()) == 0
                            && stackDet.getMatWidth().compareTo(distinctStackDet.getMatWidth()) == 0
                            && stackDet.getMatLen().compareTo(distinctStackDet.getMatLen()) == 0
                    ) {
                        discountTotalAmount = discountTotalAmount.add(stackDet.getDiscountTotalAmount());
                        num = num.add("B".equals(stackDet.getProdClassCode()) ? stackDet.getTheoryWeight() : BigDecimal.valueOf(stackDet.getWeight()));
                    }
                }

                invoiceDet.setWarespec(matCustStr);
                invoiceDet.setWareunit("吨");
                invoiceDet.setNum(num);
                invoiceDet.setUnitpricewithtax(distinctStackDet.getPrice());
                invoiceDet.setUnitpricewithouttax(null);
//                BigDecimal stackDetSettleAccount = distinctStackDet.getDiscountTotalAmount();
                invoiceDet.setPayment(discountTotalAmount);
                invoiceDet.setSaletax(BigDecimal.valueOf(0.13));
                invoiceDet.setTax(discountTotalAmount.divide(BigDecimal.valueOf(1.13), 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(0.13)));
                invoiceDet.setDiscount(settleInfo.getDiscount());
                invoiceDetList.add(invoiceDet);
            }
        } else {
            for (StackDet stackDet : stackDetList) {
                InvoiceDetail invoiceDet = new InvoiceDetail();

                invoiceDet.setMainid(invoice.getId());
                invoiceDet.setWarename(stackDet.getOldProdCname());
                String matCustStr;
                if ("2".equals(TenantContext.getTenant())) {
                    matCustStr = convertDoubleToString(stackDet.getMatThick());
                } else {
                    DecimalFormat myFormatter = new DecimalFormat("#,##0.00");
                    matCustStr = myFormatter.format(stackDet.getMatThick());
                }

                if (stackDet.getProdClassCode().equalsIgnoreCase(ProdClassCodeEnum.G.getValue())
                        || stackDet.getProdClassCode().equalsIgnoreCase(ProdClassCodeEnum.F.getValue())) {
                    String matWidthStr = convertDoubleToString(stackDet.getMatWidth());
                    matCustStr = matCustStr + " * " + matWidthStr.replaceAll(",", "");
                }

                invoiceDet.setWarespec(matCustStr);
                invoiceDet.setWareunit("吨");
                invoiceDet.setNum(BigDecimal.valueOf(stackDet.getWeight()));
                invoiceDet.setUnitpricewithtax(stackDet.getPrice());
                invoiceDet.setUnitpricewithouttax(null);

//            BigDecimal afterDiscountPrice = stackDet.getDiscountPrice() == null ? stackDet.getPrice() : stackDet.getDiscountPrice();
//            BigDecimal payment = BigDecimalUtils.multiply(stackDet.getWeight(), afterDiscountPrice);
                BigDecimal stackDetSettleAccount = stackDet.getDiscountTotalAmount();
                invoiceDet.setPayment(stackDetSettleAccount);
                invoiceDet.setSaletax(BigDecimal.valueOf(0.13));
                invoiceDet.setTax(stackDetSettleAccount.divide(BigDecimal.valueOf(1.13), 3, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(0.13)));
                invoiceDet.setDiscount(settleInfo.getDiscount());
                invoiceDetList.add(invoiceDet);
            }
        }

        invoiceDetailService.saveBatch(invoiceDetList);
    }

    private String convertDoubleToString(Double val) {
        BigDecimal bd = new BigDecimal(String.valueOf(val));
        return bd.stripTrailingZeros().toPlainString();

    }

}
