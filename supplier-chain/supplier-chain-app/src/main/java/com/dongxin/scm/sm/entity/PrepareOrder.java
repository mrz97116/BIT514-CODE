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
 * @Description: 预开单信息表
 * @Author: jeecg-boot
 * @Date:   2021-10-31
 * @Version: V1.0
 */
@Data
@TableName("sm_prepare_order")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sm_prepare_order对象", description="预开单信息表")
public class PrepareOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
    /**创建人*/
    @ApiModelProperty(value = "创建人")
    @Dict(  dictTable = "sys_user", dicText = "realname", dicCode = "username")
    private java.lang.String createBy;
    /**创建日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
    /**更新人*/
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
    /**更新日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
    /**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
    /**租户ID*/
    @Excel(name = "租户ID", width = 15)
    @ApiModelProperty(value = "租户ID")
    private java.lang.String tenantId;
    /**逻辑删除*/
    @Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private java.lang.Integer delFlag;
    /**预开单号*/
    @Excel(name = "预开单号", width = 15)
    @ApiModelProperty(value = "预开单号")
    private java.lang.String prepareOrderNo;
    /**提单生成标识*/
    @Excel(name = "提单生成标识", width = 15)
    @ApiModelProperty(value = "提单生成标识")
    @Dict(dicCode = "yn")
    private java.lang.String prepareOrderStatus;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remarks;
    /**客户ID*/
    @Excel(name = "客户ID", width = 15)
    @ApiModelProperty(value = "客户ID")
    @Dict(dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    private java.lang.String customerId;
    /**入库状态*/
    @Excel(name = "入库状态", width = 15)
    @ApiModelProperty(value = "入库状态")
    @Dict(dicCode = "stock_type")
    private String stockStatus;

    //仓库目的地
    private String destination;

    //入库单id
    private String warehouseWarrantId;

    //车皮号字符串
    private String carNos;

    private java.lang.String dispatchShipBillNo;

    private java.lang.String shipNo;
}
