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
 * @Description: 库存定时表
 * @Author: jeecg-boot
 * @Date:   2021-05-29
 * @Version: V1.0
 */
@Data
@TableName("sm_inventory_timing")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sm_inventory_timing对象", description="库存定时表")
public class InventoryTiming implements Serializable {
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
	/**期初库存*/
	@Excel(name = "期初库存", width = 15)
    @ApiModelProperty(value = "期初库存")
    private java.math.BigDecimal openingInventory;
	/**期末库存*/
	@Excel(name = "期末库存", width = 15)
    @ApiModelProperty(value = "期末库存")
    private java.math.BigDecimal endingInventory;
	/**物料编码*/
	@Excel(name = "物料编码", width = 15)
    @ApiModelProperty(value = "物料编码")
    private java.lang.String matCode;
	/**材料长度*/
	@Excel(name = "材料长度", width = 15)
    @ApiModelProperty(value = "材料长度")
    private java.lang.Double matLen;
	/**材料宽度*/
	@Excel(name = "材料宽度", width = 15)
    @ApiModelProperty(value = "材料宽度")
    private java.lang.Double matWidth;
	/**材料厚度*/
	@Excel(name = "材料厚度", width = 15)
    @ApiModelProperty(value = "材料厚度")
    private java.lang.Double matThick;
	/**逻辑删除*/
	@Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private java.lang.Integer delFlag;
	/**租户id*/
	@Excel(name = "租户id", width = 15)
    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
	/**规格*/
	@Excel(name = "规格", width = 15)
    @ApiModelProperty(value = "规格")
    private java.lang.String custMatSpecs;
	/**物料种类*/
	@Excel(name = "物料种类", width = 15)
    @ApiModelProperty(value = "物料种类")
    private java.lang.String matKind;
	/**材料总件数*/
	@Excel(name = "材料总件数", width = 15)
    @ApiModelProperty(value = "材料总件数")
    private java.lang.Double matNum;
	/**品名中文别名*/
	@Excel(name = "品名中文别名", width = 15)
    @ApiModelProperty(value = "品名中文别名")
    private java.lang.String prodCnameOther;
	/**产品大类*/
	@Excel(name = "产品大类", width = 15)
    @ApiModelProperty(value = "产品大类")
    private java.lang.String prodClassCode;
	/**牌号*/
	@Excel(name = "牌号", width = 15)
    @ApiModelProperty(value = "牌号")
    private java.lang.String sgSign;
	/**品名中文*/
	@Excel(name = "品名中文", width = 15)
    @ApiModelProperty(value = "品名中文")
    private java.lang.String prodCname;
	/**仓库id*/
	@Excel(name = "仓库id", width = 15)
    @ApiModelProperty(value = "仓库id")
    private java.lang.String stockHouseId;
	/**可销售重量*/
	@Excel(name = "可销售重量", width = 15)
    @ApiModelProperty(value = "可销售重量")
    private java.lang.Double availableWeight;
	/**可销售数量*/
	@Excel(name = "可销售数量", width = 15)
    @ApiModelProperty(value = "可销售数量")
    private java.lang.Integer availableQty;
	/**重量*/
	@Excel(name = "重量", width = 15)
    @ApiModelProperty(value = "重量")
    private java.lang.Double weight;
	/**材料号*/
	@Excel(name = "材料号", width = 15)
    @ApiModelProperty(value = "材料号")
    private java.lang.String matNo;
	/**单重*/
	@Excel(name = "单重", width = 15)
    @ApiModelProperty(value = "单重")
    private java.lang.Double pieceWeight;
	/**计重方式*/
	@Excel(name = "计重方式", width = 15)
    @ApiModelProperty(value = "计重方式")
    private java.lang.String wtMode;
	/**经销产品名称中文名*/
	@Excel(name = "经销产品名称中文名", width = 15)
    @ApiModelProperty(value = "经销产品名称中文名")
    private java.lang.String jxProdCname;
	/**SAP产品中文名*/
	@Excel(name = "SAP产品中文名", width = 15)
    @ApiModelProperty(value = "SAP产品中文名")
    private java.lang.String sapProdCname;
	/**旧系统产品中文名*/
	@Excel(name = "旧系统产品中文名", width = 15)
    @ApiModelProperty(value = "旧系统产品中文名")
    private java.lang.String oldProdCname;
	/**tmp*/
	@Excel(name = "tmp", width = 15)
    @ApiModelProperty(value = "tmp")
    private java.lang.Double tmp;
	/**库位*/
	@Excel(name = "库位", width = 15)
    @ApiModelProperty(value = "库位")
    private java.lang.String stockLocation;
	/**库存id*/
	@Excel(name = "库存id", width = 15)
    @ApiModelProperty(value = "库存id")
    private java.lang.String inventoryId;
	/**时间*/
	@Excel(name = "时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "时间")
    private java.util.Date time;
    /**期初库存重量*/
    @Excel(name = "期初库存重量", width = 15)
    @ApiModelProperty(value = "期初库存重量")
    private BigDecimal openingStockWeight;
    /**期初未结算重量*/
    @Excel(name = "期初未结算重量", width = 15)
    @ApiModelProperty(value = "期初未结算重量")
    private BigDecimal openingUnSettleWeight;
    /**期末库存重量*/
    @Excel(name = "期末库存重量", width = 15)
    @ApiModelProperty(value = "期末库存重量")
    private BigDecimal endingStockWeight;
    /**期末库存重量*/
    @Excel(name = "期末库存重量", width = 15)
    @ApiModelProperty(value = "期末库存重量")
    private BigDecimal endingUnSettleWeight;
}
