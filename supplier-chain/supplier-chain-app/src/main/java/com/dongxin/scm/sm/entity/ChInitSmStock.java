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
 * @Description: 岑海库存表
 * @Author: jeecg-boot
 * @Date:   2021-03-09
 * @Version: V1.0
 */
@Data
@TableName("ch_init_sm_stock")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ch_init_sm_stock对象", description="岑海库存表")
public class ChInitSmStock implements Serializable {
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
	/**产品编码*/
	@Excel(name = "产品编码", width = 15)
    @ApiModelProperty(value = "产品编码")
    private java.lang.String productCode;
	/**尺寸*/
	@Excel(name = "尺寸", width = 15)
    @ApiModelProperty(value = "尺寸")
    private java.lang.String size;
	/**钢号*/
	@Excel(name = "钢号", width = 15)
    @ApiModelProperty(value = "钢号")
    private java.lang.String steelNo;
	/**规格*/
	@Excel(name = "规格", width = 15)
    @ApiModelProperty(value = "规格")
    private java.lang.String specs;
	/**库存数量*/
	@Excel(name = "库存数量", width = 15)
    @ApiModelProperty(value = "库存数量")
    private java.lang.Integer stockNum;
	/**库存重量*/
	@Excel(name = "库存重量", width = 15)
    @ApiModelProperty(value = "库存重量")
    private java.math.BigDecimal stockWeight;
	/**种类*/
	@Excel(name = "种类", width = 15)
    @ApiModelProperty(value = "种类")
    private java.lang.String type;
	/**仓库*/
	@Excel(name = "仓库", width = 15)
    @ApiModelProperty(value = "仓库")
    private java.lang.String warehouse;
	/**来源*/
	@Excel(name = "来源", width = 15)
    @ApiModelProperty(value = "来源")
    private java.lang.String source;
	/**产品ID*/
	@Excel(name = "产品ID", width = 15)
    @ApiModelProperty(value = "产品ID")
    private java.lang.String productId;
	/**钢卷号*/
	@Excel(name = "钢卷号", width = 15)
    @ApiModelProperty(value = "钢卷号")
    private java.lang.String steelCoil;
	/**长度*/
	@Excel(name = "长度", width = 15)
    @ApiModelProperty(value = "长度")
    private java.math.BigDecimal length;
	/**宽度*/
	@Excel(name = "宽度", width = 15)
    @ApiModelProperty(value = "宽度")
    private java.math.BigDecimal wide;
	/**厚度*/
	@Excel(name = "厚度", width = 15)
    @ApiModelProperty(value = "厚度")
    private java.math.BigDecimal thick;
    /**产品名称*/
    @Excel(name = "产品名称", width = 15)
    @ApiModelProperty(value = "产品名称")
    private java.lang.String  prodName;
}
