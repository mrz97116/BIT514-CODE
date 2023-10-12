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
 * @Description: 财务库存余额
 * @Author: jeecg-boot
 * @Date:   2021-05-18
 * @Version: V1.0
 */
@Data
@TableName("fm_finance_stock_balance")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fm_finance_stock_balance对象", description="财务库存余额")
public class FinanceStockBalance implements Serializable {
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
	/**租户id*/
	@Excel(name = "租户id", width = 15)
    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
	/**逻辑删除*/
	@Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private java.lang.Integer delFlag;
	/**财务品名*/
	@Excel(name = "财务品名", width = 15)
    @ApiModelProperty(value = "财务品名")
    private java.lang.String financeProductName;
	/**品类*/
	@Excel(name = "品类", width = 15)
    @ApiModelProperty(value = "品类")
    private java.lang.String category;
	/**生产线*/
	@Excel(name = "生产线", width = 15)
    @ApiModelProperty(value = "生产线")
    private java.lang.String productionLine;
	/**钢号*/
	@Excel(name = "钢号", width = 15)
    @ApiModelProperty(value = "钢号")
    private java.lang.String steelGrade;
	/**月初库存量*/

    @ApiModelProperty(value = "月初库存量")
    private java.math.BigDecimal beginningInventoryWeight;
	/**本月入库量*/

    @ApiModelProperty(value = "本月入库量")
    private java.math.BigDecimal monthWarehousingWeight;
	/**本月销售量*/

    @ApiModelProperty(value = "本月销售量")
    private java.math.BigDecimal monthSaleWeight;
	/**月末库存量*/

    @ApiModelProperty(value = "月末库存量")
    private java.math.BigDecimal endMonthInventoryWeight;
	/**时间*/
	@Excel(name = "时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "时间")
    private java.util.Date time;
	/**物料编号*/
	@Excel(name = "物料编号", width = 15)
    @ApiModelProperty(value = "物料编号")
    private java.lang.String materialCode;

    @Excel(name = "月初库存量", width = 15)
    @TableField(exist = false)
	private java.lang.String beginningInventoryWeightString;

    @Excel(name = "本月入库量", width = 15)
    @TableField(exist = false)
	private java.lang.String monthWarehousingWeightString;

    @Excel(name = "本月销售量", width = 15)
    @TableField(exist = false)
	private java.lang.String monthSaleWeightString;

    @Excel(name = "月末库存量", width = 15)
    @TableField(exist = false)
	private java.lang.String endMonthInventoryWeightString;
}
