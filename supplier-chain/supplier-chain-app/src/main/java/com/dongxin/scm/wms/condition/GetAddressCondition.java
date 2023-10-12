package com.dongxin.scm.wms.condition;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 5. 获取目的地
 */
@Getter
@Setter
@Builder
public class GetAddressCondition extends BaseCondition {
    private String q; // 查询地址, 可为空
}
