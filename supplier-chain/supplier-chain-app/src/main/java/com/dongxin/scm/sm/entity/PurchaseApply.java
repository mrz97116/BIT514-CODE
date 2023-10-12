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
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 采购申请单
 * @Author: jeecg-boot
 * @Date: 2020-10-14
 * @Version: V1.0
 */
@Data
@TableName("sm_purchase_apply")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "sm_purchase_apply对象", description = "采购申请单")
public class PurchaseApply implements Serializable {
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
    /**
     * 顾客名称
     */
    @Excel(name = "顾客名称", width = 15)
    @ApiModelProperty(value = "顾客名称")
    private java.lang.String custName;
    /**
     * 产品大类
     */
    @Excel(name = "产品大类", width = 15)
    @ApiModelProperty(value = "产品大类")
    private java.lang.String prodClassCode;
    /**
     * 物料种类
     */
    @Excel(name = "物料种类", width = 15, dicCode = "mat_kind")
    @Dict(dicCode = "mat_kind")
    @ApiModelProperty(value = "物料种类")
    private java.lang.String matKind;
    /**
     * 长度
     */
    @Excel(name = "长度", width = 15)
    @ApiModelProperty(value = "长度")
    private java.lang.Double matLen;
    /**
     * 牌号
     */
    @Excel(name = "牌号", width = 15)
    @ApiModelProperty(value = "牌号")
    private java.lang.String sgSign;
    /**
     * 品名中文
     */
    @Excel(name = "品名中文", width = 15)
    @ApiModelProperty(value = "品名中文")
    private java.lang.String prodCname;
    /**
     * 宽度
     */
    @Excel(name = "宽度", width = 15)
    @ApiModelProperty(value = "宽度")
    private java.lang.Double matWidth;
    /**
     * 厚度
     */
    @Excel(name = "厚度", width = 15)
    @ApiModelProperty(value = "厚度")
    private java.lang.Double matThick;
    /**
     * 数量
     */
    @Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private java.lang.Integer qty;
    /**
     * 重量
     */
    @Excel(name = "重量", width = 15)
    @ApiModelProperty(value = "重量")
    private java.lang.Double weight;
    /**
     * 合同编号
     */
    @Excel(name = "合同编号", width = 15)
    @ApiModelProperty(value = "合同编号")
    private java.lang.String contractNo;
    /**
     * 采购状态
     */
    @Excel(name = "采购状态", width = 15, dicCode = "purchaseApply_status")
    @Dict(dicCode = "purchaseApply_status")
    @ApiModelProperty(value = "采购状态")
    private java.lang.String status;
}
