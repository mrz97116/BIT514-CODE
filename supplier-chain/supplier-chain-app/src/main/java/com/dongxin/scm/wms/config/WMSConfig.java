package com.dongxin.scm.wms.config;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.dongxin.scm.wms.NormalRequest;
import com.dongxin.scm.wms.PaginatedRequest;
import com.dongxin.scm.wms.condition.GetAccessCodeCondition;
import com.dongxin.scm.wms.condition.GetProductCondition;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
public class WMSConfig {

    @Autowired
    private final RedisUtil redisUtil;

    private static final String WMS_SECRET_KEY_REDIS_KEY = "SCM:WMS:SECRETKEY";

    private static final String WMS_PUBLIC_KEY_REDIS_KEY = "SCM:WMS:PUBLICKEY";

    public WMSConfig(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Value("${wms.app-id}")
    private String appId;

    @Value("${wms.rsa.public-key}")
    private String publicKey;

    @Value("${wms.rsa.private-key}")
    private String privateKey;

    @Value("${wms.server-url}")
    private String serverUrl;

    @Value("${wms.api.get-access-code}")
    private String accessCodeGetAPI;

    @Value("${wms.api.get-product}")
    private String productGetAPI;

    /**
     * 启动时有几步操作
     * 1. 尝试从Redis获取仓储系统的通行码
     * 2. 如果获取不到， 就使用发送wms.app-id和wms.rsa.public-key到/SystemSettingService/GetAccessCode获取通行码
     * 2-1. 如果响应状态码为400时就重新获取通行码
     * 3. 将通行码保存到Redis
     */
    @Bean
    public WMSContext wmsContext() {
        WMSContext wmsContext = new WMSContext();

        // 创建SCM的RSA加密解密对象
        RSA rsa = SecureUtil.rsa(privateKey, publicKey);
        wmsContext.setScmRSAEncrypt(rsa);

        // 获取仓储系统公钥和通行码
        Object secretKeyObj = redisUtil.get(WMS_SECRET_KEY_REDIS_KEY);
        Object wmsPublicKeyObj = redisUtil.get(WMS_PUBLIC_KEY_REDIS_KEY);
        String secretKey = null, wmsPublicKey = null;

        if (Objects.isNull(secretKeyObj) || Objects.isNull(wmsPublicKeyObj)) {
            log.info("从Redis获取到的仓储系统通行码、公钥为空, 重新获取...");
            getAccessCode(wmsContext, rsa);
        } else {
            secretKey = secretKeyObj.toString();
            wmsPublicKey = wmsPublicKeyObj.toString();
            log.info("从Redis获取仓储系统通行码、公钥成功: {} {}", secretKey, wmsPublicKey);
            wmsContext.setSecretKey(secretKey);
            wmsContext.setWmsPublicKey(wmsPublicKey);
        }
        // 创建仓储系统的RSA加密对象
        RSA wmsRsa = SecureUtil.rsa(null, wmsContext.getWmsPublicKey()); // 创建wms的RSA加密对象
        wmsContext.setWmsRSAEncrypt(wmsRsa);

        // 测试通行码是否有效
        PaginatedRequest.PaginatedRequestModel<GetProductCondition> requestBody = new PaginatedRequest.PaginatedRequestModel<>();
        requestBody.setPageSize(0);
        requestBody.setPageNumber(1);
        requestBody.setCondition(GetProductCondition.builder().build());

        try {
            HttpResponse execute = HttpUtil.createPost(serverUrl + productGetAPI)
                    .header("appId", appId)
                    .header("secretKey", wmsRsa.encryptBase64(wmsContext.getSecretKey() + "_" + DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_PATTERN), KeyType.PublicKey))
                    .body(requestBody.toJson())
                    .execute();
            if (!execute.isOk()) {
                log.info("从Redis获取的仓储系统通行码已失效, 重新获取...");
                getAccessCode(wmsContext, rsa);
            } else {
                log.info("仓储系统通行码测试验证通过");
            }
        } catch (Throwable t) {
            log.error("wmsContext error", t);
        }
        return wmsContext;
    }

    private void getAccessCode(WMSContext wmsContext, RSA rsa) {
        GetAccessCodeCondition getAccessCodeCondition = GetAccessCodeCondition.builder()
                .appId(appId)
                .publicKey(publicKey)
                .build();
        try {
            HttpResponse execute = HttpUtil.createPost(serverUrl + accessCodeGetAPI)
                    .body(getAccessCodeCondition.toJson())
                    .execute();

            NormalRequest.NormalResponse response = JSONUtil.toBean(execute.body(), NormalRequest.NormalResponse.class);

            // SecretKey
            wmsContext.setSecretKey(rsa.decryptStr(response.getData().get("secretKey").toString(), KeyType.PrivateKey)); // 解密SecretKey
            redisUtil.set(WMS_SECRET_KEY_REDIS_KEY, wmsContext.getSecretKey()); // 设置进Redis

            // wmsPublicKey
            wmsContext.setWmsPublicKey(response.getData().get("publicKey").toString()); // 物流公钥, 用于加密待传输的数据
            redisUtil.set(WMS_PUBLIC_KEY_REDIS_KEY, wmsContext.getWmsPublicKey()); // 设置进Redis

            log.info("获取仓储系统通行码、公钥成功: {} {}", wmsContext.getSecretKey(), wmsContext.getWmsPublicKey());
        } catch (Exception e) {
            log.error("获取仓储系统通行码时出错", e);
        }
    }

}
