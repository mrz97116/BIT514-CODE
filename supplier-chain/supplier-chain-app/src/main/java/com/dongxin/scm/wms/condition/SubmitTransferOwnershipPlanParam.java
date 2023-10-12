package com.dongxin.scm.wms.condition;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 8. 提交过户计划
 */
@Getter
@Setter
@Builder
public class SubmitTransferOwnershipPlanParam extends BaseCondition {
    private String warehouseCode; // 仓库代码
    private String transferOwnershipPlanNo; // 提单号
    private String customerName; // 原货主
    private String nextCustomerName; // 现货主
    private String settlemtCompany; // 结算单位
    private String effectiveDate ; // 有效时间
    private String operatorName ; // 操作人名字
    private String remark ; // 备注
    private List<Detail> detail;

    @Getter
    @Setter
    @Builder
    public static class Detail {
//        private String detailId; //明细id
        private String productCode; // 产品代码
        private String productName; //品名
        private String steelGradeName; //材质
        private String steelMillsName; //产地
        private String standardName; //生成标准
        private BigDecimal length; //长度
        private BigDecimal width; //宽度
        private BigDecimal thick; //厚度
        private int packageCount; //包装
        private BigDecimal singleWeight; //单重
        private int weightMode; //计量方式
        private String numberUnit; //数量单位
        private String weightUnit; //重量单位
        private String originalCustomerName; //货物性质
        private BigDecimal quantity; //出库数量
        private BigDecimal weight; //出库重量
        private int isOwnership; //出库指定
        private String materialNo; //材料号
    }
}
