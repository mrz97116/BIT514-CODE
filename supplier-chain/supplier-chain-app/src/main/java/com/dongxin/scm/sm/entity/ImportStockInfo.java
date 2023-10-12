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
 * @Description: 导入入库信息
 * @Author: jeecg-boot
 * @Date:   2021-07-21
 * @Version: V1.0
 */
@Data
@TableName("sm_import_stock_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sm_import_stock_info对象", description="导入入库信息")
public class ImportStockInfo implements Serializable {
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
	/**租户id*/
	@Excel(name = "租户id", width = 15)
    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
	/**逻辑删除*/
	@Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private java.lang.Integer delFlag;
	/**客户id*/
	@Excel(name = "产品名称", width = 15)
    private java.lang.String prodCnameOther;
	/**货物编码 在库存表中叫 factoryNo*/
	@Excel(name = "厂编号", width = 15)
    private java.lang.String goodsNo;
	/**材料号*/
	@Excel(name = "钢卷号", width = 15)
    @ApiModelProperty(value = "材料号")
    private java.lang.String matNo;
	/**规格*/
	@Excel(name = "规格+尺寸", width = 15)
    @ApiModelProperty(value = "规格")
    private java.lang.String standards;
	/**材质*/
	@Excel(name = "钢号", width = 15)
    private java.lang.String surfaces;
	/**抄码净重*/
	@Excel(name = "重量", width = 15)
    private java.math.BigDecimal copyCodeNetWeight;
	/**数量*/
	@Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private java.math.BigDecimal qty;
	/**仓库*/
    @Excel(name = "仓库", width = 15, dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "仓库")
    private java.lang.String stockId;
	/**产地*/
	@Excel(name = "来源", width = 15)
    @ApiModelProperty(value = "产地")
    private java.lang.String sources;
	/**船号*/
	@Excel(name = "船号", width = 15)
    @ApiModelProperty(value = "船号")
    private java.lang.String shipNo;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remarks;
	/**制单时间*/
	@Excel(name = "制单时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "制单时间")
    private java.util.Date preparationTime;
    /**入库状态*/
    @Excel(name = "入库状态", width = 15)
    @ApiModelProperty(value = "入库状态")
    private java.lang.String storageStatus;

    @Excel(name = "入库时间", width = 15)
    private Date stockTime;
    /**
     * 材料长度
     */
    @Excel(name = "材料长度", width = 15)
    @ApiModelProperty(value = "材料长度")
    private java.lang.Double matLen;
    /**
     * 材料宽度
     */
    @Excel(name = "材料宽度", width = 15)
    @ApiModelProperty(value = "材料宽度")
    private java.lang.Double matWidth;
    /**
     * 材料厚度
     */
    @Excel(name = "材料厚度", width = 15)
    @ApiModelProperty(value = "材料厚度")
    private java.lang.Double matThick;
    /**
     * 产品大类
     */
    @Excel(name = "产品大类", width = 15, dicCode = "prod_code")
    @Dict(dicCode = "prod_code")
    @ApiModelProperty(value = "产品大类")
    private java.lang.String prodClassCode;

}
