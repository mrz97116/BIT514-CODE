package com.dongxin.scm.om.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 公司银行卡信息
 * @Author: jeecg-boot
 * @Date: 2020-12-14
 * @Version: V1.0
 */
@ApiModel(value = "om_company_info对象", description = "公司信息")
@Data
@TableName("om_company_info_bank")
public class CompanyInfoBank implements Serializable {
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
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
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
     * 逻辑删除
     */
//	@Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private java.lang.Integer delFlag;
    /**
     * 租户id
     */

    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
    /**
     * 银行卡号
     */
    @Excel(name = "银行卡号", width = 15)
    @ApiModelProperty(value = "银行卡号")
    private java.lang.String bankCardNo;
    /**
     * 开户行
     */
    @Excel(name = "开户行", width = 15, dictTable = "fm_bank", dicText = "bank_name", dicCode = "id")
    @ApiModelProperty(value = "开户行")
    private java.lang.String bank;
    /**
     * 公司id
     */
    @ApiModelProperty(value = "公司id")
    private java.lang.String companyId;
}
