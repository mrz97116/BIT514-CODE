package com.dongxin.scm.sm.service;
import com.dongxin.scm.bd.entity.CompanyTenant;
import com.dongxin.scm.bd.service.CompanyTenantService;
import com.dongxin.scm.om.vo.OptionVO;
import com.dongxin.scm.sm.entity.Fleet;
import com.dongxin.scm.sm.mapper.FleetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.jeecg.common.system.base.service.BaseService;

import java.util.List;

/**
 * @Description: 车队管理
 * @Author: jeecg-boot
 * @Date:   2021-06-29
 * @Version: V1.0
 */
@Service
public class FleetService extends BaseService<FleetMapper, Fleet>{
    @Autowired
    private FleetMapper fleetMapper;
    ////利用optionVO查询车队名称，以便于与前端jeditabletable组件配合使用
    public List<OptionVO> queryFleet(){
        List<OptionVO> fleetList=fleetMapper.queryFleet();
        return fleetList;
    }
    public List<CompanyTenant> queryTenantId(){
        return null;
    }
}
