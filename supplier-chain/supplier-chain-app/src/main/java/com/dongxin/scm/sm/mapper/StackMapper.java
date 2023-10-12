package com.dongxin.scm.sm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.sm.entity.InitStack;
import com.dongxin.scm.sm.entity.InitStackDet;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.entity.Stack;
import com.dongxin.scm.sm.vo.OptionVO;
import com.dongxin.scm.sm.vo.SelectSettlenInfoVO;
import com.dongxin.scm.sm.vo.StackDetailVO;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 装车单主表
 * @Author: jeecg-boot
 * @Date: 2020-10-31
 * @Version: V1.0
 */
public interface StackMapper extends BaseMapper<Stack> {
    Long stackingNoMaxNum(String now);

    List<SelectSettlenInfoVO> selectStackDetailVOPage(Page page, @Param("stackDetailVO") StackDetailVO stackDetailVO);


    List<SelectSettlenInfoVO> selectStack(@Param("stackDetailVO") StackDetailVO stackDetailVO, @Param("ids") String ids);

    List<BigDecimal> monthSalesLast(@Param("month") String month, @Param("days") String days, @Param("tenantId") String tenantId, @Param("delFlag") Integer delFlag);

    List<BigDecimal> monthSales(@Param("month") String month, @Param("tenantId") String tenantId, @Param("delFlag") Integer delFlag);

    List<BigDecimal> somedaySales(@Param("someday") String someday, @Param("tenantId") String tenantId, @Param("delFlag") Integer delFlag);

    List<Stack> getMonthStack(@Param("thisMonthNumberOne") Date thisMonthNumberOne, @Param("delFlag") Integer delFlag);

    List<Stack> sevenDayStackInfo(@Param("sevenDaysAgoDate") Date sevenDaysAgoDate, @Param("delFlag") Integer delFlag);

    List<String> initQueryBillIds(String status);

    List<InitStack> initQueryStack(List<String> billIds);

    List<InitStackDet> initQueryStackDets(String stackId);

    OptionVO initQueryStockId(String stockName);

    Inventory initQueryInventory(String id);

    List<OptionVO> selectUnsettleCustomerId();

    List<Stack> selectDeleteStack(@Param("startTime")Date startTime, @Param("endTime")Date endTime);

    BigDecimal sumUnSettleStackAmount(@Param("customerId")String customerId);
}
