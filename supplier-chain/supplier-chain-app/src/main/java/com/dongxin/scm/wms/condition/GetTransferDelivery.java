package com.dongxin.scm.wms.condition;

import com.dongxin.scm.sm.entity.GetActualDeliveryDet;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GetTransferDelivery {
    private String transferOwnershipPlanNo; //提货单号
    private String transferOwnershipNo; //实提单号
    private String transferDate; //出库时间
    private String operateFlag; //操作类型 新增还是删除
    private String operatorName; //操作人
    private String remark; // 备注
    private List<GetActualDeliveryDet> detail; //实提明细

    @Data
    public static class Detail {
        private String materialNo; //材料号
        private String productCode; //产品代码
        private String productName; //品名
        private String steelGradeName; //材质
        private String steelMillsName; //产地
        private String standardName; //生产标准
        private BigDecimal length; //长度
        private BigDecimal width; //宽度
        private BigDecimal thick; //厚度
        private int packageCount; //包装
        private int weightMode; //计量方式
        private String numberUnit; //数量单位
        private String weightUnit; //重量单位
        private String originalCustomerName; //货物性质
        private BigDecimal quantity; //数量
        private BigDecimal weight; //重量
        private String isOwenership; //是否出库指定
    }
}
