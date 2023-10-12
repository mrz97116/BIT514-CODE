package com.dongxin.scm.om.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 合同明细表
 * @Author: jeecg-boot
 * @Date: 2020-12-14
 * @Version: V1.0
 */
@ApiModel(value = "om_contract对象", description = "合同主档表")
@Data
@TableName("om_contract_det")
public class ContractDet implements Serializable {
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
     * 标准
     */
    @Excel(name = "标准", width = 15)
    @ApiModelProperty(value = "标准")
    private java.lang.String sgStd;
    /**
     * 物料种类
     */
    @Excel(name = "物料种类", width = 15)
    @ApiModelProperty(value = "物料种类")
    private java.lang.String matKind;
    /**
     * 过磅价格
     */
    @Excel(name = "过磅价格", width = 15)
    @ApiModelProperty(value = "过磅价格")
    private java.math.BigDecimal weightingPrice;
    /**
     * 产品大类
     */
    @Excel(name = "产品大类", width = 15)
    @ApiModelProperty(value = "产品大类")
    private java.lang.String prodClassCode;
    /**
     * 加价
     */
    @Excel(name = "加价", width = 15)
    @ApiModelProperty(value = "加价")
    private java.math.BigDecimal addPrice;
    /**
     * 物料重量
     */
    @Excel(name = "物料重量", width = 15)
    @ApiModelProperty(value = "物料重量")
    private java.lang.Double weight;
    /**
     * 单价
     */
    @Excel(name = "单价", width = 15)
    @ApiModelProperty(value = "单价")
    private java.math.BigDecimal price;
    /**
     * 过磅重量
     */
    @Excel(name = "过磅重量", width = 15)
    @ApiModelProperty(value = "过磅重量")
    private java.lang.Double weightingWeight;
    /**
     * 计重方式
     */
    @Excel(name = "计重方式", width = 15)
    @ApiModelProperty(value = "计重方式")
    private java.lang.String weightType;
    /**
     * 合同编号
     */
    @Excel(name = "合同编号", width = 15)
    @ApiModelProperty(value = "合同编号")
    private java.lang.String contractNo;
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
     * 控款记录id
     */
    @Excel(name = "控款记录id", width = 15)
    @ApiModelProperty(value = "控款记录id")
    private java.lang.String controlRecordId;
    /**
     * 剩余重量
     */
    @Excel(name = "剩余重量", width = 15)
    @ApiModelProperty(value = "剩余重量")
    private java.lang.Double residualWeight;
    /**
     * 合同id
     */
    @ApiModelProperty(value = "合同id")
    private java.lang.String parentId;
    /**
     * 品名代码
     */
    @Excel(name = "品名代码", width = 15)
    @ApiModelProperty(value = "品名代码")
    private java.lang.String prodCode;
    /**
     * 牌号
     */
    @Excel(name = "牌号", width = 15)
    @ApiModelProperty(value = "牌号")
    private java.lang.String sgSign;
    /**
     * 总价
     */
    @Excel(name = "总价", width = 15)
    @ApiModelProperty(value = "总价")
    private java.math.BigDecimal totalPrice;
    /**
     * 交货期
     */
    @Excel(name = "交货期", width = 15, format = "yyyy-MM-dd")
    @ApiModelProperty(value = "交货期")
    private java.lang.String delivyDate;
    /**
     * 件数
     */
    @Excel(name = "件数", width = 15)
    @ApiModelProperty(value = "件数")
    private java.lang.Double qty;
    /**
     * 预交重量
     */
    @Excel(name = "预交重量", width = 15)
    @ApiModelProperty(value = "预交重量")
    private java.lang.Double preWeight;
    /**
     * 预交件数
     */
    @Excel(name = "预交件数", width = 15)
    @ApiModelProperty(value = "预交件数")
    private java.lang.Double preQty;
    /**
     * 已交重量
     */
    @Excel(name = "已交重量", width = 15)
    @ApiModelProperty(value = "已交重量")
    private java.lang.Double delivyWeight;
    /**
     * 已交件数
     */
    @Excel(name = "已交件数", width = 15)
    @ApiModelProperty(value = "已交件数")
    private java.lang.Double delivyQty;
    /**
     * 合同明细号
     */
    @Excel(name = "合同明细号", width = 15)
    @ApiModelProperty(value = "合同明细号")
    private java.lang.String contractListNo;
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
     * 订货重量单件目标值
     */
    @Excel(name = "订货重量单件目标值", width = 15)
    @ApiModelProperty(value = "订货重量单件目标值")
    private java.lang.Double goodWeightPieceTarget;
    /**
     * 订货重量单件最小值
     */
    @Excel(name = "订货重量单件最小值", width = 15)
    @ApiModelProperty(value = "订货重量单件最小值")
    private java.lang.Double goodWeightPieceMinimum;
    /**
     * 订货重量单件最大值
     */
    @Excel(name = "订货重量单件最大值", width = 15)
    @ApiModelProperty(value = "订货重量单件最大值")
    private java.lang.Double goodWeightPieceMaximum;
    /**
     * 合同偏差量
     */
    @Excel(name = "合同偏差量", width = 15)
    @ApiModelProperty(value = "合同偏差量")
    private java.lang.Double contractDeviation;
    /**
     * 订货最小厚度（直径）
    */
    @Excel(name = "订货最小厚度（直径）", width = 15)
    @ApiModelProperty(value = "订货最小厚度（直径）")
    private java.lang.Double goodMinimumThickness;
    /**
     * 订货最大厚度（直径）
     */
    @Excel(name = "订货最大厚度（直径）", width = 15)
    @ApiModelProperty(value = "订货最大厚度（直径）")
    private java.lang.Double goodMaximumThickness;
    /**
     * 订货最小宽度
     */
    @Excel(name = "订货最小宽度", width = 15)
    @ApiModelProperty(value = "订货最小宽度")
    private java.lang.Double goodMinimumWidth;
    /**
     * 订货最大宽度
     */
    @Excel(name = "订货最大宽度", width = 15)
    @ApiModelProperty(value = "订货最大宽度")
    private java.lang.Double goodMaximumWidth;
    /**
     * 合同类型
     */
    @Excel(name = "合同类型", width = 15)
    @ApiModelProperty(value = "合同类型")
    private java.lang.String contractType;
    /**
     * 已交金额
     */
    @Excel(name = "已交金额", width = 15)
    @ApiModelProperty(value = "已交金额")
    private java.math.BigDecimal delivyFund;
}
