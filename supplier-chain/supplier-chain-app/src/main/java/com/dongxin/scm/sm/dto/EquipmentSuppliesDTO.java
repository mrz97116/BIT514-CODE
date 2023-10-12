package com.dongxin.scm.sm.dto;

import com.dongxin.scm.sm.entity.Mat;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EquipmentSuppliesDTO {

    private String name;
    //库位
    private BigDecimal totalPrice;

}
