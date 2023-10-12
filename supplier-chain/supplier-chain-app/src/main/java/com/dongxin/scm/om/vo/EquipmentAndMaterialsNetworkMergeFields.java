package com.dongxin.scm.om.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EquipmentAndMaterialsNetworkMergeFields {
    /**
     * 产品名称
     */
    private String oldProdCname;

    /**
     * 规格
     */
    private Double orderThick;

    /**
     * 单价
     */
    private BigDecimal price;


}
