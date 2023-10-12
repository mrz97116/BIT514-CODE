package com.dongxin.scm.wms.config;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;

@Getter
@ToString
public class WMSContext {

    // 与仓储系统对接需要的配置

    @Setter
    private String secretKey; // 要用wmsPublicKey加密, 还要在末尾用下划线隔开设置时间(yyyyMMddHHmmss), 使用Base64编码

    public String getEncryptedSecretKey() {
        return getWmsRSAEncrypt().encryptBase64(
                getSecretKey() + "_" + DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_PATTERN),
                KeyType.PublicKey
        );
    }

    @Setter
    private String wmsPublicKey;

    @Value("${wms.app-id}")
    private String appId;

    @Value("${wms.rsa.public-key}")
    private String publicKey;

    @Value("${wms.rsa.private-key}")
    private String privateKey;

    @Setter
    private RSA scmRSAEncrypt;

    @Setter
    private RSA wmsRSAEncrypt;

    // API

    @Value("${wms.server-url}")
    private String serverUrl;

    @Value("${wms.api.get-access-code}")
    private String accessCodeGetAPI;

    @Value("${wms.api.get-product}")
    private String productGetAPI;

    @Value("${wms.api.get-warehouse}")
    private String warehouseGetAPI;

    @Value("${wms.api.get-inventory}")
    private String inventoryGetAPI;

    @Value("${wms.api.get-plate-no}")
    private String plateNoGetAPI;

    @Value("${wms.api.get-address}")
    private String addressGetAPI;

    @Value("${wms.api.submit-bill-of-lading-plan}")
    private String billOfLadingPlanSubmitAPI;

    @Value("${wms.api.revoke-bill-of-lading-plan}")
    private String billOfLadingPlanRevokeAPI;

    @Value("${wms.api.submit-transfer-ownership-plan}")
    private String transferOwnershipPlanSubmitAPI;

    @Value("${wms.api.revoke-transfer-ownership-plan}")
    private String transferOwnershipPlanRevokeAPI;

}
