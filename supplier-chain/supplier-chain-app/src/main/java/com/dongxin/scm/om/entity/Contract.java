package com.dongxin.scm.om.entity;

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
 * @Description: 合同主档表
 * @Author: jeecg-boot
 * @Date: 2020-12-14
 * @Version: V1.0
 */
@ApiModel(value = "om_contract对象", description = "合同主档表")
@Data
@TableName("om_contract")
public class Contract implements Serializable {
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
     * 订单编号
     */
    @Excel(name = "订单编号", width = 15)
    @ApiModelProperty(value = "订单编号")
    private java.lang.String orderNo;
    /**
     * 合同编号
     */
    @Excel(name = "合同编号", width = 15)
    @ApiModelProperty(value = "合同编号")
    private java.lang.String contractNo;
    /**
     * 订货总重量
     */
    @Excel(name = "订货总重量", width = 15)
    @ApiModelProperty(value = "订货总重量")
    private java.lang.Double orderWt;
    /**
     * 总价
     */
    @Excel(name = "总价", width = 15)
    @ApiModelProperty(value = "总价")
    private java.math.BigDecimal totalPrice;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
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
     * 客户Id
     */
    @Excel(name = "客户Id", width = 15, dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @Dict(dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @ApiModelProperty(value = "客户Id")
    private java.lang.String customerId;
    /**
     * 合同比例
     */
    @Excel(name = "合同比例", width = 15)
    @ApiModelProperty(value = "合同比例")
    private java.lang.Integer rate;
    /**
     * 目的地
     */
    @Excel(name = "目的地", width = 15)
    @ApiModelProperty(value = "目的地")
    private java.lang.String destination;
    /**
     * 合同份数
     */
    @Excel(name = "合同份数", width = 15)
    @ApiModelProperty(value = "合同份数")
    private java.lang.Integer copies;
    /**
     * 合同份数
     */
    @Excel(name = "合同状态", width = 15, dicCode = "contract_flag")
    @Dict(dicCode = "contract_flag")
    @ApiModelProperty(value = "合同状态")
    private java.lang.String contractFlag;

}
