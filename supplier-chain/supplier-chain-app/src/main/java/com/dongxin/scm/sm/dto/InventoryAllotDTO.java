package com.dongxin.scm.sm.dto;

import com.dongxin.scm.sm.entity.Inventory;
import lombok.Data;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;


@Data
public class InventoryAllotDTO {


    private List<Inventory> list;

    //stockHouseId
    private String warehouse;

    //调拨数
    private Double allotNumber;

    //库位
    private String stockLocation;

    //备注
    private String remarks;

    //卸货人
    private String dischargerName;

    //入库时间
    private Date warehouseTime;

    //调拨合计重量
    private Double allotWeight;

}
