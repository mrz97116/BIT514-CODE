package com.dongxin.scm.om.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description: 销售单明细表
 * @Author: jeecg-boot
 * @Date: 2020-11-23
 * @Version: V1.0
 */
@ApiModel(value = "om_sales_order对象", description = "销售单")
@Data
@TableName("om_sales_order_det")
public class SalesOrderDet implements Serializable {
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
     * 制单时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "制单时间")
    private java.util.Date orderTime;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
    /**
     * 品名中文
     */
    @Excel(name = "品名中文", width = 15)
    @ApiModelProperty(value = "品名中文")
    private java.lang.String prodCname;
    /**
     * 品名中文别名
     */
    @Excel(name = "品名中文别名", width = 15)
    @ApiModelProperty(value = "品名中文别名")
    private java.lang.String prodCnameOther;
    /**
     * 提单号
     */
    @Excel(name = "提单号", width = 15)
    @ApiModelProperty(value = "提单号")
    private java.lang.String saleBillNo;
    /**
     * 订单条目id
     */
    @Excel(name = "订单条目id", width = 15)
    @ApiModelProperty(value = "订单条目id")
    private java.lang.String orderItemNo;
    /**
     * 数量
     */
    @Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private java.lang.Double qty;
    /**
     * 重量
     */
    @Excel(name = "重量", width = 15)
    @ApiModelProperty(value = "重量")
    private java.lang.Double weight;
    /**
     * 单价
     */
    @Excel(name = "单价", width = 15)
    @ApiModelProperty(value = "单价")
    private java.math.BigDecimal price;
    /**
     * 总价
     */
    @Excel(name = "总价", width = 15)
    @ApiModelProperty(value = "总价")
    private java.math.BigDecimal totalPrice;
    /**
     * 订货长度
     */
    @Excel(name = "订货长度", width = 15)
    @ApiModelProperty(value = "订货长度")
    private java.lang.Double orderLen;
    /**
     * 订货宽度
     */
    @Excel(name = "订货宽度", width = 15)
    @ApiModelProperty(value = "订货宽度")
    private java.lang.Double orderWidth;
    /**
     * 订货厚度
     */
    @Excel(name = "订货厚度", width = 15)
    @ApiModelProperty(value = "订货厚度")
    private java.lang.Double orderThick;
    /**
     * 规格
     */
    @Excel(name = "规格", width = 15)
    @ApiModelProperty(value = "规格")
    private java.lang.String custMatSpecs;
    /**
     * 标准
     */
    @Excel(name = "标准", width = 15)
    @ApiModelProperty(value = "标准")
    private java.lang.String sgStd;
    /**
     * 物料种类
     */
    @Excel(name = "物料种类", width = 15, dicCode = "mat_kind")
    @Dict(dicCode = "mat_kind")
    @ApiModelProperty(value = "物料种类")
    private java.lang.String matKind;
    /**
     * 产品大类
     */
    @Excel(name = "产品大类", width = 15, dicCode = "prod_code")
    @Dict(dicCode = "prod_code")
    @ApiModelProperty(value = "产品大类")
    private java.lang.String prodClassCode;
    /**
     * 租户id
     */

    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
    /**
     * 逻辑删除
     */
    @Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private java.lang.Integer delFlag;
    /**
     * 销售单id
     */
    @ApiModelProperty(value = "销售单id")
    private java.lang.String parentId;
    /**
     * 剩余的重量
     */
    @Excel(name = "剩余的重量", width = 15)
    @ApiModelProperty(value = "剩余的重量")
    private java.lang.Double residualWeight;
    /**
     * 扣款Id
     */
    @Excel(name = "扣款Id", width = 15)
    @ApiModelProperty(value = "扣款Id")
    private java.lang.String controlRecordId;
    /**
     * 牌号
     */
    @Excel(name = "牌号", width = 15)
    @ApiModelProperty(value = "牌号")
    private java.lang.String sgSign;
    /**
     * 品名代码
     */
    @Excel(name = "品名代码", width = 15)
    @ApiModelProperty(value = "品名代码")
    private java.lang.String prodCode;
    /**
     * 已交货重量
     */
    @Excel(name = "已交货重量", width = 15)
    @ApiModelProperty(value = "已交货重量")
    private java.lang.Double deliverWeight;
    /**
     * 计重方式
     */
    @Excel(name = "计重方式", width = 15)
    @ApiModelProperty(value = "计重方式")
    private java.lang.String wtMode;
    /**
     * 加价
     */
    @Excel(name = "加价", width = 15)
    @ApiModelProperty(value = "加价")
    private java.math.BigDecimal addPrice;
    /**
     * 过磅重量
     */
    @Excel(name = "过磅重量", width = 15)
    @ApiModelProperty(value = "过磅重量")
    private java.lang.Double weightingWeight;
    /**
     * 过磅价格
     */
    @Excel(name = "过磅价格", width = 15)
    @ApiModelProperty(value = "过磅价格")
    private java.math.BigDecimal weightingPrice;
    /**
     * 材料号
     */
    @Excel(name = "材料号", width = 15)
    @ApiModelProperty(value = "材料号")
    private java.lang.String matNo;
    /**
     * 材料理论重量
     */
    @Excel(name = "材料理论重量", width = 15)
    @ApiModelProperty(value = "材料理论重量")
    private java.lang.Double matTheoryWt;
    /**
     * 库存ID
     */
    @Excel(name = "库存ID", width = 15)
    @ApiModelProperty(value = "库存ID")
    private java.lang.String inventoryId;
    /**
     * 锌层重量
     */
    @Excel(name = "锌层重量", width = 15)
    @ApiModelProperty(value = "锌层重量")
    private String platingWeight;
    /**
     * 表面处理方式(钝化方式)
     */
    @Excel(name = "钝化方式", width = 15)
    @ApiModelProperty(value = "钝化方式")
    private String surfaceTreatment;
    /**
     * 合同明细号
     */
    @Excel(name = "合同明细号", width = 15)
    @ApiModelProperty(value = "合同明细号")
    private java.lang.String contractListNo;
    /**
     * 仓库id
     */
    @Excel(name = "仓库id", width = 15, dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @Dict(dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "仓库id")
    private java.lang.String stockId;
    /**
     * 旧系统产品中文名
     */
    @Excel(name = "旧系统产品中文名", width = 15)
    @ApiModelProperty(value = "旧系统产品中文名")
    private java.lang.String oldProdCname;
    /**
     * 经销产品名称中文名
     */
    @Excel(name = "经销产品名称中文名", width = 15)
    @ApiModelProperty(value = "经销产品名称中文名")
    private java.lang.String jxProdCname;
    /**
     * SAP产品中文名
     */
    @Excel(name = "SAP产品中文名", width = 15)
    @ApiModelProperty(value = "SAP产品中文名")
    private java.lang.String sapProdCname;
    /**
     * 仓库名称
     */
    @Excel(name = "仓库名称", width = 15)
    @ApiModelProperty(value = "仓库名称")
    private java.lang.String stockName;
    /**
     * 库位
     */
    @Excel(name = "库位", width = 15)
    @ApiModelProperty(value = "库位")
    private java.lang.String stockLocation;
    /**
     * 加价
     */
    @Excel(name = "运费", width = 15)
    @ApiModelProperty(value = "运费")
    private java.math.BigDecimal deliverExpense;
    /**
     * 运费总价
     */
    @Excel(name = "运费总价", width = 15)
    @ApiModelProperty(value = "运费总价")
    private java.math.BigDecimal deliverTotalPrice;
    /**
     * 车队名称
     */
    @Excel(name = "车队名称", width = 15)
    @Dict(dictTable = "sm_fleet", dicText = "fleet_name", dicCode = "id")
    @ApiModelProperty(value = "车队名称")
    private java.lang.String fleetName;
    /**
     * 加价
     */
    @Excel(name = "车队运费", width = 15)
    @ApiModelProperty(value = "车队运费")
    private java.math.BigDecimal fleetDeliverExpense;
    /**
     * 运费总价
     */
    @Excel(name = "车队总价", width = 15)
    @ApiModelProperty(value = "车队总价")
    private java.math.BigDecimal fleetTotalPrice;
    /**
     * 基础单价
     */
    @Excel(name = "基础单价", width = 15)
    @ApiModelProperty(value = "基础单价")
    private java.math.BigDecimal basicPrice;
    @TableField(exist = false)
    private String stackDetId;

    private Double originWeight;

    private Double originQty;

    @TableField(exist = false)
    private BigDecimal theoryWeight;
    @TableField(exist = false)
    private BigDecimal theoryPrice;
    @TableField(exist = false)
    private BigDecimal weightBigDecimal;

    /**
     * 单位(数量)
     */
    private String unit;
    /**
     * 合同明细id
     */
    @Excel(name = "合同明细id", width = 15)
    @ApiModelProperty(value = "合同明细id")
    private String contractDetId;
    /**
     * 合同明细号
     */
    @Excel(name = "合同明细号", width = 15)
    @ApiModelProperty(value = "合同明细号")
    private String contractDetNo;
}
