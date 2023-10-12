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
 * @Description: 银行信息汇总表
 * @Author: jeecg-boot
 * @Date: 2020-10-28
 * @Version: V1.0
 */
@Data
@TableName("fm_bank")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "fm_bank对象", description = "银行信息汇总表")
public class Bank implements Serializable {
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
     * 银行类型
     */
    @Excel(name = "银行类型", width = 15, dictTable = "fm_bank_type", dicText = "type_name", dicCode = "type_id")
    @Dict(dictTable = "fm_bank_type", dicText = "type_name", dicCode = "type_id")
    @ApiModelProperty(value = "银行类型")
    private java.lang.String typeId;

    /**
     * 银行名称
     */
    @Excel(name = "银行名称", width = 15)
    @ApiModelProperty(value = "银行名称")
    private java.lang.String bankName;
    /**
     * 逻辑删除标识
     */
    @Excel(name = "逻辑删除标识", width = 15)
    @ApiModelProperty(value = "逻辑删除标识")
    private java.lang.Integer delFlag;

    @TableField(exist = false)
    private String tapeName;

    /**
     * 类型
     */
    @Excel(name = "类型", width = 15)
    @TableField("category")
    @ApiModelProperty(value = "类型")
    private java.lang.String category;


}
