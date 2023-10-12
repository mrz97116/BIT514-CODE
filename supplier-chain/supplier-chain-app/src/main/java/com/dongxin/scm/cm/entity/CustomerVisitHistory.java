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
 * @Description: 客户拜访记录表
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */
@Data
@TableName("cm_customer_visit_history")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "cm_customer_visit_history对象", description = "客户拜访记录表")
public class CustomerVisitHistory implements Serializable {
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
     * 顾客
     */
    @Excel(name = "顾客", width = 15, dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @Dict(dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @ApiModelProperty(value = "顾客")
    private java.lang.String customerId;
    /**
     * 拜访地点
     */
    @Excel(name = "拜访地点", width = 15)
    @ApiModelProperty(value = "拜访地点")
    private java.lang.String visitLocation;
    /**
     * 拜访时间
     */
    @Excel(name = "拜访时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "拜访时间")
    private java.util.Date visitTime;
    /**
     * 业务员姓名
     */
    @Excel(name = "业务员姓名", width = 15)
    @ApiModelProperty(value = "业务员姓名")
    private java.lang.String salesmanName;
    /**
     * 拜访客户姓名
     */
    @Excel(name = "拜访客户姓名", width = 15)
    @ApiModelProperty(value = "拜访客户姓名")
    private java.lang.String customerName;
    /**
     * 客户职级
     */
    @Excel(name = "客户职级", width = 15)
    @ApiModelProperty(value = "客户职级")
    private java.lang.String customerTitle;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
    /**
     * 附件
     */
    @Excel(name = "附件", width = 15)
    @ApiModelProperty(value = "附件")
    private java.lang.String enclosure;
    /**
     * tenant_id
     */
    @Excel(name = "tenant_id", width = 15)
    @ApiModelProperty(value = "tenant_id")
    private java.lang.Integer tenantId;
    /**
     * 删除标志
     */
    @Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
    private java.lang.Integer delFlag;

    @TableField(exist = false)
    private String customerNameText;
}
