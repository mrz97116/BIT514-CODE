package com.dongxin.scm.cm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dongxin.scm.enums.SerialNoEnum;
import com.dongxin.scm.utils.SerialNoUtil;
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
 * @Description: 委托书
 * @Author: jeecg-boot
 * @Date: 2020-10-20
 * @Version: V1.0
 */
@Data
@TableName("cm_attorney")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "cm_attorney对象", description = "委托书")
public class Attorney implements Serializable {
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
     * 委托书编号
     */
    @Excel(name = "委托书编号", width = 15)
    @ApiModelProperty(value = "委托书编号")
    public java.lang.String delegateNo;

    /**
     * 委托人单位名称
     */
    @Excel(name = "委托人单位名称", width = 15, dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @Dict(dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @ApiModelProperty(value = "委托人单位名称")
    private java.lang.String delegateUnitName;

    /**
     * 委托人法定代表人/委托代理人
     */
    @Excel(name = "委托人法定代表人/委托代理人", width = 15)
    @ApiModelProperty(value = "委托人法定代表人/委托代理人")
    public java.lang.String delegateLegalRepresentative;

    /**
     * 委托人身份证号码
     */
    @Excel(name = "委托人身份证号码", width = 15)
    @ApiModelProperty(value = "委托人身份证号码")
    private java.lang.String delegatorIdCardNo;
    /**
     * 委托人职务
     */
    @Excel(name = "委托人职务", width = 15)
    @ApiModelProperty(value = "委托人职务")
    private java.lang.String delegatorTitle;
    /**
     * 委托人联系电话
     */
    @Excel(name = "委托人联系电话", width = 15)
    @ApiModelProperty(value = "委托人联系电话")
    private java.lang.String delegatorMobile;
    /**
     * 受托人姓名
     */
    @Excel(name = "受托人姓名", width = 15)
    @ApiModelProperty(value = "受托人姓名")
    public java.lang.String trusteeName;

    /**
     * 受托人性别
     */
    @Excel(name = "受托人性别", width = 15, dicCode = "sex")
    @Dict(dicCode = "sex")
    @ApiModelProperty(value = "受托人性别")
    private java.lang.String trusteeSex;
    /**
     * 受托人职务
     */
    @Excel(name = "受托人职务", width = 15)
    @ApiModelProperty(value = "受托人职务")
    private java.lang.String trusteeTitle;
    /**
     * 受托人联系电话
     */
    @Excel(name = "受托人联系电话", width = 15)
    @ApiModelProperty(value = "受托人联系电话")
    private java.lang.String trusteeMobile;
    /**
     * 受托人身份证号
     */
    @Excel(name = "受托人身份证号", width = 15)
    @ApiModelProperty(value = "受托人身份证号")
    private java.lang.String trusteeIdCardNo;
    /**
     * 委托起始时间
     */
    @Excel(name = "委托起始时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "委托起始时间")
    private java.util.Date commissionStartTime;

    /**
     * 委托终止时间
     */
    @Excel(name = "委托终止时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "委托终止时间")
    private java.util.Date terminationTimeOfEntrustment;

    /**
     * 委托事项
     */
    @Excel(name = "委托事项", width = 15)
    @ApiModelProperty(value = "委托事项")
    private java.lang.String entrustedMatters;
    /**
     * 租户id
     */

    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
    /**
     * 附件
     */
    @Excel(name = "附件", width = 15)
    @ApiModelProperty(value = "附件")
    private java.lang.String enclosure;

}
