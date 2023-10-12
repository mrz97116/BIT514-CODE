package com.dongxin.scm.wms.condition;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 7. 撤销提货计划
 */
@Getter
@Setter
@Builder
public class RevokeBillOfLadingPlanParam extends BaseCondition {
    private String billOFLadingNo; // 单号
    private String mainId; // 提单ID
    private String operatorName; //操作人
}
