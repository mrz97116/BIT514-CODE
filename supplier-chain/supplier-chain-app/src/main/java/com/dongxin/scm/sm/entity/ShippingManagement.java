package com.dongxin.scm.sm.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
 * @Description: 船运管理
 * @Author: jeecg-boot
 * @Date:   2021-07-20
 * @Version: V1.0
 */
@Data
@TableName("sm_shipping_management")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sm_shipping_management对象", description="船运管理")
public class ShippingManagement implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
    /**创建人*/
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
    /**分单号*/
    @Excel(name = "分单号", width = 15)
    @ApiModelProperty(value = "分单号")
    private java.lang.String dispatchShipBillNo;
    /**目的地*/
    @ApiModelProperty(value = "目的地")
    private java.lang.String destinationName;
    /**出厂时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "出厂时间")
    private java.util.Date deliveryTime;
    /**产品名*/
    @Excel(name = "产品名", width = 15)
    @ApiModelProperty(value = "产品名")
    private java.lang.String productName;

    /**钢号*/
    @Excel(name = "钢号", width = 15)
    @ApiModelProperty(value = "钢号")
    private java.lang.String steelNo;

    /**规格*/
    @ApiModelProperty(value = "规格")
    private java.lang.String matSpecs;

    /**尺寸*/
    @ApiModelProperty(value = "尺寸")
    private java.lang.String matSize;

    /**
     * 材料厚度
     */
    @Excel(name = "材料厚度", width = 15)
    @ApiModelProperty(value = "材料厚度")
    private Double matThick;
    /**
     * 材料宽度
     */
    @Excel(name = "材料宽度", width = 15)
    @ApiModelProperty(value = "材料宽度")
    private Double matWidth;
    /**
     * 材料长度
     */
    @Excel(name = "材料长度", width = 15)
    @ApiModelProperty(value = "材料长度")
    private Double matLen;

    /**材料号*/
    @Excel(name = "材料号", width = 15)
    @ApiModelProperty(value = "材料号")
    private java.lang.String matNo;

    /**重量*/
    @Excel(name = "重量", width = 15)
    @ApiModelProperty(value = "重量")
    private java.lang.Double weight;
    @Excel(name = "车牌号", width = 15)
    /**车牌号*/
    @ApiModelProperty(value = "车牌号")
    private java.lang.String carNo;
    /**数量*/
    @ApiModelProperty(value = "数量")
    private java.lang.Integer quantity;

    /**备注*/
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
    /**物料状态*/
    @Dict(dicCode = "shipping_mat_status")
    @ApiModelProperty(value = "物料状态")
    private java.lang.String matStatus;
    /**租户id*/
    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
    /**材料id*/
    @ApiModelProperty(value = "材料id")
    private java.lang.String matId;
    /**
     * 逻辑删除
     */
    @ApiModelProperty(value = "逻辑删除")
    private Integer delFlag;
    /**
     * 产品大类
     */
    @Dict(dicCode = "prod_code")
    @ApiModelProperty(value = "产品大类")
    private java.lang.String prodClassCode;
    /**
     * 船号
     */
    @Excel(name = "船号", width = 15)
    @ApiModelProperty(value = "船号")
    private java.lang.String shipNo;

    //状态
    private String prepareStatus;

    //单价
    private BigDecimal price;

    //预开单id
    private String prepareOrderId;

    //提货委托id
    private String deliveryCommissionId;

    /**表面质量级别*/
    @Excel(name = "表面质量级别", width = 15)
    @ApiModelProperty(value = "表面质量级别")
    private java.lang.String surfaceQualityLevel;
    /**涂油要求*/
    @Excel(name = "涂油要求", width = 15)
    @ApiModelProperty(value = "涂油要求")
    private java.lang.String oilDemand;
    /**镀层重量*/
    @Excel(name = "镀层重量", width = 15)
    @ApiModelProperty(value = "镀层重量")
    private java.lang.String platingWeight;
    /**钝化方式*/
    @Excel(name = "钝化方式", width = 15)
    @ApiModelProperty(value = "钝化方式")
    private java.lang.String surfaceTreatment;
    /**光整要求*/
    @Excel(name = "光整要求", width = 15)
    @ApiModelProperty(value = "光整要求")
    private java.lang.String finishingRequirements;
    /**镀层结构*/
    @Excel(name = "镀层结构", width = 15)
    @ApiModelProperty(value = "镀层结构")
    private java.lang.String platingStructure;
    /**位置*/
    @Excel(name = "位置", width = 15)
    @ApiModelProperty(value = "位置")
    private java.lang.String location;

    private String actualDeliveryLocation;

    @Excel(name = "卸车时间", width = 15, format = "yyyy-MM-dd")
    private Date unloadTime;

    @Excel(name = "卸货类型", width = 15)
    private String unloadType;

}
