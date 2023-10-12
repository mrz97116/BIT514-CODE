package com.dongxin.scm.sm.dto;

import lombok.Data;

@Data
public class DailyStatementDTO {

    /**
     * 最小厚度
     */
    double minThick;

    /**
     * 最大厚度
     */
    double maxThick;

    /**
     * 开始时间
     */
    String createTimeBegin;

    /**
     * 结束时间
     */
    String createTimeEnd;

    /**
     * 产品大类
     */
    String prodClassCode;

}
