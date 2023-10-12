package com.dongxin.scm.sm.entity;

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
import java.util.Date;

/**
 * @Description: 库存信息
 * @Author: jeecg-boot
 * @Date: 2020-12-03
 * @Version: V1.0
 */
@Data
@TableName("sm_inventory")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "sm_inventory对象", description = "库存信息")
public class Inventory implements Serializable {
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
    @Dict(  dictTable = "sys_user", dicText = "realname", dicCode = "username")
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy/MM/dd/HH:mm")
    @DateTimeFormat(pattern = "yyyy/MM/dd/HH:mm")
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
     * 材料长度
     */
    @Excel(name = "材料长度", width = 15)
    @ApiModelProperty(value = "材料长度")
    private java.lang.Double matLen;
    /**
     * 材料宽度
     */
    @Excel(name = "材料宽度", width = 15)
    @ApiModelProperty(value = "材料宽度")
    private java.lang.Double matWidth;
    /**
     * 材料厚度
     */
    @Excel(name = "材料厚度", width = 15)
    @ApiModelProperty(value = "材料厚度")
    private java.lang.Double matThick;
    /**
     * 逻辑删除
     */
//	@Excel(name = "逻辑删除", width = 15)
    @ApiModelProperty(value = "逻辑删除")
    private java.lang.Integer delFlag;
    /**
     * 租户id
     */

    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
    /**
     * 规格
     */
    @Excel(name = "规格", width = 15)
    @ApiModelProperty(value = "规格")
    private java.lang.String custMatSpecs;
    /**
     * 物料种类
     */
    @Excel(name = "物料种类", width = 15, dicCode = "mat_kind")
    @Dict(dicCode = "mat_kind")
    @ApiModelProperty(value = "物料种类")
    private java.lang.String matKind;
    /**
     * 材料总件数
     */
    @Excel(name = "材料总件数", width = 15)
    @ApiModelProperty(value = "材料总件数")
    private java.lang.Double matNum;
    /**
     * 品名中文别名
     */
    @Excel(name = "品名中文别名", width = 15)
    @ApiModelProperty(value = "品名中文别名")
    private java.lang.String prodCnameOther;
    /**
     * 产品大类
     */
    @Excel(name = "产品大类", width = 15, dicCode = "prod_code")
    @Dict(dicCode = "prod_code")
    @ApiModelProperty(value = "产品大类")
    private java.lang.String prodClassCode;
    /**
     * 牌号
     */
    @Excel(name = "牌号", width = 15)
    @ApiModelProperty(value = "牌号")
    private java.lang.String sgSign;
    /**
     * 品名中文
     */
    @Excel(name = "品名中文", width = 15)
    @ApiModelProperty(value = "品名中文")
    private java.lang.String prodCname;
    /**
     * 仓库id
     */
    @Excel(name = "仓库", width = 15, dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @Dict(dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "仓库id")
    private java.lang.String stockHouseId;
    /**
     * 可销售重量
     */
    @Excel(name = "可销售重量", width = 15)
    @ApiModelProperty(value = "可销售重量")
    private java.lang.Double availableWeight;
    /**
     * 可销售数量
     */
    @Excel(name = "可销售数量", width = 15)
    @ApiModelProperty(value = "可销售数量")
    private java.lang.Double availableQty;
    /**
     * 重量
     */
    @Excel(name = "重量", width = 15)
    @ApiModelProperty(value = "重量")
    private java.lang.Double weight;
    /**
     * 材料号
     */
    @Excel(name = "材料号", width = 15)
    @ApiModelProperty(value = "材料号")
    private java.lang.String matNo;
    /**
     * 单重
     */
    @Excel(name = "单重", width = 15)
    @ApiModelProperty(value = "单重")
    private java.lang.Double pieceWeight;
    /**
     * 计重方式
     */
    @Excel(name = "计重方式", width = 15, dicCode = "wt_method_code")
    @Dict(dicCode = "wt_method_code")
    @ApiModelProperty(value = "计重方式")
    private java.lang.String wtMode;
    /**
     * 旧系统产品中文名
     */
    @Excel(name = "旧系统产品中文名", width = 15)
    @ApiModelProperty(value = "旧系统产品中文名")
    private String oldProdCname;
    /**
     * 经销产品名称中文名
     */
    @Excel(name = "经销产品名称中文名", width = 15)
    @ApiModelProperty(value = "经销产品名称中文名")
    private String jxProdCname;
    /**
     * SAP产品中文名
     */
    @Excel(name = "SAP产品中文名", width = 15)
    @ApiModelProperty(value = "SAP产品中文名")
    private String sapProdCname;
    /**
     * 库位
     */
    @Excel(name = "库位", width = 15)
    @ApiModelProperty(value = "库位")
    private String stockLocation;
    /**
     * 物料编码
     */
    @Excel(name = "物料编码", width = 15)
    @ApiModelProperty(value = "物料编码")
    private String matCode;
    /**
     * 块号
     */
//    @Excel(name = "块号", width = 15)
    @ApiModelProperty(value = "块号")
    private String cakeNo;

    /**
     * 单位
     */
    private String unit;
    /**
     * 锁定重量
     */
    @Excel(name = "锁定重量", width = 15)
    @ApiModelProperty(value = "锁定重量")
    private java.lang.Double lockWeight;
    /**
     * 锁定数量
     */
    @Excel(name = "锁定数量", width = 15)
    @ApiModelProperty(value = "锁定数量")
    private java.lang.Double lockQty;
    /**
     * 锁定id
     */
    @Excel(name = "锁定id", width = 15)
    @ApiModelProperty(value = "锁定id")
    private java.lang.Double inventoryLockId;

    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "船运管理单船号", width = 15)
    @ApiModelProperty(value = "船运管理单船号")
    private String shippingManagementNo;
    @Excel(name = "货物编码", width = 15)
    @ApiModelProperty(value = "货物编码")
    private String goodsNo;
    /**
     * 锌层重量
     */
    @Excel(name = "锌层重量", width = 15)
    @ApiModelProperty(value = "锌层重量")
    private String platingWeight;
    /**
     * 表面处理方式(钝化方式)
     */
    @Excel(name = "钝化方式", width = 15)
    @ApiModelProperty(value = "钝化方式")
    private String surfaceTreatment;

    @Excel(name = "在库时长", width = 15)
    @ApiModelProperty(value = "在库时长")
    private Integer inStockDays;

    @Excel(name = "车号", width = 15)
    @ApiModelProperty(value = "车号")
    private String carNo;

    @Dict(dictTable = "cm_customer_profile", dicText = "company_name", dicCode = "id")
    private String prepareCustomerId;

    @Excel(name = "入库时间", width = 15)
    private Date stockTime;

    //用于分类， 根据名称，钢号，厚宽长作为分类字符串
    public String geneKindStr() {
        return oldProdCname + sgSign + matThick + matWidth + matLen;
    }
}
