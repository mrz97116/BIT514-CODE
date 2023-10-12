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
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 商务条款
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */
@Data
@TableName("om_provision")
@ApiModel(value = "om_order对象", description = "订单主表")
public class Provision implements Serializable {
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
     * 条款类型
     */
    @Excel(name = "条款类型", width = 15)
    @Dict(dicCode = "provision_type")
    @ApiModelProperty(value = "条款类型")
    private java.lang.String provisionType;
    /**
     * 条款内容
     */
    @Excel(name = "条款内容", width = 15)
    @ApiModelProperty(value = "条款内容")
    private java.lang.String provisionContent;
    /**
     * 生效标识
     */
    @Excel(name = "生效标识", width = 15)
    @Dict(dicCode = "provision_status")
    @ApiModelProperty(value = "生效标识")
    private java.lang.String validityStatus;
    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号")
    private java.lang.String orderNo;
    /**
     * 订单id
     */
    @Excel(name = "订单id", width = 15)
    @ApiModelProperty(value = "订单id")
    private java.lang.String parentId;
    /**
     * 租户id
     */

    @ApiModelProperty(value = "租户id")
    private java.lang.String tenantId;
    /**
     * 逻辑删除
     */
    @Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private java.lang.Integer delFlag;
}
