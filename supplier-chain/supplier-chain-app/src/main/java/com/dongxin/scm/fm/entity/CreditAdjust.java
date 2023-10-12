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
 * @Description: 授信调整
 * @Author: jeecg-boot
 * @Date:   2022-01-24
 * @Version: V1.0
 */
@Data
@TableName("fm_credit_adjust")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fm_credit_adjust对象", description="授信调整")
public class CreditAdjust implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**删除标识*/
    @ApiModelProperty(value = "删除标识")
    private Integer delFlag;
	/**租户id*/
	@Excel(name = "租户id", width = 15)
    @ApiModelProperty(value = "租户id")
    private Integer tenantId;
    /**顾客Id*/
    @Excel(name = "顾客Id", width = 15, dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @Dict(dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @ApiModelProperty(value = "顾客Id")
    private String customerId;
	/**授信编号*/
	@Excel(name = "授信编号", width = 15)
    @ApiModelProperty(value = "授信编号")
    private String creditNo;
	/**审核状态*/
	@Excel(name = "审核状态", width = 15, dicCode = "common_check_status")
    @Dict(dicCode = "common_check_status")
    @ApiModelProperty(value = "审核状态")
    private String status;
	/**审核人*/
	@Excel(name = "审核人", width = 15)
    @ApiModelProperty(value = "审核人")
    private String checker;
	/**审核时间*/
	@Excel(name = "审核时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "审核时间")
    private Date checkDate;
	/**调整类型*/
	@Excel(name = "调整类型", width = 15, dicCode = "credit_adjust_status")
    @Dict(dicCode = "credit_adjust_status")
    @ApiModelProperty(value = "调整类型")
    private String adjustType;
	/**调整额度*/
	@Excel(name = "调整额度", width = 15)
    @ApiModelProperty(value = "调整额度")
    private BigDecimal adjustAmount;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
	/**授信id*/
	@Excel(name = "授信id", width = 15)
    @ApiModelProperty(value = "授信id")
    private String creditId;
}
