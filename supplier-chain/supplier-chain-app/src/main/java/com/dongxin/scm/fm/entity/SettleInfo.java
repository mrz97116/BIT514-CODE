package com.dongxin.scm.fm.entity;

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
import java.util.Date;

/**
 * @Description: 结算单信息
 * @Author: jeecg-boot
 * @Date: 2020-10-27
 * @Version: V1.0
 */
@Data
@TableName("fm_settle_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "fm_settle_info对象", description = "结算单信息")
public class SettleInfo implements Serializable {
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
    @Dict(  dictTable = "sys_user", dicText = "realname", dicCode = "username")
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
     * 单据状态
     */
    @Excel(name = "单据状态", width = 15, dicCode = "settle_status")
    @Dict(dicCode = "settle_status")
    @ApiModelProperty(value = "单据状态")
    private java.lang.String status;
    /**
     * 结算单编号
     */
    @Excel(name = "结算单编号", width = 15)
    @ApiModelProperty(value = "结算单编号")
    private java.lang.String settleNo;
    /**
     * 顾客
     */
    @Excel(name = "顾客", width = 15, dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @Dict(dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @ApiModelProperty(value = "顾客")
    private java.lang.String customer;
    /**
     * 产品大类
     */
    @Excel(name = "产品大类", width = 15, dicCode = "prod_code")
    @Dict(dicCode = "prod_code")
    @ApiModelProperty(value = "产品大类")
    private java.lang.String prodCode;
    /**
     * 结算金额
     */
    @Excel(name = "结算金额", width = 15)
    @ApiModelProperty(value = "结算金额")
    private java.math.BigDecimal settleAmount;
    /**
     * 租户id
     */

    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
    /**
     * 删除标志
     */
    @Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
    private java.lang.Integer delFlag;
    /**
     * 删除标志
     */
    @Excel(name = "结算单总金额", width = 15)
    @ApiModelProperty(value = "结算单总金额")
    private java.math.BigDecimal rowTotal;
    /**
     * 删除标志
     */
    @Excel(name = "折扣", width = 15)
    @ApiModelProperty(value = "折扣")
    private java.math.BigDecimal discount;
    /**
     * 业务员
     */
    @Excel(name = "业务员", width = 15)
    @ApiModelProperty(value = "业务员")
    private java.lang.String settleName;
    /**
     * 装车单id
     */
    @Excel(name = "装车单id", width = 15)
    @ApiModelProperty(value = "装车单id")
    private String stackId;
    /**
     * 结算单状态
     */
    @Excel(name = "结算单状态", width = 15)
    @ApiModelProperty(value = "结算单状态")
    private java.lang.String statementState;
    /**
     * 发票状态
     */
    @Excel(name = "发票状态", width = 15, dicCode = "invoice_status")
    @Dict(dicCode = "invoice_status")
    private String invoiceStatus;
    /**
     * 红冲时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "红冲时间")
    private java.util.Date writeOffTime;
    /**
     * 红冲编号
     */
    @Excel(name = "红冲编号", width = 15)
    @ApiModelProperty(value = "红冲编号")
    private java.lang.String writeOffNo;
    /**
     * 批量结算号
     */
    @Excel(name = "批量结算号", width = 15)
    @ApiModelProperty(value = "批量结算号")
    private java.lang.String batchSettleNo;


    @TableField(exist = false)
    private java.lang.String companyName;

    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 发货时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "发货时间")
    private Date shipDate;
    /**
     * 装车单号
     */
    @Excel(name = "装车单号", width = 15)
    @ApiModelProperty(value = "装车单号")
    private String stackingNo;
    /**
     * 销售单号
     */
    @Excel(name = "提单号", width = 15)
    @ApiModelProperty(value = "提单号")
    private String saleBillNo;
}
