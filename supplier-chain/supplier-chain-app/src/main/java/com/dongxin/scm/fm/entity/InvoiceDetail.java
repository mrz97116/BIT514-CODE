package com.dongxin.scm.fm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 发票明细
 * @Author: jeecg-boot
 * @Date: 2021-02-01
 * @Version: V1.0
 */
@ApiModel(value = "fm_invoice对象", description = "发票信息")
@Data
@TableName("fm_invoice_detail")
public class InvoiceDetail implements Serializable {
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
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
    /**
     * 主表ID
     */
    @ApiModelProperty(value = "主表ID")
    private java.lang.String mainid;
    /**
     * 产品编码
     */
    @Excel(name = "产品编码", width = 15)
    @ApiModelProperty(value = "产品编码")
    private java.lang.String productcode;
    /**
     * 商品名称
     */
    @Excel(name = "商品名称", width = 15)
    @ApiModelProperty(value = "商品名称")
    private java.lang.String warename;
    /**
     * 规格型号
     */
    @Excel(name = "规格型号", width = 15)
    @ApiModelProperty(value = "规格型号")
    private java.lang.String warespec;
    /**
     * 计量单位
     */
    @Excel(name = "计量单位", width = 15)
    @ApiModelProperty(value = "计量单位")
    private java.lang.String wareunit;
    /**
     * 数量
     */
    @Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private java.math.BigDecimal num;
    /**
     * 单价不含税
     */
    @Excel(name = "单价不含税", width = 15)
    @ApiModelProperty(value = "单价不含税")
    private java.math.BigDecimal unitpricewithouttax;
    /**
     * 单价(含税)
     */
    @Excel(name = "单价(含税)", width = 15)
    @ApiModelProperty(value = "单价(含税)")
    private java.math.BigDecimal unitpricewithtax;
    /**
     * 不含税金额
     */
    @Excel(name = "不含税金额", width = 15)
    @ApiModelProperty(value = "不含税金额")
    private java.math.BigDecimal amountnotincludedintax;
    /**
     * 金额(含税)
     */
    @Excel(name = "金额(含税)", width = 15)
    @ApiModelProperty(value = "金额(含税)")
    private java.math.BigDecimal payment;
    /**
     * 税率
     */
    @Excel(name = "税率", width = 15)
    @ApiModelProperty(value = "税率")
    private java.math.BigDecimal saletax;
    /**
     * 折扣
     */
    @Excel(name = "折扣", width = 15)
    @ApiModelProperty(value = "折扣")
    private java.math.BigDecimal discount;
    /**
     * 税额math.round(item.小计.value/1.13*0.13,2)
     */
    @Excel(name = "税额math.round(item.小计.value/1.13*0.13,2)", width = 15)
    @ApiModelProperty(value = "税额math.round(item.小计.value/1.13*0.13,2)")
    private java.math.BigDecimal tax;
    /**
     * 序号
     */
    @Excel(name = "序号", width = 15)
    @ApiModelProperty(value = "序号")
    private java.lang.Integer mxnum;
    /**
     * 税收分类编码
     */
    @Excel(name = "税收分类编码", width = 15)
    @ApiModelProperty(value = "税收分类编码")
    private java.lang.String taxsortcode;
    /**
     * 结算单号
     */
    @Excel(name = "结算单号", width = 15)
    @ApiModelProperty(value = "结算单号")
    private java.lang.Integer settleid;
    /**
     * 结算明细号
     */
    @Excel(name = "结算明细号", width = 15)
    @ApiModelProperty(value = "结算明细号")
    private java.lang.Integer settlemxid;
    /**
     * 租户id
     */
    @Excel(name = "租户id", width = 15)
    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
    /**
     * 删除标志
     */
    @Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
    private java.lang.Integer delFlag;
}
