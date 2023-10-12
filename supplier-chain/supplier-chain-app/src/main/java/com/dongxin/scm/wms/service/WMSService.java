package com.dongxin.scm.wms.service;

import com.dongxin.scm.wms.NormalRequest;
import com.dongxin.scm.wms.PaginatedRequest;
import com.dongxin.scm.wms.condition.*;
import com.dongxin.scm.wms.config.WMSContext;
import com.dongxin.scm.wms.exception.WMSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WMSService {

    @Autowired
    private WMSContext wmsContext = null;

    /**
     * 1. 获取产品信息
     * @return
     * @param condition
     */
    public PaginatedRequest.PaginatedResponseModel getProduct(PaginatedRequest.PaginatedRequestModel<GetProductCondition> condition) throws WMSException {
        return PaginatedRequest.request(wmsContext.getProductGetAPI(), condition);
    }

    /**
     * 2. 获取仓库信息
     */
    public PaginatedRequest.PaginatedResponseModel getWarehouse(PaginatedRequest.PaginatedRequestModel<GetWarehouseCondition> condition) throws WMSException {
        return PaginatedRequest.request(wmsContext.getWarehouseGetAPI(), condition);
    }

    /**
     * 3. 获取库存信息
     */
    public PaginatedRequest.PaginatedResponseModel getInventory(PaginatedRequest.PaginatedRequestModel<GetInventoryCondition> condition) throws WMSException {
        return PaginatedRequest.request(wmsContext.getInventoryGetAPI(), condition);
    }

    /**
     * 4. 获取车号
     */
    public PaginatedRequest.PaginatedResponseModel getPlateNo(PaginatedRequest.PaginatedRequestModel<GetPlateNoCondition> condition) throws WMSException {
        return PaginatedRequest.request(wmsContext.getPlateNoGetAPI(), condition);
    }

    /**
     * 5. 获取目的地
     */
    public PaginatedRequest.PaginatedResponseModel getAddress(PaginatedRequest.PaginatedRequestModel<GetAddressCondition> condition) throws WMSException {
        return PaginatedRequest.request(wmsContext.getAddressGetAPI(), condition);
    }

    /**
     * 6. 发送提货计划
     */
    public NormalRequest.NormalResponse submitBillOfLadingPlan(SubmitBillOfLadingPlanParam condition) throws WMSException {
        return NormalRequest.request(wmsContext.getBillOfLadingPlanSubmitAPI(), condition);
    }

    /**
     * 7. 撤销提货计划
     */
    public NormalRequest.NormalResponse revokeBillOfLadingPlan(RevokeBillOfLadingPlanParam condition) throws WMSException {
        return NormalRequest.request(wmsContext.getBillOfLadingPlanRevokeAPI(), condition);
    }

    /**
     * 8. 提交过户计划
     */
    public NormalRequest.NormalResponse submitTransferOwnershipPlan(SubmitTransferOwnershipPlanParam condition) throws WMSException {
        return NormalRequest.request(wmsContext.getTransferOwnershipPlanSubmitAPI(), condition);
    }

    /**
     * 9. 撤销过户计划
     */
    public NormalRequest.NormalResponse revokeTransferOwnershipPlan(RevokeTransferOwnershipPlanParam condition) throws WMSException {
        return NormalRequest.request(wmsContext.getTransferOwnershipPlanRevokeAPI(), condition);
    }
}
