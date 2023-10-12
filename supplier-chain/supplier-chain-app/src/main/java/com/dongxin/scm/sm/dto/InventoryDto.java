package com.dongxin.scm.sm.dto;

import lombok.Data;

@Data
public class InventoryDto {


    private String stockHouseId;

    private double matLen;

    private double matWidth;

    private double matThick;

    private String sgSign;

    private String prodCname;

    private String prodClassCode;

    private double weight;

    private String matNo;

    private String prodCnameOther;

}
