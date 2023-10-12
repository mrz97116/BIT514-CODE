package com.dongxin.scm.sm.dto;

import lombok.Data;

import java.util.List;


@Data
public class ProcessingDTO {

    private String inventoryId;

    private List<ProcessYrmDTO> processDtoList;

    private String customerId;

    private String salesManId;

    private String remarks;
}
