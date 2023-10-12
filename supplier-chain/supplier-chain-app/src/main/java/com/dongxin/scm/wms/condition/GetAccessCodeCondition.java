package com.dongxin.scm.wms.condition;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 获取通行码
 */
@Getter
@Setter
@Builder
public class GetAccessCodeCondition extends BaseCondition {
    private String appId;
    private String publicKey;
}
