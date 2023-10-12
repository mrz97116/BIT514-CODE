package com.dongxin.scm.sm.dto;

import com.dongxin.scm.sm.entity.Mat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class MatIdDTO {

    private Map<String, BigDecimal> map;

    private List<Mat> list;

    private String warehouse;

    private String remarks;

    private String dischargerName;

    private Date warehouseTime;

}
