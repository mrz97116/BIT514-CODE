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
 * @Description: 设备物资明细表
 * @Author: jeecg-boot
 * @Date:   2021-03-02
 * @Version: V1.0
 */
@Data
@TableName("sm_equipment_supplies_det")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sm_equipment_supplies_det对象", description="设备物资明细表")
public class EquipmentSuppliesDet implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
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
	/**领料单位*/
	@Excel(name = "领料单位", width = 15)
    @ApiModelProperty(value = "领料单位")
    private java.lang.String customerId;
	/**材料名称*/
	@Excel(name = "材料名称", width = 15)
    @ApiModelProperty(value = "材料名称")
    private java.lang.String prodCname;
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @ApiModelProperty(value = "规格型号")
    private java.lang.String custMatSpecs;
	/**出库数量*/
	@Excel(name = "出库数量", width = 15)
    @ApiModelProperty(value = "出库数量")
    private java.lang.Double deliveryQty;
	/**销售金额*/
	@Excel(name = "销售金额", width = 15)
    @ApiModelProperty(value = "销售金额")
    private java.math.BigDecimal salesAmount;
	/**设备物资主表id*/
	@Excel(name = "设备物资主表id", width = 15)
    @ApiModelProperty(value = "设备物资主表id")
    private java.lang.String parentId;
	/**状态*/
	@Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    private java.lang.String newFlag;
	/**逻辑删除*/
	@Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private java.lang.Integer delFlag;
	/**租户id*/
	@Excel(name = "租户id", width = 15)
    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
}
