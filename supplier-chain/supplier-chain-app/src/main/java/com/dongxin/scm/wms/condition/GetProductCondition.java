package com.dongxin.scm.wms.condition;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 1. 获取产品信息
 */
@Setter
@Getter
@Builder
public class GetProductCondition extends BaseCondition {
    private String productName; // 品名
    private String steelGradeName; // 材质
    private String steelMillsName; // 产地
    private String standardName; // 生产标准
    private String weightMode; // 计量方式
    private BigDecimal length; // 长度
    private BigDecimal width; // 宽度
    private BigDecimal thick; // 厚度
}
