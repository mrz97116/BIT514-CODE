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
 * @Description: 货物调拨表
 * @Author: jeecg-boot
 * @Date:   2021-08-08
 * @Version: V1.0
 */
@Data
@TableName("sm_inventory_allot")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sm_inventory_allot对象", description="货物调拨表")
public class InventoryAllot implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    @Dict(dictTable = "sys_user",dicText = "realname",dicCode = "username")
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
	/**调拨单号*/
	@Excel(name = "调拨单号", width = 15)
    @ApiModelProperty(value = "调拨单号")
    private java.lang.String allotNum;
	/**发货仓*/
	@Excel(name = "发货仓", width = 15)
    @Dict(dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "发货仓")
    private java.lang.String allotStockHouseId;
	/**接收仓*/
	@Excel(name = "接收仓", width = 15)
    @Dict(dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "接收仓")
    private java.lang.String receiveStockHouseId;
	/**库位*/
	@Excel(name = "库位", width = 15)
    @ApiModelProperty(value = "库位")
    private java.lang.String stockLocation;
	/**调拨数量合计*/
	@Excel(name = "调拨数量合计", width = 15)
    @ApiModelProperty(value = "调拨数量合计")
    private java.lang.Double allotNumberSum;
	/**调拨重量合计*/
	@Excel(name = "调拨重量合计", width = 15)
    @ApiModelProperty(value = "调拨重量合计")
    private java.lang.Double allotWeightSum;
	/**调拨时间*/
	@Excel(name = "调拨时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "调拨时间")
    private java.util.Date allotCreateTime;
	/**牌号*/
	@Excel(name = "牌号", width = 15)
    @ApiModelProperty(value = "牌号")
    private java.lang.String sgSign;
	/**材料号*/
	@Excel(name = "材料号", width = 15)
    @ApiModelProperty(value = "材料号")
    private java.lang.String matNo;
	/**材料长*/
	@Excel(name = "材料长", width = 15)
    @ApiModelProperty(value = "材料长")
    private java.lang.Double matLen;
	/**材料宽*/
	@Excel(name = "材料宽", width = 15)
    @ApiModelProperty(value = "材料宽")
    private java.lang.Double matWidth;
	/**材料厚*/
	@Excel(name = "材料厚", width = 15)
    @ApiModelProperty(value = "材料厚")
    private java.lang.Double matChick;
	/**品名中文*/
	@Excel(name = "品名中文", width = 15)
    @ApiModelProperty(value = "品名中文")
    private java.lang.String prodCname;
	/**卸货人名字*/
	@Excel(name = "卸货人名字", width = 15)
    @ApiModelProperty(value = "卸货人名字")
    private java.lang.String dischargerName;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remarks;
    /**租户ID*/
    @Excel(name = "租户ID", width = 15)
    @ApiModelProperty(value = "租户ID")
    private java.lang.Integer tenantId;
    /**调拨方ID*/
    @Excel(name = "调拨方ID", width = 15)
    @ApiModelProperty(value = "调拨方ID")
    private java.lang.String allotId;
    /**接收方ID*/
    @Excel(name = "接收方ID", width = 15)
    @ApiModelProperty(value = "接收方ID")
    private java.lang.String receiveId;
    /**逻辑删除标识*/
    @Excel(name = "逻辑删除标识", width = 15)
    @ApiModelProperty(value = "接收方ID")
    private java.lang.Integer delFlag;
}
