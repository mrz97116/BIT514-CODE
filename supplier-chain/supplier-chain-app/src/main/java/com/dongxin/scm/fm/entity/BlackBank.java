package com.dongxin.scm.fm.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 银行黑名单
 * @Author: jeecg-boot
 * @Date:   2021-12-31
 * @Version: V1.0
 */
@Data
@TableName("fm_black_bank")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fm_black_bank对象", description="银行黑名单")
public class BlackBank implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
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
    /** 银行*/
    @Excel(name = "银行", width = 15, dictTable = "fm_bank", dicText = "bank_name", dicCode = "id")
    @Dict(dictTable = "fm_bank", dicText = "bank_name", dicCode = "id")
    @ApiModelProperty(value = "银行")
    private java.lang.String bank;
    /**逻辑删除标识*/
    @Excel(name = "逻辑删除标识", width = 15)
    @ApiModelProperty(value = "逻辑删除标识")
    private java.lang.Integer delFlag;
}
