package com.dongxin.scm.home.controller;

import com.dongxin.scm.fm.service.FundPoolService;
import com.dongxin.scm.fm.service.SettleInfoService;
import com.dongxin.scm.home.entity.CountBill;
import com.dongxin.scm.home.entity.FundPoolAccountSevenInfo;
import com.dongxin.scm.home.entity.FundPoolSevenInfo;
import com.dongxin.scm.home.entity.Home;
import com.dongxin.scm.home.entity.MonthPayment;
import com.dongxin.scm.home.entity.SalesOrderSevenInfo;
import com.dongxin.scm.home.entity.SalesShow;
import com.dongxin.scm.home.entity.SettleInfoVO;
import com.dongxin.scm.home.service.HomeService;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.sm.service.StackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "首页")
@RestController
@RequestMapping("/home/home")
@Slf4j
public class HomeController {

    @Autowired
    private SettleInfoService settleInfoService;

    @Autowired
    private FundPoolService fundPoolService;

    @Autowired
    private StackService stackService;

    @Autowired
    private HomeService homeService;

    @Autowired
    private SalesOrderService salesOrderService;


    @AutoLog(value = "首页数据")
    @ApiOperation(value = "首页数据", notes = "首页数据")
    @GetMapping(value = "/selectHome")
    public Result<?> selectHome() {
        Home home = new Home();
        //销售排行**
        List<String> salesRanking = stackService.salesRanking();
        //最近七天销售金额数据**
        List<SettleInfoVO> sevenDaySalesStatistics = stackService.sevenDaySalesStatistics();
        //最近七天来款笔数数据
        List<FundPoolSevenInfo> sevenDayFundPool = fundPoolService.sevenDayFundPool();
        //本月来款笔数与日均来款笔数
//        FundPoolVO fundPoolVO = fundPoolService.numberOfPaymentsData();
        //本月销售额
        SalesShow salesShow = homeService.salesShow();
        //本月提单量
        CountBill countBill = homeService.countBill();
        //本月来款金额，日均来款金额
        MonthPayment monthPayment = homeService.monthPayment();
        //最近七天提单量
        List<SalesOrderSevenInfo> sevenDaySalesOrder = salesOrderService.sevenDaySalesOrder();
        //最近七天来款金额
        List<FundPoolAccountSevenInfo> sevenDayFundPoolAccount = fundPoolService.sevenDayFundPoolAccount();

        home.setSalesRanking(salesRanking);
        home.setSevenDaySalesStatistics(sevenDaySalesStatistics);
        home.setSevenDayFundPool(sevenDayFundPool);
//        home.setFundPoolVO(fundPoolVO);
        home.setSalesShow(salesShow);
        home.setCountBill(countBill);
        home.setMonthPayment(monthPayment);
        home.setSevenDaySalesOrder(sevenDaySalesOrder);
        home.setSevenDayFundPoolAccount(sevenDayFundPoolAccount);
        return Result.ok(home);
    }

    @GetMapping(value = "/queryCompanyName")
    public Result<?> queryCompanyName(Integer id) {
        String name = homeService.queryCompanyName(id);
        return Result.ok(name);
    }
    @GetMapping(value ="/queryWhichEnvironment")
    public Result<?> queryWhichEnvironment(){
        return Result.ok(homeService.queryWhichEnvironment());
    }
}
