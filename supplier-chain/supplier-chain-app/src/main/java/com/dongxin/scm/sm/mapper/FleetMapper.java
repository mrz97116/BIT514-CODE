package com.dongxin.scm.sm.mapper;

import java.util.List;

import com.dongxin.scm.om.vo.OptionVO;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.sm.entity.Fleet;

@Mapper
public interface FleetMapper extends BaseMapper<Fleet> {
    //利用optionVO查询车队名称，以便于与前端jeditabletable组件配合使用
    List<OptionVO> queryFleet();
}
