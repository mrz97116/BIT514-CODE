package com.dongxin.scm.om.entity;

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
 * @Description: 费用明细表
 * @Author: jeecg-boot
 * @Date:   2021-06-11
 * @Version: V1.0
 */
@Data
@TableName("om_cost_breakdown")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="om_cost_breakdown对象", description="费用明细表")
public class CostBreakdown implements Serializable {
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
    /**方向*/
    @Excel(name = "方向", width = 15, dicCode = "direction")
    @Dict(dicCode = "direction")
    @ApiModelProperty(value = "方向")
    private java.lang.String direction;
    /**日期*/
    @Excel(name = "日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "日期")
    private java.util.Date date;
    /**费用类型*/
    @Excel(name = "费用类型", width = 15, dicCode = "expense_type")
    @Dict(dicCode = "expense_type")
    @ApiModelProperty(value = "费用类型")
    private java.lang.String expenseType;
    /**结算单位*/
    @Excel(name = "结算单位", width = 15)
    @ApiModelProperty(value = "结算单位")
    private java.lang.String settlementUnit;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remarks;
    /**提单id*/
    @Excel(name = "提单id", width = 15)
    @ApiModelProperty(value = "提单id")
    private java.lang.String salesOrderId;
}
