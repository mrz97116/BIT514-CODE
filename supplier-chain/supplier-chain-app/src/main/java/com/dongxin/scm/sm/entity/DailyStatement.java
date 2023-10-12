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
 * @Description: 日报表
 * @Author: jeecg-boot
 * @Date: 2020-12-28
 * @Version: V1.0
 */
@Data
@TableName("sm_daily_statement")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "sm_daily_statement对象", description = "日报表")
public class DailyStatement implements Serializable {
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
     * 规格
     */
    @Excel(name = "规格", width = 15)
    @ApiModelProperty(value = "规格")
    private java.lang.String custMatSpecs;
    /**
     * 品种
     */
    @Excel(name = "品种", width = 15)
    @ApiModelProperty(value = "品种")
    private java.lang.String mat;
    /**
     * 计重方式
     */
    @Excel(name = "计重方式", width = 15)
    @ApiModelProperty(value = "计重方式")
    private java.lang.String wtMode;
    /**
     * 基础价(元/吨)
     */
    @Excel(name = "基础价(元/吨)", width = 15)
    @ApiModelProperty(value = "基础价(元/吨)")
    private java.math.BigDecimal basePrice;
    /**
     * 成交价(元/吨)
     */
    @Excel(name = "成交价(元/吨)", width = 15)
    @ApiModelProperty(value = "成交价(元/吨)")
    private java.math.BigDecimal transactionPrice;
    /**
     * 成交量
     */
    @Excel(name = "成交量", width = 15)
    @ApiModelProperty(value = "成交量")
    private java.lang.Double transactionWeight;
    /**
     * 进货量
     */
    @Excel(name = "进货量", width = 15)
    @ApiModelProperty(value = "进货量")
    private java.lang.Double stockWeight;
    /**
     * 库存量
     */
    @Excel(name = "库存量", width = 15)
    @ApiModelProperty(value = "库存量")
    private java.lang.Double inventoryWeight;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
}
