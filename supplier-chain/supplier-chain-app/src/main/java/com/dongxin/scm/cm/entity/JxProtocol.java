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
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 经销年度协议
 * @Author: jeecg-boot
 * @Date: 2020-12-29
 * @Version: V1.0
 */
@Data
@TableName("jx_protocol")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "jx_protocol对象", description = "经销年度协议")
public class JxProtocol implements Serializable {
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
     * 主体公司
     */
    @Excel(name = "主体公司", width = 15)
    @ApiModelProperty(value = "主体公司")
    private java.lang.String mainCompany;
    /**
     * 分公司
     */
    @Excel(name = "分公司", width = 15)
    @ApiModelProperty(value = "分公司")
    private java.lang.String branchCompany;
    /**
     * 公司全称
     */
    @Excel(name = "公司全称", width = 15)
    @ApiModelProperty(value = "公司全称")
    private java.lang.String companyName;
    /**
     * 详细地址
     */
    @Excel(name = "详细地址", width = 15)
    @ApiModelProperty(value = "详细地址")
    private java.lang.String address;
    /**
     * 法人
     */
    @Excel(name = "法人", width = 15)
    @ApiModelProperty(value = "法人")
    private java.lang.String legalPerson;
    /**
     * 联系人
     */
    @Excel(name = "联系人", width = 15)
    @ApiModelProperty(value = "联系人")
    private java.lang.String contactPerson;
    /**
     * 电子邮箱
     */
    @Excel(name = "电子邮箱", width = 15)
    @ApiModelProperty(value = "电子邮箱")
    private java.lang.String email;
    /**
     * 联系电话
     */
    @Excel(name = "联系电话", width = 15)
    @ApiModelProperty(value = "联系电话")
    private java.lang.String phone;
    /**
     * 传真
     */
    @Excel(name = "传真", width = 15)
    @ApiModelProperty(value = "传真")
    private java.lang.String tax;
    /**
     * 指定联系人
     */
    @Excel(name = "指定联系人", width = 15)
    @ApiModelProperty(value = "指定联系人")
    private java.lang.String designatedContactPerson;
    /**
     * 指定联系人电话
     */
    @Excel(name = "指定联系人电话", width = 15)
    @ApiModelProperty(value = "指定联系人电话")
    private java.lang.String designatedContactPhone;
    /**
     * 收件地址
     */
    @Excel(name = "收件地址", width = 15)
    @ApiModelProperty(value = "收件地址")
    private java.lang.String receiveAddress;
    /**
     * 开户银行
     */
    @Excel(name = "开户银行", width = 15)
    @ApiModelProperty(value = "开户银行")
    private java.lang.String bankName;
    /**
     * 银行账号
     */
    @Excel(name = "银行账号", width = 15)
    @ApiModelProperty(value = "银行账号")
    private java.lang.String bankAccount;
    /**
     * 归属
     */
    @Excel(name = "归属", width = 15)
    @ApiModelProperty(value = "归属")
    private java.lang.String owner;

    private String amount;

    private String level;

    private Integer num;

}
