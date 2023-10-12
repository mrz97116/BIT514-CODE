package com.dongxin.scm.sm;

import cn.hutool.core.collection.CollectionUtil;
import com.dongxin.scm.Application;
import com.dongxin.scm.cm.service.CustomerProfileService;
import com.dongxin.scm.om.entity.SalesOrder;
import com.dongxin.scm.om.entity.SalesOrderDet;
import com.dongxin.scm.om.mapper.SalesOrderMapper;
import com.dongxin.scm.om.service.SalesOrderDetService;
import com.dongxin.scm.om.service.SalesOrderService;
import com.dongxin.scm.om.vo.BillDetVO;
import com.dongxin.scm.om.vo.BillVO;
import com.dongxin.scm.sm.entity.Inventory;
import com.dongxin.scm.sm.service.GoodEntrustmentLetterService;
import com.dongxin.scm.sm.service.InventoryService;
import org.jeecg.config.mybatis.TenantContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class GdShippingmentTest {

    @Autowired
    private GoodEntrustmentLetterService goodEntrustmentLetterService;


    @Test
    public void test_auto_set_stay_day() {
        TenantContext.setTenant("12");
        goodEntrustmentLetterService.updateStayDays();
    }
}
