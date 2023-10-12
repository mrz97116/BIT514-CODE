package com.dongxin.scm.kingdee.entity;

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
 * @Description: 金蝶业务单据表
 * @Author: jeecg-boot
 * @Date:   2021-03-26
 * @Version: V1.0
 */
@Data
@TableName("kingdee_stack")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="kingdee_stack对象", description="金蝶业务单据表")
public class KingdeeStack implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**单据号*/
	@Excel(name = "单据号", width = 15)
    @ApiModelProperty(value = "单据号")
    private java.lang.String billNo;
	/**单据日期*/
	@Excel(name = "单据日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "单据日期")
    private java.util.Date billDate;
	/**客户id*/
	@Excel(name = "客户id", width = 15)
    @ApiModelProperty(value = "客户id")
    private java.lang.String customerId;
	/**客户姓名*/
	@Excel(name = "客户姓名", width = 15)
    @ApiModelProperty(value = "客户姓名")
    private java.lang.String customerName;
	/**仓库id*/
	@Excel(name = "仓库id", width = 15)
    @ApiModelProperty(value = "仓库id")
    private java.lang.String stockId;
	/**业务金额*/
	@Excel(name = "业务金额", width = 15)
    @ApiModelProperty(value = "业务金额")
    private java.math.BigDecimal bizAmount;
	/**税率*/
	@Excel(name = "税率", width = 15)
    @ApiModelProperty(value = "税率")
    private java.math.BigDecimal taxRate;
	/**税额*/
	@Excel(name = "税额", width = 15)
    @ApiModelProperty(value = "税额")
    private java.math.BigDecimal taxAmount;
	/**应收/付金额*/
	@Excel(name = "应收/付金额", width = 15)
    @ApiModelProperty(value = "应收/付金额")
    private java.math.BigDecimal amount;
	/**单据类型*/
	@Excel(name = "单据类型", width = 15)
    @ApiModelProperty(value = "单据类型")
    private java.lang.String type;
	/**租户id*/
	@Excel(name = "租户id", width = 15)
    @ApiModelProperty(value = "租户id")
    private java.lang.String tenantId;
}
