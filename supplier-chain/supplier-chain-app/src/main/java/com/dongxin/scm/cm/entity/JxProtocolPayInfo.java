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
import java.util.Date;

/**
 * @Description: 经销支付信息
 * @Author: jeecg-boot
 * @Date: 2021-01-20
 * @Version: V1.0
 */
@Data
@TableName("jx_protocol_pay_info")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "jx_protocol_pay_info对象", description = "经销支付信息")
public class JxProtocolPayInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
    /**
     * 更新人
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
    /**
     * 账号
     */
    @Excel(name = "账号", width = 15)
    @ApiModelProperty(value = "账号")
    private String accountNumber;
    /**
     * 账号名称
     */
    @Excel(name = "账户名称", width = 15)
    @ApiModelProperty(value = "账户名称")
    private String numberName;
    /**
     * 交易时间
     */
    @Excel(name = "交易时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "交易时间")
    private String traTime;
    /**
     * 借方发生额（支取）
     */
    @Excel(name = "借方发生额（支取）", width = 15)
    @ApiModelProperty(value = "借方发生额（支取）")
    private String debitDraw;
    /**
     * 贷方发生额（收入）
     */
    @Excel(name = "贷方发生额（收入）", width = 15)
    @ApiModelProperty(value = "贷方发生额（收入）")
    private String creditIncome;
    /**
     * 余额
     */
    @Excel(name = "余额", width = 15)
    @ApiModelProperty(value = "余额")
    private String balance;
    /**
     * 币种
     */
    @Excel(name = "币种", width = 15)
    @ApiModelProperty(value = "币种")
    private String currency;
    /**
     * 对方户名
     */
    @Excel(name = "对方户名", width = 15)
    @ApiModelProperty(value = "对方户名")
    private String accountName;
    /**
     * 对方账号
     */
    @Excel(name = "对方账号", width = 15)
    @ApiModelProperty(value = "对方账号")
    private String oppositeNumber;
    /**
     * 对方开户机构
     */
    @Excel(name = "对方开户机构", width = 15)
    @ApiModelProperty(value = "对方开户机构")
    private String mechanism;
    /**
     * 记账日期
     */
    @Excel(name = "记账日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "记账日期")
    private String bookkeepingDate;
    /**
     * 摘要
     */
    @Excel(name = "摘要", width = 15)
    @ApiModelProperty(value = "摘要")
    private String abstractt;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 账户明细编号-交易流水号
     */
    @Excel(name = "账户明细编号-交易流水号", width = 15)
    @ApiModelProperty(value = "账户明细编号-交易流水号")
    private String serialNumber;
    /**
     * 企业流水号
     */
    @Excel(name = "企业流水号", width = 15)
    @ApiModelProperty(value = "企业流水号")
    private String enterpriseNumber;
    /**
     * 凭证种类
     */
    @Excel(name = "凭证种类", width = 15)
    @ApiModelProperty(value = "凭证种类")
    private String voucherType;
    /**
     * 凭证号
     */
    @Excel(name = "凭证号", width = 15)
    @ApiModelProperty(value = "凭证号")
    private String voucherNo;
}
