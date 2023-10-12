package com.dongxin.scm.fm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 发票码信息表
 * @Author: jeecg-boot
 * @Date: 2020-12-23
 * @Version: V1.0
 */
@Data
@TableName("fm_invoice_code")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "fm_invoice_code对象", description = "发票码信息表")
public class InvoiceCode implements Serializable {
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
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
    /**
     * 产品大类
     */
    @Excel(name = "产品大类", width = 15)
    @Dict(dicCode = "prod_code")
    @ApiModelProperty(value = "产品大类")
    private java.lang.String prodClassCode;
    /**
     * 牌号
     */
    @Excel(name = "牌号", width = 15)
    @ApiModelProperty(value = "牌号")
    private java.lang.String sgSign;
    /**
     * 货物名称
     */
    @Excel(name = "货物名称", width = 15)
    @ApiModelProperty(value = "货物名称")
    private java.lang.String mat;
    /**
     * 直径(外径)起
     */
    @Excel(name = "直径(外径)起", width = 15)
    @ApiModelProperty(value = "直径(外径)起")
    private java.lang.Double diameterStart;
    /**
     * 截面描述
     */
    @Excel(name = "截面描述", width = 15)
    @ApiModelProperty(value = "截面描述")
    private java.lang.String crossDesc;
    /**
     * 产品中文名
     */
    @Excel(name = "产品中文名", width = 15)
    @ApiModelProperty(value = "产品中文名")
    private java.lang.String productCname;
    /**
     * 品名
     */
    @Excel(name = "品名", width = 15)
    @ApiModelProperty(value = "品名")
    private java.lang.String prodCname;
    /**
     * 品名别名
     */
    @Excel(name = "品名别名", width = 15)
    @ApiModelProperty(value = "品名别名")
    private java.lang.String prodCnameOther;
    /**
     * 产品编码
     */
    @Excel(name = "产品编码", width = 15)
    @ApiModelProperty(value = "产品编码")
    private java.lang.String productCode;
    /**
     * 开票中文名
     */
    @Excel(name = "开票中文名", width = 15)
    @ApiModelProperty(value = "开票中文名")
    private java.lang.String invoiceCname;
    /**
     * 直径(外径)止
     */
    @Excel(name = "直径(外径)止", width = 15)
    @ApiModelProperty(value = "直径(外径)止")
    private java.lang.Double diameterEnd;
    /**
     * 产线类型
     */
    @Excel(name = "产线类型", width = 15)
    @ApiModelProperty(value = "产线类型")
    private java.lang.String productionLineType;
    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
}
