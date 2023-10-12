package com.dongxin.scm.fm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 承兑银行
 * @Author: jeecg-boot
 * @Date: 2021-02-07
 * @Version: V1.0
 */
@Data
@TableName("fm_acceptance_bank")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "fm_acceptance_bank对象", description = "承兑银行")
public class AcceptanceBank implements Serializable {
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
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
    /**
     * 承兑银行
     */
    @Excel(name = "承兑银行", width = 15, dictTable = "fm_bank", dicText = "bank_name", dicCode = "id")
    @Dict(dictTable = "fm_bank", dicText = "bank_name", dicCode = "id")
    @ApiModelProperty(value = "承兑银行")
    private java.lang.String acceptanceBank;
    /**
     * 承兑银行类型
     */
    @Excel(name = "承兑银行类型", width = 15, dicCode = "acceptance_bank_type")
    @Dict(dicCode = "acceptance_bank_type")
    @ApiModelProperty(value = "承兑银行类型")
    private java.lang.String acceptanceBankType;
    /**
     * 租户
     */
    private java.lang.Integer tenantId;
    /**
     * 逻辑删除
     */
    private java.lang.Integer delFlag;
}
