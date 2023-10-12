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
 * @Description: 资金账户表
 * @Author: jeecg-boot
 * @Date: 2020-11-23
 * @Version: V1.0
 */
@Data
@TableName("fm_fund_account")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "fm_fund_account对象", description = "资金账户表")
public class FundAccount implements Serializable {
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
     * 顾客
     */
    @Excel(name = "顾客", width = 15)
    @ApiModelProperty(value = "顾客")
    private java.lang.String customerId;
    /**
     * 账户类型
     */
    @Excel(name = "账户类型", width = 15)
    @ApiModelProperty(value = "账户类型")
    private java.lang.String type;
    /**
     * 账户总来款/授信
     */
    @Excel(name = "账户总额", width = 15)
    @ApiModelProperty(value = "账户总额")
    private java.math.BigDecimal totalIncome;
    /**
     * 可用金额
     */
    @Excel(name = "可用金额", width = 15)
    @ApiModelProperty(value = "可用金额")
    private java.math.BigDecimal availableAmount;
    /**
     * 预用金额
     */
    @Excel(name = "预用金额", width = 15)
    @ApiModelProperty(value = "预用金额")
    private java.math.BigDecimal preuseAmount;
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

    public BigDecimal generateToBePayCredit() {
        return totalIncome.subtract(availableAmount);
    }

    //检查是否存在账户被扣减到0
    public boolean existLtZero() {
        return totalIncome.compareTo(BigDecimal.ZERO) < 0
                || availableAmount.compareTo(BigDecimal.ZERO) < 0
                || preuseAmount.compareTo(BigDecimal.ZERO) < 0
                || settleAmount.compareTo(BigDecimal.ZERO) < 0;
    }


}
