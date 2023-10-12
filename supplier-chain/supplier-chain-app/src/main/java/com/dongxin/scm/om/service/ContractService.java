package com.dongxin.scm.om.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.cm.entity.CustomerConfiguration;
import com.dongxin.scm.cm.service.CustomerConfigurationService;
import com.dongxin.scm.common.service.DataLogService;
import com.dongxin.scm.enums.AddEditEnum;
import com.dongxin.scm.enums.SerialNoEnum;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.om.entity.*;
import com.dongxin.scm.om.enums.ContractFlagEnum;
import com.dongxin.scm.om.mapper.ContractDetMapper;
import com.dongxin.scm.om.mapper.ContractMapper;
import com.dongxin.scm.om.mapper.ContractProvisionMapper;
import com.dongxin.scm.sm.entity.StackDet;
import com.dongxin.scm.sm.service.StackDetService;
import com.dongxin.scm.utils.BigDecimalUtils;
import com.dongxin.scm.utils.SerialNoUtil;
import org.jeecg.common.system.base.service.BaseService;
import org.jeecg.config.mybatis.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 合同主档表
 * @Author: jeecg-boot
 * @Date: 2020-12-14
 * @Version: V1.0
 */
@Service
public class ContractService extends BaseService<ContractMapper, Contract> {

    @Resource
    private ContractMapper contractMapper;
    @Resource
    private ContractDetMapper contractDetMapper;
    @Resource
    private ContractProvisionMapper contractProvisionMapper;
    @Autowired
    private DataLogService dataLogService;
    @Autowired
    private ContractDetService contractDetService;
    @Autowired
    private CustomerConfigurationService customerConfigurationService;
    @Autowired
    private SalesOrderDetService salesOrderDetService;
    @Autowired
    private StackDetService stackDetService;

    @Transactional(rollbackFor = Exception.class)
    public void saveMain(Contract contract, List<ContractDet> contractDetList, List<ContractProvision> contractProvisionList) {

        //合同明细非空校验
        if (CollectionUtil.isEmpty(contractDetList)) {
            throw new ScmException("合同明细不能为空！");
        }
        //设置合同编号
        contract.setContractNo(SerialNoUtil.getSerialNo(SerialNoEnum.CONTRACT_NO));
        //计算合同总价、总重量
        BigDecimal totalPrice = BigDecimal.ZERO;
        double totalWeight = 0.00;
        if (contractDetList != null && contractDetList.size() > 0) {
            for (ContractDet entity : contractDetList) {
                totalPrice = totalPrice.add(entity.getTotalPrice());
                totalWeight = totalWeight + entity.getWeight();
            }
        }
        //根据合同比例得出最终总价
        if (contract.getRate() != null && contract.getRate() != 0) {
            BigDecimal rate = BigDecimal.valueOf(contract.getRate() * 0.01);
            totalPrice = BigDecimalUtils.multiply(totalPrice, rate);
        }
        contract.setTotalPrice(totalPrice);
        contract.setOrderWt(totalWeight);
        String contracListNo = contract.getContractNo();
        contractMapper.insert(contract);
        //开启快照
        String tableName = Contract.class.getAnnotation(TableName.class).value();
        String dataContent = JSONObject.toJSONString(contract);
        dataLogService.dataLog(tableName, contract.getId(), dataContent);

        if (contractDetList != null && contractDetList.size() > 0) {
            Integer num = 0;
            for (ContractDet entity : contractDetList) {
                if (entity.getQty() <= 0 || entity.getWeight() <= 0 || entity.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new ScmException("输入的数量、物料重量、单价不能小于或等于0");
                }
                if (entity.getOrderLen() < 0 || entity.getOrderWidth() < 0 || entity.getOrderThick() < 0) {
                    throw new ScmException("输入的长、宽、厚不能小于0");
                }
                //外键设置
                entity.setParentId(contract.getId());

                //开启快照
                String detTableName = ContractDet.class.getAnnotation(TableName.class).value();
                String detDataContent = JSONObject.toJSONString(entity);
                dataLogService.dataLog(detTableName, entity.getId(), detDataContent);

                //合同明细号设置
                DecimalFormat decimalFormat = new DecimalFormat("000");
                String numNo = decimalFormat.format(num++);
                String preNo = contracListNo.substring(0, 2);
                String endNo = contracListNo.substring(2);
                entity.setContractListNo(preNo + "D" + endNo + numNo);
                entity.setContractNo(contracListNo);

                //初始化预交件数、预交重量、已交件数、已交重量
                entity.setPreWeight(0d);
                entity.setPreQty(0d);
                entity.setDelivyWeight(0d);
                entity.setDelivyQty(0d);
                contractDetMapper.insert(entity);
            }
        }
        if (contractProvisionList != null && contractProvisionList.size() > 0) {
            for (ContractProvision entity : contractProvisionList) {
                //外键设置
                entity.setParentId(contract.getId());

                //开启快照
                String provisionTableName = ContractProvision.class.getAnnotation(TableName.class).value();
                String provisionDataContent = JSONObject.toJSONString(entity);
                dataLogService.dataLog(provisionTableName, entity.getId(), provisionDataContent);

                contractProvisionMapper.insert(entity);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMain(Contract contract, List<ContractDet> contractDetList, List<ContractProvision> contractProvisionList) {
        //计算合同总价、总重量
        BigDecimal totalPrice = BigDecimal.ZERO;
        double totalWeight = 0.00;
        if (contractDetList != null && contractDetList.size() > 0) {
            for (ContractDet entity : contractDetList) {
                totalPrice = totalPrice.add(entity.getTotalPrice());
                totalWeight = totalWeight + entity.getWeight();
            }
        }
        contract.setTotalPrice(totalPrice);
        contract.setOrderWt(totalWeight);

        //开启快照
        String tableName = Contract.class.getAnnotation(TableName.class).value();
        String dataContent = JSONObject.toJSONString(contract);
        dataLogService.dataLog(tableName, contract.getId(), dataContent);

        contractMapper.updateById(contract);

        //1.先删除子表数据
        contractDetMapper.deleteByMainId(contract.getId());
        contractProvisionMapper.deleteByMainId(contract.getId());

        //2.子表数据重新插入
        if (contractDetList != null && contractDetList.size() > 0) {
            for (ContractDet entity : contractDetList) {
                //外键设置
                entity.setParentId(contract.getId());

                //开启快照
                String detTableName = ContractDet.class.getAnnotation(TableName.class).value();
                String detDataContent = JSONObject.toJSONString(entity);
                dataLogService.dataLog(detTableName, entity.getId(), detDataContent);

                contractDetMapper.insert(entity);
            }
        }
        if (contractProvisionList != null && contractProvisionList.size() > 0) {
            for (ContractProvision entity : contractProvisionList) {
                //外键设置
                entity.setParentId(contract.getId());

                //开启快照
                String provisionTableName = ContractProvision.class.getAnnotation(TableName.class).value();
                String provisionDataContent = JSONObject.toJSONString(entity);
                dataLogService.dataLog(provisionTableName, entity.getId(), provisionDataContent);

                contractProvisionMapper.insert(entity);
            }
        }
    }


    /*
     * 当合同上的总重量或者是总件数，被提单挂满的时候，
     * 将合同的合同状态更新为完结状态
     * */
    @Transactional(rollbackFor = Exception.class)
    public void contractControl(SalesOrderDet salesOrderDet, String methodFlag, int count) {

        //合同控量租户ID校验
        QueryWrapper<CustomerConfiguration> customerConfigWrapper = new QueryWrapper<>();
        customerConfigWrapper.lambda().eq(CustomerConfiguration::getTenantId, TenantContext.getTenant());
        List<CustomerConfiguration> customerConfigList = customerConfigurationService.list(customerConfigWrapper);
        if (customerConfigList.size() > 1) {
            log.error(StrUtil.format("当前租户为：{}，合同控量中存在{}条合同控量配置记录！",
                    TenantContext.getTenant(), customerConfigList.size()));
            throw new ScmException(StrUtil.format("当前租户为：{}，合同控量中存在{}条合同控量配置记录！",
                    TenantContext.getTenant(), customerConfigList.size()));
        }

        //校验是否合同控量
        if (CollectionUtil.isNotEmpty(customerConfigList)
                && (customerConfigList.get(0).getFlag().equals(YesNoEnum.YES.getCode()))) {

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
                    .eq(ContractDet::getProdClassCode, salesOrderDet.getProdClassCode());
            ContractDet contractDet = contractDetService.getOne(contractDetQueryWrapper);

            //判断是否是销售单新增
            if (AddEditEnum.ADD.getCode().equals(methodFlag)) {

                //查询销售单生成前的提单明细
                SalesOrderDet querySalesOrderDet = salesOrderDetService.getById(salesOrderDet);
                //可开单量校验
                Double currentQty = contractDet.getQty() - contractDet.getPreQty()
                        - contractDet.getDelivyQty() + querySalesOrderDet.getQty();
                Double currentWeight = contractDet.getWeight() - contractDet.getPreWeight()
                        - contractDet.getDelivyWeight() + querySalesOrderDet.getWeight();

                if (salesOrderDet.getQty() > currentQty) {
                    throw new ScmException(StrUtil.format("第{}条明细：可开单数量：{}, 现开单数量：{}",
                            count, currentQty, salesOrderDet.getQty()));
                }
                if (salesOrderDet.getWeight() > currentWeight) {
                    throw new ScmException(StrUtil.format("第{}条明细：可开单重量：{}, 现开单重量：{}",
                            count, currentWeight, salesOrderDet.getWeight()));
                }

                Double preQty = contractDet.getPreQty();
                Double preWeight = contractDet.getPreWeight();
                //减扣预交件数
                if (NumberUtil.equals(BigDecimal.valueOf(salesOrderDet.getQty()), BigDecimal.valueOf(querySalesOrderDet.getQty()))) {
                    preQty -= salesOrderDet.getQty();
                } else {
                    preQty -= querySalesOrderDet.getQty();
                }

                //减扣预交重量
                if (NumberUtil.equals(BigDecimal.valueOf(salesOrderDet.getWeight()), BigDecimal.valueOf(querySalesOrderDet.getWeight()))) {
                    preWeight -= salesOrderDet.getWeight();
                } else {
                    preWeight -= querySalesOrderDet.getWeight();
                }
                contractDet.setPreWeight(preWeight);
                contractDet.setPreQty(preQty);
                //设置已交重量、已交件数
                contractDet.setDelivyWeight(contractDet.getDelivyWeight() + salesOrderDet.getWeight());
                contractDet.setDelivyQty(contractDet.getDelivyQty() + salesOrderDet.getQty());
            }
            //销售单编辑时执行
            if (AddEditEnum.EDIT.getCode().equals(methodFlag)) {

                StackDet previousDet = stackDetService.getById(salesOrderDet.getId());
                Double currentWeight = contractDet.getWeight() - contractDet.getPreWeight()
                        - contractDet.getDelivyWeight() + previousDet.getWeight();
                Double currentQty = contractDet.getQty() - contractDet.getPreQty()
                        - contractDet.getDelivyQty() + previousDet.getQty();
                //可开单量校验
                if (salesOrderDet.getQty() > currentQty) {
                    throw new ScmException(StrUtil.format("第{}条明细：可开单数量：{}, 现开单数量：{}",
                            count, currentQty, salesOrderDet.getQty()));
                }
                if (salesOrderDet.getWeight() > currentWeight) {
                    throw new ScmException(StrUtil.format("第{}条明细：可开单重量：{}, 现开单重量：{}",
                            count, currentWeight, salesOrderDet.getWeight()));
                }
                //设置已交重量、已交件数
                contractDet.setDelivyWeight(contractDet.getDelivyWeight() - previousDet.getWeight() + salesOrderDet.getWeight());
                contractDet.setDelivyQty(contractDet.getDelivyQty() - previousDet.getQty() + salesOrderDet.getQty());
            }

            //记录每条合同明细，比较预交件数（重量）、已交件数（重量）是否相等
            BigDecimal contractDetDeliverWeight = BigDecimal.valueOf(contractDet.getDelivyWeight());
            BigDecimal contractDetDeliverQty = BigDecimal.valueOf(contractDet.getDelivyQty());
            BigDecimal contractDetQty = BigDecimal.valueOf(contractDet.getQty());
            BigDecimal contractDetWeight = BigDecimal.valueOf(contractDet.getWeight());

            //更新合同明细预交量、已交量
            contractDetService.updateById(contractDet);
            //获取合同主表记录
            Contract contract = getById(contractDet.getParentId());

            //已交重量（件数）等于重量（件数）时更新合同状态
            if (NumberUtil.equals(contractDetDeliverWeight, contractDetWeight)
                    || NumberUtil.equals(contractDetDeliverQty, contractDetQty)) {

                //查询全部明细
                QueryWrapper<ContractDet> queryWrapper = new QueryWrapper();
                queryWrapper.lambda().eq(ContractDet::getParentId, contractDet.getParentId());
                List<ContractDet> contractDets = contractDetService.list(queryWrapper);

                //对所有的提单明细已交件数、已交重量求和
                Double deliverWeight = 0d, totalQty = 0d, deliverQty = 0d;
                for (ContractDet contractDet1 : contractDets) {
                    deliverWeight += contractDet1.getDelivyWeight();
                    totalQty += contractDet1.getQty();
                    deliverQty += contractDet1.getDelivyQty();
                }
                //比较合同总件数（重量）是否等于合同明细总件数（重量）
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
            //更新合同主表状态
            updateById(contract);

        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delMain(String id) {
        contractDetMapper.deleteByMainId(id);
        contractProvisionMapper.deleteByMainId(id);
        contractMapper.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delBatchMain(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            contractDetMapper.deleteByMainId(id.toString());
            contractProvisionMapper.deleteByMainId(id.toString());
            contractMapper.deleteById(id);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void stackContractControl(SalesOrderDet salesOrderDet, int count) {
        //合同控量租户ID校验
        QueryWrapper<CustomerConfiguration> customerConfigWrapper = new QueryWrapper<>();
        customerConfigWrapper.lambda().eq(CustomerConfiguration::getTenantId, TenantContext.getTenant());
        List<CustomerConfiguration> customerConfigList = customerConfigurationService.list(customerConfigWrapper);
        if (customerConfigList.size() > 1) {
            log.error(StrUtil.format("当前租户为：{}，合同控量中存在{}条合同控量配置记录！",
                    TenantContext.getTenant(), customerConfigList.size()));
            throw new ScmException(StrUtil.format("当前租户为：{}，合同控量中存在{}条合同控量配置记录！",
                    TenantContext.getTenant(), customerConfigList.size()));
        }
        //校验是否合同控量
        if (CollectionUtil.isNotEmpty(customerConfigList)
                && (customerConfigList.get(0).getStackContractConfiguration().equals(YesNoEnum.YES.getCode()))) {

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
                    .eq(ContractDet::getProdClassCode, salesOrderDet.getProdClassCode());
            ContractDet contractDet = contractDetService.getOne(contractDetQueryWrapper);
            if (ObjectUtil.isNull(contractDet)) {
                throw new ScmException(StrUtil.format("{}装车单明细无对应合同！", count));
            }
            //明细合同
            if (contractDet.getContractType().equals("0")) {
                if ((ObjectUtil.equal(contractDet.getOrderThick(), salesOrderDet.getOrderThick())
                        && (ObjectUtil.equal(contractDet.getOrderWidth(), salesOrderDet.getOrderWidth())))) {
                    ContractDet updatedContractDet = stackContractControlUpdate(contractDet, salesOrderDet,count);
                    //更新合同明细预交量、已交量
                    contractDetService.updateById(updatedContractDet);
                } else {
                    throw new ScmException(StrUtil.format("第{}装车单明细规格与对应合同明细中的规格不一致", count));
                }
                //范围合同
            } else {
                if (salesOrderDet.getMatTheoryWt() < contractDet.getGoodWeightPieceMaximum()
                        && salesOrderDet.getMatTheoryWt() > contractDet.getGoodWeightPieceMinimum()
                        && salesOrderDet.getOrderWidth() < contractDet.getGoodMaximumWidth()
                        && salesOrderDet.getOrderWidth() > contractDet.getGoodMinimumWidth()
                        && salesOrderDet.getOrderThick() < contractDet.getGoodMaximumThickness()
                        && salesOrderDet.getOrderThick() > contractDet.getGoodMinimumThickness()) {
                    ContractDet updatedContractDet = stackContractControlUpdate(contractDet, salesOrderDet,count);
                    //更新合同明细预交量、已交量
                    contractDetService.updateById(updatedContractDet);
                } else {
                    throw new ScmException(StrUtil.format("第{}装车单明细规格不在对应范围合同规格内", count));
                }
            }

        }
    }
    @Transactional(rollbackFor = Exception.class)
    public ContractDet stackContractControlUpdate(ContractDet contractDet, SalesOrderDet salesOrderDet, int count){
        //合同偏移量
        Double contractDeviation = contractDet.getContractDeviation();
        //可开单量重量校验
        Double delivyWeight = contractDet.getDelivyWeight();
        Double availableWeight = contractDet.getWeight() * (1 + contractDeviation) - delivyWeight;
        BigDecimal delivyFund = contractDet.getDelivyFund();
        BigDecimal availableFund = contractDet.getTotalPrice().subtract(delivyFund);
        if (salesOrderDet.getWeight() > availableWeight) {
            throw new ScmException(StrUtil.format("第{}条明细：可开单重量：{}, 现开单重量：{}",
                    count, availableWeight, salesOrderDet.getWeight()));
        }
        if (salesOrderDet.getTotalPrice().compareTo(availableFund) > 0) {
            throw new ScmException(StrUtil.format("第{}条明细：可开单重量：{}, 现开单重量：{}",
                    count, availableFund, salesOrderDet.getTotalPrice()));
        }
        //待交重量和金额
        Double pendingWeight = salesOrderDet.getWeight();
        BigDecimal pendingTotalPrice = salesOrderDet.getTotalPrice();
        //新已交重量
        Double newDelivyWeight = pendingWeight + delivyWeight;
        BigDecimal newDelivyFund = pendingTotalPrice.add(delivyFund);
        //更新合同已交重量、已交金额
        contractDet.setDelivyWeight(newDelivyWeight);
        contractDet.setDelivyFund(newDelivyFund);

        contractDet.setDelivyWeight(contractDet.getDelivyWeight() + salesOrderDet.getWeight());
        contractDet.setDelivyQty(contractDet.getDelivyQty() + salesOrderDet.getQty());
        return contractDet;
    }
}
