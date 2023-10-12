package com.dongxin.scm.wms.condition;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 4. 获取车号
 */
@Getter
@Setter
@Builder
public class GetPlateNoCondition extends BaseCondition {
    private String q; // 查询车号, 可为空
}
