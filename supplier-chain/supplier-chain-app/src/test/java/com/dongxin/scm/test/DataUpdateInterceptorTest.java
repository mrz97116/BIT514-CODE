package com.dongxin.scm.test;

import com.dongxin.scm.Application;
import com.dongxin.scm.cm.entity.CustomerConfiguration;
import com.dongxin.scm.cm.entity.CustomerProfile;
import com.dongxin.scm.cm.mapper.CustomerConfigurationMapper;
import com.dongxin.scm.cm.mapper.CustomerProfileMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Succy
 * create on 2021/3/16
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@RunWith(SpringRunner.class)
public class DataUpdateInterceptorTest {
    @Autowired
    CustomerProfileMapper customerProfileMapper;

    @Autowired
    CustomerConfigurationMapper customerConfigurationMapper;

    @Test
    public void testQuery() {
        CustomerProfile customerProfile = customerProfileMapper.selectById("1375253566750658561");
        System.out.println(customerProfile);

        customerProfile.setAlias("北部湾生态监狱");
        customerProfileMapper.updateById(customerProfile);

      //  customerProfileMapper.updateAddressById("1336265417614520322");
    }

    @Test
    public void testConfHis() {
        CustomerConfiguration customerConfiguration = customerConfigurationMapper.selectById("1352164913828655105");
        customerConfiguration.setFlag("1");

        customerConfigurationMapper.updateById(customerConfiguration);
    }
}
