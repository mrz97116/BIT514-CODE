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
 * @Description: 旧系统数据转换
 * @Author: jeecg-boot
 * @Date:   2021-03-04
 * @Version: V1.0
 */
@Data
@TableName("sm_old_system_data_conversion")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sm_old_system_data_conversion对象", description="旧系统数据转换")
public class OldSystemDataConversion implements Serializable {
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
	/**产品大类*/
	@Excel(name = "产品大类", width = 15, dicCode = "prod_code")
	@Dict(dicCode = "prod_code")
    @ApiModelProperty(value = "产品大类")
    private java.lang.String prodClassCode;
	/**品名中文*/
	@Excel(name = "品名中文", width = 15)
    @ApiModelProperty(value = "品名中文")
    private java.lang.String prodCname;
	/**牌号*/
	@Excel(name = "牌号", width = 15)
    @ApiModelProperty(value = "牌号")
    private java.lang.String sgSign;
	/**产品名称*/
	@Excel(name = "产品名称", width = 15)
    @ApiModelProperty(value = "产品名称")
    private java.lang.String oldProdCname;
}
