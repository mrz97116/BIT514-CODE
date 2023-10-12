package com.dongxin.scm.sm.entity;

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
 * @Description: 客户月度未结算量汇总统计
 * @Author: jeecg-boot
 * @Date:   2021-10-14
 * @Version: V1.0
 */
@Data
@TableName("sm_unsettle_summary")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sm_unsettle_summary对象", description="客户月度未结算量汇总统计")
public class UnsettleSummary implements Serializable {
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
	/**顾客id*/
	@Excel(name = "顾客id", width = 15)
    @ApiModelProperty(value = "顾客id")
    private java.lang.String customerId;
	/**月份*/
	@Excel(name = "月份", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "月份")
    private java.util.Date month;
	/**棒线重量*/
	@Excel(name = "棒线重量", width = 15)
    @ApiModelProperty(value = "棒线重量")
    private java.math.BigDecimal bxWeight;
	/**棒线金额*/
	@Excel(name = "棒线金额", width = 15)
    @ApiModelProperty(value = "棒线金额")
    private java.math.BigDecimal bxAmount;
	/**高线重量*/
	@Excel(name = "高线重量", width = 15)
    @ApiModelProperty(value = "高线重量")
    private java.math.BigDecimal gxWeight;
	/**高线金额*/
	@Excel(name = "高线金额", width = 15)
    @ApiModelProperty(value = "高线金额")
    private java.math.BigDecimal gxAmount;
	/**中板材重量*/
	@Excel(name = "中板材重量", width = 15)
    @ApiModelProperty(value = "中板材重量")
    private java.math.BigDecimal zbcWeight;
	/**中板材金额*/
	@Excel(name = "中板材金额", width = 15)
    @ApiModelProperty(value = "中板材金额")
    private java.math.BigDecimal zbcAmount;
	/**热轧重量*/
	@Excel(name = "热轧重量", width = 15)
    @ApiModelProperty(value = "热轧重量")
    private java.math.BigDecimal rzWeight;
	/**热轧金额*/
	@Excel(name = "热轧金额", width = 15)
    @ApiModelProperty(value = "热轧金额")
    private java.math.BigDecimal rzAmount;
	/**冷轧重量*/
	@Excel(name = "冷轧重量", width = 15)
    @ApiModelProperty(value = "冷轧重量")
    private java.math.BigDecimal lzWeight;
	/**冷轧金额*/
	@Excel(name = "冷轧金额", width = 15)
    @ApiModelProperty(value = "冷轧金额")
    private java.math.BigDecimal lzAmount;
	/**中型重量*/
	@Excel(name = "中型重量", width = 15)
    @ApiModelProperty(value = "中型重量")
    private java.math.BigDecimal zxWeight;
	/**中型金额*/
	@Excel(name = "中型金额", width = 15)
    @ApiModelProperty(value = "中型金额")
    private java.math.BigDecimal zxAmount;
	/**外购材重量*/
	@Excel(name = "外购材重量", width = 15)
    @ApiModelProperty(value = "外购材重量")
    private java.math.BigDecimal wgcWeight;
	/**外购材金额*/
	@Excel(name = "外购材金额", width = 15)
    @ApiModelProperty(value = "外购材金额")
    private java.math.BigDecimal wgcAmount;
	/**自用材重量*/
	@Excel(name = "自用材重量", width = 15)
    @ApiModelProperty(value = "自用材重量")
    private java.math.BigDecimal zycWeight;
	/**自用材金额*/
	@Excel(name = "自用材金额", width = 15)
    @ApiModelProperty(value = "自用材金额")
    private java.math.BigDecimal zycAmount;
	/**删除标识*/
	@Excel(name = "删除标识", width = 15)
    @ApiModelProperty(value = "删除标识")
    private java.lang.Integer delFlag;
	/**租户id*/
	@Excel(name = "租户id", width = 15)
    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
	/**总重量*/
	@Excel(name = "总重量", width = 15)
    @ApiModelProperty(value = "总重量")
    private java.math.BigDecimal totalWeight;
	/**总金额*/
	@Excel(name = "总金额", width = 15)
    @ApiModelProperty(value = "总金额")
    private java.math.BigDecimal totalAmount;
}
