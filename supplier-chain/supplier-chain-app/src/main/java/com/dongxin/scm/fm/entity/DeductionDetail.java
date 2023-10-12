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
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description: 控款明细表
 * @Author: jeecg-boot
 * @Date: 2020-10-14
 * @Version: V1.0
 */
@Data
@TableName("fm_deduction_detail")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "fm_deduction_detail对象", description = "控款明细表")
public class DeductionDetail implements Serializable {
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
     * 扣款记录id
     */
    @Excel(name = "扣款记录id", width = 15)
    @ApiModelProperty(value = "扣款记录id")
    private java.lang.String deductionId;
    /**
     * 金额
     */
    @Excel(name = "金额", width = 15)
    @ApiModelProperty(value = "金额")
    private java.math.BigDecimal amount;
    /**
     * 资金记录id
     */
    @Excel(name = "资金记录id", width = 15)
    @ApiModelProperty(value = "资金记录id")
    private java.lang.String fundPoolId;
    /**
     * 逻辑删除字段
     */
    @Excel(name = "逻辑删除字段", width = 15)
    @ApiModelProperty(value = "逻辑删除字段")
    private java.lang.Integer delFlag;
    /**
     * settled_amount 已结算金额
     */
    @Excel(name = "已结算金额", width = 15)
    @ApiModelProperty(value = "已结算金额")
    private java.math.BigDecimal settledAmount;


    public BigDecimal getUnsettleAmount() {
        return this.amount.subtract(settledAmount);
    }
}
