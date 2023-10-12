package com.dongxin.scm.bd.vo;


import lombok.Data;

/**
 * @Description:
 * @Autor: liujiazhi
 * Date:2021/3/29
 * @Version: V1.0
 */
@Data
public class CompanyTenantVo {
    private static final long serialVersionUID = 1L;

    /**主键*/
    private String id;

    /**租户id*/
    private Integer tenantCode;

    /**公司名称*/
    private String companyName;

    /**控款**/
    private String configurationName;

    private String configuration;

    /**入库单删除**/
    private String stockConfigurationName;

    private String stockConfiguration;

    /**中板合并**/
    private String cutdealConfigurationName;

    private String cutdealConfiguration;

    /**销售单仓库**/
    private String orderStockConfigurationName;

    private String orderStockConfiguration;

    /**自动结算**/
    private String autoSettleName;

    private String autoSettle;

    /**异客户批量结算**/
    private String diffCusBatSetName;

    private String diffCusBatSettle;

    /**提单明细显示运费**/
    private String salesDetDeliverExpenseName;

    private String salesDetDeliverExpense;

    /**提单明细显示加价**/
    private String salesDetAddPriceName;

    private String salesDetAddPrice;

    /**入库收货单位校验**/
    private String storageConsigneeUnitCheckName;

    private String storageConsigneeUnitCheck;

    /**库存定时任务配置*/
    private String inventoryTimingConfigurationName;

    private String inventoryTimingConfiguration;

    /**船运管理配置*/
    private String shippingManagementConfigurationName;

    private String shippingManagementConfiguration;

    /**提单明细显示基础单价*/
    private String salesDetBasicPriceName;

    private String salesDetBasicPrice;

    private String salesDetTheoryWeight;

    private String salesDetTheoryPrice;

    private String salesDetTheoryWeightName;

    private String salesDetTheoryPriceName;

    private String averageCostCheck;

    private String averageCostCheckName;

    private String prodClassSerialNoName;

    private String prodClassSerialNo;

    /**装车定时任务配置*/
    private String stackTimingConfigurationName;

    private String stackTimingConfiguration;

    /** 红冲复核 */
    private String writeOffReviewer;
}
