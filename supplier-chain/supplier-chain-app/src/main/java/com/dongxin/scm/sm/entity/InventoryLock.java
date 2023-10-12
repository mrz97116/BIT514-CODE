package com.dongxin.scm.sm.entity;

import com.baomidou.mybatisplus.annotation.*;
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
 * @Description: 库存锁定
 * @Author: jeecg-boot
 * @Date:   2021-06-23
 * @Version: V1.0
 */
@Data
@TableName("sm_inventory_lock")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sm_inventory_lock对象", description="库存锁定")
public class InventoryLock implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
    /**创建人*/
    @Dict(  dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
    /**创建日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
    /**更新人*/
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
    /**更新日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
    /**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
    /**仓库id*/
    @Excel(name = "仓库id", width = 15, dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @Dict(dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "仓库id")
    private java.lang.String stockHouseId;
    /**锁定状态*/
    @Excel(name = "锁定状态", width = 15)
    @ApiModelProperty(value = "锁定状态")
    private java.lang.String lockStatus;
    /**逻辑删除*/
//    @Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private java.lang.Integer delFlag;
    /**租户id*/
//    @Excel(name = "租户id", width = 15)
    @ApiModelProperty(value = "租户id")
    private java.lang.String tenantId;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
    /**解锁时间*/
    @Excel(name = "解锁时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "解锁时间")
    private java.util.Date unlockTime;
    /**锁定单号*/
    @Excel(name = "锁定单号", width = 15)
    @ApiModelProperty(value = "锁定单号")
    private java.lang.String lockNo;
    /**解锁人*/
    @Excel(name = "解锁人", width = 15)
    @ApiModelProperty(value = "解锁人")
    private java.lang.String unlockMan;
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
    /**库存id*/
//    @Excel(name = "库存id", width = 15)
    @ApiModelProperty(value = "库存id")
    private java.lang.String inventoryId;
    /**材料号*/
    @Excel(name = "材料号", width = 15)
    @ApiModelProperty(value = "材料号")
    private java.lang.String matNo;
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
    /**厂编*/
    @Excel(name = "厂编", width = 15)
    @ApiModelProperty(value = "厂编")
    private String stockLocation;
    /** 钢号*/
    @Excel(name = "钢号", width = 15)
    @ApiModelProperty(value = "钢号")
    private java.lang.String sgSign;

}
