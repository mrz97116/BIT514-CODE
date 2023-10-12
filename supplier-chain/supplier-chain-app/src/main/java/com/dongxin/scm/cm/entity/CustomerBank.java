package com.dongxin.scm.cm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 顾客银行卡信息
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */
@Data
@TableName("cm_customer_bank")
@ApiModel(value = "cm_customer_profile对象", description = "客户基础信息")
public class CustomerBank implements Serializable {
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
     * 顾客id
     */
    @ApiModelProperty(value = "顾客id")
    private java.lang.String customerId;
    /**
     * 开户行
     */
    @Excel(name = "开户行", width = 15)
    @Dict(dicCode = "id", dicText = "bank_name", dictTable = "fm_bank")
    @ApiModelProperty(value = "开户行")
    private java.lang.String bank;
    /**
     * 开户网点
     **/
//	@Excel(name = "开户网点", width = 15)
    @ApiModelProperty(value = "开户网点")
    private java.lang.String bankBranch;
    /**
     * 银行卡号
     */
    @Excel(name = "银行卡号", width = 15)
    @ApiModelProperty(value = "银行卡号")
    private java.lang.String bankCardNo;
    /**
     * 开户人姓名
     */
    @Excel(name = "开户人姓名", width = 15)
    @ApiModelProperty(value = "开户人姓名")
    private java.lang.String accountHolderName;
    /**
     * 开户手机号
     */
    @Excel(name = "开户手机号", width = 15)
    @ApiModelProperty(value = "开户手机号")
    private java.lang.String mobile;
    /**
     * 删除标志
     */
    @Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
    private java.lang.Integer delFlag;
    /**
     * 租户id
     */

    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;

    /**
     * 开户行名称
     */

    @ApiModelProperty(value = "开户行名称")
    private String bankName;
}
