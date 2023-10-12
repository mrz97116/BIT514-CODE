package com.dongxin.scm.sm.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 入库单
 * @Author: jeecg-boot
 * @Date: 2021-04-06
 * @Version: V1.0
 */
@Data
@TableName("sm_warehouse_warrant")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "sm_warehouse_warrant对象", description = "入库单")
public class WarehouseWarrant implements Serializable {
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
    @Dict(dictTable = "sys_user",dicText = "realname",dicCode = "username")
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
     * 仓库id
     */
    @Excel(name = "仓库id", width = 15)
    @Dict(dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "仓库id")
    private java.lang.String stockId;
    /**
     * 来源地
     */
    @Excel(name = "来源地", width = 15)
    @ApiModelProperty(value = "来源地")
    private java.lang.String areaSource;
    /**
     * 数量合计
     */
    @Excel(name = "数量合计", width = 15)
    @ApiModelProperty(value = "数量合计")
    private java.lang.Integer qty;
    /**
     * 重量合计
     */
    @Excel(name = "重量合计", width = 15)
    @ApiModelProperty(value = "重量合计")
    private java.lang.Double weight;
    /**
     * 入库时间
     */
    @Excel(name = "入库时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "入库时间")
    private java.util.Date stockTime;
    /**
     * 备注
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
    /**
     * 逻辑删除
     */
    @ApiModelProperty(value = "逻辑删除")
    private Integer delFlag;
    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;

    /**
     * 入库单号
     */
    @ApiModelProperty(value = "入库单号")
    private String stockNo;
}
