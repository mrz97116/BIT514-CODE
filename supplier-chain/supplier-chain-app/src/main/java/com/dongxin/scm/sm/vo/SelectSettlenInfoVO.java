package com.dongxin.scm.sm.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class SelectSettlenInfoVO implements Serializable {

    /**
     * 提单号
     */
    @Excel(name = "提单号", width = 15)
    @ApiModelProperty(value = "提单号")
    private java.lang.String saleBillNo;
    /**
     * 装车单号
     */
    @Excel(name = "车号", width = 15)
    @ApiModelProperty(value = "装车单号")
    private java.lang.String stackingNo;
    /**
     * 装车单id
     */
    @Excel(name = "装车单id", width = 15)
    @ApiModelProperty(value = "装车单id")
    private java.lang.String stackId;
    /**
     * 顾客名称
     */
    @Excel(name = "顾客名称", width = 15)
    @ApiModelProperty(value = "顾客名称")
    private java.lang.String customerId;
    /**
     * 业务员
     */
    @Excel(name = "业务员", width = 15)
    @ApiModelProperty(value = "业务员")
    private java.lang.String salesMan;
    /**
     * 状态
     */
    @Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    private java.lang.String state;
    /**
     * 重量
     */
    @Excel(name = "重量", width = 15)
    @ApiModelProperty(value = "重量")
    private java.lang.Double weight;
//    /**单价*/
//    @Excel(name = "单价", width = 15)
//    @ApiModelProperty(value = "单价")
//    private java.math.BigDecimal price;
    /**
     * 价格
     */
    @Excel(name = "总价", width = 15)
    @ApiModelProperty(value = "总价")
    private java.math.BigDecimal totalAmount;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
    /**
     * 提单创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "提单创建日期")
    private Date createTime;
    /**
     * 创建人
     */
    @Dict(  dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**
     * 车号
     */
    @Excel(name = "车号", width = 15)
    @ApiModelProperty(value = "车号")
    private java.lang.String carNo;
    /**
     * 装车单创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "装车单创建日期")
    private Date stackCreateTime;


}
