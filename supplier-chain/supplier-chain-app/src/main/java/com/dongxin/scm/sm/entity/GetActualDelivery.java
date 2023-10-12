package com.dongxin.scm.sm.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.jeecgframework.poi.excel.annotation.Excel;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 获取装车实际
 * @Author: jeecg-boot
 * @Date:   2021-09-22
 * @Version: V1.0
 */
@Data
@TableName("sm_get_actual_delivery")
@ApiModel(value="sm_get_actual_delivery对象", description="获取装车实际")
public class GetActualDelivery implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建人*/
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    @TableField(fill = FieldFill.UPDATE)
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    @TableField(fill = FieldFill.UPDATE)
    private java.util.Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
	/**租户id*/
    @Excel(name = "租户id", width = 15)
    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
	/**逻辑删除*/
    @Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private java.lang.Integer delFlag;
	/**提货单号*/
    @Excel(name = "提货单号", width = 15)
    @ApiModelProperty(value = "提货单号")
    private java.lang.String billOfLadingNo;
	/**实提单号*/
    @Excel(name = "实提单号", width = 15)
    @ApiModelProperty(value = "实提单号")
    private java.lang.String stockOutNoteNo;
	/**出库时间*/
    @Excel(name = "出库时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "出库时间")
    private java.util.Date stockOutDate;
	/**操作人*/
    @Excel(name = "操作人", width = 15)
    @ApiModelProperty(value = "操作人")
    private java.lang.String operatorName;
	/**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
	/**提单id*/
    @Excel(name = "提单id", width = 15)
    @ApiModelProperty(value = "提单id")
    private java.lang.String mainId;
	/**开单时间*/
    @Excel(name = "开单时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "开单时间")
    private java.util.Date billDate;
    /**是否确认*/
    @Excel(name = "是否确认", width = 15)
    @ApiModelProperty(value = "是否确认")
    private java.lang.String confirmStack;
    /**装车状态*/
    @Excel(name = "装车状态", width = 15)
    @ApiModelProperty(value = "装车状态")
    private java.lang.String stackStatusType;
    @Excel(name = "开单人", width = 15)
    @ApiModelProperty(value = "开单人")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    private java.lang.String salesCreateBy;
    /**车号*/
    @Excel(name = "车号", width = 15)
    @ApiModelProperty(value = "车号")
    private java.lang.String carNo;
    /**
     * 客户编码
     */
    @Excel(name = "客户编码", width = 15, dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @Dict(dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @ApiModelProperty(value = "客户编码")
    private java.lang.String customerId;
}
