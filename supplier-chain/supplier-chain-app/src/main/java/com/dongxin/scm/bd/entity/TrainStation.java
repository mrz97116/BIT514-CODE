package com.dongxin.scm.bd.entity;

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

/**
 * @Description: 铁路到站表
 * @Author: jeecg-boot
 * @Date: 2020-11-19
 * @Version: V1.0
 */
@Data
@TableName("bd_train_station")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "bd_train_station对象", description = "铁路到站表")
public class TrainStation implements Serializable {
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
     * 到站编码
     */
    @Excel(name = "到站编码", width = 15)
    @ApiModelProperty(value = "到站编码")
    private java.lang.String stationCode;
    /**
     * 到站名称
     */
    @Excel(name = "到站名称", width = 15)
    @ApiModelProperty(value = "到站名称")
    private java.lang.String stationName;
    /**
     * 到局代码
     */
    @Excel(name = "到局代码", width = 15)
    @ApiModelProperty(value = "到局代码")
    private java.lang.String bureauCode;
    /**
     * 到局名称
     */
    @Excel(name = "到局名称", width = 15)
    @ApiModelProperty(value = "到局名称")
    private java.lang.String bureauName;
}
