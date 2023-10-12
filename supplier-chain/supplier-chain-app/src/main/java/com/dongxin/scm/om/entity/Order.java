package com.dongxin.scm.om.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 订单主表
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */
@Data
@TableName("om_order")
@ApiModel(value = "om_order对象", description = "订单主表")
public class Order extends Model<Order> {
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
    @TableField(fill = FieldFill.UPDATE)
    private java.lang.String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    @TableField(fill = FieldFill.UPDATE)
    private java.util.Date updateTime;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
    /**
     * 父级节点
     */
    @Excel(name = "父级节点", width = 15)
    @ApiModelProperty(value = "父级节点")
    private java.lang.String pid;
    /**
     * 是否有子节点
     */
    @Excel(name = "是否有子节点", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否有子节点")
    private java.lang.String hasChild;
    /**
     * 订单编号
     */
    @Excel(name = "订单编号", width = 15)
    @ApiModelProperty(value = "订单编号")
    private java.lang.String orderNo;
    /**
     * 订单性质
     */
    @Excel(name = "订单性质", width = 15, dicCode = "order_type")
    @Dict(dicCode = "order_type")
    @ApiModelProperty(value = "订单性质")
    private java.lang.String orderType;
    /**
     * 目的地
     */
    @Excel(name = "目的地", width = 15)
    @ApiModelProperty(value = "目的地")
    private java.lang.String desCity;
    /**
     * 订货用户
     */
    @Excel(name = "订货用户", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @ApiModelProperty(value = "订货用户")
    private java.lang.String orderCustCname;
    /**
     * 产品大类
     */
    @Excel(name = "产品大类", width = 15, dicCode = "product_code")
    @Dict(dicCode = "product_code")
    @ApiModelProperty(value = "产品大类")
    private java.lang.String productCode;
    /**
     * 订单总重量
     */
    @Excel(name = "订单总重量", width = 15)
    @ApiModelProperty(value = "订单总重量")
    private java.lang.Double orderWt;
    /**
     * 总价
     */
    @Excel(name = "总价", width = 15)
    @ApiModelProperty(value = "总价")
    private java.math.BigDecimal totalPrice;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
    /**
     * 逻辑删除
     */
    @Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private java.lang.Integer delFlag;
    /**
     * 租户id
     */

    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;

    /**
     * 顾客id
     */
    @Excel(name = "顾客id", width = 15)
    @ApiModelProperty(value = "顾客id")
    private java.lang.String customerId;

    /**
     * 顾客id
     */
    @Excel(name = "顾客名称", width = 15)
    @ApiModelProperty(value = "顾客名称")
    @TableField(exist = false)
    private java.lang.String customerText;

    @Override
    protected Serializable pkVal() {
        return id;
    }
}
