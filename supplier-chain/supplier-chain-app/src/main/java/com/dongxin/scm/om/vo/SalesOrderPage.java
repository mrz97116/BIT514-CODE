package com.dongxin.scm.om.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dongxin.scm.om.entity.SalesOrderDet;
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
 * @Description: 销售单
 * @Author: jeecg-boot
 * @Date: 2020-12-02
 * @Version: V1.0
 */
@Data
@ApiModel(value = "om_sales_orderPage对象", description = "销售单")
public class SalesOrderPage {

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
     * 制单时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "制单时间")
    private Date orderTime;
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
     * 提单号
     */
    @Excel(name = "提单号", width = 15)
    @ApiModelProperty(value = "提单号")
    private String saleBillNo;
    /**
     * 合同编号
     */
    @Excel(name = "合同编号", width = 15)
    @ApiModelProperty(value = "合同编号")
    private String contractNo;
    /**
     * 订单编号
     */
    @Excel(name = "订单编号", width = 15)
    @ApiModelProperty(value = "订单编号")
    private String orderNo;
    /**
     * 客户编码
     */
    @Excel(name = "客户编码", width = 15, dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @Dict(dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @ApiModelProperty(value = "客户编码")
    private String customerId;
    /**
     * 顾客名称
     */
    @Excel(name = "顾客", width = 15)
    @ApiModelProperty(value = "顾客")
    private String customerName;
    /**
     * 助记码
     */
    @Excel(name = "助记码", width = 15)
    @Dict(dictTable = "cm_customer_profile", dicText = "alias", dicCode = "alias")
    @ApiModelProperty(value = "助记码")
    private java.lang.String alias;
    /**
     * 控款记录id
     */
    @Excel(name = "控款记录id", width = 15)
    @ApiModelProperty(value = "控款记录id")
    private String relationshipHoldId;
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
    private String tenantId;
    /**
     * 仓库id
     */
    @Excel(name = "仓库id", width = 15, dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @Dict(dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "仓库id")
    private String stockId;
    /**
     * 到站编码
     */
    @Excel(name = "到站编码", width = 15, dictTable = "bd_train_station", dicText = "station_name", dicCode = "station_code")
    @Dict(dictTable = "bd_train_station", dicText = "station_name", dicCode = "station_code")
    @ApiModelProperty(value = "到站编码")
    private String stationCode;
    /**
     * 专用线编码
     */
    @Excel(name = "专用线编码", width = 15, dictTable = "bd_private_route_name", dicText = "route_name", dicCode = "route_code")
    @Dict(dictTable = "bd_private_route_name", dicText = "route_name", dicCode = "route_code")
    @ApiModelProperty(value = "专用线编码")
    private String routeCode;
    /**
     * 运输方式编码
     */
    @Excel(name = "运输方式编码", width = 15, dicCode = "trnp_mode_code")
    @Dict(dicCode = "trnp_mode_code")
    @ApiModelProperty(value = "运输方式编码")
    private String trnpModeCode;
    /**
     * 车号
     */
    @Excel(name = "车号", width = 15)
    @ApiModelProperty(value = "车号")
    private String carNo;
    /**
     * 业务员
     */
    @Excel(name = "业务员", width = 15)
    @ApiModelProperty(value = "业务员")
    private String salesMan;
    /**
     * 提货人
     */
    @Excel(name = "提货人", width = 15)
    @ApiModelProperty(value = "提货人")
    private String consigneeName;
    /**
     * 身份证
     */
    @Excel(name = "身份证", width = 15)
    @ApiModelProperty(value = "身份证")
    private String idCard;
    /**
     * 目的地
     */
    @Excel(name = "目的地", width = 15)
    @ApiModelProperty(value = "目的地")
    private String destination;
    /**
     * 提货人手机号
     */
    @Excel(name = "提货人手机号", width = 15)
    @ApiModelProperty(value = "提货人手机号")
    private String phone;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 生成销售单状态
     */
    @Excel(name = "销售单生成状态", width = 15)
    @Dict(dicCode = "yes_no_status")
    @ApiModelProperty(value = "销售单生成状态")
    private String createStackStatus;

    @ExcelCollection(name = "销售单明细表")
    @ApiModelProperty(value = "销售单明细表")
    private List<SalesOrderDet> salesOrderDetList;
    /**
     * 收料人
     */
    @Excel(name = "收料人", width = 15)
    @ApiModelProperty(value = "收料人")
    private String receivingName;
    /**
     * 收料人电话
     */
    @Excel(name = "收料人电话", width = 15)
    @ApiModelProperty(value = "收料人电话")
    private String receivingPhone;
    /**
     * 收料地址
     */
    @Excel(name = "收料地址", width = 15)
    @ApiModelProperty(value = "收料地址")
    private String receivingAddress;
    /**
     * 发货时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "发货时间")
    private Date shipDate;

    /**
     * 数据标识 addBy liujiazhi 2021.3.27
     */
    @Excel(name = "付款标识", width = 15)
    @ApiModelProperty(value = "付款标识")
    private String paymentFlag;

    @Excel(name = "设备物资标识", width = 15, dicCode = "yes_no_status")
    @Dict(dicCode = "yes_no_status")
    @ApiModelProperty(value = "设备物资标识")
    private String equipmentSuppliesFlag;
    /**
     * 装货人
     */
    @Excel(name = "装货人", width = 15)
    @ApiModelProperty(value = "装货人")
    private String shipperName;
    /**
     * 截止日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "截止日期")
    private Date closeTime;

    /**
     * 车队名称
     */
    @Excel(name = "车队名称", width = 15)
    @ApiModelProperty(value = "车队名称")
    private String fleetName;

    /**
     * 资金ids
     */
    @Excel(name = "资金ids", width = 15)
    @ApiModelProperty(value = "资金ids")
    private String fundIds;
    /**
     * 物流园目的地
     */
    @TableField(exist = false)
    private String pushDestination;
    /**
     * 物流园车号
     */
    @TableField(exist = false)
    private String pushCarNo;
    /**
     * 推送物流园
     */
    @Excel(name = "推送物流园", width = 15)
    @ApiModelProperty(value = "推送物流园")
    private String pushLogistics;

    //预开单id
    private String prepareOrderId;

    private String fundDesc;

    private String attach;

}
