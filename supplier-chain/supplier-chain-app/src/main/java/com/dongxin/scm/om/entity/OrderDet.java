package com.dongxin.scm.om.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description: 订单明细表
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */
@Data
@TableName("om_order_det")
@ApiModel(value = "om_order对象", description = "订单主表")
public class OrderDet implements Serializable {
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
     * 订单ID
     */
    @ApiModelProperty(value = "订单ID")
    private java.lang.String orderMainId;
    /**
     * 标准
     */
    @Excel(name = "标准", width = 15)
    @ApiModelProperty(value = "标准")
    private java.lang.String sgStd;
    /**
     * 订货长度
     */
    @Excel(name = "订货长度", width = 15)
    @ApiModelProperty(value = "订货长度")
    private java.lang.Double orderLen;
    /**
     * 牌号
     */
    @Excel(name = "牌号", width = 15)
    @ApiModelProperty(value = "牌号")
    private java.lang.String sgSign;
    /**
     * 订货宽度
     */
    @Excel(name = "订货宽度", width = 15)
    @ApiModelProperty(value = "订货宽度")
    private java.lang.Double orderWidth;
    /**
     * 订货厚度
     */
    @Excel(name = "订货厚度", width = 15)
    @ApiModelProperty(value = "订货厚度")
    private java.lang.Double orderThick;
    /**
     * 品名中文
     */
    @Excel(name = "品名中文", width = 15)
    @ApiModelProperty(value = "品名中文")
    private java.lang.String prodCname;
    /**
     * 品名中文别名
     */
    @Excel(name = "品名中文别名", width = 15)
    @ApiModelProperty(value = "品名中文别名")
    private java.lang.String prodCnameOther;
    /**
     * 物料种类
     */
    @Excel(name = "物料种类", width = 15)
    @Dict(dicCode = "mat_kind")
    @ApiModelProperty(value = "物料种类")
    private java.lang.String matKind;
    /**
     * 物料重量
     */
    @Excel(name = "物料重量", width = 15)
    @ApiModelProperty(value = "物料重量")
    private java.lang.Double weight;
    /**
     * 单价
     */
    @Excel(name = "单价", width = 15)
    @ApiModelProperty(value = "单价")
    private BigDecimal price;
    /**
     * 加价
     */
    @Excel(name = "加价", width = 15)
    @ApiModelProperty(value = "加价")
    private java.lang.Double addPrice;
    /**
     * 单价
     */
    @Excel(name = "成本价", width = 15)
    @ApiModelProperty(value = "成本价")
    private BigDecimal costPrice;
    /**
     * 过磅重量
     */
    @Excel(name = "过磅重量", width = 15)
    @ApiModelProperty(value = "过磅重量")
    private java.lang.Double weightingWeight;
    /**
     * 过磅价格
     */
    @Excel(name = "过磅价格", width = 15)
    @ApiModelProperty(value = "过磅价格")
    private java.math.BigDecimal weightingPrice;
    /**
     * 计重方式
     */
    @Excel(name = "计重方式", width = 15)
    @ApiModelProperty(value = "计重方式")
    private java.lang.String weightType;
    /**
     * 产品大类
     */
    @Excel(name = "产品大类", width = 15)
    @ApiModelProperty(value = "产品大类")
    private java.lang.String prodClassCode;
    /**
     * 租户id
     */

    @ApiModelProperty(value = "租户id")
    private java.lang.String tenantId;
    /**
     * 逻辑删除
     */
    @Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private java.lang.Integer delFlag;
    /**
     * 订单编号
     */
    @Excel(name = "订单编号", width = 15)
    @ApiModelProperty(value = "订单编号")
    private java.lang.String orderNo;
    /**
     * 品名代码
     */
    @Excel(name = "品名代码", width = 15)
    @ApiModelProperty(value = "品名代码")
    private java.lang.String prodCode;
}
