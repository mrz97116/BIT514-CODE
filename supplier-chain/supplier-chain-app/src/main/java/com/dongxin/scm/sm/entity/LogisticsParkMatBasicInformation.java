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
 * @Description: 物流园材料基础信息
 * @Author: jeecg-boot
 * @Date: 2021-09-16
 * @Version: V1.0
 */
@Data
@TableName("sm_logistics_park_mat_basic_information")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "sm_logistics_park_mat_basic_information对象", description = "物流园材料基础信息")
public class LogisticsParkMatBasicInformation implements Serializable {
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
     * 租户id
     */
    @Excel(name = "租户id", width = 15)
    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
    /**
     * 逻辑删除
     */
    @Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private java.lang.Integer delFlag;
    /**
     * 产品代码
     */
    @Excel(name = "产品代码", width = 15)
    @ApiModelProperty(value = "产品代码")
    private java.lang.String productCode;
    /**
     * 品名
     */
    @Excel(name = "品名", width = 15)
    @ApiModelProperty(value = "品名")
    private java.lang.String productName;
    /**
     * 系统牌号
     */
    @Excel(name = "系统牌号", width = 15)
    @ApiModelProperty(value = "系统牌号")
    private java.lang.String sysSgSign;
    /**
     * 产地
     */
    @Excel(name = "产地", width = 15)
    @ApiModelProperty(value = "产地")
    private java.lang.String steelMillsName;
    /**
     * 生成标准
     */
    @Excel(name = "生成标准", width = 15)
    @ApiModelProperty(value = "生成标准")
    private java.lang.String standardName;
    /**
     * 长度
     */
    @Excel(name = "长度", width = 15)
    @ApiModelProperty(value = "长度")
    private java.math.BigDecimal length;
    /**
     * 宽度
     */
    @Excel(name = "宽度", width = 15)
    @ApiModelProperty(value = "宽度")
    private java.math.BigDecimal width;
    /**
     * 厚度
     */
    @Excel(name = "厚度", width = 15)
    @ApiModelProperty(value = "厚度")
    private java.math.BigDecimal thick;
    /**
     * 包装
     */
    @Excel(name = "包装", width = 15)
    @ApiModelProperty(value = "包装")
    private java.lang.Integer packageCount;
    /**
     * 单重
     */
    @Excel(name = "单重", width = 15)
    @ApiModelProperty(value = "单重")
    private java.math.BigDecimal singleWeight;
    /**
     * 计重方式
     */
    @Excel(name = "计重方式", width = 15)
    @ApiModelProperty(value = "计重方式")
    private java.lang.Integer weightMode;
    /**
     * 数量单位
     */
    @Excel(name = "数量单位", width = 15)
    @ApiModelProperty(value = "数量单位")
    private java.lang.String numberUnit;
    /**
     * 重量单位
     */
    @Excel(name = "重量单位", width = 15)
    @ApiModelProperty(value = "重量单位")
    private java.lang.String weightUnit;
    /**
     * 材质
     */
    @Excel(name = "材质", width = 15)
    @ApiModelProperty(value = "材质")
    private java.lang.String steelGradeName;
    /**
     * 系统产品名称
     */
    @Excel(name = "系统产品名称", width = 15)
    @ApiModelProperty(value = "系统产品名称")
    private java.lang.String sysProductName;
    /**
     * 系统厚度
     */
    @Excel(name = "系统厚度", width = 15)
    @ApiModelProperty(value = "系统厚度")
    private java.math.BigDecimal sysThick;
    /**
     * 系统宽度
     */
    @Excel(name = "系统宽度", width = 15)
    @ApiModelProperty(value = "系统宽度")
    private java.math.BigDecimal sysWidth;
    /**
     * 系统长度
     */
    @Excel(name = "系统长度", width = 15)
    @ApiModelProperty(value = "系统长度")
    private java.math.BigDecimal sysLength;
}
