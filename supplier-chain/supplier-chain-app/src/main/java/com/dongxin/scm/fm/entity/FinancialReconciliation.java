package com.dongxin.scm.fm.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.Version;
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
 * @Description: 财务对账
 * @Author: jeecg-boot
 * @Date:   2021-04-18
 * @Version: V1.0
 */
@Data
@TableName("fm_financial_reconciliation")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fm_financial_reconciliation对象", description="财务对账")
public class FinancialReconciliation implements Serializable {
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
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
	/**客户id*/
    @Excel(name = "客户", width = 15, dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @Dict(dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @ApiModelProperty(value = "客户")
    private java.lang.String customerId;
	/**时间*/
	@Excel(name = "时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "时间")
    private java.util.Date date;
	/**期末贷方金额*/
	@Excel(name = "期末贷方金额", width = 15)
    @ApiModelProperty(value = "期末贷方金额")
    private java.math.BigDecimal endingCreditAccount;
	/**逻辑删除*/
	@Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private java.lang.Integer delFlag;
    /**租户id*/
    @Excel(name = "租户id", width = 15)
    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
	/**期末贷方*/
    @Excel(name = "期末余额贷方", width = 15)
    @TableField(exist = false)
    private java.lang.String endingCreditAccountString;
    /**本期发生借方*/
    @Excel(name = "本期发生额贷方", width = 15)
    @TableField(exist = false)
    private java.lang.String paymentAccountString;
    /**本期发生贷方*/
    @Excel(name = "本期发生额借方", width = 15)
    @TableField(exist = false)
    private java.lang.String settleAccountString;
    /**来款金额*/
    @Excel(name = "来款金额", width = 15)
    @ApiModelProperty(value = "来款金额")
    private java.math.BigDecimal paymentAccount;
    /**结算金额*/
    @Excel(name = "结算金额", width = 15)
    @ApiModelProperty(value = "结算金额")
    private java.math.BigDecimal settleAccount;

}
