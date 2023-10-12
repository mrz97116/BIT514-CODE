package com.dongxin.scm.om.entity;

import cn.hutool.core.util.StrUtil;
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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @Description: 销售单
 * @Author: jeecg-boot
 * @Date: 2020-11-23
 * @Version: V1.0
 */
@ApiModel(value = "om_sales_order对象", description = "销售单")
@Data
@TableName("om_sales_order")
public class SalesOrder implements Serializable {
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
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
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
     * 提单号
     */
    @Excel(name = "提单号", width = 15)
    @ApiModelProperty(value = "提单号")
    private java.lang.String saleBillNo;
    /**
     * 合同编号
     */
    @Excel(name = "合同编号", width = 15)
    @ApiModelProperty(value = "合同编号")
    private java.lang.String contractNo;
    /**
     * 订单编号
     */
    @Excel(name = "订单编号", width = 15)
    @ApiModelProperty(value = "订单编号")
    private java.lang.String orderNo;
    /**
     * 客户编码
     */
    @Excel(name = "客户编码", width = 15, dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @Dict(dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    @ApiModelProperty(value = "客户编码")
    private java.lang.String customerId;
    /**
     * 顾客名称
     */
    @Excel(name = "顾客", width = 15)
    @ApiModelProperty(value = "顾客")
    private String customerName;
    /**
     * 助记码
     */
    @Excel(name = "助记码", width = 15)
    @ApiModelProperty(value = "助记码")
    private java.lang.String alias;
    /**
     * 控款记录id
     */
    @Excel(name = "控款记录id", width = 15)
    @ApiModelProperty(value = "控款记录id")
    private java.lang.String relationshipHoldId;
    /**
     * 产品大类
     */
    @Excel(name = "产品大类", width = 15, dicCode = "prod_code")
    @Dict(dicCode = "prod_code")
    @ApiModelProperty(value = "产品大类")
    private java.lang.String prodClassCode;
    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private java.lang.String tenantId;
    /**
     * 仓库id
     */
    @Excel(name = "仓库id", width = 15, dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @Dict(dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "仓库id")
    private java.lang.String stockId;
    /**
     * 到站编码
     */
    @Excel(name = "到站编码", width = 15, dictTable = "bd_train_station", dicText = "station_name", dicCode = "station_code")
    @Dict(dictTable = "bd_train_station", dicText = "station_name", dicCode = "station_code")
    @ApiModelProperty(value = "到站编码")
    private java.lang.String stationCode;
    /**
     * 专用线编码
     */
    @Excel(name = "专用线编码", width = 15, dictTable = "bd_private_route_name", dicText = "route_name", dicCode = "route_code")
    @Dict(dictTable = "bd_private_route_name", dicText = "route_name", dicCode = "route_code")
    @ApiModelProperty(value = "专用线编码")
    private java.lang.String routeCode;
    /**
     * 运输方式编码
     */
    @Excel(name = "运输方式编码", width = 15, dicCode = "trnp_mode_code")
    @Dict(dicCode = "trnp_mode_code")
    @ApiModelProperty(value = "运输方式编码")
    private java.lang.String trnpModeCode;
    /**
     * 车号
     */
    @Excel(name = "车号", width = 15)
    @ApiModelProperty(value = "车号")
    private java.lang.String carNo;
    /**
     * 业务员
     */
    @Excel(name = "业务员", width = 15)
    @Dict(dictTable = "cm_salesman_info", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "业务员")
    private java.lang.String salesMan;
    /**
     * 提货人
     */
    @Excel(name = "提货人", width = 15)
    @ApiModelProperty(value = "提货人")
    private java.lang.String consigneeName;
    /**
     * 身份证
     */
    @Excel(name = "身份证", width = 15)
    @ApiModelProperty(value = "身份证")
    private java.lang.String idCard;
    /**
     * 目的地
     */
    @Excel(name = "目的地", width = 15)
    @ApiModelProperty(value = "目的地")
    private java.lang.String destination;

    /**
     * 提货人手机号
     */
    @Excel(name = "提货人手机号", width = 15)
    @ApiModelProperty(value = "提货人手机号")
    private java.lang.String phone;
    /**
     * 逻辑删除
     */
    @Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private Integer delFlag;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 是否已装车
     */
    @Excel(name = "是否已装车", width = 15)
    @Dict(dicCode = "yes_no_status")
    @ApiModelProperty(value = "是否已装车")
    private String createStackStatus;
    /**
     * 收料人
     */
    @Excel(name = "收料人", width = 15)
    @ApiModelProperty(value = "收料人")
    private String receivingName;
    /**
     * 收料人电话
     */
    @Excel(name = "收料人电话", width = 15)
    @ApiModelProperty(value = "收料人电话")
    private String receivingPhone;
    /**
     * 收料地址
     */
    @Excel(name = "收料地址", width = 15)
    @ApiModelProperty(value = "收料地址")
    private String receivingAddress;

    /**
     * 重量
     */
    @Excel(name = "重量", width = 15)
    @ApiModelProperty(value = "重量")
    private java.lang.Double weight;
    /**
     * 发货时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "发货时间")
    private Date shipDate;

    /**
     * 旧系统销售计划单号
     */
    @Excel(name = "旧系统销售计划单号", width = 15)
    @ApiModelProperty(value = "旧系统销售计划单号")
    private String oldNo;

    @TableField(exist = false)
    private String stackId;

    /**
     * 数据标识 addBy liujiazhi 2021.3.27
     */
    @Excel(name = "付款标识", width = 15)
    @ApiModelProperty(value = "付款标识")
    private String paymentFlag;

    @Excel(name = "设备物资标识", width = 15, dicCode = "yes_no_status")
    @Dict(dicCode = "yes_no_status")
    @ApiModelProperty(value = "设备物资标识")
    private String equipmentSuppliesFlag;

    /**
     * 制单时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "制单时间")
    private Date orderTime;

    /**
     * 提单状态
     */
    private String status;

    /**
     * 总金额
     */
    @Excel(name = "总金额", width = 15)
    @ApiModelProperty(value = "总金额")
    private java.math.BigDecimal totalAmount;
    /**
     * 装货人
     */
    @Excel(name = "装货人", width = 15)
    @ApiModelProperty(value = "装货人")
    private String shipperName;
    /**
     * 授信可用金额
     */
    @TableField(exist = false)
    private BigDecimal creditAvailableAccount;
    /**
     * 来款可用金额
     */
    @TableField(exist = false)
    private BigDecimal incomeAvailableAccount;
    /**
     * 总金额(YRM)
     */
    @TableField(exist = false)
    private BigDecimal totalAccountYRM;

    private String signatureId;

    /**
     * 截止日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "截止日期")
    private Date closeTime;

    /**
     * 车队名称
     */
    @Excel(name = "车队名称", width = 15)
    @ApiModelProperty(value = "车队名称")
    private java.lang.String fleetName;
    /**
     * 推送物流园
     */
    @Excel(name = "推送物流园", width = 15)
    @ApiModelProperty(value = "推送物流园")
    private String pushLogistics;
    /**
     * 失败原因
     */
    @Excel(name = "失败原因", width = 15)
    @ApiModelProperty(value = "失败原因")
    private String failureCause;
    /**
     * 装车单编号
     */
    @TableField(exist = false)
    private String stackNo;
    /**
     * 生成发票状态
     */
    @Excel(name = "是否生成发票", width = 15)
    @Dict(dicCode = "yes_no_status")
    @ApiModelProperty(value = "是否生成发票")
    private String createInvoiceStatus;
    /**
     * 生成发票时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "生成发票时间")
    private Date createInvoiceTime;

    /**
     * 模板id
     */
    private String templateId;

    /**
     * 是否自动发起
     */
    private String launch;

    /**
     * 是否使用模板
     */
    private String isTemplateUse;

    @TableField(exist = false)
    private BigDecimal customerNeedToPayAmount;
    /**
     * 物流园目的地
     */
    @TableField(exist = false)
    private String pushDestination;
    /**
     * 物流园车号
     */
    @TableField(exist = false)
    private String pushCarNo;


    /**
     * 是否自动选款
     */
    private String autoSelectFund;

    /**
     * 选款id
     */
    private String fundIds;

    //预开单id
    private String prepareOrderId;

    //广东公司现货单号
    private String extendBillNo;

    public List<String> generateFundIdList() {
        List<String> fundIdList = newArrayList();
        if (StrUtil.isNotBlank(fundIds)) {
            fundIdList = Arrays.asList(fundIds.split(","));
        }
        return fundIdList;
    }

    //用款描述
    private String fundDesc;

    //广东公司物料车皮号
    private String carNos;

    //广东公司物料船号
    private String shipNos;

    //附件
    private String attach;
    //广东导入装车实绩标识
    private String gdImportStackDetail;

    //打印次数
    private Integer printNum;

}
