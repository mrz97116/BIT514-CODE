package com.dongxin.scm.fm.entity;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dongxin.scm.enums.IncomeAndCreditEnum;
import com.dongxin.scm.fm.enums.IncomingTypeEnum;
import com.dongxin.scm.fm.enums.PaymentMethodEnum;
import com.dongxin.scm.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import me.zhyd.oauth.utils.StringUtils;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 来款资金表
 * @Author: jeecg-boot
 * @Date: 2020-10-27
 * @Version: V1.0
 */
@Data
@TableName("fm_fund_pool")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "fm_fund_pool对象", description = "来款资金表")
public class FundPool implements Serializable {
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
     * 顾客
     */
    @Excel(name = "顾客", width = 15, dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @Dict(dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @ApiModelProperty(value = "顾客")
    private java.lang.String customerId;
    /**
     * 来款方式
     */
    @Excel(name = "来款方式", width = 15, dicCode = "payment_method")
    @Dict(dicCode = "payment_method")
    @ApiModelProperty(value = "来款方式")
    private java.lang.String paymentMethod;
    /**
     * 来款金额
     */
    @Excel(name = "来款金额", width = 15)
    @ApiModelProperty(value = "来款金额")
    private java.math.BigDecimal incomingAmount;
    /**
     * 来款用途
     */
    @Excel(name = "来款用途", width = 15)
    @ApiModelProperty(value = "来款用途")
    private java.lang.String purpose;
    /**
     * 汇款银行
     */
    @Excel(name = "汇款银行", width = 15, dictTable = "fm_bank", dicText = "bank_name", dicCode = "id")
    @Dict(dictTable = "fm_bank", dicText = "bank_name", dicCode = "id")
    @ApiModelProperty(value = "汇款银行")
    private java.lang.String paymentBank;
    /**
     * 承兑银行
     */
    @Excel(name = "承兑银行", width = 15, dictTable = "fm_bank", dicText = "bank_name", dicCode = "id")
    @Dict(dictTable = "fm_bank", dicText = "bank_name", dicCode = "id")
    @ApiModelProperty(value = "承兑银行")
    private java.lang.String acceptanceBank;
    /**
     * 承兑汇票号
     */
    @Excel(name = "承兑汇票号", width = 15)
    @ApiModelProperty(value = "承兑汇票号")
    private java.lang.String acceptanceTicketNo;
    /**
     * 来款日期
     */
    @Excel(name = "来款日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "来款日期")
    private java.util.Date incomingDate;
    /**
     * 承兑出票人
     */
    @Excel(name = "承兑出票人", width = 15)
    @ApiModelProperty(value = "承兑出票人")
    private java.lang.String acceptancePeople;
    /**
     * 收据编号
     */
    @Excel(name = "收据编号", width = 15)
    @ApiModelProperty(value = "收据编号")
    private java.lang.String receiptCode;
    /**
     * 审核日期
     */
    @Excel(name = "审核日期", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "审核日期")
    private java.util.Date verifyDate;
    /**
     * 租户id
     */

    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
    /**
     * 逻辑删除
     */
//    @Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private java.lang.Integer delFlag;

    @TableField(exist = false)
    private String customerNameText;

    /**
     * 来款类型
     */
//    @Excel(name = "来款类型", width = 15, dicCode = "incoming_type")
    @Dict(dicCode = "incoming_type")
    @ApiModelProperty(value = "来款类型")
    private java.lang.String incomingType;

    /**
     * 授信冲销额
     */
//    @Excel(name = "授信冲销额", width = 15)
    @ApiModelProperty(value = "授信冲销额")
    private java.math.BigDecimal creditAmount;

    /**
     * 审核状态
     */
    @Excel(name = "审核状态", width = 15, dicCode = "common_check_status")
    @Dict(dicCode = "common_check_status")
    @ApiModelProperty(value = "审核状态")
    private java.lang.String status;
    /**
     * 汇票到期日期
     */
    @Excel(name = "汇票到期日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "汇票到期日期")
    private java.util.Date ticketDate;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remarks;
    /**
     * 出票日期
     */
    @Excel(name = "出票日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "出票日期")
    private java.util.Date issueTicketsDate;
    /**
     * 助记码
     */
    @TableField(exist = false)
    private java.lang.String alias;

    @TableField(exist = false)
    private String gapDays;

    //期限
    @TableField(exist = false)
    @Excel(name = "期限", width = 15)
    private String term;
    /**
     * 承兑银行类型
     */
    @Excel(name = "承兑银行类型", width = 15, dicCode = "acceptance_bank_type")
    @Dict(dicCode = "acceptance_bank_type")
    @ApiModelProperty(value = "承兑银行类型")
    private java.lang.String acceptanceBankType;
    /**
     * 进账方式
     */
    @Excel(name = "进账方式", width = 15, dicCode = "income_method")
    @Dict(dicCode = "income_method")
    @ApiModelProperty(value = "进账方式")
    private java.lang.String incomeMethod;

    /**
     * 到账银行名字
     */
    @TableField(exist = false)
    private String paymentBankName;

    /**
     * 承兑银行名字
     */
    @TableField(exist = false)
    private String acceptanceBankName;
    /**
     * 可用金额
     */
    @Excel(name = "可用金额", width = 15)
    @ApiModelProperty(value = "可用金额")
    private java.math.BigDecimal availAmount;

    public String geneFundType() {
        if (PaymentMethodEnum.ACCEPTANCE_BILL.getCode().equalsIgnoreCase(paymentMethod)) {
            return IncomeAndCreditEnum.ACCEPTANCE_BILL.getCode();
        } else if (PaymentMethodEnum.CASH.getCode().equalsIgnoreCase(paymentMethod)) {
            return IncomeAndCreditEnum.CASH.getCode();
        }
        return "";
    }

    public int geneGapDays() {
        if (PaymentMethodEnum.ACCEPTANCE_BILL.getCode().equalsIgnoreCase(this.paymentMethod)) {

            return (int) DateUtil.betweenDay(new Date(), ticketDate, true);

        }
        return 0;
    }

    /**
     * 期限
     */
    public void geneTerm() {
        if (PaymentMethodEnum.ACCEPTANCE_BILL.getCode().equalsIgnoreCase(this.paymentMethod) &&
                !this.getGapDays().equals("已到期") &&
                StringUtils.isNotEmpty(this.getGapDays())) {
            int remainingDaysDue = this.geneGapDays();
            if (remainingDaysDue <= 105) {
                setTerm("3个月");
            } else if (remainingDaysDue <= 135) {
                setTerm("4个月");
            } else if (remainingDaysDue <= 165) {
                setTerm("5个月");
            } else if (remainingDaysDue <= 195) {
                setTerm("6个月");
            }
        }
    }

    /**
     * 期限 大写
     */
    public String geneTermD() {
        if (PaymentMethodEnum.ACCEPTANCE_BILL.getCode().equalsIgnoreCase(this.paymentMethod) &&
                !this.getGapDays().equals("已到期") &&
                StringUtils.isNotEmpty(this.getGapDays())) {
            int remainingDaysDue = this.geneGapDays();
            if (remainingDaysDue <= 105) {
                return "三个月";
            } else if (remainingDaysDue <= 135) {
                return "四个月";
            } else if (remainingDaysDue <= 165) {
                return "五个月";
            } else if (remainingDaysDue <= 195) {
                return "六个月";
            }
            return "";
        }
        return "";
    }

    public String geneFundDesc() {
        String result = "现款含税价";
        if (PaymentMethodEnum.ACCEPTANCE_BILL.getCode().equalsIgnoreCase(this.paymentMethod)) {
            String term = geneTermD();

            if (StrUtil.isNotEmpty(term)) {
                result = term + "银行承兑汇票含税价";
            }
        }

        return result;
    }

}
