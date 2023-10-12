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
import java.math.BigDecimal;

/**
 * @Description: 资金明细
 * @Author: jeecg-boot
 * @Date: 2020-10-15
 * @Version: V1.0
 */
@Data
@TableName("fm_fund_detail")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "fm_fund_detail对象", description = "资金明细")
public class FundDetail implements Serializable {
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
    @Dict(dicCode = "id", dictTable = "cm_customer_profile", dicText = "company_name")
    @Excel(name = "顾客", width = 15)
    @ApiModelProperty(value = "顾客")
    private java.lang.String customerId;
    /**
     * 金额
     */
    @Excel(name = "金额", width = 15)
    @ApiModelProperty(value = "金额")
    private java.math.BigDecimal amount;
    /**
     * 类型
     */
    @Excel(name = "类型", width = 15, dicCode = "fund_detail_type")
    @Dict(dicCode = "fund_detail_type")
    @ApiModelProperty(value = "类型")
    private java.lang.String type;
    /**
     * 账户id
     */
    @Excel(name = "账户id", width = 15)
    @ApiModelProperty(value = "账户id")
    private java.lang.String accountId;
    /**
     * 外部订单号
     */
    @Excel(name = "外部订单号", width = 15)
    @ApiModelProperty(value = "外部订单号")
    private java.lang.String outTradeNo;


    /**
     * 外部订单号
     */
    @Excel(name = "资金id", width = 15)
    @ApiModelProperty(value = "资金id")
    private java.lang.String fundId;

    @ApiModelProperty(value = "逻辑删除")
    private java.lang.Integer delFlag;

    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;

    private String fundType;

    public FundDetail() {
    }

    public FundDetail(String customerId, BigDecimal amount, String type, String outTradeNo, String fundId, String fundType) {
        this.customerId = customerId;
        this.amount = amount;
        this.type = type;
        this.outTradeNo = outTradeNo;
        this.fundId = fundId;
        this.fundType = fundType;
    }

    @TableField(exist = false)
    private String customerName;
}
