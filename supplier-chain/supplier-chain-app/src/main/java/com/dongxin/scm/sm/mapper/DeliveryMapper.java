package com.dongxin.scm.sm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dongxin.scm.sm.dto.SalesOrderDTO;
import com.dongxin.scm.sm.vo.SalesOrderVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:
 * @Author: jeecg-boot
 * @Date: 2020-09-17
 * @Version: V1.0
 */
public interface DeliveryMapper extends BaseMapper<SalesOrderDTO> {

    List<SalesOrderDTO> querySalesOrderDTOList(Page page, @Param("salesOrderVO") SalesOrderVO salesOrderVO);
}

