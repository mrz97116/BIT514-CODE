package com.dongxin.scm.sm.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 销售单
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */
@Data
public class SalesOrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 提单号
     */
    @Excel(name = "提单号", width = 15)
    @ApiModelProperty(value = "提单号")
    private java.lang.String BillNo;
    /**
     * 合同编号
     */
    @Excel(name = "合同编号", width = 15)
    @ApiModelProperty(value = "合同编号")
    private java.lang.String contractNo;
    /**
     * 订单编号
     */
    @Excel(name = "订单编号", width = 15)
    @ApiModelProperty(value = "订单编号")
    private java.lang.String orderNo;
    /**
     * 客户编码
     */
    @Excel(name = "客户编码", width = 15)
    @ApiModelProperty(value = "客户编码")
    private java.lang.String customerId;
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
     * 提单号
     */
    @ApiModelProperty(value = "提单号")
    private java.lang.String saleBillNo;
    /**
     * 订单条目id
     */
    @Excel(name = "订单条目id", width = 15)
    @ApiModelProperty(value = "订单条目id")
    private java.lang.String orderItemNo;
    /**
     * 数量
     */
    @Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private java.lang.Double qty;
    /**
     * 重量
     */
    @Excel(name = "重量", width = 15)
    @ApiModelProperty(value = "重量")
    private java.lang.Double weight;
    /**
     * 单价
     */
    @Excel(name = "单价", width = 15)
    @ApiModelProperty(value = "单价")
    private java.lang.Double price;
    /**
     * 总价
     */
    @Excel(name = "总价", width = 15)
    @ApiModelProperty(value = "总价")
    private java.math.BigDecimal totalPrice;
    /**
     * 订货长度
     */
    @Excel(name = "订货长度", width = 15)
    @ApiModelProperty(value = "订货长度")
    private java.lang.Double orderLen;
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
     * 标准
     */
    @Excel(name = "标准", width = 15)
    @ApiModelProperty(value = "标准")
    private java.lang.String sgStd;
    /**
     * 物料种类
     */
    @Excel(name = "物料种类", width = 15)
    @ApiModelProperty(value = "物料种类")
    private java.lang.String matKind;
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
    private java.lang.Integer tenantId;
    /**
     * 逻辑删除
     */
    @Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private java.lang.Integer delFlag;


}
