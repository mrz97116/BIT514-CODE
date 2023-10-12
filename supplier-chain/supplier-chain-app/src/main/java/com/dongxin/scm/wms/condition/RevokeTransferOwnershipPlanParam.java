package com.dongxin.scm.wms.condition;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 9. 撤销过户计划
 */
@Getter
@Setter
@Builder
public class RevokeTransferOwnershipPlanParam extends BaseCondition {
    private String transferOwnershipPlanNo; // 单号
//    private String mainId; // 提单ID
    private String operatorName; //操作人
}
