package com.dongxin.scm.sm.vo;

import com.dongxin.scm.sm.entity.StackDet;
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
 * @Description: 装车单主表
 * @Author: jeecg-boot
 * @Date: 2020-12-01
 * @Version: V1.0
 */
@Data
@ApiModel(value = "sm_stackPage对象", description = "装车单主表")
public class StackPage {

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
     * 装车单号
     */
    @Excel(name = "装车单号", width = 15)
    @ApiModelProperty(value = "装车单号")
    private String stackingNo;
    /**
     * 销售单号
     */
    @Excel(name = "销售单号", width = 15)
    @ApiModelProperty(value = "销售单号")
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
     * 重量
     */
    @Excel(name = "重量", width = 15)
    @ApiModelProperty(value = "重量")
    private Double weight;
    /**
     * 数量
     */
    @Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private Double qty;
    /**
     * 顾客
     */
    @Excel(name = "顾客", width = 15)
    @Dict(dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @ApiModelProperty(value = "顾客")
    private String customerId;
    /**
     * 仓储
     */
    @Excel(name = "仓储", width = 15)
    @Dict(dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "仓储")
    private String stockNoClass;
    /**
     * 车号
     */
    @Excel(name = "车号", width = 15)
    @ApiModelProperty(value = "车号")
    private String carNo;
    /**
     * 提货人手机号
     */
    @Excel(name = "提货人手机号", width = 15)
    @ApiModelProperty(value = "提货人手机号")
    private String phone;
    /**
     * 船号
     */
    @Excel(name = "船号", width = 15)
    @ApiModelProperty(value = "船号")
    private String shipNo;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 租户id
     */

    @ApiModelProperty(value = "租户id")
    private String tenantId;
    /**
     * 运输方式
     */
    @Excel(name = "运输方式", width = 15)
    @Dict(dicCode = "trnp_mode_code")
    @ApiModelProperty(value = "运输方式")
    private String trnpModeCode;
    /**
     * 业务人员
     */
    @Excel(name = "业务人员", width = 15)
    @ApiModelProperty(value = "业务人员")
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
     * 到站
     */
    @Excel(name = "到站", width = 15)
    @Dict(dictTable = "bd_train_station", dicText = "station_name", dicCode = "station_code")
    @ApiModelProperty(value = "到站")
    private String stationCode;
    /**
     * 专用线
     */
    @Excel(name = "专用线", width = 15)
    @Dict(dictTable = "bd_private_route_name", dicText = "route_name", dicCode = "route_code")
    @ApiModelProperty(value = "专用线")
    private String routeCode;
    /**
     * 目的地
     */
    @Excel(name = "目的地", width = 15)
    @ApiModelProperty(value = "目的地")
    private String destination;
    /**
     * 结算金额
     */
    @Excel(name = "结算金额", width = 15)
    @ApiModelProperty(value = "结算金额")
    private java.math.BigDecimal settledTotalPrice;
    /**
     * 每吨优惠
     */
    @Excel(name = "每吨优惠", width = 15)
    @ApiModelProperty(value = "每吨优惠")
    private java.math.BigDecimal discount;

    @ExcelCollection(name = "装车单明细表")
    @ApiModelProperty(value = "装车单明细表")
    private List<StackDet> stackDetList;

}
