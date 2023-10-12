package com.dongxin.scm.om;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongxin.scm.Application;
import com.dongxin.scm.om.entity.OrderMat;
import com.dongxin.scm.om.mapper.OrderMatMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class orderMatServiceTest {

    @Resource
    public OrderMatMapper orderMatMapper;


    /**
     *  新开租户初始化数据（测试数据）
     *  将om_order_mat 表中租户id=2的数据复制并更改租户id为4插入表中
     *  启动前需要先在src\main\resources\application-dev.yml中将239行tenant.table注释
     */
    @Test
    public void init(){

        //查询表中租户id为'2'的数据
        QueryWrapper<OrderMat> orderMatQueryWrapper = new QueryWrapper<>();
        orderMatQueryWrapper.lambda().eq(OrderMat::getTenantId ,'2');
        List<OrderMat> orderMatList =  orderMatMapper.selectList(orderMatQueryWrapper);

        //复制数据并将id改为4
        for(OrderMat orderMat : orderMatList){
            //给主键赋空值，防止主键冲突
            orderMat.setId(null);
            orderMat.setTenantId(4);
        //将新数据重新插入到om_order_mat表中
            orderMatMapper.insert(orderMat);
        }
    }
}
