package com.dongxin.scm.sm.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.bd.entity.CompanyTenant;
import com.dongxin.scm.bd.entity.CompanyTenantDet;
import com.dongxin.scm.bd.mapper.CompanyTenantDetMapper;
import com.dongxin.scm.bd.service.CompanyTenantDetService;
import com.dongxin.scm.bd.service.CompanyTenantService;
import com.dongxin.scm.enums.ProdClassCodeEnum;
import com.dongxin.scm.enums.YesNoEnum;
import com.dongxin.scm.exception.ScmException;
import com.dongxin.scm.sm.entity.AverageCost;
import com.dongxin.scm.sm.mapper.AverageCostMapper;
import com.dongxin.scm.utils.BigDecimalUtils;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Description: 每日平均成本
 * @Author: jeecg-boot
 * @Date: 2021-08-02
 * @Version: V1.0
 */
@Service
public class AverageCostService extends BaseService<AverageCostMapper, AverageCost> {
    @Autowired
    private MatService matService;
    @Autowired
    private CompanyTenantService companyTenantService;
    @Autowired
    private CompanyTenantDetService companyTenantDetService;
    @Resource
    private CompanyTenantDetMapper companyTenantDetMapper;

    /**
     * CH每日成本价
     */
    @Transactional(rollbackFor = Exception.class)
    public void averagePriceDay() {
        AverageCost averageCost = new AverageCost();

        QueryWrapper<CompanyTenant> companyTenantQueryWrapper = new QueryWrapper<>();
        companyTenantQueryWrapper.lambda().eq(CompanyTenant::getAverageCostCheck, YesNoEnum.YES.getCode());

        List<CompanyTenant> companyTenantList = companyTenantService.list(companyTenantQueryWrapper);
        for (CompanyTenant companyTenant : companyTenantList) {
            QueryWrapper<CompanyTenantDet> companyTenantDetQueryWrapper = new QueryWrapper<>();
            companyTenantDetQueryWrapper.lambda().eq(CompanyTenantDet::getParentId, companyTenant.getId());

            CompanyTenantDet companyTenantDet = companyTenantDetService.getOne(companyTenantDetQueryWrapper);
            Integer tenantId = companyTenantDet.getTenantCode();
            for (ProdClassCodeEnum prodClassCodeEnum : ProdClassCodeEnum.values()) {
                BigDecimal averagePriceInDb = matService.selectAveragePrice(tenantId, prodClassCodeEnum.getValue(), new Date());
                if (ObjectUtil.isNotNull(averagePriceInDb)) {
                    averageCost.setId(null);
                    averageCost.setProdClassCord(prodClassCodeEnum.getValue());
                    averageCost.setAveragePrice(averagePriceInDb);
                    averageCost.setTenantId(tenantId);
                    save(averageCost);
                }
            }
        }
    }

    //开单材料单价监控:材料单价低于百分之二十则抛异常
    public void averageCostPrice(Date date, String prodClassCord, BigDecimal price, int count) {
        /**
        QueryWrapper<AverageCost> averagePriceQueryWrapper = new QueryWrapper<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        Date sevenDaysAgoDate = calendar.getTime();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(sevenDaysAgoDate);


        averagePriceQueryWrapper.lambda().eq(AverageCost::getProdClassCord, prodClassCord)
                .like(AverageCost::getCreateTime, dateString);

        List<AverageCost> averagePriceList = list(averagePriceQueryWrapper);

        for (AverageCost averagePrice : averagePriceList) {
            if (ObjectUtil.isNotNull(averagePrice.getAveragePrice())) {
                int result = price.compareTo(BigDecimalUtils.multiply(averagePrice.getAveragePrice(), 0.8));
//                result = -1;//表示a小于b;  result = 0;//表示a等于b;  result = 1;//表示a大于b;
                if (result == -1) {
                    throw new ScmException("第" + count + "条材料单价低于成本价80%");
                }
            }
        }
         */
    }
}
