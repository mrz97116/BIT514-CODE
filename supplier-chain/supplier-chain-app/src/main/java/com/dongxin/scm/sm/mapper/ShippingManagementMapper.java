package com.dongxin.scm.sm.mapper;

import java.util.List;

import com.dongxin.scm.sm.vo.OptionVO;
import org.apache.ibatis.annotations.Param;
import com.dongxin.scm.sm.entity.ShippingManagement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description: 船运管理
 * @Author: jeecg-boot
 * @Date:   2021-07-20
 * @Version: V1.0
 */
public interface ShippingManagementMapper extends BaseMapper<ShippingManagement> {

    List<OptionVO> getMatThicks(@Param("prodClassCode")String prodClassCode);
}
