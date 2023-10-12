package com.dongxin.scm.fm.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dongxin.scm.bd.entity.CompanyTenant;
import com.dongxin.scm.bd.entity.CompanyTenantDet;
import com.dongxin.scm.bd.mapper.CompanyTenantDetMapper;
import com.dongxin.scm.bd.mapper.CompanyTenantMapper;
import com.dongxin.scm.common.service.LockService;
import com.dongxin.scm.enums.ProdClassCodeEnum;
import com.dongxin.scm.enums.SerialNoEnum;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.fm.entity.FundPoolDetail;
import com.dongxin.scm.fm.entity.SettleInfo;
import com.dongxin.scm.fm.enums.FundDetailTypeEnum;
import com.dongxin.scm.fm.enums.InvoiceStatusEnum;
import com.dongxin.scm.fm.enums.SettleStatusEnum;
import com.dongxin.scm.fm.enums.StatementStateEnum;
import com.dongxin.scm.fm.mapper.SettleInfoMapper;
import com.dongxin.scm.fm.vo.IdAndAccountVO;
import com.dongxin.scm.om.util.Constants;
import com.dongxin.scm.sm.entity.Stack;
import com.dongxin.scm.sm.entity.StackDet;
import com.dongxin.scm.sm.enums.StatusEnum;
import com.dongxin.scm.sm.service.StackDetService;
import com.dongxin.scm.sm.service.StackService;
import com.dongxin.scm.utils.BigDecimalUtils;
import com.dongxin.scm.utils.ProdClassToSerialNoUtils;
import com.dongxin.scm.utils.SerialNoUtil;
import org.jeecg.common.system.base.service.BaseService;
import org.jeecg.config.mybatis.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 结算单信息
 * @Author: jeecg-boot
 * @Date: 2020-10-27
 * @Version: V1.0
 */
@Service
public class SettleInfoService extends BaseService<SettleInfoMapper, SettleInfo> {

    @Autowired
    private StackService stackService;
    @Autowired
    private StackDetService stackDetService;
    @Resource
    private SettleInfoMapper settleInfoMapper;
    @Resource
    private CompanyTenantMapper companyTenantMapper;
    @Resource
    private CompanyTenantDetMapper companyTenantDetMapper;
    @Autowired
    private FundPoolDetailService fundPoolDetailService;
    @Autowired
    private FundService fundService;
    @Autowired
    private LockService lockService;

    @Transactional(rollbackFor = Exception.class)
    public void settle(List<String> stackIdList, BigDecimal discount, String remark) {
        if (CollectionUtil.isEmpty(stackIdList)) {
            throw new ScmException("请选择装车单");
        }

        List<Stack> stackList = stackService.listByIds(stackIdList);
        String tenantId = TenantContext.getTenant();

        if ("12".equalsIgnoreCase(tenantId) && discount.compareTo(BigDecimal.ZERO) > 0) {
            throw new ScmException("广东分公司暂时不支持结算优惠");
        }
        //通过tenantId查询对应的companyTenantDet
        QueryWrapper<CompanyTenantDet> companyTenantDetQueryWrapper = new QueryWrapper<>();
        companyTenantDetQueryWrapper.lambda().eq(CompanyTenantDet::getTenantCode, tenantId);
        //通过companyTenantDet的parentId找到对应的companyTenant
        String parentId = companyTenantDetMapper.selectOne(companyTenantDetQueryWrapper).getParentId();
        QueryWrapper<CompanyTenant> companyTenantQueryWrapper = new QueryWrapper<>();
        companyTenantQueryWrapper.lambda().eq(CompanyTenant::getId, parentId);
        //获取companyTenant下的不同客户配置标识，并判断是否为是
        String diffCusBatSettle = companyTenantMapper.selectOne(companyTenantQueryWrapper).getDiffCusBatSettle();
        if (diffCusBatSettle.equals(YesNoEnum.NO.getCode())) {
            //不允许不同客户的装车单同时结算
            String customerId = stackList.get(0).getCustomerId();
            for (Stack stack : stackList) {
                if (!customerId.equals(stack.getCustomerId())) {
                    throw new ScmException("不同客户不能在一起结算");
                }
            }
        }

        QueryWrapper<StackDet> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(StackDet::getStackId, stackIdList);
        List<StackDet> stackDetList = stackDetService.list(queryWrapper);
        for (StackDet stackDet : stackDetList) {
            if (discount.compareTo(stackDet.getPrice()) > 0) {
                log.error(StrUtil.format("{}折扣金额大于单价", stackDet.getId()));
                throw new ScmException("折扣金额大于单价");
            }
        }
        if (CollectionUtils.isEmpty(stackIdList)) {
            throw new ScmException("结算错误，未检出到数据库有选中的装车单信息");
        }


        for (Stack stack : stackList) {
            if (stack.getStatus().equals(StatusEnum.WRITE_OFF.getCode()) || stack.getStatus().equals(StatusEnum.WRITE_OFF_REVIEWER.getCode())) {
                if (stack.getDiscount().compareTo(discount) != 0) {
                    log.error("冲红的装车单每吨优惠金额不一致，装车单id：" + stack.getId());
                    throw new ScmException(StrUtil.format("冲红的装车单每吨优惠金额不一致，冲红装车每吨单优惠金额:"
                            + stack.getDiscount() + ",输入每吨优惠金额:" + discount));
                }
                settleWriteOffStackAddFundDetail(stack);
            }
            BigDecimal totalSettleAmount = BigDecimal.ZERO;
            BigDecimal totalStackAmount = stack.getTotalAmount();

            QueryWrapper<StackDet> stackDetQueryWrapper = new QueryWrapper<>();
            stackDetQueryWrapper.lambda().eq(StackDet::getStackId, stack.getId());
            List<StackDet> settleStackDetList = stackDetService.list(stackDetQueryWrapper);
            for (StackDet stackDet : settleStackDetList) {
                BigDecimal detSettleAccount;
                if ("12".equalsIgnoreCase(TenantContext.getTenant())
                        && ProdClassCodeEnum.B.getValue().equalsIgnoreCase(stackDet.getProdClassCode())) {
                    detSettleAccount  = BigDecimalUtils.multiply(stackDet.getTheoryPrice().subtract(discount), stackDet.getTheoryWeight());
                } else {
                    detSettleAccount = BigDecimalUtils.multiply(stackDet.getPrice().subtract(discount), stackDet.getWeight());
                }
                totalSettleAmount = totalSettleAmount.add(detSettleAccount);
            }
            BigDecimal totalDiscount = totalStackAmount.subtract(totalSettleAmount);

            if (ObjectUtil.isEmpty(stack)) {
                log.error(StrUtil.format("{}未检查到的装车单", stack.getId()));
                throw new ScmException("结算错误，未检查到数据库有该条装车单信息");
            }
            if (stack.getSettled().equals(SettleStatusEnum.SETTLED.getCode())) {
                throw new ScmException("结算单已结算，请刷新后重试");
            }
            if (!stack.getStatus().equals(StatusEnum.WRITE_OFF.getCode()) && !stack.getStatus().equals(StatusEnum.WRITE_OFF_REVIEWER.getCode())) {
                if (totalDiscount.compareTo(stack.getTotalAmount()) > 0) {
                    log.error(StrUtil.format("{}折扣总金额", totalDiscount));
                    log.error(StrUtil.format("{}装车单总金额", stack.getTotalAmount()));
                    throw new ScmException("折扣金额大于装车单总金额");
                }
            }

            if (discount.compareTo(BigDecimal.ZERO) < 0) {
                log.error(StrUtil.format("{}折扣", discount));
//            log.error(StringUtils.format("参数错误，discount:{}", discount));
                throw new ScmException("折扣金额小于零");
            }

            LockInfo lockInfo = lockService.lock(Constants.CUSTOMER_LOCK_HEADER + stack.getCustomerId());

            try {

                //生成结算单
                SettleInfo settleInsert = new SettleInfo();
                //产品大类
                Set set = new HashSet();

                QueryWrapper<StackDet> stackDetInDbQueryWrapper = new QueryWrapper<>();
                stackDetInDbQueryWrapper.lambda().eq(StackDet::getStackId, stack.getId());
                List<StackDet> stackDetInDbList = stackDetService.list(stackDetInDbQueryWrapper);
                for (StackDet stackDet : stackDetInDbList) {
                    set.add(stackDet.getProdClassCode());
                }
                settleInsert.setProdCode(CollectionUtil.join(set, ","));
                settleInsert.setCustomer(stack.getCustomerId());
                settleInsert.setRowTotal(totalStackAmount);
                settleInsert.setSettleAmount(totalSettleAmount);
                settleInsert.setDiscount(discount);
                settleInsert.setStatus(SettleStatusEnum.SETTLED.getCode());
                //初始化单据编码
                String settleNo = null;
                //判断是否配置产品大类单据编码
                if (queryIfProdClassSerialNo(stack.getTenantId())) {
                    settleNo = ProdClassToSerialNoUtils.generateSettleNoWithProdClassPrefix(stackDetList.get(0).getProdClassCode());
                } else {
                    settleNo = SerialNoUtil.getSerialNo(SerialNoEnum.SETTLE_NO);
                }
                settleInsert.setSettleNo(settleNo);
                settleInsert.setSettleName(stack.getSalesMan());
                settleInsert.setStackId(stack.getId());
                settleInsert.setStatementState(StatementStateEnum.NORMAL.getCode());
                settleInsert.setInvoiceStatus(InvoiceStatusEnum.NO.getCode());
                settleInsert.setShipDate(stack.getShipDate());
                settleInsert.setSaleBillNo(stack.getSaleBillNo());
                settleInsert.setStackingNo(stack.getStackingNo());
                settleInsert.setRemark(remark);
                save(settleInsert);

                //修改装车单数据
                Stack upDateStack = new Stack();
                upDateStack.setId(stack.getId());
                upDateStack.setSettled(SettleStatusEnum.SETTLED.getCode());
                upDateStack.setSettledId(settleInsert.getId());

                stackService.updateById(upDateStack);

                if (!stack.getStatus().equals(StatusEnum.WRITE_OFF.getCode()) && !stack.getStatus().equals(StatusEnum.WRITE_OFF_REVIEWER.getCode())) {
                    //修改装车单明细
                    stackService.updateTotalPriceAfterSettled(stack.getId(), discount);
                    //结算
                    fundService.settle(stack);
                }
            } finally {
                lockService.releaseLock(lockInfo);
            }
        }
    }

    private void settleWriteOffStackAddFundDetail(Stack stack) {

        QueryWrapper<FundPoolDetail> fundPoolDetailQueryWrapper = new QueryWrapper<>();
        fundPoolDetailQueryWrapper.lambda().eq(FundPoolDetail::getOutTradeNo, stack.getId())
                .eq(FundPoolDetail::getType, FundDetailTypeEnum.WRITE_OFF.getCode());
        List<FundPoolDetail> fundPoolDetailList = fundPoolDetailService.list(fundPoolDetailQueryWrapper);
        String fundAccountId = fundPoolDetailList.get(0).getAccountId();
        for (FundPoolDetail fundPoolDetail : fundPoolDetailList) {
            fundPoolDetailService.addFundPoolDetail(stack.getCustomerId(), fundPoolDetail.getAccountId()
                    , fundPoolDetail.getFundPoolId(), stack.getId(), fundPoolDetail.getAmount().negate(), FundDetailTypeEnum.SETTLE.getCode());
        }

//        fundDetailService.saveFundDetail(stack.getSettledTotalPrice(), fundAccountId, stack.getCustomerId()
//                , stack.getId(), FundDetailTypeEnum.SETTLE);

        QueryWrapper<StackDet> stackDetQueryWrapper = new QueryWrapper<>();
        stackDetQueryWrapper.lambda().eq(StackDet::getStackId, stack.getId());
        List<StackDet> stackDetList = stackDetService.list(stackDetQueryWrapper);

        BigDecimal discountAccount = BigDecimal.ZERO;
        for (StackDet stackDet : stackDetList) {
            discountAccount = discountAccount.add(stackDet.getTotalAmount().subtract(stackDet.getDiscountTotalAmount()));
        }
        if (discountAccount.compareTo(BigDecimal.ZERO) != 0) {
//            fundDetailService.saveFundDetail(discountAccount, fundAccountId
//                    , stack.getCustomerId(), stack.getId(), FundDetailTypeEnum.DISCOUNT);

            for (FundPoolDetail fundPoolDetail : fundPoolDetailList) {
                BigDecimal minDiscountAccount = fundPoolDetail.getAmount().min(discountAccount);

                fundPoolDetailService.addFundPoolDetail(stack.getCustomerId(), fundAccountId
                        , fundPoolDetail.getFundPoolId(), stack.getId(), minDiscountAccount, FundDetailTypeEnum.DISCOUNT.getCode());

                discountAccount = discountAccount.subtract(minDiscountAccount);
                if (discountAccount.compareTo(BigDecimal.ZERO) == 0) {
                    break;
                }

            }


        }

        throw new ScmException(StrUtil.format("红单暂时不可结算"));

    }


    public IPage<SettleInfo> translate(IPage<SettleInfo> pageList) {
        Map<String, String> statementStateEnumMap = new HashMap<>();
        StatementStateEnum[] StatementStateEnums = StatementStateEnum.values();
        for (StatementStateEnum statementStateEnum : StatementStateEnums) {
            statementStateEnumMap.put(statementStateEnum.getCode(), statementStateEnum.getDesc());
        }

        List<String> createByList = newArrayList();
        for (SettleInfo settleInfo : pageList.getRecords()) {
            createByList.add(settleInfo.getCreateBy());
        }

        Map<String, String> createByMap = stackService.listCreateBy(createByList);
        for (SettleInfo settleInfo : pageList.getRecords()) {
            settleInfo.setCreateBy(createByMap.get(settleInfo.getCreateBy()));
            settleInfo.setStatementState(statementStateEnumMap.get(settleInfo.getStatementState()));
        }

        return pageList;
    }


    /**
     * 编辑结算单页面备注
     *
     * @param settleInfo
     */
    @Transactional(rollbackFor = Exception.class)
    public void editRemark(SettleInfo settleInfo) {
        QueryWrapper<SettleInfo> settleInfoQueryWrapper = new QueryWrapper<>();
        settleInfoQueryWrapper.lambda().eq(SettleInfo::getId, settleInfo.getId());
        SettleInfo settleInfo1 = settleInfoMapper.selectOne(settleInfoQueryWrapper);
        settleInfo1.setRemark(settleInfo.getRemark());
        settleInfoMapper.updateById(settleInfo1);
    }

    public List<IdAndAccountVO> updateStackSettleAccount() {
        return baseMapper.updateStackSettleAccount();
    }

    public List<IdAndAccountVO> updateSettleMainSettleAccount() {
        return baseMapper.updateSettleMainSettleAccount();
    }

    public List<IdAndAccountVO> errorFundDetSettleAccount() {
        return baseMapper.errorFundDetSettleAccount();
    }

    //判断是否配置产品大类单据编码
    private boolean queryIfProdClassSerialNo(String tenantId) {
        //获取CompanyTenantDet的parentId
        QueryWrapper<CompanyTenantDet> companyTenantDetQueryWrapper = new QueryWrapper<>();
        companyTenantDetQueryWrapper.lambda().eq(CompanyTenantDet::getTenantCode, tenantId);
        CompanyTenantDet companyTenantDet = companyTenantDetMapper.selectOne(companyTenantDetQueryWrapper);
        if (ObjectUtil.isNotNull(companyTenantDet)) {
            //获取CompanyTenant的基础单价配置项字段
            QueryWrapper<CompanyTenant> companyTenantQueryWrapper = new QueryWrapper<>();
            companyTenantQueryWrapper.lambda().eq(CompanyTenant::getId, companyTenantDet.getParentId());
            CompanyTenant companyTenant = companyTenantMapper.selectOne(companyTenantQueryWrapper);
            return companyTenant.getProdClassSerialNo().equals(YesNoEnum.YES.getCode());
        }
        return false;
    }

    public BigDecimal selectSettleAccount(String customerId) {
        return baseMapper.settleAccount(customerId);
    }
}
