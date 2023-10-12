package com.dongxin.scm.bd.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.bd.entity.Unsettled;
import com.dongxin.scm.bd.mapper.UnsettledMapper;
import com.dongxin.scm.fm.entity.FinanceMatCode;
import com.dongxin.scm.fm.enums.SettleStatusEnum;
import com.dongxin.scm.fm.service.FinanceMatCodeService;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.entity.Stack;
import com.dongxin.scm.sm.entity.StackDet;
import com.dongxin.scm.sm.mapper.InventoryMapper;
import com.dongxin.scm.sm.service.InventoryService;
import com.dongxin.scm.sm.service.StackDetService;
import com.dongxin.scm.sm.service.StackService;
import com.dongxin.scm.utils.BigDecimalUtils;
import com.dongxin.scm.utils.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description: 未结算量
 * @Author: jeecg-boot
 * @Date: 2021-11-15
 * @Version: V1.0
 */
@Service
public class UnsettledService extends BaseService<UnsettledMapper, Unsettled> {

    @Autowired
    private StackService stackService;
    @Autowired
    private StackDetService stackDetService;
    @Autowired
    private FinanceMatCodeService financeMatCodeService;
    @Resource
    private InventoryMapper inventoryMapper;

    /**
     * 未结算提单
     */
    @Transactional(rollbackFor = Exception.class)
    public void unsettledSalesOrder() {

        //获取未结算单据（明细）
        QueryWrapper<Stack> stackQueryWrapper = new QueryWrapper<>();
        stackQueryWrapper.lambda().eq(Stack::getSettled, SettleStatusEnum.UNSETTLE.getCode())
                .eq(Stack::getTenantId, 2)
                .le(Stack::getOrderTime, DateUtils.clearHourMinuteSecond(new Date()));

        List<Stack> stackList = stackService.list(stackQueryWrapper);

        for (Stack stack : stackList) {

            Unsettled unsettled = new Unsettled();

            unsettled.setCustomerId(stack.getCustomerId())
                    .setShipDate(stack.getShipDate())
                    .setCarNo(stack.getCarNo())
                    .setTenantId(stack.getTenantId())
                    .setStackId(stack.getId());

            QueryWrapper<StackDet> stackDetQueryWrapper = new QueryWrapper<>();
            stackDetQueryWrapper.lambda().eq(StackDet::getStackId, stack.getId());

            List<StackDet> stackDetList = stackDetService.list(stackDetQueryWrapper);
            for (StackDet stackDet : stackDetList) {
                Unsettled addUnsettled = new Unsettled();
                BeanUtils.copyProperties(unsettled, addUnsettled);

                addUnsettled.setId(null)
                        .setCreateTime(null)
                        .setCreateBy(null);

                QueryWrapper<Inventory> inventoryQueryWrapper = new QueryWrapper<>();
                inventoryQueryWrapper.lambda().eq(Inventory::getId, stackDet.getInventoryId());
                Inventory inventory = inventoryMapper.getInventoryDataDelFlagById(stackDet.getInventoryId());

                if (ObjectUtil.isNotEmpty(inventory)) {
                    addUnsettled.setProdCnameOther(stackDet.getProdCnameOther())
                            .setMatCode(inventory.getMatCode());
                    if (StrUtil.isNotEmpty(inventory.getMatCode())) {

                        QueryWrapper<FinanceMatCode> financeMatCodeQueryWrapper = new QueryWrapper<>();
                        financeMatCodeQueryWrapper.lambda().eq(FinanceMatCode::getMatCode, inventory.getMatCode())
                                .eq(FinanceMatCode::getTenantId, inventory.getTenantId());

                        FinanceMatCode financeMatCode = financeMatCodeService.getOne(financeMatCodeQueryWrapper);

                        addUnsettled.setName(financeMatCode.getName())
                                .setProductionLine(financeMatCode.getProductionLine())
                                .setCategoryIdentification(financeMatCode.getCategoryIdentification());
                    }
                }

                addUnsettled.setSgSign(stackDet.getSgSign())
                        .setStockName(stackDet.getStockName())
                        .setMatThick(stackDet.getMatThick())
                        .setMatLen(stackDet.getMatLen())
                        .setMatWidth(stackDet.getMatWidth())
                        .setPrice(stackDet.getPrice())
                        .setWeight(stackDet.getWeight())
                        .setProdClassCode(stackDet.getProdClassCode())
                        .setStackDetId(stackDet.getId());

                //含税金额
                BigDecimal taxAmount = BigDecimalUtils.multiply(addUnsettled.getPrice(), addUnsettled.getWeight());
                addUnsettled.setTaxAmount(taxAmount);

                //不含税金额
                BigDecimal noTaxAmount = taxAmount.divide(BigDecimal.valueOf(1.13), 2, BigDecimal.ROUND_HALF_UP);
                addUnsettled.setNoTaxAmount(noTaxAmount);

                //税额
                BigDecimal tax = BigDecimalUtils.multiply(noTaxAmount, 0.13);
                addUnsettled.setTax(tax);

                saveOrUpdate(addUnsettled);
            }
        }
    }
}
