package com.dongxin.scm.sm.vo;

import com.dongxin.scm.sm.entity.Mat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

@Data
public class MatVo {

    /**
     * 仓库id
     */
    @Excel(name = "仓库id", width = 15, dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @Dict(dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "仓库id")
    private String stockHouseId;
    /**
     * 库位
     */
    @Excel(name = "库位", width = 15)
    @ApiModelProperty(value = "库位")
    private String stockLocation;
    /**
     * 库存类型
     */
    @Excel(name = "库存类型", width = 15, dicCode = "stock_type")
    @Dict(dicCode = "stock_type")
    @ApiModelProperty(value = "库存类型")
    private String stockType;
    /**
     * 车号
     */
    @Excel(name = "车号", width = 15)
    @ApiModelProperty(value = "车号")
    private String carNo;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remarks;
    /**
     * 入库时间
     */
    @Excel(name = "入库时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "入库时间")
    private java.util.Date stockTime;


    private List<Mat> matDetList;

    private String dischargerName;
}
