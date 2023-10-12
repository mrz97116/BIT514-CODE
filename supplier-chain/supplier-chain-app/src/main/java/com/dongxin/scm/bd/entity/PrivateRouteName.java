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
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 铁路专用线
 * @Author: jeecg-boot
 * @Date: 2020-11-19
 * @Version: V1.0
 */
@Data
@TableName("bd_private_route_name")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "bd_private_route_name对象", description = "铁路专用线")
public class PrivateRouteName implements Serializable {
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
     * 专用线代码
     */
    @Excel(name = "专用线代码", width = 15)
    @ApiModelProperty(value = "专用线代码")
    private java.lang.String routeCode;
    /**
     * 专用线名称
     */
    @Excel(name = "专用线名称", width = 15)
    @ApiModelProperty(value = "专用线名称")
    private java.lang.String routeName;
    /**
     * 到站代码
     */
    @Excel(name = "到站代码", width = 15, dictTable = "bd_train_station", dicText = "station_name", dicCode = "station_code")
    @Dict(dictTable = "bd_train_station", dicText = "station_name", dicCode = "station_code")
    @ApiModelProperty(value = "到站代码")
    private java.lang.String stationCode;
}
