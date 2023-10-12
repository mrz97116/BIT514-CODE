package com.dongxin.scm.home.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.common.enums.CommonCheckStatusEnum;
import com.dongxin.scm.fm.entity.FundPool;
import com.dongxin.scm.fm.service.FundPoolService;
import com.dongxin.scm.home.entity.CountBill;
import com.dongxin.scm.home.entity.MonthPayment;
import com.dongxin.scm.home.entity.SalesShow;
import com.dongxin.scm.home.mapper.HomeMapper;
import com.dongxin.scm.sm.service.StackService;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service
public class HomeService {

    @Resource
    HomeMapper homeMapper;

    @Autowired
    StackService stackService;

    @Autowired
    private FundPoolService fundPoolService;

    @Autowired
    private Environment environment;

    public SalesShow salesShow() {

        SalesShow salesshow = new SalesShow();
        //月同比
        salesshow.setSalesMonththan(stackService.salesMonththan());
        //本月总销售额
        salesshow.setSalesThis(stackService.salesShow());
        //日均销售额
        salesshow.setSalesDayAvg(stackService.dayAvg());
        //日同比
        salesshow.setSalesDaythan(stackService.daythan());

        return salesshow;
    }

    public CountBill countBill() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
        Date date = new Date();
        String createTime = simpleDateFormat.format(date);
        Integer delFlag = 0;
        //获取本月提单量
        int monthCount = homeMapper.monthSaleBillNo(createTime, delFlag);
        int day = date.getDate();

        //计算本月日均提单量
        float avgSaleBill;
        if (monthCount == 0) {
            avgSaleBill = 0;
        } else {
            float avgCount = ((float) monthCount / (float) day);
            avgSaleBill = (float) (Math.round(avgCount * 100)) / 100;
        }

        CountBill countBill = new CountBill();
        countBill.setMonthCount(String.valueOf(monthCount));
        countBill.setAvgCount(String.valueOf(avgSaleBill));

        return countBill;
    }

    /*
     * 本月来款总金额
     * */
    public MonthPayment monthPayment() {
//        //获取当前月份
//        Date date = new Date();
//        String strDateFormat = "yyyy-MM";
//        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
//        String currentMonth = sdf.format(date);
        //当月一号
        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(Calendar.DAY_OF_MONTH, -1);
        Date thisMonthNumberOne = calendar1.getTime();
        //查询符合条件的记录
        QueryWrapper<FundPool> fundPoolQueryWrapper = new QueryWrapper<>();
        fundPoolQueryWrapper.lambda().ge(FundPool::getCreateTime, thisMonthNumberOne)
                .eq(FundPool::getStatus, CommonCheckStatusEnum.APPROVE.getCode());
        List<FundPool> fundPoolList = fundPoolService.list(fundPoolQueryWrapper);
        if (CollectionUtils.isEmpty(fundPoolList)) {
            MonthPayment monthPayment = new MonthPayment();
            monthPayment.setMonthFundAccount("本月还未有来款");
            monthPayment.setAvgOfMonthPayment("本月还未有来款");
            return monthPayment;
        }
        //求来款总金额
        BigDecimal monthSum = BigDecimal.ZERO;
        for (FundPool incomingAmount : fundPoolList) {
            monthSum = monthSum.add(incomingAmount.getIncomingAmount());
        }
        //计算日均来款金额
        Calendar ca = Calendar.getInstance();
        int day = ca.get(Calendar.DAY_OF_MONTH);
        BigDecimal toDay = new BigDecimal(0);
        toDay = BigDecimal.valueOf(day);
        BigDecimal avg = monthSum.divide(toDay, 2, BigDecimal.ROUND_UP);
        MonthPayment monthPayment = new MonthPayment();
        monthPayment.setMonthFundAccount(monthSum.toString());
        monthPayment.setAvgOfMonthPayment(avg.toString());
        return monthPayment;
    }

    /**
     * 通过tenantId查找公司名称
     */
    public String queryCompanyName(Integer id) {
        String name = homeMapper.queryCompanyHome(id);
        return name;
    }

    /**
     * 通过配置文件spring.profiles.active区分测试和正式环境
     */
    public Boolean queryWhichEnvironment() {
        String property = environment.getProperty("spring.profiles.active");
        if(property.equals("dev")){
            return true;
        } else{
            return false;
        }
    }
}
