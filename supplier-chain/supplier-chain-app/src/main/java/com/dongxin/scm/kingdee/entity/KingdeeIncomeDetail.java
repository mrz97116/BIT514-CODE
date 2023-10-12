package com.dongxin.scm.kingdee.entity;

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
 * @Description: 金蝶资金明细表
 * @Author: jeecg-boot
 * @Date:   2021-03-26
 * @Version: V1.0
 */
@Data
@TableName("kingdee_income_detail")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="kingdee_income_detail对象", description="金蝶资金明细表")
public class KingdeeIncomeDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**单据号*/
	@Excel(name = "单据号", width = 15)
    @ApiModelProperty(value = "单据号")
    private java.lang.String billNo;
	/**收付款日期*/
	@Excel(name = "收付款日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "收付款日期")
    private java.util.Date date;
	/**客户id*/
	@Excel(name = "客户id", width = 15)
    @ApiModelProperty(value = "客户id")
    private java.lang.String customerId;
	/**客户名称*/
	@Excel(name = "客户名称", width = 15)
    @ApiModelProperty(value = "客户名称")
    private java.lang.String customerName;
	/**结算方式*/
	@Excel(name = "结算方式", width = 15)
    @ApiModelProperty(value = "结算方式")
    private java.lang.String settleType;
	/**结算单号*/
	@Excel(name = "结算单号", width = 15)
    @ApiModelProperty(value = "结算单号")
    private java.lang.String settleNo;
	/**应收/付金额*/
	@Excel(name = "应收/付金额", width = 15)
    @ApiModelProperty(value = "应收/付金额")
    private java.math.BigDecimal amount;
	/**已核销金额*/
	@Excel(name = "已核销金额", width = 15)
    @ApiModelProperty(value = "已核销金额")
    private java.math.BigDecimal writeOffAmount;
	/**实际收付金额*/
	@Excel(name = "实际收付金额", width = 15)
    @ApiModelProperty(value = "实际收付金额")
    private java.math.BigDecimal actualAmount;
	/**类型*/
	@Excel(name = "类型", width = 15)
    @ApiModelProperty(value = "类型")
    private java.lang.String type;
	/**租户id*/
	@Excel(name = "租户id", width = 15)
    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
}
