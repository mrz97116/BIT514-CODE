package com.dongxin.scm.fm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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

/**
 * @Description: 发票信息
 * @Author: jeecg-boot
 * @Date: 2021-02-01
 * @Version: V1.0
 */
@ApiModel(value = "fm_invoice对象", description = "发票信息")
@Data
@TableName("fm_invoice")
public class Invoice implements Serializable {
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
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
    /**
     * 账户名称
     */
    @Excel(name = "账户名称", width = 15)
    @ApiModelProperty(value = "账户名称")
    private java.lang.String accountType;
    /**
     * 顾客
     */
    @Excel(name = "顾客", width = 15, dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @Dict(dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @ApiModelProperty(value = "顾客")
    private java.lang.String customerId;
    /**
     * 审核状态
     */
    @Excel(name = "审核状态", width = 15, dicCode = "common_check_status")
    @Dict(dicCode = "common_check_status")
    @ApiModelProperty(value = "审核状态")
    private java.lang.String status;
    /**
     * 总金额
     */
    @Excel(name = "总金额", width = 15)
    @ApiModelProperty(value = "总金额")
    private java.math.BigDecimal amount;
    /**
     * 租户id
     */
    @Excel(name = "租户id", width = 15)
    @ApiModelProperty(value = "租户id")
    private java.lang.String tenantId;
    /**
     * 单号
     */
    @Excel(name = "单号", width = 15)
    @ApiModelProperty(value = "单号")
    private java.lang.String hdorderno;
    /**
     * 单据日期
     */
    @Excel(name = "单据日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private java.util.Date lasttime;
    /**
     * 购买方
     */
    @Excel(name = "购买方", width = 15)
    @ApiModelProperty(value = "购买方")
    private java.lang.String receiverName;
    /**
     * 税号
     */
    @Excel(name = "税号", width = 15)
    @ApiModelProperty(value = "税号")
    private java.lang.String taxnum;
    /**
     * 地址电话
     */
    @Excel(name = "地址电话", width = 15)
    @ApiModelProperty(value = "地址电话")
    private java.lang.String addresstel;
    /**
     * 银行账户
     */
    @Excel(name = "银行账户", width = 15)
    @ApiModelProperty(value = "银行账户")
    private java.lang.String bankinfo;
    /**
     * 合计金额
     */
    @Excel(name = "合计金额", width = 15)
    @ApiModelProperty(value = "合计金额")
    private java.math.BigDecimal invoiceMakeMoney;
    /**
     * 操作员
     */
    @Excel(name = "操作员", width = 15)
    @ApiModelProperty(value = "操作员")
    private java.lang.String checker1;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String notes;
    /**
     * 发票号
     */
    @Excel(name = "发票号", width = 15)
    @ApiModelProperty(value = "发票号")
    private java.lang.String invoiceno;
    /**
     * 专票与普票识别参数 =s
     */
    @Excel(name = "专票与普票识别参数 =s", width = 15)
    @ApiModelProperty(value = "专票与普票识别参数 =s")
    private java.lang.String billtype = "s";
    /**
     * 结算id
     */
    @Excel(name = "结算id", width = 15)
    @ApiModelProperty(value = "结算id")
    private java.lang.Integer settleid;
    /**
     * 删除标志
     */
    @Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
    private java.lang.Integer delFlag;
    /**
     * 发票标志
     */
    @Excel(name = "发票标志", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "发票标志")
    private java.lang.String yn;
}
