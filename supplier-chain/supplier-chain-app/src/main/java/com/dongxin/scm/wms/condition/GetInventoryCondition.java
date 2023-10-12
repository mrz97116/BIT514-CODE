package com.dongxin.scm.wms.condition;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 3. 获取库存信息
 */
@Getter
@Setter
@Builder
public class GetInventoryCondition extends BaseCondition {
    private String warehouseCode; // 所在仓库
    private String customerName; // 货权单位
    private int isOwenership; // 货物性质
    private String productCode; // 产品代码
    private String companyName; // 客户名称
}
