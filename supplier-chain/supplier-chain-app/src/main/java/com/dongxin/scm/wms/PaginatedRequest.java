package com.dongxin.scm.wms;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.dongxin.scm.wms.config.WMSContext;
import com.dongxin.scm.wms.exception.WMSException;
import com.dongxin.scm.wms.condition.BaseCondition;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

/**
 * 分页接口请求
 * @param <T> 查询对象类型
 */
@Component
public class PaginatedRequest<T extends BaseCondition> {

    private static WMSContext wmsContext;

    @Autowired
    public PaginatedRequest(WMSContext wmsContext) {
        PaginatedRequest.wmsContext = wmsContext;
    }

    /**
     * 发起请求
     * @return 响应对象
     */
    public static <T> PaginatedResponseModel request(String api, PaginatedRequestModel paginatedRequestModel) throws WMSException {
        HttpRequest request = HttpUtil.createPost(wmsContext.getServerUrl() + api)
                .header("appId", wmsContext.getAppId())
                .header("secretKey", wmsContext.getEncryptedSecretKey())
                .body(paginatedRequestModel.toJson());
        return doRequest(api, request);
    }

    public static <T> PaginatedResponseModel request(String url, PaginatedRequestModel paginatedRequestModel, Map<String, List<String>> headers) throws WMSException {
        HttpRequest request = HttpUtil.createPost(url)
                .header(headers)
                .body(paginatedRequestModel.toJson());
        return doRequest(url, request);
    }

    private static PaginatedResponseModel doRequest(String url, HttpRequest request) throws WMSException {
        try {
            HttpResponse response = request.execute();
            if (!response.isOk()) {
                throw new WMSException(url, response.getStatus());
            }
            return JSONUtil.toBean(response.body(), PaginatedResponseModel.class);
        } catch (Throwable t) {
            throw new WMSException(url, 0, StrUtil.format("向 {} 发起请求时抛出异常, 异常信息: {}", url, t.getMessage()));
        }
    }

    /**
     * 分页请求对象模型
     * @param <T> 查询包装类类型
     */
    @Data
    public static class PaginatedRequestModel<T extends BaseCondition> {
        /**
         * 页面大小
         */
        private int pageSize;

        /**
         * 页码
         */
        private int pageNumber;

        /**
         * 查询包装类
         */
        private T condition;

        public String toJson() {
            return JSONUtil.toJsonStr(this);
        }
    }

    /**
     * 分页请求响应模型
     */
    @Data
    public static class PaginatedResponseModel {

        /**
         * 请求状态
         */
        private int state;

        /**
         * 返回结果说明
         */
        private String message;

        /**
         * 是否最后一页
         */
        private int isLast;

        /**
         * 类
         */
        private List<Map<String, Object>> data;

        public boolean isSuccess() {
            return 1 == state;
        }
    }
}
