package com.dongxin.scm.wms;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.dongxin.scm.trustedthird.rsa.RSAUtil;
import com.dongxin.scm.wms.config.WMSContext;
import com.dongxin.scm.wms.exception.WMSException;
import com.dongxin.scm.wms.condition.BaseCondition;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 一般接口请求
 */
@Component
public class NormalRequest {

    private static WMSContext wmsContext;

    @Autowired
    public NormalRequest(WMSContext wmsContext) {
        NormalRequest.wmsContext = wmsContext;
    }

    /**
     * 发起请求
     * @return 响应对象
     */
    public static <T extends BaseCondition> NormalResponse request(String api, T condition) throws WMSException {
        HttpRequest request = HttpUtil.createPost(wmsContext.getServerUrl() + api)
                .header("appId", wmsContext.getAppId())
                .header("secretKey", wmsContext.getEncryptedSecretKey())
                .body(condition.toJson());
        return doRequest(api, request);
    }

    public static <T extends BaseCondition> NormalResponse request(String url, Map<String, List<String>> headers, T condition) throws WMSException {
        HttpRequest request = HttpUtil.createPost(url)
                .header(headers)
                .body(condition.toJson());
        return doRequest(url, request);
    }

    private static NormalResponse doRequest(String url, HttpRequest request) throws WMSException {
        HttpResponse response;
        try {
            response = request.execute();
        } catch (Throwable t) {
            throw new WMSException(url, 0, StrUtil.format("向 {} 发起请求时抛出异常, 异常信息: {}", url, t.getMessage()));
        }
        if (Objects.nonNull(response) && !response.isOk()) {
            throw new WMSException(url, response.getStatus());
        } else {
            return JSONUtil.toBean(response.body(), NormalResponse.class);
        }
    }

    @Data
    public static class NormalResponse {

        /**
         * 请求状态
         */
        private int state;

        /**
         * 返回结果说明
         */
        private String message;

        /**
         * 类
         */
        private Map<String, Object> data;

        public boolean isSuccess() {
            return 1 == state;
        }
    }
}
