package com.dongxin.scm.sm.vo;

import com.dongxin.scm.sm.entity.Mat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;

import java.util.List;

@Data
public class ProcessPage {

    /**
     * 需要加工的材料的id
     */
    @Excel(name = "需要加工的材料的id", width = 15)
    @ApiModelProperty(value = "需要加工的材料的id")
    private String matId;
    /**
     * 原材料号
     */
    @Excel(name = "原材料号", width = 15)
    @ApiModelProperty(value = "原材料号")
    private String rawMaterialNo;

    @ExcelCollection(name = "物料明细表")
    @ApiModelProperty(value = "物料明细表")
    private List<Mat> matDetList;
}
