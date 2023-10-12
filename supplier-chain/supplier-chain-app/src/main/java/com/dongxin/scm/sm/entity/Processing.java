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
 * @Description: 加工管理
 * @Author: jeecg-boot
 * @Date: 2021-02-20
 * @Version: V1.0
 */
@Data
@TableName("sm_processing")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "sm_processing对象", description = "加工管理")
public class Processing implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
    /**
     * 创建人
     */
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
     * 逻辑删除
     */
    @Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private java.lang.Integer delFlag;
    /**
     * 牌号
     */
    @Excel(name = "牌号", width = 15)
    @ApiModelProperty(value = "牌号")
    private java.lang.String sgSign;
    /**
     * 材料号
     */
    @Excel(name = "材料号", width = 15)
    @ApiModelProperty(value = "材料号")
    private java.lang.String matNo;
    /**
     * 材料长度
     */
    @Excel(name = "材料长度", width = 15)
    @ApiModelProperty(value = "材料长度")
    private java.lang.Double matLen;
    /**
     * 材料宽度
     */
    @Excel(name = "材料宽度", width = 15)
    @ApiModelProperty(value = "材料宽度")
    private java.lang.Double matWidth;
    /**
     * 材料厚度
     */
    @Excel(name = "材料厚度", width = 15)
    @ApiModelProperty(value = "材料厚度")
    private java.lang.Double matThick;
    /**
     * 产品大类
     */
    @Excel(name = "产品大类", width = 15)
    @ApiModelProperty(value = "产品大类")
    private java.lang.String prodClassCode;
    /**
     * 品名中文别名
     */
    @Excel(name = "品名中文别名", width = 15)
    @ApiModelProperty(value = "品名中文别名")
    private java.lang.String prodCnameOther;
    /**
     * 租户id
     */
    @Excel(name = "租户id", width = 15)
    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
    /**
     * 材料id
     */
    @Excel(name = "材料id", width = 15)
    @ApiModelProperty(value = "材料id")
    private java.lang.String matId;
    /**
     * 加工编号
     */
    @Excel(name = "加工编号", width = 15)
    @ApiModelProperty(value = "加工编号")
    private java.lang.String processingNo;
    /**
     * 客户编码
     */
    @Excel(name = "客户编码", width = 15)
    @ApiModelProperty(value = "客户编码")
    private java.lang.String customerId;
    /**
     * 业务员编码
     */
    @Excel(name = "业务员编码", width = 15)
    @ApiModelProperty(value = "业务员编码")
    private java.lang.String salesManId;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remarks;
    /**
     * 卷重
     */
    @Excel(name = "卷重", width = 15)
    @ApiModelProperty(value = "卷重")
    private java.lang.Double weight;
    /**
     * 规格
     */
    @Excel(name = "规格", width = 15)
    @ApiModelProperty(value = "规格")
    private java.lang.String custMatSpecs;
    /**
     * 物料编码
     */
    @Excel(name = "物料编码", width = 15)
    @ApiModelProperty(value = "物料编码")
    private String matCode;
}
