package com.dongxin.scm.fm.entity;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 应收账款
 * @Author: jeecg-boot
 * @Date: 2021-06-09
 * @Version: V1.0
 */
@Data
@TableName("fm_fund_accountreceivable")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "fm_fund_accountreceivable对象", description = "应收账款")
public class FundAccountreceivable implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
    /**
     * 更新人
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
    /**
     * 客户id
     */
    @Excel(name = "客户id", width = 15, dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @Dict(dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @ApiModelProperty(value = "客户id")
    private String customerId;
    /**
     * 总金额
     */
    @Excel(name = "总金额", width = 15)
    @ApiModelProperty(value = "总金额")
    private BigDecimal totalAmount;
    /**
     * 已收金额
     */
    @Excel(name = "已收金额", width = 15)
    @ApiModelProperty(value = "已收金额")
    private BigDecimal receivedAmount;
    /**
     * 待收金额
     */
    @Excel(name = "待收金额", width = 15)
    @ApiModelProperty(value = "待收金额")
    private BigDecimal pendingAmount;
    /**
     * 状态
     */
    @Excel(name = "状态", width = 15, dicCode = "fundaccountreceivable_status")
    @Dict(dicCode = "fundaccountreceivable_status")
    @ApiModelProperty(value = "状态")
    private String status;
    /**
     * 类型
     */
    @Excel(name = "类型", width = 15, dicCode = "fundaccountreceivable_type")
    @Dict(dicCode = "fundaccountreceivable_type")
    @ApiModelProperty(value = "类型")
    private String type;
    /**
     * 流水号
     */
    @Excel(name = "流水号", width = 15)
    @ApiModelProperty(value = "流水号")
    private String serialNo;

    /**
     * 外部单据号
     */
    @Excel(name = "外部单据号", width = 15)
    @ApiModelProperty(value = "外部单据号")
    private String outTradeNo;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String comment;

    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
    /**
     * 删除标志
     */
//    @Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
    private java.lang.Integer delFlag;
}
