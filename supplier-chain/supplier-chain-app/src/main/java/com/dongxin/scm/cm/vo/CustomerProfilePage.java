package com.dongxin.scm.cm.vo;

import com.dongxin.scm.cm.entity.CustomerAddress;
import com.dongxin.scm.cm.entity.CustomerBank;
import com.dongxin.scm.cm.entity.CustomerRate;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * @Description: 客户基础信息
 * @Author: jeecg-boot
 * @Date: 2020-09-16
 * @Version: V1.0
 */
@Data
@ApiModel(value = "cm_customer_profilePage对象", description = "客户基础信息")
public class CustomerProfilePage {

    /**
     * 主键
     */
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
     * 状态
     */
    @Excel(name = "状态", width = 15, dicCode = "status")
    @Dict(dicCode = "status")
    @ApiModelProperty(value = "状态")
    private java.lang.String status;
    /**
     * 类型
     */
    @Excel(name = "类型", width = 15, dicCode = "CUSTOMER_TPYE")
    @Dict(dicCode = "CUSTOMER_TPYE")
    @ApiModelProperty(value = "类型")
    private java.lang.String type;
    /**
     * 租户id
     */

    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;

    @ExcelCollection(name = "顾客银行卡信息")
    @ApiModelProperty(value = "顾客银行卡信息")
    private List<CustomerBank> customerBankList;
    @ExcelCollection(name = "顾客星级评分")
    @ApiModelProperty(value = "顾客星级评分")
    private List<CustomerRate> customerRateList;
    @ExcelCollection(name = "顾客收货地址信息")
    @ApiModelProperty(value = "顾客收货地址信息")
    private List<CustomerAddress> customerAddressList;

}
