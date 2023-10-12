package com.dongxin.scm.bd.entity;

import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 未结算量
 * @Author: jeecg-boot
 * @Date: 2021-11-15
 * @Version: V1.0
 */
@Data
@TableName("bd_unsettled")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "bd_unsettled对象", description = "未结算量")
public class Unsettled implements Serializable {
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
     * 装车单id
     */
    @Excel(name = "装车单id", width = 15)
    @ApiModelProperty(value = "装车单id")
    private String stackId;
    /**
     * 装车明细id
     */
    @Excel(name = "装车明细id", width = 15)
    @ApiModelProperty(value = "装车明细id")
    private String stackDetId;
    /**
     * 单位名称
     */
    @Excel(name = "单位名称", width = 15)
    @ApiModelProperty(value = "单位名称")
    private String customerId;
    /**
     * 装车时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "装车时间")
    private Date shipDate;
    /**
     * 车号
     */
    @Excel(name = "车号", width = 15)
    @ApiModelProperty(value = "车号")
    private String carNo;
    /**
     * 产品大类
     */
    @Excel(name = "产品大类", width = 15)
    @ApiModelProperty(value = "产品大类")
    private String prodClassCode;
    /**
     * 仓库
     */
    @Excel(name = "仓库", width = 15)
    @ApiModelProperty(value = "仓库")
    private String stockName;
    /**
     * 物料名称
     */
    @Excel(name = "物料名称", width = 15)
    @ApiModelProperty(value = "物料名称")
    private String name;
    /**
     * 钢号
     */
    @Excel(name = "钢号", width = 15)
    @ApiModelProperty(value = "钢号")
    private String sgSign;
    /**
     * 规格
     */
    @Excel(name = "规格", width = 15)
    @ApiModelProperty(value = "规格")
    private String custMatSpecs;
    /**
     * 产品名称
     */
    @Excel(name = "产品名称", width = 15)
    @ApiModelProperty(value = "产品名称")
    private String ProdCnameOther;
    /**
     * 物料编码
     */
    @Excel(name = "物料编码", width = 15)
    @ApiModelProperty(value = "物料编码")
    private String matCode;
    /**
     * 生产线
     */
    @Excel(name = "生产线", width = 15)
    @ApiModelProperty(value = "生产线")
    private String productionLine;
    /**
     * 品类
     */
    @Excel(name = "品类", width = 15)
    @ApiModelProperty(value = "品类")
    private String categoryIdentification;
    /**
     * 含税单价
     */
    @Excel(name = "含税单价", width = 15)
    @ApiModelProperty(value = "含税单价")
    private BigDecimal price;
    /**
     * 重量
     */
    @Excel(name = "重量", width = 15)
    @ApiModelProperty(value = "重量")
    private Double weight;
    /**
     * 含税金额
     */
    @Excel(name = "含税金额", width = 15)
    @ApiModelProperty(value = "含税金额")
    private BigDecimal taxAmount;
    /**
     * 不含税金额
     */
    @Excel(name = "不含税金额", width = 15)
    @ApiModelProperty(value = "不含税金额")
    private BigDecimal noTaxAmount;
    /**
     * 税额
     */
    @Excel(name = "税额", width = 15)
    @ApiModelProperty(value = "税额")
    private BigDecimal tax;
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
     * 租户id
     */
    @Excel(name = "租户id", width = 15)
    @ApiModelProperty(value = "租户id")
    private String tenantId;
    /**
     * 逻辑删除
     */
    @Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private Integer delFlag;
}
