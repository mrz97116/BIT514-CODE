package com.dongxin.scm.sm.entity;

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
import java.util.Date;

/**
 * @Description: 收料人信息
 * @Author: jeecg-boot
 * @Date: 2021-01-13
 * @Version: V1.0
 */
@Data
@TableName("sm_receiving")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "sm_receiving对象", description = "收料人信息")
public class Receiving implements Serializable {
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
     * 收料人姓名
     */
    @Excel(name = "姓名", width = 15)
    @ApiModelProperty(value = "收料人姓名")
    private String receivingName;
    /**
     * 身份证
     */
    @Excel(name = "身份证", width = 15)
    @ApiModelProperty(value = "身份证")
    private String idCard;
    /**
     * 电话
     */
    @Excel(name = "电话", width = 15)
    @ApiModelProperty(value = "电话")
    private String phone;
    /**
     * 收料人地址
     */
    @Excel(name = "收料人地址", width = 15)
    @ApiModelProperty(value = "收料人地址")
    private String address;
    /**
     * 逻辑删除
     */
    @Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private Integer delFlag;
    /**
     * 车队运费
     */
    @Excel(name = "车队运费", width = 15)
    @ApiModelProperty(value = "车队运费")
    private java.math.BigDecimal fleetDeliverExpense;
    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remarks;
    /**
     * 运费
     */
    @Excel(name = "运费", width = 15)
    @ApiModelProperty(value = "运费")
    private java.math.BigDecimal deliverExpense;
    /**
     *  发货仓库
     */
    @ApiModelProperty(value = "发货仓库")
    @Dict(dictTable = "sm_stock", dicText = "name", dicCode = "id")
    private java.lang.String stockId;
    /**
     *  收货城市
     */
    @ApiModelProperty(value = "收货城市")
    private java.lang.String receivingCity;

}
