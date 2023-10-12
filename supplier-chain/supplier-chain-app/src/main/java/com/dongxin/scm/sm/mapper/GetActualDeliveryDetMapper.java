package com.dongxin.scm.sm.mapper;

import java.util.List;
import com.dongxin.scm.sm.entity.GetActualDeliveryDet;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 获取装车实际明细
 * @Author: jeecg-boot
 * @Date:   2021-09-22
 * @Version: V1.0
 */
public interface GetActualDeliveryDetMapper extends BaseMapper<GetActualDeliveryDet> {

	public boolean deleteByMainId(@Param("mainId") String mainId);
    
	public List<GetActualDeliveryDet> selectByMainId(@Param("mainId") String mainId);

}
