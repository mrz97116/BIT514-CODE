package com.dongxin.scm.om.vo;

import com.dongxin.scm.om.entity.OrderDet;
import com.dongxin.scm.om.entity.Provision;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: 订单主表
 * @Author: jeecg-boot
 * @Date: 2020-11-13
 * @Version: V1.0
 */
@Data
@ApiModel(value = "om_orderPage对象", description = "订单主表")
public class OrderPage {

    /**
     * 主键
     */
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
     * 父级节点
     */
    @Excel(name = "父级节点", width = 15)
    @ApiModelProperty(value = "父级节点")
    private String pid;
    /**
     * 是否有子节点
     */
    @Excel(name = "是否有子节点", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "是否有子节点")
    private String hasChild;
    /**
     * 订单编号
     */
    @Excel(name = "订单编号", width = 15)
    @ApiModelProperty(value = "订单编号")
    private String orderNo;
    /**
     * 订单性质
     */
    @Excel(name = "订单性质", width = 15, dicCode = "order_type")
    @Dict(dicCode = "order_type")
    @ApiModelProperty(value = "订单性质")
    private String orderType;
    /**
     * 目的地
     */
    @Excel(name = "目的地", width = 15)
    @ApiModelProperty(value = "目的地")
    private String desCity;
    /**
     * 订货用户
     */
    @Excel(name = "订货用户", width = 15, dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @ApiModelProperty(value = "订货用户")
    private String orderCustCname;
    /**
     * 产品大类
     */
    @Excel(name = "产品大类", width = 15, dicCode = "prod_code")
    @Dict(dicCode = "prod_code")
    @ApiModelProperty(value = "产品大类")
    private String productCode;
    /**
     * 订单总重量
     */
    @Excel(name = "订单总重量", width = 15)
    @ApiModelProperty(value = "订单总重量")
    private Double orderWt;
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
    private String remark;
    /**
     * 逻辑删除
     */
    @Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private Integer delFlag;
    /**
     * 租户id
     */

    @ApiModelProperty(value = "租户id")
    private Integer tenantId;
    /**
     * 顾客Id
     */
    @Excel(name = "顾客Id", width = 15, dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @Dict(dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @ApiModelProperty(value = "顾客Id")
    private String customerId;
    /**
     * 顾客名称
     */
    @Excel(name = "顾客名称", width = 15)
    @ApiModelProperty(value = "顾客名称")
    private String customerText;

    @ExcelCollection(name = "订单明细表")
    @ApiModelProperty(value = "订单明细表")
    private List<OrderDet> orderDetList;
    @ExcelCollection(name = "商务条款")
    @ApiModelProperty(value = "商务条款")
    private List<Provision> provisionList;

}
