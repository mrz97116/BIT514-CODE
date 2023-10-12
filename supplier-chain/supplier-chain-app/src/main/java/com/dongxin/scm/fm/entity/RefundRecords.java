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

/**
 * @Description: 退款记录表
 * @Author: jeecg-boot
 * @Date: 2020-11-25
 * @Version: V1.0
 */
@Data
@TableName("fm_refund_records")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "fm_refund_records对象", description = "退款记录表")
public class RefundRecords implements Serializable {
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
     * 顾客名称
     */
    @Excel(name = "顾客名称", width = 15)
    @ApiModelProperty(value = "顾客名称")
    private java.lang.String customerId;
    /**
     * 退款金额
     */
    @Excel(name = "退款金额", width = 15)
    @ApiModelProperty(value = "退款金额")
    private java.math.BigDecimal refundAmount;
    /**
     * 审核日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "审核日期")
    private java.util.Date approveDate;
    /**
     * 退款时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "退款日期")
    private java.util.Date refundDate;
    /**
     * 审核状态
     */
    @Excel(name = "审核状态", width = 15, dicCode = "common_check_status")
    @Dict(dicCode = "common_check_status")
    @ApiModelProperty(value = "审核状态")
    private java.lang.String status;
    /**
     * 租户id
     */

    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remarks;
    /**
     * 收据编号
     */
    @Excel(name = "收据编号", width = 15)
    @ApiModelProperty(value = "收据编号")
    private java.lang.String receiptCode;
}
