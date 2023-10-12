package com.dongxin.scm.fm.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.fm.entity.CustomerFoundSummary;
import com.dongxin.scm.fm.entity.RefundRecords;
import com.dongxin.scm.fm.entity.SettleInfo;
import com.dongxin.scm.fm.mapper.*;
import com.dongxin.scm.fm.vo.CustomerBalanceVO;
import com.dongxin.scm.fm.vo.FundAccountVO;
import com.dongxin.scm.sm.entity.EquipmentSupplies;
import com.dongxin.scm.sm.mapper.EquipmentSuppliesMapper;
import com.dongxin.scm.utils.DateUtils;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

import static org.hibernate.validator.internal.util.CollectionHelper.newArrayList;

/**
 * @Description: 客户资金情况汇总表
 * @Author: jeecg-boot
 * @Date: 2021-04-06
 * @Version: V1.0
 */
@Service
public class CustomerFoundSummaryService extends BaseService<CustomerFoundSummaryMapper, CustomerFoundSummary> {
    @Resource
    private FundAccountMapper fundAccountMapper;
    @Resource
    private CustomerFoundSummaryMapper customerFoundSummaryMapper;
    @Resource
    private RefundRecordsMapper refundRecordsMapper;
    @Resource
    private FundPoolMapper fundPoolMapper;
    @Resource
    private SettleInfoMapper settleInfoMapper;
    @Resource
    private EquipmentSuppliesMapper equipmentSuppliesMapper;
    @Autowired
    private FundAccountService fundAccountService;

    @Transactional(rollbackFor = Exception.class)
    public void customerFoundSummary() {
        //获取客户当月资金账户余额
//        List<FundAccountVO> fundAccountSummaryList = fundAccountMapper.getFundAccountSummaryList();
        List<FundAccountVO> fundAccountSummaryList = newArrayList();
        List<CustomerBalanceVO> customerBalanceVOList = fundAccountService.getCustomerBalance(null);
        for (CustomerBalanceVO customerBalanceVO : customerBalanceVOList) {
            FundAccountVO fundAccountVO = new FundAccountVO();
            fundAccountVO.setCompanyName(customerBalanceVO.getCustomerName());
            fundAccountVO.setCustomerId(customerBalanceVO.getCustomerId());
            fundAccountVO.setEndingBanlance(customerBalanceVO.getCustomerBalance());
            fundAccountVO.setTenantId(customerBalanceVO.getTenantId());
            fundAccountSummaryList.add(fundAccountVO);
        }
        //更新当月期末余额记录（资金账户余额+未退还金额）
        for (FundAccountVO fundAccountVO : fundAccountSummaryList) {
            //本月期初余额、来款、结算、退款为0的数据不记录

            //查询客户当月期初余额
            QueryWrapper<CustomerFoundSummary> queryOpeningBalance = new QueryWrapper<>();
            queryOpeningBalance.lambda().eq(CustomerFoundSummary::getTenantId, fundAccountVO.getTenantId())
                    .eq(CustomerFoundSummary::getCustomerId, fundAccountVO.getCustomerId())
                    .eq(CustomerFoundSummary::getMonth, DateUtils.getCurrentMonth());
            CustomerFoundSummary summary = customerFoundSummaryMapper.selectOne(queryOpeningBalance);
            BigDecimal openingBanlance = BigDecimal.ZERO;
            if (summary != null) {
                openingBanlance = summary.getOpeningBalance();
                if (openingBanlance == null) {
                    openingBanlance = BigDecimal.ZERO;
                }
            }

            //查询客户当月来款
            BigDecimal incomeMoney = fundPoolMapper.sumInComeMoney(fundAccountVO.getCustomerId(), DateUtils.getFirstDay(), DateUtils.getLastDay(), fundAccountVO.getTenantId());

            //查询客户当月结算金额(正常结算+设备物资网结算)
            QueryWrapper<SettleInfo> settleInfoQueryWrapper = new QueryWrapper<>();
            settleInfoQueryWrapper.lambda().ge(SettleInfo::getCreateTime, DateUtils.getFirstDay())
                    .le(SettleInfo::getCreateTime, DateUtils.getLastDay())
                    .eq(SettleInfo::getCustomer, fundAccountVO.getCustomerId())
                    .eq(SettleInfo::getTenantId, fundAccountVO.getTenantId());
            BigDecimal normalSettle = settleInfoMapper.selectList(settleInfoQueryWrapper).stream().map(SettleInfo::getSettleAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

            QueryWrapper<EquipmentSupplies> equipmentSuppliesQueryWrapper = new QueryWrapper<>();
            equipmentSuppliesQueryWrapper.lambda().ge(EquipmentSupplies::getCreateTime, DateUtils.getFirstDay())
                    .le(EquipmentSupplies::getCreateTime, DateUtils.getLastDay())
                    .eq(EquipmentSupplies::getTenantId, fundAccountVO.getTenantId())
                    .eq(EquipmentSupplies::getCustomer, fundAccountVO.getCompanyName());
            BigDecimal equipmentSupplies = equipmentSuppliesMapper.selectList(equipmentSuppliesQueryWrapper).stream().map(EquipmentSupplies::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalSettleAmount = normalSettle.add(equipmentSupplies);

            //查询客户当月退还金额
            QueryWrapper<RefundRecords> recordsQueryWrapper = new QueryWrapper<>();
            recordsQueryWrapper.lambda().eq(RefundRecords::getCustomerId, fundAccountVO.getCustomerId())
                    .ge(RefundRecords::getUpdateTime, DateUtils.getFirstDay())
                    .le(RefundRecords::getUpdateTime, DateUtils.getLastDay())
                    .eq(RefundRecords::getTenantId, fundAccountVO.getTenantId())
                    .eq(RefundRecords::getStatus, "approve");
            List<RefundRecords> recordsList = refundRecordsMapper.selectList(recordsQueryWrapper);
            BigDecimal refundAmount = recordsList.stream().map(RefundRecords::getRefundAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

            //查询客户未退还金额
            QueryWrapper<RefundRecords> refundRecordsQueryWrapper = new QueryWrapper<>();
            refundRecordsQueryWrapper.lambda().eq(RefundRecords::getCustomerId, fundAccountVO.getCustomerId())
                    .eq(RefundRecords::getTenantId, fundAccountVO.getTenantId())
                    .eq(RefundRecords::getStatus, "pending_verify");
            List<RefundRecords> refundRecordsList = refundRecordsMapper.selectList(refundRecordsQueryWrapper);
            BigDecimal refundRecordSum = BigDecimal.ZERO; //客户未退还金额
            for (RefundRecords refundRecords : refundRecordsList) {
                refundRecordSum = refundRecordSum.add(refundRecords.getRefundAmount());
            }


            QueryWrapper<CustomerFoundSummary> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(CustomerFoundSummary::getCustomerId, fundAccountVO.getCustomerId())
                    .eq(CustomerFoundSummary::getMonth, DateUtils.getCurrentMonth())
                    .eq(CustomerFoundSummary::getTenantId, fundAccountVO.getTenantId());
            CustomerFoundSummary customerFoundSummary1 = customerFoundSummaryMapper.selectOne(queryWrapper);

            BigDecimal customerBalance = fundAccountVO.getEndingBanlance().add(refundRecordSum);
            if (customerFoundSummary1 != null) {

                customerFoundSummary1.setEndingBanlance(customerBalance);
                customerFoundSummaryMapper.updateCustomerFoundSummary(customerFoundSummary1);//更新资金汇总表已存在的客户当月期末余额记录
                if (customerBalance.compareTo(BigDecimal.ZERO) > 0) {
                    CustomerFoundSummary customerFoundSummary2 = new CustomerFoundSummary();
                    customerFoundSummary2.setId(null);
                    customerFoundSummary2.setCustomerId(fundAccountVO.getCustomerId());
                    customerFoundSummary2.setCompanyName(fundAccountVO.getCompanyName());
                    customerFoundSummary2.setTenantId(fundAccountVO.getTenantId());
                    customerFoundSummary2.setMonth(DateUtils.getNextMonth());
                    customerFoundSummary2.setOpeningBalance(customerBalance);
                    customerFoundSummary2.setEndingBanlance(null);
                    customerFoundSummaryMapper.insert(customerFoundSummary2);//增添资金汇总表已存在的客户下个月期初余额记录
                }


            } else {
                if (incomeMoney.compareTo(BigDecimal.ZERO) != 0
                        || totalSettleAmount.compareTo(BigDecimal.ZERO) != 0
                        || refundAmount.compareTo(BigDecimal.ZERO) != 0) {

                    CustomerFoundSummary customerFoundSummary = new CustomerFoundSummary();
                    customerFoundSummary.setCustomerId(fundAccountVO.getCustomerId());
                    customerFoundSummary.setTenantId((fundAccountVO.getTenantId()));
                    customerFoundSummary.setOpeningBalance(BigDecimal.ZERO);
                    customerFoundSummary.setEndingBanlance(customerBalance);
                    customerFoundSummary.setMonth(DateUtils.getCurrentMonth());
                    customerFoundSummary.setCompanyName(fundAccountVO.getCompanyName());
                    customerFoundSummaryMapper.insert(customerFoundSummary);//增添已有客户当月期末余额>=0资金汇总记录、新增客户当月末余额>=0资金汇总记录

                    if (customerBalance.compareTo(BigDecimal.ZERO) > 0) {
                        CustomerFoundSummary customerFoundSummary3 = new CustomerFoundSummary();
                        BeanUtils.copyProperties(customerFoundSummary, customerFoundSummary3);
                        customerFoundSummary3.setId(null);
                        customerFoundSummary3.setMonth(DateUtils.getNextMonth());
                        customerFoundSummary3.setOpeningBalance(customerBalance);
                        customerFoundSummary3.setEndingBanlance(null);
                        customerFoundSummaryMapper.insert(customerFoundSummary3);//增添客户下个月期初余额记录
                    }

                } else {
                    if (customerBalance.compareTo(BigDecimal.ZERO) > 0) {
                        CustomerFoundSummary haveBalanceCustomer = new CustomerFoundSummary();
                        haveBalanceCustomer.setTenantId(fundAccountVO.getTenantId());
                        haveBalanceCustomer.setCustomerId(fundAccountVO.getCustomerId());
                        haveBalanceCustomer.setCompanyName(fundAccountVO.getCompanyName());
                        haveBalanceCustomer.setMonth(DateUtils.getCurrentMonth());
                        haveBalanceCustomer.setOpeningBalance(BigDecimal.ZERO);
                        haveBalanceCustomer.setEndingBanlance(customerBalance);
                        customerFoundSummaryMapper.insert(haveBalanceCustomer);

                        CustomerFoundSummary haveBalanceCustomerNext = new CustomerFoundSummary();
                        haveBalanceCustomerNext.setTenantId(fundAccountVO.getTenantId());
                        haveBalanceCustomerNext.setCustomerId(fundAccountVO.getCustomerId());
                        haveBalanceCustomerNext.setCompanyName(fundAccountVO.getCompanyName());
                        haveBalanceCustomerNext.setMonth(DateUtils.getNextMonth());
                        haveBalanceCustomerNext.setOpeningBalance(customerBalance);
                        haveBalanceCustomerNext.setEndingBanlance(BigDecimal.ZERO);
                        customerFoundSummaryMapper.insert(haveBalanceCustomerNext);

                    }
                }
            }

        }

    }

}
