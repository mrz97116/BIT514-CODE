package com.dongxin.scm.common.service;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.system.service.ISysDataLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DataLogService {

    @Autowired
    private ISysDataLogService sysDataLogService;

    public void dataLog(String tableName, String id, String dataContent) {
        try {
            sysDataLogService.addDataLog(tableName, id, dataContent);
        } catch (Exception e) {
            log.error("履历更新出错:{}", e.getMessage());
        }

    }

}
