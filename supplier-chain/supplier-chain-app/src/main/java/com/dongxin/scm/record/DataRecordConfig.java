package com.dongxin.scm.record;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Succy
 * create on 2021/3/16
 */
@Configuration
@ConditionalOnProperty(name = "data.update-record.enable", havingValue = "true")
public class DataRecordConfig {

    @Bean
    public DataUpdateInterceptor dataUpdateInterceptor(DataSource dataSource) {
        return new DataUpdateInterceptor(dataSource);
    }
}
