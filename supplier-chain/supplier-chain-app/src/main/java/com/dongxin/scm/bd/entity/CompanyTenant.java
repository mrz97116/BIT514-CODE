package com.dongxin.scm.bd.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 公司租户表
 * @Author: jeecg-boot
 * @Date:   2021-02-26
 * @Version: V1.0
 */
@ApiModel(value="sys_company_tenant对象", description="公司租户表")
@Data
@TableName("sys_company_tenant")
public class CompanyTenant implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
    /**创建日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
    /**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
    /**更新日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
    /**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
    /**公司名称*/
    @Excel(name = "公司名称", width = 15)
    @ApiModelProperty(value = "公司名称")
    private java.lang.String companyName;
    /**配置*/
    @Excel(name = "配置", width = 15)
    @ApiModelProperty(value = "配置")
    private java.lang.String configuration;

    /**入库单删除*/
    @Excel(name = "入库单删除", width = 15)
    @ApiModelProperty(value = "入库单删除")
    private java.lang.String stockConfiguration;
    /**中板合并*/
    @Excel(name = "中板合并", width = 15)
    @ApiModelProperty(value = "中板合并")
    private java.lang.String cutdealConfiguration;
    /**销售单仓库*/
    @Excel(name = "销售单仓库", width = 15)
    @ApiModelProperty(value = "销售单仓库")
    private java.lang.String orderStockConfiguration;
    @Excel(name = "自动结算", width = 15)
    @ApiModelProperty(value = "自动结算")
    private java.lang.String autoSettle;
    @Excel(name = "异客户批量结算", width = 15)
    @ApiModelProperty(value = "异客户批量结算")
    private java.lang.String diffCusBatSettle;
    @Excel(name = "提单明细显示运费", width = 15)
    @ApiModelProperty(value = "提单明细显示运费")
    private java.lang.String salesDetDeliverExpense;
    @Excel(name = "提单明细显示加价", width = 15)
    @ApiModelProperty(value = "提单明细显示加价")
    private java.lang.String salesDetAddPrice;
    @Excel(name = "入库收货单位校验", width = 15)
    @ApiModelProperty(value = "入库收货单位校验")
    private java.lang.String storageConsigneeUnitCheck;
    @Excel(name = "库存定时任务配置", width = 15)
    @ApiModelProperty(value = "库存定时任务配置")
    private java.lang.String inventoryTimingConfiguration;
    @Excel(name = "船运管理配置", width = 15)
    @ApiModelProperty(value = "船运管理配置")
    private java.lang.String shippingManagementConfiguration;
    @Excel(name = "提单明细显示理论重量", width = 15)
    @ApiModelProperty(value = "提单明细显示理论重量")
    private java.lang.String salesDetTheoryWeight;
    @Excel(name = "提单明细显示理论单价", width = 15)
    @ApiModelProperty(value = "提单明细显示理论单价")
    private java.lang.String salesDetTheoryPrice;
    @Excel(name = "提单明细显示基础单价", width = 15)
    @ApiModelProperty(value = "提单明细显示基础单价")
    private java.lang.String salesDetBasicPrice;
    @Excel(name = "开单校验单价", width = 15)
    @ApiModelProperty(value = "开单校验单价")
    private java.lang.String averageCostCheck;
    @Excel(name = "产品大类单据编号", width = 15)
    @ApiModelProperty(value = "产品大类单据编号")
    private java.lang.String prodClassSerialNo;
    @Excel(name = "装车定时任务配置", width = 15)
    @ApiModelProperty(value = "装车定时任务配置")
    private java.lang.String stackTimingConfiguration;
    @Excel(name = "红冲复核", width = 15)
    @ApiModelProperty(value = "红冲复核")
    private java.lang.String writeOffReviewer;
}
