package com.dongxin.scm.sm.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dongxin.scm.om.entity.OrderDet;
import com.dongxin.scm.sm.dto.MatDTO;
import com.dongxin.scm.sm.entity.Mat;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 物料信息表
 * @Author: jeecg-boot
 * @Date: 2020-09-17
 * @Version: V1.0
 */
public interface MatMapper extends BaseMapper<Mat> {

    List<Mat> queryMatList(Map<String, Object> map);

    Double selectRepertoryWt(@Param("orderDet") OrderDet orderDet);

    List<Mat> queryByMatNo(@Param("matNo") String matNo);

    Integer queryTenantIdByCompanyName(String companyName);

    String queryCompanyNameByTenantId(Integer tenantId);

    List<Mat> queryByCH();

    List<String> selectMatRecoverId();

    List<MatDTO> queryMatYrmH();

    List<Mat> queryByYrmH();

    List<Mat> selectLastCarLoadingTime(String id, Date date);

    BigDecimal selectAveragePrice(Integer tenantId, String prodClassCode, Date date);

    List<Mat> selectByWarehouseWarrantId(@Param("warehouseWarrantId") String warehouseWarrantId);

    Date selectCarLodingTime(String inventoryId);
}
