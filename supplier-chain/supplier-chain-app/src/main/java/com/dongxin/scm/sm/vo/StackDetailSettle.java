package com.dongxin.scm.sm.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;

@Data
public class StackDetailSettle {

    private static final long serialVersionUID = 1L;

    //装车单号
    private String stackingNo;

    //顾客id
    private String customerId;

    //业务员
    private String salesMan;

    //单价
    private java.math.BigDecimal price;

    //总价
    private java.math.BigDecimal totalAmount;

    //数量
    private Double qty;

    //重量
    private Double weight;

    //长
    private Double matLen;

    //宽
    private Double matWidth;

    //厚
    private Double matThick;

    //品名中文
    private String prodCname;

    //品名中文别名
    private String prodCnameOther;

    //牌号
    private String sgSign;

    //产品名称
    private String oldProdCname;

    @Dict(  dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @ApiModelProperty(value = "创建人")
    private String createBy;
}
