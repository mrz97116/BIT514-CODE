package com.dongxin.scm.sm.dto;

import lombok.Data;

import java.util.List;

@Data
public class PrepareOrderDTO {

    private List<String> ids;

    private String remark;

    private String stockHouseId;
}
