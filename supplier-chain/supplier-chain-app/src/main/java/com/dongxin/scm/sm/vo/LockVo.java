package com.dongxin.scm.sm.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;


@Data
public class LockVo {
    private String id;

    private  String inventoryLockId;

    private  String inventoryId;

    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
    /**创建日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")

    private java.util.Date createTime;
    /**锁定数量*/
    @Excel(name = "锁定数量", width = 15)
    @ApiModelProperty(value = "锁定数量")
    private java.lang.Double lockQty;
    /**锁定重量*/
    @Excel(name = "锁定重量", width = 15)
    @ApiModelProperty(value = "锁定重量")
    private java.lang.Double lockWeight;
    /**解锁数量*/
    @Excel(name = "解锁数量", width = 15)
    @ApiModelProperty(value = "解锁数量")
    private java.lang.Double unlockQty;
    /**解锁重量*/
    @Excel(name = "解锁重量", width = 15)
    @ApiModelProperty(value = "解锁重量")
    private java.lang.Double unlockWeight;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
    /**仓库*/
    @Excel(name = "仓库", width = 15)
    @ApiModelProperty(value = "仓库")
    private java.lang.String stockHouseId;
    /**更新人*/
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "解锁人")
    private java.lang.String unlockMan;
    /**材料长度*/
    @Excel(name = "材料长度", width = 15)
    @ApiModelProperty(value = "材料长度")
    private java.lang.Double matLen;
    /**材料宽度*/
    @Excel(name = "材料宽度", width = 15)
    @ApiModelProperty(value = "材料宽度")
    private java.lang.Double matWidth;
    /**材料厚度*/
    @Excel(name = "材料厚度", width = 15)
    @ApiModelProperty(value = "材料厚度")
    private java.lang.Double matThick;
    /**品名中文*/
    @Excel(name = "品名中文", width = 15)
    @ApiModelProperty(value = "品名中文")
    private java.lang.String prodCname;
    /**产品大类*/
    @Excel(name = "产品大类", width = 15)
    @ApiModelProperty(value = "产品大类")
    private java.lang.String prodClassCode;
}
