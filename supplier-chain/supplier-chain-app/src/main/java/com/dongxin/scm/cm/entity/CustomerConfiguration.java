package com.dongxin.scm.cm.entity;

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

/**
 * @Description: 顾客合同控量配置表
 * @Author: jeecg-boot
 * @Date: 2021-01-04
 * @Version: V1.0
 */
@Data
@TableName("cm_customer_configuration")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "cm_customer_configuration对象", description = "顾客合同控量配置表")
public class CustomerConfiguration implements Serializable {
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
     * 是否有销售单合同控量
     */
    @Excel(name = "销售单合同控量", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "销售单合同控量")
    private java.lang.String flag;
    /**
     * 租户id
     */
    @Excel(name = "租户id", width = 15, dictTable = "sys_tenant", dicText = "name", dicCode = "id")
    @Dict(dictTable = "sys_tenant", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "租户id")
    private java.lang.String tenantId;
    /**
     * 是否有装车单合同控量
     */
    @Excel(name = "装车单合同控量", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "装车单合同控量")
    private java.lang.String stackContractConfiguration;


}
