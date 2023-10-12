package com.dongxin.scm.om.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.cm.entity.CustomerConfiguration;
import com.dongxin.scm.cm.service.CustomerConfigurationService;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.om.entity.Contract;
import com.dongxin.scm.om.entity.ContractDet;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.entity.SalesOrderDet;
import com.dongxin.scm.om.enums.ContractFlagEnum;
import com.dongxin.scm.om.mapper.ContractDetMapper;
import com.dongxin.scm.sm.entity.StackDet;
import com.dongxin.scm.sm.mapper.StackMapper;
import org.jeecg.common.system.base.service.BaseService;
import org.jeecg.config.mybatis.TenantContext;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 合同明细表
 * @Author: jeecg-boot
 * @Date: 2020-12-14
 * @Version: V1.0
 */
@Service
public class ContractDetService extends BaseService<ContractDetMapper, ContractDet> {

    @Resource
    private ContractDetMapper contractDetMapper;
    @Autowired
    private ContractService contractService;
    @Autowired
    private CustomerConfigurationService customerConfigurationService;

    @Transactional(rollbackFor = Exception.class)
    public void contractControl(SalesOrderDet salesOrderDet, SalesOrder salesOrder, int count) {
        //合同控量租户ID校验
        QueryWrapper<CustomerConfiguration> customerConfigWrapper = new QueryWrapper<>();
        customerConfigWrapper.lambda().eq(CustomerConfiguration::getTenantId, TenantContext.getTenant());
        List<CustomerConfiguration> customerConfigList = customerConfigurationService.list(customerConfigWrapper);
        if (customerConfigList.size() > 1) {
            log.error(StrUtil.format("当前租户为：{}，合同控量中存在{}条合同控量配置记录！", TenantContext.getTenant(), customerConfigList.size()));
            throw new ScmException(StrUtil.format("当前租户为：{}，合同控量中存在{}条合同控量配置记录！", TenantContext.getTenant(), customerConfigList.size()));
        }

        //校验是否合同控量
        if (CollectionUtil.isNotEmpty(customerConfigList) &&
                (customerConfigList.get(0).getFlag().equals(YesNoEnum.YES.getCode()))) {
            if (StrUtil.isBlank(salesOrderDet.getContractListNo())) {
                throw new ScmException("请选择合同明细号！");
            }

            //合同明细、提单明细物料信息校验
            QueryWrapper<ContractDet> contractDetQueryWrapper = new QueryWrapper<>();
            contractDetQueryWrapper.lambda()
                    .eq(ContractDet::getContractListNo, salesOrderDet.getContractListNo())
                    .eq(ContractDet::getProdCname, salesOrderDet.getProdCname())
                    .eq(ContractDet::getProdCnameOther, salesOrderDet.getProdCnameOther())
                    .eq(ContractDet::getSgSign, salesOrderDet.getSgSign())
                    .eq(ContractDet::getProdClassCode, salesOrderDet.getProdClassCode())
                    .eq(ContractDet::getOrderLen, salesOrderDet.getOrderLen())
                    .eq(ContractDet::getOrderWidth, salesOrderDet.getOrderWidth())
                    .eq(ContractDet::getOrderThick, salesOrderDet.getOrderThick());
            ContractDet contractDet = getOne(contractDetQueryWrapper);
            if (ObjectUtil.isNull(contractDet)) {
                log.error(StrUtil.format("第{}条明细：合同明细:{}，提单明细{}, 物料信息不一致！", count, contractDet, salesOrderDet));
                throw new ScmException(StrUtil.format("第{}条明细：合同明细、提单明细，物料信息不一致！", count));
            }
            //校验合同客户、提单主表客户是否一致
            Contract contract = contractService.getById(contractDet.getParentId());
            if (ObjectUtil.notEqual(salesOrder.getCustomerId(), contract.getCustomerId())) {
                throw new ScmException(StrUtil.format("提单、合同客户不一致！请重新选择客户或第 {} 条明细的合同明细号！", count));
            }
            Double currentWeight = contractDet.getWeight() - contractDet.getPreWeight() - contractDet.getDelivyWeight();
            Double currentQty = contractDet.getQty() - contractDet.getPreQty() - contractDet.getDelivyQty();
            if (salesOrderDet.getQty() > currentQty) {
                throw new ScmException(StrUtil.format("第{}条明细：可开单数量：{}, 现开单数量：{}", count, currentQty, salesOrderDet.getQty()));
            }
            if (salesOrderDet.getWeight() > currentWeight) {
                throw new ScmException(StrUtil.format("第{}条明细：可开单重量：{}, 现开单重量：{}", count, currentWeight, salesOrderDet.getWeight()));
            }
            contractDet.setPreWeight(contractDet.getPreWeight() + salesOrderDet.getWeight());
            contractDet.setPreQty(contractDet.getPreQty() + salesOrderDet.getQty());
            updateById(contractDet);

        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void subPreAndDeliverAmount(List<SalesOrderDet> salesOrderDetList) {
        List<ContractDet> contractDetList = new ArrayList<>();
        for (SalesOrderDet salesOrderDet : salesOrderDetList) {
            QueryWrapper<ContractDet> contractDetQueryWrapper = new QueryWrapper<>();
            contractDetQueryWrapper.lambda()
                    .eq(ContractDet::getContractListNo, salesOrderDet.getContractListNo());
            ContractDet contractDet = getOne(contractDetQueryWrapper);
            contractDet.setPreQty(contractDet.getPreQty() - salesOrderDet.getQty());
            contractDet.setPreWeight(contractDet.getPreWeight() - salesOrderDet.getWeight());
            contractDetList.add(contractDet);
        }
        updateBatchById(contractDetList);
    }

    public List<ContractDet> selectByMainId(String mainId) {
        return contractDetMapper.selectByMainId(mainId);
    }

    //删除销售单减扣已交件数（重量），增加预交件数（重量）
    @Transactional(rollbackFor = Exception.class)
    public void refundPreAndDeliver(List<StackDet> stackDetList) {
        List<ContractDet> contractDetList = new ArrayList<>();

        for (StackDet stackDet : stackDetList) {
            QueryWrapper<ContractDet> contractDetQueryWrapper = new QueryWrapper<>();
            contractDetQueryWrapper.lambda()
                    .eq(ContractDet::getContractListNo, stackDet.getContractListNo())
                    .eq(ContractDet::getProdCname, stackDet.getProdCname())
                    .eq(ContractDet::getProdCnameOther, stackDet.getProdCnameOther())
                    .eq(ContractDet::getSgSign, stackDet.getSgSign())
                    .eq(ContractDet::getProdClassCode, stackDet.getProdClassCode());
            ContractDet contractDet = getOne(contractDetQueryWrapper);
            contractDet.setPreWeight(contractDet.getPreWeight() + stackDet.getWeight());
            contractDet.setPreQty(contractDet.getPreQty() + stackDet.getQty());
            contractDet.setDelivyWeight(contractDet.getDelivyWeight() - stackDet.getWeight());
            contractDet.setDelivyQty(contractDet.getDelivyQty() - stackDet.getQty());

            contractDetList.add(contractDet);

            BigDecimal contractDetDeliverWeight = BigDecimal.valueOf(contractDet.getDelivyWeight());
            BigDecimal contractDetDeliverQty = BigDecimal.valueOf(contractDet.getDelivyQty());
            BigDecimal contractDetQty = BigDecimal.valueOf(contractDet.getQty());
            BigDecimal contractDetWeight = BigDecimal.valueOf(contractDet.getWeight());

            //获取合同主表记录
            Contract contract = contractService.getById(contractDet.getParentId());

            if (NumberUtil.equals(contractDetDeliverWeight, contractDetWeight)
                    || NumberUtil.equals(contractDetDeliverQty, contractDetQty)) {

                //查询全部明细
                QueryWrapper<ContractDet> queryWrapper = new QueryWrapper();
                queryWrapper.lambda().eq(ContractDet::getParentId, contractDet.getParentId());
                List<ContractDet> contractDets = list(queryWrapper);

                //对所有的提单明细已交件数、已交重量求和
                Double deliverWeight = 0d, totalQty = 0d, deliverQty = 0d;
                for (ContractDet contractDet1 : contractDets) {
                    deliverWeight += contractDet1.getDelivyWeight();
                    totalQty += contractDet1.getQty();
                    deliverQty += contractDet1.getDelivyQty();
                }

                BigDecimal totalQtyBigDecimal = BigDecimal.valueOf(totalQty);
                BigDecimal diliverQtyBigDecimal = BigDecimal.valueOf(deliverQty);
                BigDecimal deliverWeightBigDecimal = BigDecimal.valueOf(deliverWeight);
                BigDecimal orderWeightBigDecimal = BigDecimal.valueOf(contract.getOrderWt());

                if (NumberUtil.equals(orderWeightBigDecimal, deliverWeightBigDecimal)
                        || NumberUtil.equals(totalQtyBigDecimal, diliverQtyBigDecimal)
                ) {
                    contract.setContractFlag(ContractFlagEnum.FINISH.getCode());
                }
            } else {
                contract.setContractFlag(ContractFlagEnum.NORMAL.getCode());
            }
            contractService.updateById(contract);
            updateBatchById(contractDetList);
        }
    }

    //删除装车单减扣已交金额（重量）
    @Transactional(rollbackFor = Exception.class)
    public void refundPriceAndWeight(List<StackDet> stackDetList) {
        List<ContractDet> contractDetList = new ArrayList<>();
        for (StackDet stackDet : stackDetList) {
            QueryWrapper<ContractDet> contractDetQueryWrapper = new QueryWrapper<>();
            contractDetQueryWrapper.lambda()
                    .eq(ContractDet::getContractListNo, stackDet.getContractListNo())
                    .eq(ContractDet::getProdCname, stackDet.getProdCname())
                    .eq(ContractDet::getProdCnameOther, stackDet.getProdCnameOther())
                    .eq(ContractDet::getSgSign, stackDet.getSgSign())
                    .eq(ContractDet::getProdClassCode, stackDet.getProdClassCode());
            ContractDet contractDet = getOne(contractDetQueryWrapper);
            //待扣减重量（金额）
            Double pendingRefundWeight = stackDet.getWeight();
            BigDecimal pendingRefundPrice = stackDet.getTotalAmount();
            //合同明细已交重量（金额）
            Double delivyWeight = contractDet.getDelivyWeight();
            BigDecimal delivyFund = contractDet.getDelivyFund();
            //扣减后已交重量（金额）
            Double newDelivyWeight = delivyWeight - pendingRefundWeight;
            BigDecimal newDelivyPrice = delivyFund.subtract(pendingRefundPrice);
            //更新合同明细重量（金额）
            contractDet.setDelivyWeight(newDelivyWeight);
            contractDet.setDelivyFund(newDelivyPrice);
            contractDetList.add(contractDet);
        }
        updateBatchById(contractDetList);
    }
}
