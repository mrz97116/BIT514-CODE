package com.dongxin.scm.sm.dto;

import com.dongxin.scm.sm.entity.ShippingManagement;
import lombok.Data;

import java.util.List;

@Data
public class ShippingManagementDTO {

    private List<ShippingManagement> list;

    private String remark;

    private String customerId;

    private String destination;

    private String prepareOrderNo;

}
