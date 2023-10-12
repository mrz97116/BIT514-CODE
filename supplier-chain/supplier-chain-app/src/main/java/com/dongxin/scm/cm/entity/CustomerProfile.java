package com.dongxin.scm.cm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dongxin.scm.enums.YesNoEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @Description: 客户基础信息
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */
@Data
@TableName("cm_customer_profile")
@ApiModel(value = "cm_customer_profile对象", description = "客户基础信息")
public class CustomerProfile implements Serializable {
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
    @TableField(fill = FieldFill.UPDATE)
    private java.lang.String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    @TableField(fill = FieldFill.UPDATE)
    private java.util.Date updateTime;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
    /**
     * 单位名称
     */
    @Excel(name = "单位名称", width = 15)
    @ApiModelProperty(value = "单位名称")
    private java.lang.String companyName;
    /**
     * 助记码
     */
    @Excel(name = "助记码", width = 15)
    @ApiModelProperty(value = "助记码")
    private java.lang.String alias;
    /**
     * 税号
     */
    @Excel(name = "税号", width = 15)
    @ApiModelProperty(value = "税号")
    private java.lang.String taxNo;
    /**
     * 开票名称
     */
    @Excel(name = "开票名称", width = 15)
    @ApiModelProperty(value = "开票名称")
    private java.lang.String billingName;
    /**
     * 地址
     */
    @Excel(name = "地址", width = 15)
    @ApiModelProperty(value = "地址")
    private java.lang.String address;
    /**
     * 手机号
     */
    @Excel(name = "手机号", width = 15)
    @ApiModelProperty(value = "手机号")
    private java.lang.String mobile;
    /**
     * 区域
     */
    @Excel(name = "区域", width = 15)
    @ApiModelProperty(value = "区域")
    private java.lang.String area;
    /**
     * 租户id
     */

    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;

    /**
     * 营业执照图片路径
     */
    private String businessLicense;

    /**
     * 传真
     */
    @Excel(name = "传真", width = 15)
    @ApiModelProperty(value = "传真")
    private java.lang.String fax;

    /**
     * 座机号
     */
    @Excel(name = "座机号", width = 15)
    @ApiModelProperty(value = "座机号")
    private java.lang.String landLineNo;

    /**
     * 删除标识
     */
    @Excel(name = "删除标识", width = 15)
    @ApiModelProperty(value = "删除标识")
    private java.lang.Integer delFlag;
    /**
     * 物流园服务类型
     */
    @ApiModelProperty(value = "物流园服务类型")
    @Dict(dicCode = "wms_supervise")
    private java.lang.String wmsSupervise;

    /**
     *超期天数  阳蕊明使用，超过这个天数未付款，不允许开提单
     */
    private Integer orderMaxDueDays;

    /**
     * 是否可开单 阳蕊明使用，超过上一个天数未打款，不允许开提单
     */
    @Dict(dicCode = "yn")
    private String canLadingBill;
    /**
     * 交易方式
     */
    @Dict(dicCode = "deal_way")
    private String dealWay;




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerProfile)) return false;
        CustomerProfile that = (CustomerProfile) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getCreateBy(), that.getCreateBy()) &&
                Objects.equals(getCreateTime(), that.getCreateTime()) &&
                Objects.equals(getUpdateBy(), that.getUpdateBy()) &&
                Objects.equals(getUpdateTime(), that.getUpdateTime()) &&
                Objects.equals(getSysOrgCode(), that.getSysOrgCode()) &&
                Objects.equals(getCompanyName(), that.getCompanyName()) &&
                Objects.equals(getAlias(), that.getAlias()) &&
                Objects.equals(getTaxNo(), that.getTaxNo()) &&
                Objects.equals(getBillingName(), that.getBillingName()) &&
                Objects.equals(getAddress(), that.getAddress()) &&
                Objects.equals(getMobile(), that.getMobile()) &&
                Objects.equals(getArea(), that.getArea()) &&
                Objects.equals(getTenantId(), that.getTenantId()) &&
                Objects.equals(getBusinessLicense(), that.getBusinessLicense()) &&
                Objects.equals(getFax(), that.getFax()) &&
                Objects.equals(getLandLineNo(), that.getLandLineNo()) &&
                Objects.equals(getDelFlag(), that.getDelFlag());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCreateBy(), getCreateTime(), getUpdateBy(), getUpdateTime(), getSysOrgCode(), getCompanyName(), getAlias(), getTaxNo(), getBillingName(), getAddress(), getMobile(), getArea(), getTenantId(), getBusinessLicense(), getFax(), getLandLineNo(), getDelFlag());
    }
}
