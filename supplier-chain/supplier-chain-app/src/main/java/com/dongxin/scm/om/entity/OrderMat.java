package com.dongxin.scm.om.entity;

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
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 订单物料信息表
 * @Author: jeecg-boot
 * @Date: 2020-12-08
 * @Version: V1.0
 */
@Data
@TableName("om_order_mat")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "om_order_mat对象", description = "订单物料信息表")
public class OrderMat implements Serializable {
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
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
    /**
     * 产品编号
     */
    @Excel(name = "产品编号", width = 15)
    @ApiModelProperty(value = "产品编号")
    private String productNo;
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
     * 物料种类
     */
    @Excel(name = "物料种类", width = 15, dicCode = "mat_kind")
    @Dict(dicCode = "mat_kind")
    @ApiModelProperty(value = "物料种类")
    private String matKind;
    /**
     * 牌号
     */
    @Excel(name = "牌号", width = 15)
    @ApiModelProperty(value = "牌号")
    private String sgSign;
    /**
     * 规格
     */
    @Excel(name = "规格", width = 15)
    @ApiModelProperty(value = "规格")
    private BigDecimal specs;
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
    private BigDecimal price;
    /**
     * 加价
     */
    @Excel(name = "加价", width = 15)
    @ApiModelProperty(value = "加价")
    private BigDecimal addPrice;
    /**
     * 技术标准
     */
    @Excel(name = "技术标准", width = 15)
    @ApiModelProperty(value = "技术标准")
    private String sgStd;
    /**
     * 过磅重量
     */
    @Excel(name = "过磅重量", width = 15)
    @ApiModelProperty(value = "过磅重量")
    private BigDecimal weightingWeight;
    /**
     * 过磅价格
     */
    @Excel(name = "过磅价格", width = 15)
    @ApiModelProperty(value = "过磅价格")
    private BigDecimal weightingPrice;
    /**
     * 尺寸
     */
    @Excel(name = "尺寸", width = 15)
    @ApiModelProperty(value = "尺寸")
    private String dimensions;
    /**
     * 每捆支数
     */
    @Excel(name = "每捆支数", width = 15)
    @ApiModelProperty(value = "每捆支数")
    private String bindNumber;
    /**
     * 计重方式
     */
    @Excel(name = "计重方式", width = 15, dicCode = "wt_method_code")
    @Dict(dicCode = "wt_method_code")
    @ApiModelProperty(value = "计重方式")
    private String wtMethodCode;
    /**
     * 产品大类
     */
    @Excel(name = "产品大类", width = 15, dicCode = "prod_code")
    @Dict(dicCode = "prod_code")
    @ApiModelProperty(value = "产品大类")
    private String prodClassCode;
    /**
     * 租户id
     */

    @ApiModelProperty(value = "租户id")
    private Integer tenantId;
    /**
     * 逻辑删除
     */
    @Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private Integer delFlag;
    /**
     * 材料厚度
     */
    @Excel(name = "材料厚度", width = 15)
    @ApiModelProperty(value = "材料厚度")
    private Double matThick;
    /**
     * 材料宽度
     */
    @Excel(name = "材料宽度", width = 15)
    @ApiModelProperty(value = "材料宽度")
    private Double matWidth;
    /**
     * 材料长度
     */
    @Excel(name = "材料长度", width = 15)
    @ApiModelProperty(value = "材料长度")
    private Double matLen;
    /**
     * 单重
     */
    @Excel(name = "单重", width = 15)
    @ApiModelProperty(value = "单重")
    private java.lang.Double pieceWeight;
    /**
     * 钝化方式
     */
    @Excel(name = "钝化方式", width = 15)
    @ApiModelProperty(value = "钝化方式")
    private String surfaceTreatment;
}
