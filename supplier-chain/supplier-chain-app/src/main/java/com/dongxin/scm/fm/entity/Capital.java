package com.dongxin.scm.fm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class Capital implements Serializable {
    /**
     * 银行名称
     */
//    @Excel(name = "顾客id", width = 15)
    @ApiModelProperty(value = "顾客id")
    private java.lang.String customerId;
    /**
     * 银行名称
     */
    @Excel(name = "顾客姓名", width = 15)
    @ApiModelProperty(value = "顾客姓名")
    private java.lang.String customerName;
    /**
     * 银行名称
     */
//    @Excel(name = "总金额", width = 15)
    @ApiModelProperty(value = "总金额")
    private java.math.BigDecimal totalAmount;
    /**
     * 银行名称
     */
    @Excel(name = "来款金额", width = 15)
    @ApiModelProperty(value = "来款金额")
    private java.math.BigDecimal incomeAccount;
    /**
     * 银行名称
     */
//    @Excel(name = "授信金额", width = 15)
    @ApiModelProperty(value = "授信金额")
    private java.math.BigDecimal creditAccount;
    /**
     * 银行名称
     */
    @Excel(name = "冻结金额", width = 15)
    @ApiModelProperty(value = "冻结金额")
    private java.math.BigDecimal frozenAccount;
    /**
     * 来款预用金额
     */
    @TableField(exist = false)
    private java.math.BigDecimal incomePreUseAccount;
    /**
     * 来款预用金额
     */
    @TableField(exist = false)
    private java.math.BigDecimal creditPreUseAccount;
    /**
     * 来款预用金额
     */
//    @Excel(name = "预用金额", width = 15)
    @TableField(exist = false)
    private java.math.BigDecimal preUseAccount;
    /**
     * 客户实际总金额
     */
    @Excel(name = "客户实际总金额", width = 15)
    @TableField(exist = false)
    private java.math.BigDecimal availableAndPreUseAccount;
    /**
     * 来款可用金额加冻结金额
     */
    @TableField(exist = false)
    private java.math.BigDecimal incomeAccountAndFrozenAccount;
}
