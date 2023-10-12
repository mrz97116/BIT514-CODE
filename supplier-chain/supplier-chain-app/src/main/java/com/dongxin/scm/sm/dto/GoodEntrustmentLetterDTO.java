package com.dongxin.scm.sm.dto;

import com.baomidou.mybatisplus.annotation.*;
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
 * @Description: 提货委托管理
 * @Author: jeecg-boot
 * @Date: 2021-11-03
 * @Version: V1.0
 */
@Data
@ApiModel(value = "sm_good_entrustment_letter对象", description = "提货委托管理")
public class GoodEntrustmentLetterDTO {

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
     * 提货点
     */
    @Excel(name = "提货点", width = 15)
    @ApiModelProperty(value = "提货点")
    private String pickupSpot;

    /**提货点*/
    @Excel(name = "目的地", width = 15)
    @ApiModelProperty(value = "目的地")
    private String destination;
    /**
     * 委托单位
     */
    @Excel(name = "委托单位", width = 15)
    @ApiModelProperty(value = "委托单位")
    private String clientUnit;
    /**
     * 委托提货单位
     */
    @Excel(name = "委托提货单位", width = 15)
    @ApiModelProperty(value = "委托提货单位")
    private String entrustedToPick;
    /**
     * 出发地
     */
    @Excel(name = "出发地", width = 15)
    @ApiModelProperty(value = "出发地")
    private String departureSpot;
    /**
     * 出发地地址
     */
    @Excel(name = "出发地地址", width = 15)
    @ApiModelProperty(value = "出发地地址")
    private String departureSpotAddress;
    /**
     * 出发地电话
     */
    @Excel(name = "出发地电话", width = 15)
    @ApiModelProperty(value = "出发地电话")
    private String departureSpotPhone;
    /**
     * 提货人
     */
    @Excel(name = "提货人", width = 15)
    @ApiModelProperty(value = "提货人")
    private String consignee;
    /**
     * 提货人电话
     */
    @Excel(name = "提货人电话", width = 15)
    @ApiModelProperty(value = "提货人电话")
    private String consigneePhone;
    /**
     * 提货人身份证号
     */
    @Excel(name = "提货人身份证号", width = 15)
    @ApiModelProperty(value = "提货人身份证号")
    private String consigneeIdentity;
    /**
     * 运输方式
     */
    @Excel(name = "运输方式", width = 15)
    @ApiModelProperty(value = "运输方式")
    private String transportMode;
    /**
     * 船号
     */
    @Excel(name = "船号", width = 15)
    @ApiModelProperty(value = "船号")
    private String shipNo;
    /**
     * 派船分单号
     */
    @Excel(name = "派船分单号", width = 15)
    @ApiModelProperty(value = "派船分单号")
    private String dispatchShipBillNo;
    /**
     * 委托日期
     */
    @Excel(name = "委托日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "委托日期")
    private Date consignationDate;
    /**
     * 整车车号
     */
    @Excel(name = "整车车号", width = 15)
    @ApiModelProperty(value = "整车车号")
    private String wholeCarNo;
    @Excel(name = "到货状态", width = 15)
    @ApiModelProperty(value = "到货状态")
    private String arrivalStatus;
    /**备注*/
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;

    private List<String> shippingManagementIds;
}
