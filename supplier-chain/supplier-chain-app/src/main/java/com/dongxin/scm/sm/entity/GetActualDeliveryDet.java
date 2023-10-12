package com.dongxin.scm.sm.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import org.jeecg.common.aspect.annotation.Dict;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 获取装车实际明细
 * @Author: jeecg-boot
 * @Date:   2021-09-22
 * @Version: V1.0
 */
@Data
@TableName("sm_get_actual_delivery_det")
@ApiModel(value="sm_get_actual_delivery对象", description="获取装车实际")
public class GetActualDeliveryDet implements Serializable {
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
	/**获取装车实际id*/
	@ApiModelProperty(value = "获取装车实际id")
	private java.lang.String getActualDeliveryId;
	/**材料号*/
	@Excel(name = "材料号", width = 15)
	@ApiModelProperty(value = "材料号")
	private java.lang.String materialNo;
	/**产品代码*/
	@Excel(name = "产品代码", width = 15)
	@ApiModelProperty(value = "产品代码")
	private java.lang.String productCode;
	/**品名*/
	@Excel(name = "品名", width = 15)
	@ApiModelProperty(value = "品名")
	private java.lang.String productName;
	/**材质*/
	@Excel(name = "材质", width = 15)
	@ApiModelProperty(value = "材质")
	private java.lang.String steelGradeName;
	/**产地*/
	@Excel(name = "产地", width = 15)
	@ApiModelProperty(value = "产地")
	private java.lang.String steelMillsName;
	/**生产标准*/
	@Excel(name = "生产标准", width = 15)
	@ApiModelProperty(value = "生产标准")
	private java.lang.String standardName;
	/**长度*/
	@Excel(name = "长度", width = 15)
	@ApiModelProperty(value = "长度")
	private java.math.BigDecimal length;
	/**宽度*/
	@Excel(name = "宽度", width = 15)
	@ApiModelProperty(value = "宽度")
	private java.math.BigDecimal width;
	/**厚度*/
	@Excel(name = "厚度", width = 15)
	@ApiModelProperty(value = "厚度")
	private java.math.BigDecimal thick;
	/**包装*/
	@Excel(name = "包装", width = 15)
	@ApiModelProperty(value = "包装")
	private java.lang.String packageCount;
	/**计量方式*/
	@Excel(name = "计量方式", width = 15)
	@ApiModelProperty(value = "计量方式")
	private java.lang.Integer weightMode;
	/**数量单位*/
	@Excel(name = "数量单位", width = 15)
	@ApiModelProperty(value = "数量单位")
	private java.lang.String numberUnit;
	/**重量单位*/
	@Excel(name = "重量单位", width = 15)
	@ApiModelProperty(value = "重量单位")
	private java.lang.String weightUnit;
	/**货物性质*/
	@Excel(name = "货物性质", width = 15)
	@ApiModelProperty(value = "货物性质")
	private java.lang.String originalCustomerName;
	/**数量*/
	@Excel(name = "数量", width = 15)
	@ApiModelProperty(value = "数量")
	private java.math.BigDecimal quantity;
	/**重量*/
	@Excel(name = "重量", width = 15)
	@ApiModelProperty(value = "重量")
	private java.math.BigDecimal weight;
	/**是否出库指定*/
	@Excel(name = "是否出库指定", width = 15)
	@ApiModelProperty(value = "是否出库指定")
	private java.lang.Integer isOwenership;
	/**租户id*/
	@Excel(name = "租户id", width = 15)
	@ApiModelProperty(value = "租户id")
	private java.lang.Integer tenantId;
	/**逻辑删除*/
	@Excel(name = "逻辑删除", width = 15)
	@ApiModelProperty(value = "逻辑删除")
	private java.lang.Integer delFlag;
	/**提单明细表id，用于匹配*/
	private java.lang.String detailId;
}
