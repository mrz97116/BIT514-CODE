package com.dongxin.scm.sm.service;

import com.dongxin.scm.sm.entity.GetActualDeliveryDet;
import com.dongxin.scm.sm.mapper.GetActualDeliveryDetMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import org.jeecg.common.system.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 获取装车实际明细
 * @Author: jeecg-boot
 * @Date:   2021-09-22
 * @Version: V1.0
 */
@Service
public class GetActualDeliveryDetService extends BaseService<GetActualDeliveryDetMapper, GetActualDeliveryDet>{

	public List<GetActualDeliveryDet> selectByMainId(String mainId) {
		return baseMapper.selectByMainId(mainId);
	}

	public void deleteBatchByMainId(String id) {
        List<GetActualDeliveryDet> details = selectByMainId(id);
        for(GetActualDeliveryDet detail : details){
            logicDelete(detail);
        }
    }

}
