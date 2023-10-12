package com.dongxin.scm.cm.entity;

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
 * @Description: 授信额度表
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */
@Data
@TableName("cm_customer_credit")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "cm_customer_credit对象", description = "授信额度表")
public class CustomerCredit implements Serializable {
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
    @Excel(name = "顾客", width = 15, dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @Dict(dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @ApiModelProperty(value = "顾客")
    private java.lang.String customerId;
    /**
     * 额度
     */
    @Excel(name = "额度", width = 15)
    @ApiModelProperty(value = "额度")
    private java.math.BigDecimal amount;
    /**
     * 剩余可用额度
     */
    @Excel(name = "剩余可用额度", width = 15)
    @ApiModelProperty(value = "剩余可用额度")
    private java.math.BigDecimal availableAmount;
    /**
     * 授信编号
     */
    @Excel(name = "授信编号", width = 15)
    @ApiModelProperty(value = "授信编号")
    private java.lang.String creditNo;
    /**
     * 授信类型
     */
    @Excel(name = "授信类型", width = 15, dicCode = "credit_type")
    @Dict(dicCode = "credit_type")
    @ApiModelProperty(value = "授信类型")
    private java.lang.String type;
    /**
     * 结束时间
     */
    @Excel(name = "结束时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "结束时间")
    private java.util.Date endTime;
    /**
     * 账户类型
     */
    @Excel(name = "账户类型", width = 15)
    @ApiModelProperty(value = "账户类型")
    private java.lang.String accountType;
    /**
     * 审核状态
     */
    @Excel(name = "审核状态", width = 15, dicCode = "common_check_status")
    @Dict(dicCode = "common_check_status")
    @ApiModelProperty(value = "审核状态")
    private java.lang.String status;
    /**
     * 是否滚动
     */
    @Excel(name = "是否滚动", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否滚动")
    private java.lang.String scroll;
    /**
     * 产品大类
     */
    @Excel(name = "产品大类", width = 15, dicCode = "prod_code")
    @Dict(dicCode = "prod_code")
    @ApiModelProperty(value = "产品大类")
    private java.lang.String prodCode;
    /**
     * 多租户id
     */
    @Excel(name = "多租户id", width = 15)
    @ApiModelProperty(value = "多租户id")
    private java.lang.Integer tenantId;
    /**
     * 删除标识
     */
    @Excel(name = "删除标识", width = 15)
    @ApiModelProperty(value = "删除标识")
    private java.lang.Integer delFlag;
}
