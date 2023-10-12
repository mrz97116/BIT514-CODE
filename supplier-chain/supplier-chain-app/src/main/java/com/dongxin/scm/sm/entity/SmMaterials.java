package com.dongxin.scm.sm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 建筑材单重表
 * @Author: jeecg-boot
 * @Date: 2021-02-02
 * @Version: V1.0
 */
@Data
@TableName("sm_materials")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "sm_materials对象", description = "建筑材单重表")
public class SmMaterials implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    //创建人
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
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
    private java.lang.String updateBy;
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
    private java.lang.String sysOrgCode;

    /**
     * 规格
     */
    @Excel(name = "规格", width = 15)
    @ApiModelProperty(value = "规格")
    private String standardS;
    /**
     * 理论重量(kg/m)
     */
    @Excel(name = "理论重量(kg/m)", width = 15)
    @ApiModelProperty(value = "理论重量(kg/m)")
    private Double theoryWeightM;
    /**
     * 单支理重（kg/支）
     */
    @Excel(name = "单支理重(kg/支)", width = 15)
    @ApiModelProperty(value = "单支理重(kg/支)")
    private Double kickStand;
    /**
     * 每捆支数（支/捆）
     */
    @Excel(name = "每捆支数(支/捆)", width = 15)
    @ApiModelProperty(value = "每捆支数(支/捆)")
    private Double bundleBranch;
    /**
     * 理论重量(kg/捆)
     */
    @Excel(name = "理论重量(kg/捆)", width = 15)
    @ApiModelProperty(value = "理论重量(kg/捆)")
    private Double theoryWeighB;
    /**
     * 国标允许最小重量(Kg/捆)
     */
    @Excel(name = "国标允许最小重量(Kg/捆)", width = 15)
    @ApiModelProperty(value = "国标允许最小重量(Kg/捆)")
    private Double nationalStandard;
    /**
     * 定尺
     */
    @Excel(name = "定尺", width = 15)
    @ApiModelProperty(value = "定尺")
    private String lengthS;
}
