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

/**
 * @Description: 授信额度表
 * @Author: jeecg-boot
 * @Date: 2020-10-27
 * @Version: V1.0
 */
@Data
@TableName("fm_credit")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "fm_credit对象", description = "授信额度表")
public class Credit implements Serializable {
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
     * 顾客Id
     */
    @Excel(name = "顾客Id", width = 15, dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @Dict(dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @ApiModelProperty(value = "顾客Id")
    private java.lang.String customerId;
    /**
     * 额度
     */
    @Excel(name = "额度", width = 15)
    @ApiModelProperty(value = "额度")
    private java.math.BigDecimal amount;
    /**
     * 剩余可用金额
     */
    @Excel(name = "剩余可用金额", width = 15)
    @ApiModelProperty(value = "剩余可用金额")
    private java.math.BigDecimal avatlableAmount;
    /**
     * 授信编号
     */
    @Excel(name = "授信编号", width = 15)
    @ApiModelProperty(value = "授信编号")
    private java.lang.String creditNo;

    /**
     * 生效标识
     */
    @Excel(name = "生效标识", width = 15, dicCode = "valid_status")
    @Dict(dicCode = "valid_status")
    @ApiModelProperty(value = "生效标识")
    private java.lang.String active;
    /**
     * 审核状态
     */
    @Excel(name = "审核状态", width = 15, dicCode = "common_check_status")
    @Dict(dicCode = "common_check_status")
    @ApiModelProperty(value = "审核状态")
    private java.lang.String status;
    /**
     * 审核人
     */
    @Excel(name = "审核人", width = 15)
    @ApiModelProperty(value = "审核人")
    private java.lang.String checker;
    /**
     * 审核日期
     */
    @Excel(name = "审核日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "审核日期")
    private java.util.Date checkDate;

    /**
     * 顾客id
     */
    @Excel(name = "顾客名称", width = 15)
    @ApiModelProperty(value = "顾客名称")
    @TableField(exist = false)
    private java.lang.String customerText;
    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;

    /**
     * 新可用金额
     */
    @Excel(name = "新可用金额", width = 15)
    @ApiModelProperty(value = "新可用金额")
    private java.math.BigDecimal availAmount;

    /**
     * 逻辑删除标识
     */
    @Excel(name = "逻辑删除标识", width = 15)
    @ApiModelProperty(value = "逻辑删除标识")
    private java.lang.Integer delFlag;

}
