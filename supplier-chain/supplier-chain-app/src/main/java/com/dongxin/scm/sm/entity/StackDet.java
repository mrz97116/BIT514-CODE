package com.dongxin.scm.sm.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 装车单明细表
 * @Author: jeecg-boot
 * @Date: 2020-12-01
 * @Version: V1.0
 */
@ApiModel(value = "sm_stack对象", description = "装车单明细表")
@Data
@TableName("sm_stack_det")
public class StackDet implements Serializable {
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
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
    /**
     * 装车单号
     */
    @Excel(name = "装车单号", width = 15)
    @ApiModelProperty(value = "装车单号")
    private String stackingNo;
    /**
     * 销售单号
     */
    @Excel(name = "提单单号", width = 15)
    @ApiModelProperty(value = "提单单号")
    private String saleNoId;
    /**
     * 品名代码
     */
    @Excel(name = "品名代码", width = 15)
    @ApiModelProperty(value = "品名代码")
    private String prodCode;
    /**
     * 装车单id
     */
    @ApiModelProperty(value = "装车单id")
    private String stackId;
    /**
     * 租户id
     */

    @ApiModelProperty(value = "租户id")
    private String tenantId;
    /**
     * 合同明细id
     */
    @Excel(name = "合同明细号", width = 15)
    @ApiModelProperty(value = "合同明细号")
    private String contractListNo;
    /**
     * 品名中文
     */
    @Excel(name = "品名中文", width = 15)
    @ApiModelProperty(value = "品名中文")
    private String prodCname;
    /**
     * 品名中文别名
     */
    @Excel(name = "品名中文别名", width = 15)
    @ApiModelProperty(value = "品名中文别名")
    private String prodCnameOther;
    /**
     * 数量
     */
    @Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private Double qty;
    /**
     * 重量
     */
    @Excel(name = "重量", width = 15)
    @ApiModelProperty(value = "重量")
    private Double weight;
    /**
     * 单价
     */
    @Excel(name = "单价", width = 15)
    @ApiModelProperty(value = "单价")
    private java.math.BigDecimal price;
    /**
     * 总金额
     */
    @Excel(name = "总金额", width = 15)
    @ApiModelProperty(value = "总金额")
    private java.math.BigDecimal totalAmount;
    /**
     * 材料长度
     */
    @Excel(name = "材料长度", width = 15)
    @ApiModelProperty(value = "材料长度")
    private Double matLen;
    /**
     * 材料宽度
     */
    @Excel(name = "材料宽度", width = 15)
    @ApiModelProperty(value = "材料宽度")
    private Double matWidth;
    /**
     * 材料厚度
     */
    @Excel(name = "材料厚度", width = 15)
    @ApiModelProperty(value = "材料厚度")
    private Double matThick;
    /**
     * 牌号
     */
    @Excel(name = "牌号", width = 15)
    @ApiModelProperty(value = "牌号")
    private String sgSign;
    /**
     * 计重方式
     */
    @Excel(name = "计重方式", width = 15)
    @ApiModelProperty(value = "计重方式")
    private String wtMode;
    /**
     * 过磅重量
     */
    @Excel(name = "过磅重量", width = 15)
    @ApiModelProperty(value = "过磅重量")
    private Double weightingWeight;
    /**
     * 过磅价格
     */
    @Excel(name = "过磅价格", width = 15)
    @ApiModelProperty(value = "过磅价格")
    private Double weightingPrice;
    /**
     * 加价
     */
    @Excel(name = "加价", width = 15)
    @ApiModelProperty(value = "加价")
    private Double addPrice;
    /**
     * 物料种类
     */
    @Excel(name = "物料种类", width = 15)
    @Dict(dicCode = "mat_kind")
    @ApiModelProperty(value = "物料种类")
    private String matKind;
    /**
     * 产品大类
     */
    @Excel(name = "产品大类", width = 15)
    @Dict(dicCode = "prod_code")
    @ApiModelProperty(value = "产品大类")
    private String prodClassCode;
    /**
     * 标准
     */
    @Excel(name = "标准", width = 15)
    @ApiModelProperty(value = "标准")
    private String sgStd;
    //	/**是否结算*/
//	@Excel(name = "是否结算", width = 15)
//	@ApiModelProperty(value = "是否结算")
//	private String settled;
//	/**结算id*/
//	@Excel(name = "结算id", width = 15)
//	@ApiModelProperty(value = "结算id")
//	private String settleId;
//	/**提单明细id*/
    @Excel(name = "提单明细id", width = 15)
    @ApiModelProperty(value = "提单明细id")
    private java.lang.String salesOrderDetId;
    /**
     * 材料理论重量
     */
    @Excel(name = "材料理论重量", width = 15)
    @ApiModelProperty(value = "材料理论重量")
    private java.lang.Double matTheoryWt;
    /**
     * 材料号
     */
    @Excel(name = "材料号", width = 15)
    @ApiModelProperty(value = "材料号")
    private java.lang.String matNo;
    /**
     * 逻辑删除
     */
    @Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private Integer delFlag;
    /**
     * 库存ID
     */
    @Excel(name = "库存ID", width = 15)
    @ApiModelProperty(value = "库存ID")
    private java.lang.String inventoryId;
    /**
     * 优惠后的单价
     */
    @Excel(name = "优惠后的单价", width = 15)
    @ApiModelProperty(value = "优惠后的单价")
    @TableField(updateStrategy= FieldStrategy.IGNORED)
    private java.math.BigDecimal discountPrice;
    /**
     * 优惠后的总价
     */
    @Excel(name = "优惠后的总价", width = 15)
    @ApiModelProperty(value = "优惠后的总价")
    @TableField(updateStrategy= FieldStrategy.IGNORED)
    private java.math.BigDecimal discountTotalAmount;

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
     * 库位
     */
    @Excel(name = "车队名称", width = 15)
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
    /**
     * 理论重量
     */
    @Excel(name = "理论重量", width = 15)
    @ApiModelProperty(value = "理论重量")
    private java.math.BigDecimal theoryWeight;
    /**
     * 理论单价
     */
    @Excel(name = "理论单价", width = 15)
    @ApiModelProperty(value = "理论单价")
    private java.math.BigDecimal theoryPrice;
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
     * 单位(数量)
     */
    private String unit;

}
