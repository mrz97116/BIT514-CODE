package com.dongxin.scm.fm.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.lock.LockInfo;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.common.enums.CommonCheckStatusEnum;
import com.dongxin.scm.common.service.DataLogService;
import com.dongxin.scm.common.service.LockService;
import com.dongxin.scm.enums.IncomeAndCreditEnum;
import com.dongxin.scm.enums.SerialNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.dto.FundPoolDTO;
import com.dongxin.scm.fm.entity.*;
import com.dongxin.scm.fm.enums.*;
import com.dongxin.scm.fm.mapper.FundPoolMapper;
import com.dongxin.scm.fm.vo.AcceptanceBillVO;
import com.dongxin.scm.fm.vo.AcceptanceImport;
import com.dongxin.scm.home.entity.FundPoolAccountSevenInfo;
import com.dongxin.scm.home.entity.FundPoolSevenInfo;
import com.dongxin.scm.home.entity.FundPoolVO;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.om.util.Constants;
import com.dongxin.scm.utils.DateUtils;
import com.dongxin.scm.utils.SerialNoUtil;
import com.google.common.collect.Lists;
import org.jeecg.common.system.base.service.BaseService;
import org.jeecg.config.mybatis.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.newArrayList;
import static cn.hutool.core.collection.CollUtil.newHashMap;


/**
 * @Description: 来款资金表
 * @Author: jeecg-boot
 * @Date: 2020-10-27
 * @Version: V1.0
 */
@Service
public class FundPoolService extends BaseService<FundPoolMapper, FundPool> {

    @Autowired
    private FundDetailService fundDetailService;
    @Autowired
    private DataLogService dataLogService;
    @Autowired
    private CustomerProfileService customerProfileService;
    @Autowired
    private BankService bankService;
    @Autowired
    private LockService lockService;
    @Autowired
    private FundService fundService;
    @Autowired
    private BlackBankService blackBankService;
    @Autowired
    private SalesOrderService salesOrderService;

    public void checkAcceptBillInSixMonth(FundPool fundPool) {
        if (Constants.DIRECT_SALE_TENANT_IDS.contains(TenantContext.getTenant()) && fundPool.geneGapDays() > 180) {
            throw new ScmException("汇票剩余天数超过180天");
        }
    }


    //更新审核状态 并将新可用金额设置为初始来款金额
    @Transactional(rollbackFor = Exception.class)
    public void setApprove(FundPool param) {
        FundPool fundPool = getById(param);
        fundPool.setVerifyDate(new Date());
        fundPool.setStatus(CommonCheckStatusEnum.APPROVE.getCode());
        fundPool.setAvailAmount(param.getIncomingAmount());
        updateById(fundPool);
        salesOrderService.updateYRMCanLadingBill(fundPool.getCustomerId());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(FundPool entity) {
        if (PaymentMethodEnum.CASH.getCode().equals(entity.getPaymentMethod()) && StringUtils.isBlank(entity.getPaymentBank())) {
            throw new ScmException("请输入到账银行");
        }
        if (entity.getIncomingAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new ScmException("来款金额小于0");
        }
        String receiptCode = SerialNoUtil.getSerialNo(SerialNoEnum.RECEIPT_NO);
        entity.setReceiptCode(receiptCode);
        if (PaymentMethodEnum.ACCEPTANCE_BILL.getCode().equals(entity.getPaymentMethod())) {
            //校验必填

            if (StringUtils.isBlank(entity.getAcceptanceBank())) {
                throw new ScmException("请输入承兑银行");
            } else if (blackBankService.blackBankChick(entity.getAcceptanceBank())) {
                throw new ScmException("该银行已加入黑名单，不能收取");
            }

            //岑海不需要的校验必填
            if (!TenantContext.getTenant().equals("2")) {
                if (StringUtils.isBlank(entity.getPaymentBank())) {
                    throw new ScmException("请输入到账银行");
                }
                if (StringUtils.isBlank(entity.getAcceptanceTicketNo())) {
                    throw new ScmException("请输入承兑汇票号");
                }
                if (entity.getTicketDate() == null) {
                    throw new ScmException("请输入汇票到期日期");
                }
                if (entity.getIssueTicketsDate() == null) {
                    throw new ScmException("请输入出票日期");
                }
            }


            String bankType = baseMapper.queryBankTypeByBankId(entity.getAcceptanceBank());


            entity.setAcceptanceBankType(bankType);
        }

        if (ObjectUtil.isNull(entity.getIncomingDate())) {
            entity.setIncomingDate(new Date());
        }

        boolean flag = super.save(entity);
        String tableName = FundPool.class.getAnnotation(TableName.class).value();
        String dataContent = JSONObject.toJSONString(entity);
        dataLogService.dataLog(tableName, entity.getId(), dataContent);
        return flag;
    }

    @Override
    public boolean updateById(FundPool entity) {
        String tableName = FundPool.class.getAnnotation(TableName.class).value();
        String dataContent = JSONObject.toJSONString(entity);
        dataLogService.dataLog(tableName, entity.getId(), dataContent);
        return super.updateById(entity);
    }

    public List<FundPool> checkFile(List<FundPool> list) {

        for (int i = 0; list.size() > i; i++) {
            FundPool fundPool = list.get(i);

            //校验是否同时存在两个顾客名称，来款金额，来款时间的数据
            for (int j = i + 1; list.size() > j; j++) {
                FundPool fundPoolCheck = list.get(j);
                if (fundPool.getCustomerId().equals(fundPoolCheck.getCustomerId())
                        && fundPool.getIncomingDate().compareTo(fundPoolCheck.getIncomingDate()) == 0
                        && fundPool.getIncomingAmount().compareTo(fundPoolCheck.getIncomingAmount()) == 0) {
                    throw new ScmException(StrUtil.format("同时存在两个顾客名称、来款金额、来款时间相同的数据;" +
                            fundPool.getCustomerId() + fundPool.getIncomingAmount() + fundPool.getIncomingDate()));
                }
            }

            //校验顾客姓名
            CustomerProfile customerProfile = customerProfileService.getById(fundPool.getCustomerId());
            if (ObjectUtils.isEmpty(customerProfile)) {
                throw new ScmException(StrUtil.format("客户管理中没有:" + fundPool.getCustomerId()));
            }
            //校验来款方式
            boolean errorPaymentMethod = true;
            if (fundPool.getPaymentMethod().equals("acceptance") || fundPool.getPaymentMethod().equals(PaymentMethodEnum.CASH.getCode())) {
                errorPaymentMethod = false;
            }
            if (errorPaymentMethod) {
                throw new ScmException(StrUtil.format("来款方式存在错误导入信息;" + fundPool.getCustomerId()));
            }

            if (fundPool.getPaymentMethod().equals("acceptance")) {
                fundPool.setPaymentMethod(PaymentMethodEnum.ACCEPTANCE_BILL.getCode());
            }
            fundPool.setStatus(CommonCheckStatusEnum.PENDING_VERIFY.getCode());
            fundPool.setReceiptCode(SerialNoUtil.getSerialNo(SerialNoEnum.RECEIPT_NO));
        }
        return list;
    }

    public FundPoolVO numberOfPaymentsData() {

        FundPoolVO fundPoolVO = new FundPoolVO();

        //当月一号
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.DAY_OF_MONTH, -1);
        Date thisMonthNumberOne = calendar1.getTime();
        //当月来款笔数
        List<FundPool> fundPoolList = baseMapper.selectThisMonthFundPoolList(thisMonthNumberOne, CommonCheckStatusEnum.APPROVE.getCode());
        if (CollectionUtils.isEmpty(fundPoolList)) {
            String numberOfPayments = "本月还未有来款";
            fundPoolVO.setPayment(numberOfPayments);
            fundPoolVO.setAverageDailyPayment(numberOfPayments);
        } else {
            //获取当前时间并转为String类型
            String date = DateFormat.getDateInstance().format(new Date());
            //截图单月已过天数
            String daysPassedString = date.substring(date.lastIndexOf("-") + 1);
            BigDecimal daysPassed = new BigDecimal(daysPassedString);
            BigDecimal paymentSize = new BigDecimal(fundPoolList.size());
            BigDecimal averageDailyPayment = paymentSize.divide(daysPassed, 2, BigDecimal.ROUND_HALF_UP);

            fundPoolVO.setPayment(String.valueOf(paymentSize));
            fundPoolVO.setAverageDailyPayment(String.valueOf(averageDailyPayment));
        }

        return fundPoolVO;
    }

    public List<FundPoolSevenInfo> sevenDayFundPool() {
        //获取七天前的日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -7);
        Date sevenDaysAgoDate = calendar.getTime();

        //查询出过去七天的所有数据
        List<FundPool> fundPoolList = baseMapper.selectFundPoolSeven(sevenDaysAgoDate);
        if (CollectionUtils.isEmpty(fundPoolList)) {
            return null;
        }

        List<FundPoolSevenInfo> fundPoolSevenInfoList = newArrayList();
        BigDecimal numberOfPayments = BigDecimal.ZERO;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = -6; i <= 0; i++) {
            Calendar calendarDate = Calendar.getInstance();
            calendarDate.setTime(new Date());
            calendarDate.add(Calendar.DATE, i);
            Date daysAgoDate = calendarDate.getTime();
            String daysAgoDateString = simpleDateFormat.format(daysAgoDate);
            String year = daysAgoDateString.substring(0, 4);
            String month = daysAgoDateString.substring(daysAgoDateString.indexOf("-") + 1, daysAgoDateString.lastIndexOf("-"));
            String day = daysAgoDateString.substring(daysAgoDateString.lastIndexOf("-") + 1);
            String yearMonthDay = year + month + day;
            int yearMonthDayInt = Integer.parseInt(yearMonthDay);
            FundPoolSevenInfo fundPoolSevenInfo = new FundPoolSevenInfo();
            fundPoolSevenInfo.setDate(yearMonthDayInt);
            fundPoolSevenInfo.setNumberOfPayments(numberOfPayments);
            fundPoolSevenInfo.setDateString(daysAgoDateString);
            fundPoolSevenInfoList.add(fundPoolSevenInfo);
        }

        for (FundPool fundPool : fundPoolList) {
            String dateString = simpleDateFormat.format(fundPool.getIncomingDate());
            String year = dateString.substring(0, 4);
            String month = dateString.substring(dateString.indexOf("-") + 1, dateString.lastIndexOf("-"));
            String day = dateString.substring(dateString.lastIndexOf("-") + 1);
            String yearMonthDay = year + month + day;
            int yearMonthDayInt = Integer.parseInt(yearMonthDay);

            BigDecimal bigDecimalOne = BigDecimal.ONE;
            for (FundPoolSevenInfo fundPoolSevenInfo : fundPoolSevenInfoList) {
                if (fundPoolSevenInfo.getDate() == yearMonthDayInt) {
                    fundPoolSevenInfo.setNumberOfPayments(fundPoolSevenInfo.getNumberOfPayments().add(bigDecimalOne));
                }
            }
        }


        return fundPoolSevenInfoList;
    }

    public List<FundPoolAccountSevenInfo> sevenDayFundPoolAccount() {
        //获取七天前的日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -7);
        Date sevenDaysAgoDate = calendar.getTime();

        //查询出过去七天的所有数据
        QueryWrapper<FundPool> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().ge(FundPool::getCreateTime, sevenDaysAgoDate)
                .eq(FundPool::getStatus, CommonCheckStatusEnum.APPROVE.getCode());
        List<FundPool> fundPoolList = list(queryWrapper);
//        List<FundPool> fundPoolList = baseMapper.selectFundPoolSeven(sevenDaysAgoDate);
        if (CollectionUtils.isEmpty(fundPoolList)) {
            return null;
        }

        List<FundPoolAccountSevenInfo> fundPoolAccountSevenInfoArrayList = newArrayList();
        BigDecimal numberOfPayments = BigDecimal.ZERO;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = -6; i <= 0; i++) {
            Calendar calendarDate = Calendar.getInstance();
            calendarDate.setTime(new Date());
            calendarDate.add(Calendar.DATE, i);
            Date daysAgoDate = calendarDate.getTime();
            String daysAgoDateString = simpleDateFormat.format(daysAgoDate);
            String year = daysAgoDateString.substring(0, 4);
            String month = daysAgoDateString.substring(daysAgoDateString.indexOf("-") + 1, daysAgoDateString.lastIndexOf("-"));
            String day = daysAgoDateString.substring(daysAgoDateString.lastIndexOf("-") + 1);
            String yearMonthDay = year + month + day;
            int yearMonthDayInt = Integer.parseInt(yearMonthDay);
            FundPoolAccountSevenInfo fundPoolAccountSevenInfo = new FundPoolAccountSevenInfo();
            fundPoolAccountSevenInfo.setDate(yearMonthDayInt);
            fundPoolAccountSevenInfo.setNumberOfPayments(numberOfPayments);
            fundPoolAccountSevenInfo.setDateString(daysAgoDateString);
            fundPoolAccountSevenInfoArrayList.add(fundPoolAccountSevenInfo);
        }

        for (FundPool fundPool : fundPoolList) {
            String dateString = simpleDateFormat.format(fundPool.getCreateTime());
            String year = dateString.substring(0, 4);
            String month = dateString.substring(dateString.indexOf("-") + 1, dateString.lastIndexOf("-"));
            String day = dateString.substring(dateString.lastIndexOf("-") + 1);
            String yearMonthDay = year + month + day;
            int yearMonthDayInt = Integer.parseInt(yearMonthDay);

            for (FundPoolAccountSevenInfo fundPoolAccountSevenInfo : fundPoolAccountSevenInfoArrayList) {
                if (fundPoolAccountSevenInfo.getDate() == yearMonthDayInt) {
                    fundPoolAccountSevenInfo.setNumberOfPayments(fundPoolAccountSevenInfo.getNumberOfPayments()
                            .add(fundPool.getIncomingAmount()));
                }
            }
        }

        return fundPoolAccountSevenInfoArrayList;
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchAudit(List<String> fundPoolIdList) {
        if (CollectionUtils.isEmpty(fundPoolIdList)) {
            throw new ScmException(StrUtil.format("请选择审核数据"));
        }
        QueryWrapper<FundPool> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(FundPool::getId, fundPoolIdList);
        List<FundPool> fundPoolList = list(queryWrapper);
        if (CollectionUtils.isEmpty(fundPoolList)) {
            throw new ScmException(StrUtil.format("未查询到来款数据"));
        }
        for (FundPool fundPool : fundPoolList) {
            if (IncomingTypeEnum.NORMAL.getCode().equals(fundPool.getIncomingType())) {
                fundPool.setCreditAmount(BigDecimal.ZERO);
            }
            LockInfo lockInfo = lockService.lock(Constants.CUSTOMER_LOCK_HEADER + fundPool.getCustomerId());

            try {
                //判断来款审核是否是已通过
                if (CommonCheckStatusEnum.APPROVE.getCode().equals(fundPool.getStatus())) {
                    throw new ScmException(StrUtil.format("{}此来款已审核！", "来款id:" + fundPool.getId()));
                } else {
                    setApprove(fundPool);
                }

            } finally {
                lockService.releaseLock(lockInfo);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void editFundPool(FundPool fundPoolForm) {
        FundPool fundPool = getById(fundPoolForm.getId());
        if (ObjectUtil.isEmpty(fundPool)) {
            throw new ScmException("查询不到来款：" + fundPoolForm.getId());
        }
        fundPool.setIncomingAmount(fundPoolForm.getIncomingAmount());
        updateById(fundPool);
    }

    /**
     * 2021/06/04 zhuangjia
     * 汇款导入
     *
     * @param list
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public List<FundPool> importExcelHK(List<AcceptanceImport> list) {
        List<FundPool> fundPoolList = newArrayList();
        int count = 0;
        for (AcceptanceImport acceptanceImport : list) {
            count++;
            FundPool fundPool = new FundPool();

            if (StrUtil.isBlank(acceptanceImport.getBankName())) {
                continue;
            }

            //到账银行
            String bankName = acceptanceImport.getBankName().substring(0, acceptanceImport.getBankName().indexOf("银行") + 2);
            QueryWrapper<Bank> queryWrapperBank = new QueryWrapper<>();
            queryWrapperBank.lambda().eq(Bank::getBankName, bankName);
            List<Bank> bankList = bankService.list(queryWrapperBank);
            Bank bank = bankList.get(0);
            if (CollectionUtils.isEmpty(bankList)) {
                log.error(StrUtil.format("导入报错数据:{}", acceptanceImport.toString()));
                throw new ScmException(StrUtil.format("第{}条数据承兑人未在系统中维护银行信息!银行名称:" + bankName, count));
            }
            fundPool.setPaymentBank(bank.getId());

            //承兑汇票号
            if (StrUtil.isBlank(acceptanceImport.getAcceptanceTicketNo())) {
                log.error(StrUtil.format("导入报错数据:{}", acceptanceImport.toString()));
                throw new ScmException(StrUtil.format("第{}条数据票据号码为空！", count));
            }
            if (acceptanceImport.getAcceptanceTicketNo().length() < 9) {
                log.error(StrUtil.format("导入报错数据:{}", acceptanceImport.toString()));
                throw new ScmException(StrUtil.format("第{}条数据票据号码位数小于9！", count));
            }
            String acceptanceTicketNo = acceptanceImport.getAcceptanceTicketNo();
            //保留后9位
            fundPool.setAcceptanceTicketNo(acceptanceTicketNo.substring(acceptanceTicketNo.length() - 9));

            //票据金额
            if (StrUtil.isBlank(acceptanceImport.getIncomeAccount())) {
                log.error(StrUtil.format("导入报错数据:{}", acceptanceImport.toString()));
                throw new ScmException(StrUtil.format("第{}条数据票据金额为空！", count));
            }
            //去掉字符串中的逗号
            String billAccountString = acceptanceImport.getIncomeAccount().replaceAll(",", "");
            //String转BigDecimal
            BigDecimal billAccountBigDecimal = new BigDecimal(billAccountString);
            if (billAccountBigDecimal.compareTo(BigDecimal.ZERO) < 0) {
                log.error(StrUtil.format("导入报错数据:{}", acceptanceImport.toString()));
                throw new ScmException(StrUtil.format("第{}条数据票据金额小于零！", count));
            }
            fundPool.setIncomingAmount(billAccountBigDecimal);

            //出票日期
            if (StrUtil.isBlank(acceptanceImport.getIssueTicketsDate())) {
                throw new ScmException(StrUtil.format("第{}条数据票据出票日期为空!或日期格式错误", count));
            }
            if (acceptanceImport.getIssueTicketsDate().length() == 5) {
                int days = Integer.parseInt(acceptanceImport.getIssueTicketsDate());
                fundPool.setIssueTicketsDate(getDate(days));
            } else {
                String issueTicketsDay = acceptanceImport.getIssueTicketsDate().replaceAll("/", "");
                issueTicketsDay = issueTicketsDay.replaceAll("-", "");
                String issueTicketsDate = issueTicketsDay.substring(0, 4) + "-" + issueTicketsDay.substring(4, 6) + "-"
                        + issueTicketsDay.substring(6, 8);
                LocalDate issueTicketsLocalDate = LocalDate.parse(issueTicketsDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                Date issueDate = Date.from(issueTicketsLocalDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                fundPool.setIssueTicketsDate(issueDate);
            }
            //到期日期
            if (StrUtil.isBlank(acceptanceImport.getTicketDate())) {
                throw new ScmException(StrUtil.format("第{}条数据票据到期日期为空!或日期格式错误", count));
            }
            if (acceptanceImport.getTicketDate().length() == 5) {
                int days = Integer.parseInt(acceptanceImport.getTicketDate());
                fundPool.setTicketDate(getDate(days));
            } else {
                String ticketsDay = acceptanceImport.getTicketDate().replaceAll("/", "");
                ticketsDay = ticketsDay.replaceAll("-", "");
                String ticketsDate = ticketsDay.substring(0, 4) + "-" + ticketsDay.substring(4, 6) + "-" + ticketsDay.substring(6, 8);

                LocalDate ticketsLocalDate = LocalDate.parse(ticketsDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                Date tickDate = Date.from(ticketsLocalDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                fundPool.setTicketDate(tickDate);
            }


            //承兑银行
            if (StrUtil.isBlank(acceptanceImport.getAcceptor())) {
                fundPool.setAcceptanceBank("");
            } else {
                String acceptorName = acceptanceImport.getAcceptor().substring(0, acceptanceImport.getAcceptor().indexOf("银行") + 2);
                if (acceptorName.equals("上海浦东发展银行")) {
                    acceptorName = "浦发银行";
                }
                QueryWrapper<Bank> queryWrapperBank1 = new QueryWrapper<>();
                queryWrapperBank1.lambda().eq(Bank::getBankName, acceptorName);
                List<Bank> bankList1 = bankService.list(queryWrapperBank1);
                if (CollectionUtils.isEmpty(bankList1)) {
                    log.error(StrUtil.format("导入报错数据:{}", acceptanceImport.toString()));
                    throw new ScmException(StrUtil.format("第{}条数据承兑人未在系统中维护银行信息！银行名称:{}", count, acceptorName));
                }
                Bank bank1 = bankList1.get(0);
                fundPool.setAcceptanceBank(bank1.getId());
                if (blackBankService.blackBankChick(fundPool.getAcceptanceBank())) {
                    throw new ScmException("该银行已加入黑名单，不能收取");
                }
                fundPool.setAcceptanceBankType(bank1.getCategory());
            }

            //客户名称
            if (StrUtil.isBlank(acceptanceImport.getCustomerId())) {
                log.error(StrUtil.format("导入报错数据:{}", acceptanceImport.toString()));
                throw new ScmException(StrUtil.format("第{}条数据顾客为空！", count));
            }
            CustomerProfile customerProfile = customerProfileService.getCustomerId(acceptanceImport.getCustomerId());
            if (ObjectUtil.isNull(customerProfile)) {
                log.error(StrUtil.format("导入报错数据:{}", acceptanceImport.toString()));
                throw new ScmException(StrUtil.format("第{}条数据顾客未在系统中维护客户信息！" + "客户名称:{}", count, acceptanceImport.getCustomerId()));
            }
            fundPool.setCustomerId(customerProfile.getId());

            //来款方式：银承
            fundPool.setPaymentMethod(PaymentMethodEnum.ACCEPTANCE_BILL.getCode());

            //收据编号
            fundPool.setReceiptCode(SerialNoUtil.getSerialNo(SerialNoEnum.RECEIPT_NO));

            //审核状态
            fundPool.setStatus(CommonCheckStatusEnum.PENDING_VERIFY.getCode());

            //进账方式
            fundPool.setIncomeMethod(IncomeMethodTypeEnum.NORMAL_PAYMENT.getCode());

            //来款日期
            Date today = new Date();
            fundPool.setIncomingDate(today);

            //备注
            fundPool.setRemarks(acceptanceImport.getRemarks());

            fundPoolList.add(fundPool);
        }
        return fundPoolList;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<FundPool> importExcelCH(List<AcceptanceBillVO> list) {
        List<FundPool> fundPoolList = newArrayList();
        int count = 0;
        for (AcceptanceBillVO acceptanceBillVO : list) {
            count++;
            FundPool fundPool = new FundPool();
            //客户
            if (StrUtil.isBlank(acceptanceBillVO.getEndorser())) {
                if (StrUtil.isBlank(acceptanceBillVO.getDrawer())) {
                    log.error(StrUtil.format("导入报错数据:{}", acceptanceBillVO.toString()));
                    throw new ScmException(StrUtil.format("第" + count + "条数据出票人与背书人都为空！"));
                }
                CustomerProfile customerProfile = customerProfileService.getCustomerId(acceptanceBillVO.getDrawer());
                if (ObjectUtil.isNull(customerProfile)) {
                    log.error(StrUtil.format("导入报错数据:{}", acceptanceBillVO.toString()));
                    throw new ScmException(StrUtil.format("第" + count + "条数据出票人未在系统中维护客户信息！" + "客户名称:" + acceptanceBillVO.getDrawer()));
                }
                fundPool.setCustomerId(customerProfile.getId());
            } else {
                CustomerProfile customerProfile = customerProfileService.getCustomerId(acceptanceBillVO.getEndorser());
                if (ObjectUtil.isNull(customerProfile)) {
                    log.error(StrUtil.format("导入报错数据:{}", acceptanceBillVO.toString()));
                    throw new ScmException(StrUtil.format("第" + count + "条数据背书人未在系统中维护客户信息！" + "客户名称:" + acceptanceBillVO.getEndorser()));
                }
                fundPool.setCustomerId(customerProfile.getId());
            }

            //来款金额
            if (StrUtil.isBlank(acceptanceBillVO.getBillAccount())) {
                log.error(StrUtil.format("导入报错数据:{}", acceptanceBillVO.toString()));
                throw new ScmException(StrUtil.format("第" + count + "条数据票据金额为空！"));
            }
            //去掉字符串中的逗号
            String billAccountString = acceptanceBillVO.getBillAccount().replaceAll(",", "");
            //String转BigDecimal
            BigDecimal billAccountBigDecimal = new BigDecimal(billAccountString);
            if (billAccountBigDecimal.compareTo(BigDecimal.ZERO) < 0) {
                log.error(StrUtil.format("导入报错数据:{}", acceptanceBillVO.toString()));
                throw new ScmException(StrUtil.format("第" + count + "条数据票据金额小于零！"));
            }
            fundPool.setIncomingAmount(billAccountBigDecimal);

            //承兑银行
            if (StrUtil.isBlank(acceptanceBillVO.getAcceptor())) {
                log.error(StrUtil.format("导入报错数据:{}", acceptanceBillVO.toString()));
                throw new ScmException(StrUtil.format("第" + count + "条数据承兑人为空！"));
            }
            String acceptanceBank = acceptanceBillVO.getAcceptor();
            String bankName = acceptanceBank.substring(0, acceptanceBank.indexOf("银行") + 2);
            QueryWrapper<Bank> queryWrapperBank = new QueryWrapper<>();
            queryWrapperBank.lambda().eq(Bank::getBankName, bankName);
            List<Bank> bankList = bankService.list(queryWrapperBank);
            if (CollectionUtils.isEmpty(bankList)) {
                log.error(StrUtil.format("导入报错数据:{}", acceptanceBillVO.toString()));
                throw new ScmException(StrUtil.format("第" + count + "条数据承兑人未在系统中维护银行信息！" + "银行名称:" + bankName));
            }
            Bank bank = bankList.get(0);
            fundPool.setAcceptanceBank(bank.getId());
            if (blackBankService.blackBankChick(fundPool.getAcceptanceBank())) {
                throw new ScmException("该银行已加入黑名单，不能收取");
            }
            fundPool.setAcceptanceBankType(bank.getCategory());

            //承兑汇票号
            if (StrUtil.isBlank(acceptanceBillVO.getBillNumber())) {
                log.error(StrUtil.format("导入报错数据:{}", acceptanceBillVO.toString()));
                throw new ScmException(StrUtil.format("第" + count + "条数据票据号码为空！"));
            }
            if (acceptanceBillVO.getBillNumber().length() < 9) {
                log.error(StrUtil.format("导入报错数据:{}", acceptanceBillVO.toString()));
                throw new ScmException(StrUtil.format("第" + count + "条数据票据号码位数小于9！"));
            }
            String acceptanceTicketNo = acceptanceBillVO.getBillNumber();
            fundPool.setAcceptanceTicketNo(acceptanceTicketNo.substring(acceptanceTicketNo.length() - 9));


            //来款日期
            if (StrUtil.isBlank(acceptanceBillVO.getSubmitDate())) {
                log.error(StrUtil.format("导入报错数据:{}", acceptanceBillVO.toString()));
                throw new ScmException(StrUtil.format("第" + count + "条数据票提交日期为空！"));
            }
            //String转Date
            LocalDateTime submitDateLocalDateTime = LocalDateTime.parse(acceptanceBillVO.getSubmitDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Date submitDate = Date.from(submitDateLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
            fundPool.setIncomingDate(submitDate);


            //汇票到期日期
            if (StrUtil.isBlank(acceptanceBillVO.getMaturityDateOfBill())) {
                log.error(StrUtil.format("导入报错数据:{}", acceptanceBillVO.toString()));
                throw new ScmException(StrUtil.format("第" + count + "条数据票据到期日为空！"));
            }
            //String转Date
            LocalDate maturityDateOfBillLocalDate = LocalDate.parse(acceptanceBillVO.getMaturityDateOfBill(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Date maturityDateOfBill = Date.from(maturityDateOfBillLocalDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            fundPool.setTicketDate(maturityDateOfBill);


            //出票日期
            if (StrUtil.isBlank(acceptanceBillVO.getIssueTicketsDate())) {
                log.error(StrUtil.format("导入报错数据:{}", acceptanceBillVO.toString()));
                throw new ScmException(StrUtil.format("第" + count + "条数据出票日期为空！"));
            }
            //String转Date
            LocalDate issueTicketsDateLocalDate = LocalDate.parse(acceptanceBillVO.getIssueTicketsDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Date issueTicketsDate = Date.from(issueTicketsDateLocalDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            fundPool.setIssueTicketsDate(issueTicketsDate);

            //银行类型
            String bankType = baseMapper.queryBankTypeByBankId(bank.getId());
            fundPool.setAcceptanceBankType(bankType);

            //备注
            fundPool.setRemarks(acceptanceBillVO.getRemarks());

            //来款方式
            fundPool.setPaymentMethod(PaymentMethodEnum.ACCEPTANCE_BILL.getCode());

            //收据编号
            fundPool.setReceiptCode(SerialNoUtil.getSerialNo(SerialNoEnum.RECEIPT_NO));

            //审核状态
            fundPool.setStatus(CommonCheckStatusEnum.PENDING_VERIFY.getCode());

            //进账方式
            fundPool.setIncomeMethod(IncomeMethodTypeEnum.NORMAL_PAYMENT.getCode());

            fundPoolList.add(fundPool);
        }

        return fundPoolList;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteFundPool(String id) {
        if (StrUtil.isBlank(id)) {
            throw new ScmException(StrUtil.format("id为空"));
        }
        FundPool fundPool = getById(id);
        if (ObjectUtil.isEmpty(fundPool)) {
            throw new ScmException(StrUtil.format("找不到来款信息" + id));
        }
        //删除审核通过来款
        if (fundPool.getStatus().equals(CommonCheckStatusEnum.APPROVE.getCode())) {
            //判断该来款是否已被使用
            QueryWrapper<FundDetail> fundDetailQueryWrapper = new QueryWrapper<>();
            fundDetailQueryWrapper.lambda().eq(FundDetail::getFundId, fundPool.getId())
                    .ne(FundDetail::getType, FundDetailTypeEnum.DISCOUNT.getCode());
            List<FundDetail> fundDetailList = fundDetailService.list(fundDetailQueryWrapper);
            //注意，有的来款没有用款明细
            if (CollectionUtils.isNotEmpty(fundDetailList) || fundPool.getIncomingAmount().compareTo(fundPool.getAvailAmount()) != 0) {
                //校验没有流水又已使用的来款
                if (CollectionUtils.isEmpty(fundDetailList)) {
                    //找没有流水的装车单，给他加上流水（流水来款id为需删除的来款的id）
                    throw new ScmException(StrUtil.format("此来款没有资金流水，请联系技术人员处理！"));
                }

                //直销公司使用过的来款不可以删除（这里可以做成配置 分为直销公司与外部公司）
                if (1 != fundPool.getTenantId()
                        && 5 != fundPool.getTenantId()
                        && 6 != fundPool.getTenantId()
                        && 7 != fundPool.getTenantId()
                        && 8 != fundPool.getTenantId()
                        && 13 != fundPool.getTenantId()) {
                    throw new ScmException(StrUtil.format("已使用过的来款不可删除"));
                }


                //校验流水与以用金额是否相等
                BigDecimal fundDetailTotalAmount = BigDecimal.ZERO;
                for (FundDetail fundDetail : fundDetailList) {
                    fundDetailTotalAmount = fundDetailTotalAmount.add(fundDetail.getAmount());
                }
                if (fundDetailTotalAmount.compareTo(fundPool.getIncomingAmount().subtract(fundPool.getAvailAmount())) != 0) {
                    //找没有流水的装车单，给他加上流水（流水来款id为需删除的来款的id）
                    throw new ScmException(StrUtil.format("此来款用款明细不全，请联系技术人员处理！"));
                }

                List<FundDetail> addFundDetailList = newArrayList();
                //阳蕊明只有退款与装车预用两个类型的资金流水
                for (FundDetail fundDetail : fundDetailList) {
                    //装车预用流水
                    if (fundDetail.getType().equalsIgnoreCase(FundDetailTypeEnum.PRE_USE_STACK.getCode())) {
                        //重新选款
                        List<FundPoolDTO> fundPoolDTOList = fundService.autoSelectFundPool(fundDetail.getAmount()
                                , fundDetail.getCustomerId(), null, newArrayList(fundPool.getId()));
                        for (FundPoolDTO fundPoolDTO : fundPoolDTOList) {
                            FundPool updateFundPool = getById(fundPoolDTO.getFundId());
                            updateFundPool.setAvailAmount(updateFundPool.getAvailAmount().subtract(fundPoolDTO.getUseAmount()));
                            updateById(updateFundPool);

                            addFundDetailList.add(fundDetailService.addFundDetail(fundPoolDTO.getUseAmount()
                                    , fundPool.getCustomerId()
                                    , fundDetail.getOutTradeNo()
                                    , FundDetailTypeEnum.PRE_USE_STACK
                                    , fundPoolDTO.getFundId()
                                    , IncomeAndCreditEnum.CASH.getCode()));
                        }
                    }

                    //退款流水
                    if (fundDetail.getType().equalsIgnoreCase(FundDetailTypeEnum.REFUND.getCode())) {
                        //重新选款
                        List<FundPoolDTO> fundPoolDTOList = fundService.autoSelectFundPool(fundDetail.getAmount()
                                , fundDetail.getCustomerId(), null, newArrayList(fundPool.getId()));

                        for (FundPoolDTO fundPoolDTO : fundPoolDTOList) {
                            FundPool updateFundPool = getById(fundPoolDTO.getFundId());
                            updateFundPool.setAvailAmount(updateFundPool.getAvailAmount().subtract(fundPoolDTO.getUseAmount()));
                            updateById(updateFundPool);

                            addFundDetailList.add(fundDetailService.addFundDetail(fundPoolDTO.getUseAmount()
                                    , fundPool.getCustomerId()
                                    , fundDetail.getOutTradeNo()
                                    , FundDetailTypeEnum.REFUND
                                    , fundPoolDTO.getFundId()
                                    , IncomeAndCreditEnum.CASH.getCode()));
                        }
                    }
                }
                fundDetailService.saveBatch(addFundDetailList);
            }
            if (CollectionUtils.isNotEmpty(fundDetailList)) {
                fundDetailService.removeByIds(fundDetailList);
            }

        }
        removeById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateFundPool(FundPool param) {
        if (PaymentMethodEnum.CASH.getCode().equals(param.getPaymentMethod()) && StringUtils.isBlank(param.getPaymentBank())) {
            throw new ScmException("请输入到账银行");
        }
        if (param.getIncomingAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new ScmException("来款金额小于0");
        }
        if (PaymentMethodEnum.ACCEPTANCE_BILL.getCode().equals(param.getPaymentMethod())) {
            //校验必填
            if (StringUtils.isBlank(param.getAcceptanceBank())) {
                throw new ScmException("请输入承兑银行");
            } else if (blackBankService.blackBankChick(param.getAcceptanceBank())) {
                throw new ScmException("修改失败：该银行已加入黑名单");
            }

            //岑海不需要的校验必填
            if (!TenantContext.getTenant().equals("2")) {
                if (StringUtils.isBlank(param.getPaymentBank())) {
                    throw new ScmException("请输入到账银行");
                }
                if (StringUtils.isBlank(param.getAcceptanceTicketNo())) {
                    throw new ScmException("请输入承兑汇票号");
                }
                if (param.getTicketDate() == null) {
                    throw new ScmException("请输入汇票到期日期");
                }
                if (param.getIssueTicketsDate() == null) {
                    throw new ScmException("请输入出票日期");
                }
            }

            String bankType = baseMapper.queryBankTypeByBankId(param.getAcceptanceBank());

            param.setAcceptanceBankType(bankType);
        }

        updateById(param);
    }

    public void setFundPool(List<FundPool> pageList) {

        Map<String, String> paymentMethodEnumMap = new HashMap<>();
        PaymentMethodEnum[] paymentMethodEnums = PaymentMethodEnum.values();
        for (PaymentMethodEnum paymentMethod : paymentMethodEnums) {
            paymentMethodEnumMap.put(paymentMethod.getCode(), paymentMethod.getName());
        }

        Map<String, String> incomeMethodTypeEnumMap = new HashMap<>();
        IncomeMethodTypeEnum[] incomeMethodTypeEnum = IncomeMethodTypeEnum.values();
        for (IncomeMethodTypeEnum incomeMethodType : incomeMethodTypeEnum) {
            incomeMethodTypeEnumMap.put(incomeMethodType.getCode(), incomeMethodType.getDesc());
        }

        Map<String, String> fundPoolDetailTypeEnumMap = new HashMap<>();
        FundPoolDetailTypeEnum[] fundPoolDetailTypeEnum = FundPoolDetailTypeEnum.values();
        for (FundPoolDetailTypeEnum fundPoolDetailType : fundPoolDetailTypeEnum) {
            fundPoolDetailTypeEnumMap.put(fundPoolDetailType.getCode(), fundPoolDetailType.getDesc());
        }

        for (FundPool fundPool : pageList) {

            fundPool.setGapDays(DateUtils.gapDays(fundPool.getIncomingDate(), fundPool.getTicketDate()));
            fundPool.setTerm(getFundPoolTerm(fundPool));
            fundPool.setPaymentMethod(paymentMethodEnumMap.get(fundPool.getPaymentMethod()));
            fundPool.setIncomeMethod(incomeMethodTypeEnumMap.get(fundPool.getIncomeMethod()));


//            if (fundPool.getPaymentMethod().equals(PaymentMethodEnum.ACCEPTANCE_BILL.getName()) &&
//                    !fundPool.getGapDays().equals("已到期") &&
//                    me.zhyd.oauth.utils.StringUtils.isNotEmpty(fundPool.getGapDays())) {
//
////                汇票到期剩余天数
//            fundPool.geneTerm();
//                int remainingDaysDue = fundPool.geneGapDays();
//
//                if (remainingDaysDue <= 105) {
//                    fundPool.setTerm("3个月");
//                } else if (remainingDaysDue <= 135) {
//                    fundPool.setTerm("4个月");
//                } else if (remainingDaysDue <= 165) {
//                    fundPool.setTerm("5个月");
//                } else if (remainingDaysDue <= 195) {
//                    fundPool.setTerm("6个月");
//                }
//            }
        }

    }

    //获取期限
    public String getFundPoolTerm(FundPool fundPool) {
        if (PaymentMethodEnum.ACCEPTANCE_BILL.getCode().equalsIgnoreCase(fundPool.getPaymentMethod()) &&
                !fundPool.getGapDays().equals("已到期") &&
                StrUtil.isNotEmpty(fundPool.getGapDays())) {
            int remainingDaysDue = fundPool.geneGapDays();
            if (remainingDaysDue <= 105) {
                fundPool.setTerm("3个月");
            } else if (remainingDaysDue <= 135) {
                fundPool.setTerm("4个月");
            } else if (remainingDaysDue <= 165) {
                fundPool.setTerm("5个月");
            } else if (remainingDaysDue <= 195) {
                fundPool.setTerm("6个月");
            }
        }
        return fundPool.getTerm();
    }

    public static Date getDate(int days) {
        Calendar c = Calendar.getInstance();
        c.set(1900, 0, 1);
        c.add(Calendar.DATE, days - 2);
        return c.getTime();
    }


    public List<FundPool> selectFundPool(String customerId, String paymentMethod) {
        List<FundPool> fundPoolList = baseMapper.selectFundPool(customerId, paymentMethod);

        //获取顾客Id,汇款银行Id,承兑银行Id
        List<String> customerIds = Lists.newArrayList();
        List<String> paymentBankIds = Lists.newArrayList();
        List<String> acceptanceBankIds = Lists.newArrayList();
        List<String> fundPoolIds = newArrayList();

        if (CollectionUtils.isEmpty(fundPoolList)) {
            return newArrayList();
        }
        for (FundPool record : fundPoolList) {
            customerIds.add(record.getCustomerId());
            paymentBankIds.add(record.getPaymentBank());
            acceptanceBankIds.add(record.getAcceptanceBank());
            fundPoolIds.add(record.getId());
        }

        Map<String, BigDecimal> idAndPreUseAmount = newHashMap();
        QueryWrapper<FundDetail> fundDetailQueryWrapper = new QueryWrapper<>();
        fundDetailQueryWrapper.lambda().in(FundDetail::getFundId, fundPoolIds)
                .eq(FundDetail::getType, FundDetailTypeEnum.PRE_USE.getCode());
        List<FundDetail> fundDetailList = fundDetailService.list(fundDetailQueryWrapper);
        fundDetailList.forEach(i -> idAndPreUseAmount.merge(i.getFundId(), i.getAmount(), BigDecimal::add));
        //返回顾客姓名,汇款银行名,承兑银行名
        Map<String, String> customerName = customerProfileService.getNameMap(customerIds);
        Map<String, String> paymentBankNameMap = bankService.getBankNameMap(paymentBankIds);
        Map<String, String> acceptanceBankNameMap = bankService.getBankNameMap(acceptanceBankIds);
        for (FundPool record : fundPoolList) {
            BigDecimal preUserAmount = BigDecimal.ZERO;
            if (idAndPreUseAmount.get(record.getId()) != null) {
                preUserAmount = preUserAmount.add(idAndPreUseAmount.get(record.getId()));
            }

            record.setCustomerNameText(customerName.get(record.getCustomerId()));
            record.setPaymentBankName(paymentBankNameMap.get(record.getPaymentBank()));
            record.setAcceptanceBankName(acceptanceBankNameMap.get(record.getAcceptanceBank()));
            record.setGapDays(DateUtils.gapDays(record.getIncomingDate(), record.getTicketDate()));
            record.setAvailAmount(record.getAvailAmount().subtract(preUserAmount));
            record.geneTerm();
        }

        fundPoolList = fundPoolList.stream().filter(i -> i.getAvailAmount().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());

        return fundPoolList;
    }

    public List<FundPool> getCustomerFundPool(String customerId) {
        return baseMapper.getCustomerFundPool(customerId);
    }

    /**
     * 获取可用余额，适用于阳蕊明，提单不控款的情况
     */
    public Map<String, BigDecimal> getCustomerAvailAmount(List<String> customerIds) {
        Map<String, BigDecimal> result = newHashMap();

        if (CollectionUtil.isEmpty(customerIds)) {
            return result;
        }

        QueryWrapper<FundPool> queryWrapper = new QueryWrapper<>();

        queryWrapper.lambda().in(FundPool::getCustomerId, customerIds)
                .eq(FundPool::getStatus, CommonCheckStatusEnum.APPROVE.getCode())
                .gt(FundPool::getAvailAmount, BigDecimal.ZERO);

        List<FundPool> fundPoolList = list(queryWrapper);

        for (FundPool fundPool : fundPoolList) {
            result.merge(fundPool.getCustomerId(), fundPool.getAvailAmount(), BigDecimal::add);
        }

        return result;

    }

    /**
     * 获取可用余额，适用于阳蕊明，提单不控款的情况
     */
    public Map<String, BigDecimal> getCustomerTotalIncomeAmount(List<String> customerIds) {
        Map<String, BigDecimal> result = newHashMap();

        if (CollectionUtil.isEmpty(customerIds)) {
            return result;
        }

        QueryWrapper<FundPool> queryWrapper = new QueryWrapper<>();

        queryWrapper.lambda().in(FundPool::getCustomerId, customerIds)
                .eq(FundPool::getStatus, CommonCheckStatusEnum.APPROVE.getCode());

        List<FundPool> fundPoolList = list(queryWrapper);

        for (FundPool fundPool : fundPoolList) {
            result.merge(fundPool.getCustomerId(), fundPool.getIncomingAmount(), BigDecimal::add);
        }

        return result;

    }
}
