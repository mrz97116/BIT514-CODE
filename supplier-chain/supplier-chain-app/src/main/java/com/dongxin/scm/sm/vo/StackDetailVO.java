package com.dongxin.scm.sm.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author ：melon
 * @date ：Created in 2020/10/27 20:40
 * 装车单明细VO，汇总并打平stack和stackDet
 */
@Data
public class StackDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String stackDetailId;

    //顾客id
    private String customerId;

    //顾客名称
    private String companyName;

    //合同号
    private String contractNo;

    //装车单号
    private String stackingNo;

    //提单号
    private String saleBillNo;

    //
    private BigDecimal totalAmount;

    //装车单生成时间
    private Date createTime;

    //备注
    private java.lang.String remark;

    //产品大类
    private String prodCode;

//    @Dict(  dictTable = "sys_user", dicText = "realname", dicCode = "username")
//    @ApiModelProperty(value = "创建人")
    private String createBy;

    //车号
    private String carNo;

//    //起始时间
//    private Date createTimeBegin;

//    //终止时间
//    private Date createTimeEnd;

    /**
     * 提单起始日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "起始日期")
    private Date createTimeBegin;
    /**
     * 提单终止日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "终止日期")
    private Date createTimeEnd;


    /**
     * 装车单起始日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "起始日期")
    private Date stackTimeBegin;
    /**
     * 装车单终止日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "终止日期")
    private Date stackTimeEnd;


}
