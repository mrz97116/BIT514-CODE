package com.dongxin.scm.sm.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.dongxin.scm.sm.entity.Mat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * @Description: 入库单
 * @Author: jeecg-boot
 * @Date: 2021-04-06
 * @Version: V1.0
 */
@Data
@ApiModel(value = "sm_warehouse_warrant数据传输对象", description = "入库单编辑")
public class WarehouseWarrantDTO{

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    @Dict(dictTable = "sys_user",dicText = "realname",dicCode = "username")
    private String createBy;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
    /**
     * 更新人
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
    /**
     * 仓库id
     */
    @Excel(name = "仓库id", width = 15)
    @Dict(dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "仓库id")
    private String stockId;
    /**
     * 来源地
     */
    @Excel(name = "来源地", width = 15)
    @ApiModelProperty(value = "来源地")
    private String areaSource;
    /**
     * 数量合计
     */
    @Excel(name = "数量合计", width = 15)
    @ApiModelProperty(value = "数量合计")
    private Integer qty;
    /**
     * 重量合计
     */
    @Excel(name = "重量合计", width = 15)
    @ApiModelProperty(value = "重量合计")
    private Double weight;
    /**
     * 入库时间
     */
    @Excel(name = "入库时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "入库时间")
    private java.util.Date stockTime;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;

    @ExcelCollection(name = "入库明细")
    @ApiModelProperty(value = "入库明细")
    private List<Mat> matList;
}
