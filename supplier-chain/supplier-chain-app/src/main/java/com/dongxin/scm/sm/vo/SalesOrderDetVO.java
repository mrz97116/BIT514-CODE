package com.dongxin.scm.sm.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 销售单-材料表基础信息
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */
@Data
@ApiModel(value = "SalesOrderDetVo", description = "销售单-材料表基础信息")
public class SalesOrderDetVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 车号
     */
    @Excel(name = "车号", width = 15)
    @ApiModelProperty(value = "车号")
    private String carNo;

    /**
     * 材料号
     */
    private List<String> matNos;
//    private String matNos;

    /**
     * 销售单号
     */
    private String saleBillNo;


}

