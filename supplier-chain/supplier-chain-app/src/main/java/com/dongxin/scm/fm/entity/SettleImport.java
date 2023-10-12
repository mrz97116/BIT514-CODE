package com.dongxin.scm.fm.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 结算单导入
 * @Author: jeecg-boot
 * @Date:   2021-07-26
 * @Version: V1.0
 */
@Data
@TableName("fm_settle_import")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fm_settle_import对象", description="结算单导入")
public class SettleImport implements Serializable {
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
    /**租户ID*/
    @Excel(name = "租户ID", width = 15)
    @ApiModelProperty(value = "租户ID")
    private java.lang.Integer tenantId;
    /**逻辑删除*/
    @Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private java.lang.Integer delFlag;
    /**仓库*/
    @Excel(name = "仓库", width = 15, dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "仓库")
    private java.lang.String stockId;
    /**提单编号*/
    @Excel(name = "原始提单信息", width = 15)
    @ApiModelProperty(value = "提单编号")
    private java.lang.String saleBillNo;
    /**品名中文*/
    @Excel(name = "品种", width = 15)
    @ApiModelProperty(value = "品名中文")
    private java.lang.String prodCname;
    /**车号*/
    @Excel(name = "提货车号", width = 15)
    @ApiModelProperty(value = "车号")
    private java.lang.String carNo;
    /**规格*/
    @Excel(name = "规格", width = 15)
    @ApiModelProperty(value = "规格")
    private java.lang.String custMatSpecs;
    /**产地*/
    @Excel(name = "产地", width = 15)
    @ApiModelProperty(value = "产地")
    private java.lang.String placeOfOrigin;
    /**数量*/
    @Excel(name = "件数", width = 15)
    @ApiModelProperty(value = "数量")
    private java.lang.Integer qty;
    /**重量*/
    @Excel(name = "重量", width = 15)
    @ApiModelProperty(value = "重量")
    private java.lang.Double weight;
    /**牌号*/
    @Excel(name = "钢种", width = 15)
    @ApiModelProperty(value = "牌号")
    private java.lang.String sgSign;
    /**出库类型*/
    @Excel(name = "出库类型", width = 15)
    @ApiModelProperty(value = "出库类型")
    private java.lang.String outType;
    /**装车单编号*/
    @Excel(name = "货主提货单", width = 15)
    @ApiModelProperty(value = "装车单编号")
    private java.lang.String stockNo;
    /**理计重量*/
    @Excel(name = "磅计重量", width = 15)
    @ApiModelProperty(value = "磅计重量")
    private java.lang.Double realityWeight;
    /**价格*/
    @Excel(name = "价格", width = 15)
    @ApiModelProperty(value = "价格")
    private java.math.BigDecimal price;
    /**理计价格*/
    @Excel(name = "磅计价格", width = 15)
    @ApiModelProperty(value = "磅计价格")
    private java.math.BigDecimal realityPrice;
}
