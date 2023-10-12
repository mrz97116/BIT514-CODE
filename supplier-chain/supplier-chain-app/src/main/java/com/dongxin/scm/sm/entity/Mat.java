package com.dongxin.scm.sm.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dongxin.scm.enums.ProdClassCodeEnum;
import com.dongxin.scm.utils.ScmNumberUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.base.Joiner;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 物料信息表
 * @Author: jeecg-boot
 * @Date: 2020-11-02
 * @Version: V1.0
 */
@Data
@TableName("sm_mat")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "sm_mat对象", description = "物料信息表")
public class Mat implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
    /**
     * 更新人
     */
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
    /**
     * 牌号
     */
    @Excel(name = "牌号（钢级）", width = 15)
    @ApiModelProperty(value = "牌号")
    private String sgSign;
    /**
     * 品名代码
     */
    @Excel(name = "品名代码", width = 15)
    @ApiModelProperty(value = "品名代码")
    private String prodCode;
    /**
     * 品名中文
     */
    @Excel(name = "品名中文", width = 15)
    @ApiModelProperty(value = "品名中文")
    private String prodCname;
    /**
     * 产品大类
     */
    @Excel(name = "产品大类码描述", width = 15, dicCode = "prod_code")
    @Dict(dicCode = "prod_code")
    @ApiModelProperty(value = "产品大类")
    private String prodClassCode;
    /**
     * 产品大类描述
     */
//    @Excel(name = "产品大类描述", width = 15)
    @ApiModelProperty(value = "产品大类描述")
    private String prodClassDesc;
    /**
     * 品名中文别名
     */
    @Excel(name = "品名中文别名", width = 15)
    @ApiModelProperty(value = "品名中文别名")
    private String prodCnameOther;
    /**
     * 产品状态码
     */
//    @Excel(name = "产品状态码", width = 15)
    @ApiModelProperty(value = "产品状态码")
    private String matStatus;
    /**
     * 仓库id
     */
//    @Excel(name = "仓库", width = 15, dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @Dict(dictTable = "sm_stock", dicText = "name", dicCode = "id")
    @ApiModelProperty(value = "仓库")
    private String stockHouseId;
    /**
     * 库存类型
     */
    @Excel(name = "库存类型", width = 15, dicCode = "stock_type")
    @Dict(dicCode = "stock_type")
    @ApiModelProperty(value = "库存类型")
    private String stockType;
    /**
     * 材料号
     */
    @Excel(name = "材料号", width = 15)
    @ApiModelProperty(value = "材料号")
    private String matNo;
    /**
     * 材料实际重量
     */
    @Excel(name = "材料实重量", width = 15)
    @ApiModelProperty(value = "材料实重量")
    private Double matActWt;
    /**
     * 材料净重
     */
    @Excel(name = "材料实际重量", width = 15)
    @ApiModelProperty(value = "材料实际重量")
    private Double matNetWt;
    /**
     * 材料磅差重量
     */
    @Excel(name = "材料磅差重量", width = 15)
    @ApiModelProperty(value = "材料磅差重量")
    private Double matDiscrepWt;
    /**
     * 材料件数
     */
    @Excel(name = "材料件数", width = 15)
    @ApiModelProperty(value = "材料件数")
    private Integer matNum;
    /**
     * 物料种类
     */
    @Excel(name = "物料种类", width = 15, dicCode = "mat_kind")
    @Dict(dicCode = "mat_kind")
    @ApiModelProperty(value = "物料种类")
    private String matKind;
    /**
     * 可以销售重量
     */
//    @Excel(name = "可销售重量", width = 15)
    @ApiModelProperty(value = "可销售重量")
    private Double availableWeight;
    /**
     * 可销售数量
     */
//    @Excel(name = "可销售数量", width = 15)
    @ApiModelProperty(value = "可销售数量")
    private Integer availableQty;
    /**
     * 预用数量
     */
//    @Excel(name = "预用数量", width = 15)
    @ApiModelProperty(value = "预用数量")
    private Integer preuseQty;
    /**
     * 预用重量
     */
//    @Excel(name = "预用重量", width = 15)
    @ApiModelProperty(value = "预用重量")
    private Double preuseWeight;
    /**
     * 材料长度
     */
    @Excel(name = "材料长度", width = 15)
    @ApiModelProperty(value = "材料长度")
    private Double matLen;
    /**
     * 材料宽度
     */
    @Excel(name = "材料宽度", width = 15)
    @ApiModelProperty(value = "材料宽度")
    private Double matWidth;
    /**
     * 材料厚度
     */
    @Excel(name = "材料厚度", width = 15)
    @ApiModelProperty(value = "材料厚度")
    private Double matThick;
    /**
     * 实际长度
     */
//    @Excel(name = "实际长度", width = 15)
    @ApiModelProperty(value = "实际长度")
    private Double matActLen;
    /**
     * 实际宽度
     */
//    @Excel(name = "实际宽度", width = 15)
    @ApiModelProperty(value = "实际宽度")
    private Double matActWidth;
    /**
     * 实际厚度
     */
//    @Excel(name = "实际厚度", width = 15)
    @ApiModelProperty(value = "实际厚度")
    private Double matActThick;
    /**
     * 材料规格
     */
    @Excel(name = "材料规格", width = 15)
    @ApiModelProperty(value = "材料规格")
    private String custMatSpecs;
    /**
     * 收货顾客编码
     */
//    @Excel(name = "收货顾客编码", width = 15)
    @ApiModelProperty(value = "收货顾客编码")
    private String dealerId;
    /**
     * 采购价
     */
    @Excel(name = "采购价", width = 15)
    @ApiModelProperty(value = "采购价")
    private BigDecimal purchasePrice;
    /**
     * 成本价
     */
    @Excel(name = "成本价", width = 15)
    @ApiModelProperty(value = "成本价")
    private BigDecimal costPrice;
    /**
     * 技术标准
     */
    @Excel(name = "标准", width = 15)
    @ApiModelProperty(value = "技术标准")
    private String technicalStandard;
    /**
     * 原材料号
     */
//    @Excel(name = "原材料号", width = 15)
    @ApiModelProperty(value = "原材料号")
    private String rawMaterialNo;
    /**
     * 新增类型
     */
//    @Excel(name = "新增类型", width = 15)
    @ApiModelProperty(value = "新增类型")
    private String addType;
    /**
     * 逻辑删除
     */
    @ApiModelProperty(value = "逻辑删除")
    private Integer delFlag;
    /**
     * 材料理论重量
     */
//    @Excel(name = "材料理论重量", width = 15)
    @ApiModelProperty(value = "材料理论重量")
    private Double matTheoryWt;
    /**
     * 码单号
     */
//    @Excel(name = "码单号", width = 15)
    @ApiModelProperty(value = "码单号")
    private String stackingNo;
    /**
     * 经销订货用户名称
     */
//    @Excel(name = "经销订货用户名称", width = 15)
    @ApiModelProperty(value = "经销订货用户名称")
    private String orderCustCname;
    /**
     * 经销收货用户名称
     */
    @Excel(name = "收货用户名称", width = 15)
    @ApiModelProperty(value = "收货用户名称")
    private String consignUserName;
    /**
     * 计重方式
     */
    @Excel(name = "计重方式", width = 15, dicCode = "wt_method_code")
    @ApiModelProperty(value = "计重方式")
    @Dict(dicCode = "wt_method_code")
    private String wtMode;
    /**
     * 切边标记
     */
//    @Excel(name = "切边标记", width = 15)
    @ApiModelProperty(value = "切边标记")
    private String trimFlag;
    /**
     * 切边类型
     */
//    @Excel(name = "切边类型", width = 15)
    @ApiModelProperty(value = "切边类型")
    private String trimType;
    /**
     * 脱脂标记
     */
//    @Excel(name = "脱脂标记", width = 15)
    @ApiModelProperty(value = "脱脂标记")
    private String degreaseFlag;
    /**
     * 表面结构
     */
//    @Excel(name = "表面结构", width = 15)
    @ApiModelProperty(value = "表面结构")
    private String strucCode;
    /**
     * 产品等级
     */
//    @Excel(name = "产品等级", width = 15)
    @ApiModelProperty(value = "产品等级")
    private String prodClass;
    /**
     * 锌层结构(表面结构)
     */
//    @Excel(name = "锌层结构(表面结构)", width = 15)
    @ApiModelProperty(value = "锌层结构(表面结构)")
    private String platingStructure;
    /**
     * 表面处理方式(钝化方式)
     */
    @Excel(name = "钝化方式", width = 15)
    @ApiModelProperty(value = "钝化方式")
    private String surfaceTreatment;
    /**
     * 涂油量要求
     */
//    @Excel(name = "涂油量要求", width = 15)
    @ApiModelProperty(value = "涂油量要求")
    private String oilDemand;
    /**
     * 宽度精度等级（公差精度）
     */
//    @Excel(name = "宽度精度等级（公差精度）", width = 15)
    @ApiModelProperty(value = "宽度精度等级（公差精度）")
    private String widthTolAccu;
    /**
     * 表面质量等级
     */
//    @Excel(name = "表面质量等级", width = 15)
    @ApiModelProperty(value = "表面质量等级")
    private String surfaceQualityLevel;
    /**
     * 不平度精度
     */
//    @Excel(name = "不平度精度", width = 15)
    @ApiModelProperty(value = "不平度精度")
    private String flatlessAccu;
    /**
     * 锌层重量
     */
    @Dict(dicCode = "plating_weight")
    @Excel(name = "镀层重量", width = 15)
    @ApiModelProperty(value = "锌层重量")
    private String platingWeight;
    /**
     * 优面标记
     */
//    @Excel(name = "优面标记", width = 15)
    @ApiModelProperty(value = "优面标记")
    private String optimalFaceToward;
    /**
     * 光整要求
     */
    @Excel(name = "光整要求", width = 15)
    @ApiModelProperty(value = "光整要求")
    private String finishingRequirements;
    /**
     * 库位
     */
//    @Excel(name = "库位", width = 15)
    @ApiModelProperty(value = "库位")
    private String stockLocation;
    /**
     * 入库单号
     */
//    @Excel(name = "入库单号", width = 15)
    @ApiModelProperty(value = "入库单号")
    private String stockNo;
    /**
     * 出售标识
     */
//    @Excel(name = "出售标识", width = 15, dicCode = "yn")
    @Dict(dicCode = "yn")
    @ApiModelProperty(value = "出售标识")
    private String sellFlag;
    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private java.lang.Integer tenantId;
    /**
     * 车号
     */
    @Excel(name = "车号", width = 15)
    @ApiModelProperty(value = "车号")
    private String carNo;
    /**
     * 块号
     */
    private String cakeNo;
    /**
     * 入库时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "入库时间")
    private Date storageTime;
    /**
     * 单重
     */
    @Excel(name = "单重", width = 15)
    @ApiModelProperty(value = "单重")
    private java.lang.Double pieceWeight;
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
     * 支数
     */
    @Excel(name = "支数", width = 15)
    @ApiModelProperty(value = "支数")
    private Integer countNumber;

    /**
     * 出厂时刻
     */
    @TableField(exist = false)
    @Excel(name = "出厂时刻", width = 15)
    @ApiModelProperty(value = "出厂时刻")
    private String deliveryTime;
    /**
     * 柳钢装车时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "柳钢装车时间", width = 15)
    @ApiModelProperty(value = "柳钢装车时间")
    private Date carLoadingTime;
    /**
     * 备注
     */
//    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remarks;
    /**
     * 卸货人姓名
     */
    @Excel(name = "卸货人姓名", width = 15)
    @ApiModelProperty(value = "卸货人姓名")
    private String dischargerName;
    /**
     * 物料编码
     */
    @Excel(name = "物料编码", width = 15)
    @ApiModelProperty(value = "物料编码")
    private String matCode;
    /**
     * 加工标识
     */
    @Excel(name = "加工标识", width = 15)
    @ApiModelProperty(value = "加工标识")
    private java.lang.String processingIdentification;
    /**
     * 船号
     */
    @Excel(name = "船运管理单船号", width = 15)
    @ApiModelProperty(value = "船运管理单船号")
    private java.lang.String shippingManagementNo;

    /**
     * 经销导入的船号
     */
    @Excel(name = "船号", width = 15)
    @ApiModelProperty(value = "船号")
    private java.lang.String shipNo;
    @Excel(name = "货物编码", width = 15)
    @ApiModelProperty(value = "货物编码")
    private String goodsNo;
    /**运输方式*/
    @Excel(name = "运输方式", width = 15)
    @ApiModelProperty(value = "运输方式")
    private java.lang.String transportWay;
    /**
     * 到货状态
     */
    @Dict(dicCode = "arrival_status")
    @Excel(name = "到货状态", width = 15, dicCode = "arrival_status")
    @ApiModelProperty(value = "到货状态")
    private String arrivalStatus;
    /**
     * 到货审核人
     */
    @Excel(name = "到货审核人", width = 15)
    @ApiModelProperty(value = "到货审核人")
    private String arrivalChecker;
    /**
     * 到货审核日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "到货审核日期", width = 15)
    @ApiModelProperty(value = "到货审核日期")
    private Date arrivalCheckDate;
    /**
     * 到货状态
     */
    @Dict(dicCode = "allot_status")
    @Excel(name = "分货状态", width = 15, dicCode = "allot_status")
    @ApiModelProperty(value = "分货状态")
    private String allotStatus;
    /**
     * 库存id
     */
    private String inventoryId;
    /**
     * 入库单id
     */
    private String warehouseWarrantId;
    /**
     * 轧制号
     */
    private String planNo;

    /**
     * 单位
     */
    private String unit;

    @TableField(exist = false)
    private Date stockTime;

    @Excel(name = "实际交货点名称", width = 15)
    private String actualDeliveryLocation;

    public String generateCustMatSpecs() {
        String matLenStr = this.matLen == null ? null : ScmNumberUtils.delRedundantZero(this.matLen);
        String matWidthStr = this.matWidth == null ? null : ScmNumberUtils.delRedundantZero(this.matWidth);
        String matThickStr = this.matThick == null ? null : ScmNumberUtils.delRedundantZero(this.matThick);


        if ("0".equalsIgnoreCase(matLenStr)) {
            matLenStr = null;
        }

        if ("0".equalsIgnoreCase(matWidthStr)) {
            matWidthStr = null;
        }

        if ("0".equalsIgnoreCase(matThickStr)) {
            matThickStr = null;
        }

        if (ProdClassCodeEnum.H.getValue().equals(this.getProdClassCode())
                || ProdClassCodeEnum.F.getValue().equals(this.getProdClassCode())
                 ||   ProdClassCodeEnum.G.getValue().equals(this.getProdClassCode())) {
            matLenStr = "C";
        }

        Joiner joiner = Joiner.on("*").skipNulls();
        return joiner.join(matThickStr, matWidthStr, matLenStr);

    }

    public static void main(String[] args) {

        Mat mat = new Mat();
        mat.setProdClassCode(ProdClassCodeEnum.E.getValue());
        mat.setMatThick(75d);
        mat.setMatWidth(0d);
        mat.setMatLen(9000d);
        System.out.println(mat.generateCustMatSpecs());
    }


}
