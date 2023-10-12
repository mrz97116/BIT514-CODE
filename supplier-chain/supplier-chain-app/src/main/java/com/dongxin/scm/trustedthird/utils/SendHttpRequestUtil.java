package com.dongxin.scm.trustedthird.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * 描述 用于发送http请求的工具类
 *
 * @author liujintao
 * @version 0.0.1
 * @date 2017/2/27.
 * @see
 */
@Slf4j
public class SendHttpRequestUtil {

    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 7000;
    private static final String CODE = "UTF-8";



    static {
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager();
        // 设置连接池大小
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        // 在提交请求之前 测试连接是否可用
        configBuilder.setStaleConnectionCheckEnabled(true);
        requestConfig = configBuilder.build();
    }

    /**
     * 填充请求头参数至get请求中
     *
     * @param httpGet
     * @param headers
     * @return
     */
    private static HttpGet setHeadersToGet(HttpGet httpGet, Map<String, String> headers) {
        if (headers != null && headers.size() != 0) {
            for (String key : headers.keySet()) {
                httpGet.addHeader(key, headers.get(key));
            }
        }
        return httpGet;
    }


    /**
     * 填充请求头参数至Post请求中
     *
     * @param httpPost
     * @param headers
     * @return
     */
    private static HttpPost setHeadersToPost(HttpPost httpPost, Map<String, String> headers) {
        if (headers != null && headers.size() != 0) {
            for (String key : headers.keySet()) {
                httpPost.addHeader(key, headers.get(key));
            }
        }
        return httpPost;
    }

    /**
     * 填充请求头参数至Post请求中
     *
     * @param httpPost
     * @param headers
     * @return
     */
    private static HttpPost setHeadersToPost(HttpPost httpPost, Header[] headers) {
        for (Header header : headers) {
            httpPost.addHeader(header);
        }
        return httpPost;
    }


    /**
     * 填充请求头参数至Put请求中
     *
     * @param httpPut
     * @param headers
     * @return
     */
    private static HttpPut setHeadersToPut(HttpPut httpPut, Map<String, String> headers) {
        if (headers != null && headers.size() != 0) {
            for (String key : headers.keySet()) {
                httpPut.addHeader(key, headers.get(key));
            }
        }
        return httpPut;
    }


    /**
     * 填充请求体参数至Post请求中
     *
     * @param httpPost
     * @param params
     * @return
     */
    private static HttpPost setParamsToRequest(HttpPost httpPost, Map<String, Object> params) {

        List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            NameValuePair pair = null;
            if (entry.getValue() == null) {
                pair = new BasicNameValuePair(entry.getKey(), null);
            } else {
                pair = new BasicNameValuePair(entry.getKey(), entry
                        .getValue().toString());
            }
            pairList.add(pair);
        }
//        try {
//            JSONObject param = new JSONObject(params);
//            StringEntity requestentity = new StringEntity(param.toString());
//            requestentity.setContentType(ContentType.APPLICATION_JSON.toString());
//            httpPost.setEntity(requestentity);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8"));
        entity.setContentType("application/json;charset=UTF-8");
        httpPost.setEntity(entity);
        return httpPost;
    }


    /**
     * 发送 GET 请求（HTTP），不带输入数据
     *
     * @param url
     * @return
     */
    public static Result<JSONObject> doGet(String url,String privateKey,String fromClientName) throws Exception {
        return doGet(url, new HashMap<String, Object>(),privateKey,fromClientName);
    }

//    /**
//     * 发送 GET 请求（HTTP）,K-V形式,无请求头参数
//     *
//     * @param url
//     * @param params
//     * @return
//     */
//    public static Result<JSONObject> doGet(String url, Map<String, Object> params,String privateKey,String fromClientName) throws Exception {
//        return doGet(url, params, privateKey,fromClientName);
//    }

//    /**
//     * 发送 get 请求（HTTP）,有请求参数 ，有请求头参数
//     *
//     * @param url     API接口URL
//     * @param request servlet请求
//     * @param params  请求参数
//     * @return
//     */
//    public static Result<JSONObject> doGet(String url, Map<String, Object> params,  Map<String, String> headers) throws Exception{
////        Map<String, String> headers = new HashMap<>();
//        //获取请求头信息
//        Enumeration headerNames = request.getHeaderNames();
//        //使用循环遍历请求头，并通过getHeader()方法获取一个指定名称的头字段
//        while (headerNames.hasMoreElements()) {
//
//            String headerName = (String) headerNames.nextElement();
//            if(headerName.toLowerCase().equals("content-length"))
//                continue;
//            String headervalue = request.getHeader(headerName);
//            headers.put(headerName, headervalue);
//        }
//        return doGet(url, params, headers);
//    }

    /**
     * 发送 GET 请求（HTTP），K-V形式，有请求头参数
     *
     * @param url          API接口URL
     * @param params       参数map
     * @return
     */
    public static Result<JSONObject> doGet(String url, Map<String, Object> params,String privateKey, String fromClientName) throws Exception {

        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        HttpGet httpGet = null;
        HttpResponse response = null;
        RequestResult requestResult = new RequestResult();
        String apiUrl = url;
        StringBuffer param = new StringBuffer();
        Result<JSONObject> result = new Result<JSONObject>();
        int i = 0;

        if (params != null && params.size() > 0) {
            for (String key : params.keySet()) {
                if (i == 0)
                    param.append("?");
                else
                    param.append("&");
                param.append(key).append("=").append(params.get(key));
                i++;
            }
        }
        Map<String, String> headers = new HashMap<>(10);
        headers.put("content-type", "application/json;charset=utf-8");
        headers.put("timestamp", UTCTimeUtil.getUTCTimeStr());
        headers.put("from-client-name", fromClientName);
        headers.put("to-client-name", "trusted-data");
        headers.put("method", "POST");

        apiUrl += param;
        //签名
        String authorization = TrustedEncryptUtil.sign(apiUrl.replaceAll("test/api","trusted-data").replaceAll("prod/api","trusted-data"), headers,privateKey);
        headers.put("authorization", authorization);

        String str = null;
        try {
            HttpEntity entity = null;
            httpGet = new HttpGet(apiUrl);
            httpGet = setHeadersToGet(httpGet, headers);
            response = httpClient.execute(httpGet);
            if (response != null) {
                entity = response.getEntity();
            }

            if (entity != null) {
                InputStream instream = entity.getContent();
                str = IOUtils.toString(instream, "UTF-8");
                result = decodeAesRsa(str,privateKey);
            }
        } catch (IOException e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            result.setCode(520);
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 发送 POST 请求（HTTP），无请求参数
     *
     * @param url
     * @return
     */
    public static Result<JSONObject> doPost(String url,String privateKey,String fromClientName) throws Exception {
        return doPost(url, new JSONObject(),privateKey,fromClientName);
    }


//    /**
//     * 发送 POST 请求（HTTP）,K-V形式,无请求头参数
//     *
//     * @param url
//     * @param params
//     * @return
//     */
//    public static Result<JSONObject> doPost(String url, Map<String, Object> params) throws Exception{
//        return doPost(url,params,new HashMap<String,String>());
//    }
//
////    /**
////     * 发送 POST 请求（HTTP），K-V形式 ，有请求头参数
////     *
////     * @param url     API接口URL
////     * @param request servlet请求
////     * @param params  请求参数
////     * @return
////     */
////    public static Result<JSONObject> doPost(String url, Map<String, Object> params, HttpServletRequest request) throws Exception{
////        Map<String, String> headers = new HashMap<>();
////        //获取请求头信息
////        Enumeration headerNames = request.getHeaderNames();
////        //使用循环遍历请求头，并通过getHeader()方法获取一个指定名称的头字段
////        while (headerNames.hasMoreElements()) {
////            String headerName = (String) headerNames.nextElement();
////            if (headerName.equals("content-length")) {
////                continue;
////            }
////            String headervalue = request.getHeader(headerName);
////            headers.put(headerName, headervalue);
////
////        }
////        return doPost(url, params, headers);
////    }
//
//    /**
//     * 发送 POST 请求（HTTP），K-V形式 ，有请求头参数
//     *
//     * @param url     API接口URL
//     * @param params  参数map
//     * @param headers 请求头参数
//     * @return
//     */
//    public static Result<JSONObject> doPost(String url, Map<String, Object> params, Map<String, String> headers) throws Exception{
//        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
////        RequestResult requestResult = new RequestResult();
//        String httpStr = null;
//        HttpPost httpPost = new HttpPost(url);
//        CloseableHttpResponse response = null;
//        Result<JSONObject> result = new Result<JSONObject>();
////        JSONObject obj = new JSONObject();
//        String authorization = HttpRequestSignatrueUtil.getAuthorization("POST",url,headers);
//        headers.put("authorization",authorization);
//        try {
//
//            httpPost.setConfig(requestConfig);
//            httpPost = setHeadersToPost(httpPost, headers);
//            httpPost = setParamsToRequest(httpPost, params);
//            response = httpClient.execute(httpPost);
//            if (response != null) {
//                HttpEntity entity = response.getEntity();
//                httpStr = EntityUtils.toString(entity, "UTF-8");
////                JSONObject json = ;
//                result = JSONObject.toJavaObject(JSONObject.parseObject(httpStr),Result.class);
//            }
//
////            result.setResult((JSONObject)json.get("resutl"));
////            result.setSuccess((Boolean)json.get("success"));
////            result.setMessage((String)json.get("message"));
////            result.setCode((Integer)json.get("code"));
////            result.setTimestamp((Long)json.get("timestamp"));
////            requestResult.setResult(httpStr);
//        } catch (IOException e) {
//            result.setSuccess(false);
//            result.setMessage(e.getMessage());
//            result.setCode(520);
//            e.printStackTrace();
//        } finally {
//            if (response != null) {
//                try {
//                    EntityUtils.consume(response.getEntity());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
////            requestResult.setStatusCode(response.getStatusLine().getStatusCode());
//        }
//        return result;
//    }

    /**
     * 发送 POST 请求（HTTP），JSON形式，无请求参数
     *
     * @param url
     * @param json json对象
     * @return
     */
    public static Result<JSONObject> doPost(String url, JSONObject json,String privateKey,String fromClientName) throws Exception {
        return doPost(url, json, "trusted-data",privateKey,fromClientName);
    }

    /**
     * 发送 POST 请求（HTTP），JSON形式，有请求头参数
     *
     * @param url          请求URL
     * @param body         请求体
     * @param toServerName 目标服务器名
     * @return
     */
    public static Result<JSONObject> doPost(String url, JSONObject body, String toServerName,String privateKey,String fromClientName) throws Exception {
        Map<String, String> headers = new HashMap(32);
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        RequestResult requestResult = new RequestResult();
        Result<JSONObject> result = new Result<JSONObject>();

        String httpStr = null;
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;

//        try {
//            headers.put("host", InetAddress.getLocalHost().getHostName());
//        } catch (UnknownHostException e) {
        //headers.put("host", "127.0.0.1");
//        }
        headers.put("content-type", "application/json;charset=utf-8");
        headers.put("timestamp", UTCTimeUtil.getUTCTimeStr());
        headers.put("from-client-name", fromClientName);
        headers.put("to-client-name", toServerName);
        headers.put("method", "POST");
        headers.put("request-id", UUID.randomUUID().toString());
        log.info("post请求签的加签请求头" + headers.toString());
        String authorization = TrustedEncryptUtil.sign(url.replaceAll("test/api","trusted-data").replaceAll("prod/api","trusted-data"), headers,privateKey);
//        String authorization = HttpRequestSignatrueUtil.getAuthorization("POST",url,headers);
        headers.put("authorization", authorization);
        log.info("业务系统签名值:" + authorization);
        try {
            httpPost.setConfig(requestConfig);
            httpPost = setHeadersToPost(httpPost, headers);
            StringEntity stringEntity = new StringEntity(encodeAesRsa(body,"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCGAuAvwoDMWkHKr2guCkid5S7iM832vns1uciqE6il1f6NlAlYbbTxCjlTpiC+gjknAgkc3fUTWEnneXOmu4+mZ0chuBjpzUcAtN/GmN+1LkdulZ0rGluPXm64I6jZLrs/sVA5lxBT3R47vsu013O7BruwVhL5yJDpXPv8M7LzEQIDAQAB"), "UTF-8");//解决中文乱码问题
//            String encrypted = TrustedEncryptUtil.dissymmetricEncrypt("trusted-data",body.toJSONString());
////            encrypt.encryptByPublicKey(tymDissymmetrice.getPublicKey(),data.getBytes());
//            byte[] decrypted = TrustedEncryptUtil.dissymmetricDecrypt(encrypted);
//            System.out.println("解密后数据："+new String(decrypted));
//            byte[] decrypt = encrypt.decryptByPrivateKey(TrustedEncryptUtil.privateKey,encrypted);
//            StringEntity stringEntity = new StringEntity(encrypted, "UTF-8");//解决中文乱码问题

            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity entity = response.getEntity();
                httpStr = EntityUtils.toString(entity, "UTF-8");
//                String content = new String(TrustedEncryptUtil.dissymmetricDecrypt(httpStr));
                result = decodeAesRsa(httpStr,privateKey);
//                result = JSONObject.toJavaObject(JSONObject.parseObject(content),Result.class);
            }
        } catch (IOException e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            result.setCode(520);
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //requestResult.setStatusCode(response.getStatusLine().getStatusCode());
        }
        return result;
    }

//    /**
//     * 发送 POST 请求（HTTP），JSON形式，有请求头参数
//     *
//     * @param url
//     * @param json    请求体，json对象
//     * @param  headers http请求
//     * @param  params http请求参数
//     * @return
//     */
//    public static Result<JSONObject> doPost(String url,Map<String,Object> params, Object json, Map<String, String> headers) throws Exception{
//        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
//        RequestResult requestResult = new RequestResult();
//        Result<JSONObject> result = new Result<JSONObject>();
//
//        String httpStr = null;
//        HttpPost httpPost = new HttpPost(url);
//        CloseableHttpResponse response = null;
//
////        //获取请求头信息
////        Enumeration headerNames = request.getHeaderNames();
////        //使用循环遍历请求头，并通过getHeader()方法获取一个指定名称的头字段
////        while (headerNames.hasMoreElements()) {
////            String headerName = (String) headerNames.nextElement();
////            if (headerName.equals("content-length")) {
////                continue;
////            }
////            String headervalue = request.getHeader(headerName);
////            headers.put(headerName, headervalue);
////
////        }
//        String authorization = HttpRequestSignatrueUtil.getAuthorization("POST",url,headers);
//        headers.put("authorization",authorization);
//        try {
//
//            httpPost.setConfig(requestConfig);
//            httpPost = setHeadersToPost(httpPost, headers);
//            httpPost = setParamsToRequest(httpPost, params);
//            StringEntity stringEntity = new StringEntity(encodeAesRsa((JSONObject) json), "UTF-8");//解决中文乱码问题
//            stringEntity.setContentEncoding("UTF-8");
//            stringEntity.setContentType("application/json");
//            httpPost.setEntity(stringEntity);
//
//            response = httpClient.execute(httpPost);
//            if (response != null) {
//                HttpEntity entity = response.getEntity();
//                httpStr = EntityUtils.toString(entity, "UTF-8");
//                String content = decodeAesRsa(JSONObject.parseObject(httpStr));
//                result = JSONObject.toJavaObject(JSONObject.parseObject(content),Result.class);
//            }
//        } catch (IOException e) {
//            result.setSuccess(false);
//            result.setMessage(e.getMessage());
//            result.setCode(520);
//            e.printStackTrace();
//        } finally {
//            if (response != null) {
//                try {
//                    EntityUtils.consume(response.getEntity());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            requestResult.setStatusCode(response.getStatusLine().getStatusCode());
//        }
//        return result;
//    }

    /**
     * 发送 SSL POST 请求（HTTPS），无K-V形式参数，无请求头参数
     *
     * @param url API接口URL
     * @return
     */
    public static Result<JSONObject> doPostSSL(String url) {
        return doPostSSL(url, null, null);
    }

    /**
     * 发送 SSL POST 请求（HTTPS），K-V形式，无请求头参数
     *
     * @param url    API接口URL
     * @param params 参数map
     * @return
     */
    public static Result<JSONObject> doPostSSL(String url, Map<String, Object> params) {
        return doPostSSL(url, params, null);
    }

    /**
     * 发送 SSL POST 请求（HTTPS），K-V形式，有请求头参数
     *
     * @param url     API接口URL
     * @param params  参数map
     * @param headers 请求头参数
     * @return
     */
    public static Result<JSONObject> doPostSSL(String url, Map<String, Object> params, Map<String, String> headers) {
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        RequestResult requestResult = new RequestResult();
        Result<JSONObject> result = new Result<>();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        String httpStr = null;

        try {
            HttpEntity entity = null;
            httpPost.setConfig(requestConfig);

            httpPost = setHeadersToPost(httpPost, headers);
            httpPost = setParamsToRequest(httpPost, params);
            response = httpClient.execute(httpPost);
            if (response != null) {
                int statusCode = response.getStatusLine().getStatusCode();
                entity = response.getEntity();
            }
            httpStr = EntityUtils.toString(entity, "utf-8");
            result = JSONObject.toJavaObject(JSONObject.parseObject(httpStr), Result.class);

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            result.setCode(520);
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    /**
     * 发送 SSL POST 请求（HTTPS），JSON形式，有请求头参数
     *
     * @param url  API接口URL
     * @param json JSON对象
     * @return
     */
    public static Result<JSONObject> doPostSSL(String url, Object json) {
        return doPostSSL(url, json, null);
    }

    /**
     * 发送 SSL POST 请求（HTTPS），JSON形式，有请求头参数
     *
     * @param url     API接口URL
     * @param json    JSON对象
     * @param headers 请求头参数
     * @return
     */
    public static Result<JSONObject> doPostSSL(String url, Object json, Map<String, String> headers) {
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        RequestResult requestResult = new RequestResult();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        String httpStr = null;
        Result<JSONObject> result = new Result<>();

        try {
            HttpEntity entity = null;
            httpPost.setConfig(requestConfig);
            httpPost = setHeadersToPost(httpPost, headers);
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");//解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            if (response != null) {
                int statusCode = response.getStatusLine().getStatusCode();
                entity = response.getEntity();
            }
            httpStr = EntityUtils.toString(entity, "utf-8");
            result = JSONObject.toJavaObject(JSONObject.parseObject(httpStr), Result.class);

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            result.setCode(520);
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    /**
     * 发送 SSL GET 请求（HTTPs），无参数，无请求头参数
     *
     * @param url API接口URL
     * @return
     */
    public static Result<JSONObject> doGetSSL(String url) {
        return doGetSSL(url, null, null);
    }

    /**
     * 发送 SSL GET 请求（HTTPs），K-V形式，无请求头参数
     *
     * @param url    API接口URL
     * @param params 参数map
     * @return
     */
    public static Result<JSONObject> doGetSSL(String url, Map<String, Object> params) {
        return doGetSSL(url, params, null);
    }

    /**
     * 发送 SSL GET 请求（HTTPs），K-V形式，有请求头参数
     *
     * @param url     API接口URL
     * @param params  参数map
     * @param headers 请求头参数
     * @return
     */
    public static Result<JSONObject> doGetSSL(String url, Map<String, Object> params, Map<String, String> headers) {

        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        HttpGet httpGet = null;
        HttpResponse response = null;
        RequestResult requestResult = new RequestResult();
        String apiUrl = url;
        StringBuffer param = new StringBuffer();
        Result<JSONObject> result = new Result<>();
        int i = 0;
        if (params != null && params.size() > 0) {
            for (String key : params.keySet()) {
                if (i == 0)
                    param.append("?");
                else
                    param.append("&");
                param.append(key).append("=").append(params.get(key));
                i++;
            }
        }
        apiUrl += param;
//        String result = null;
        try {
            HttpEntity entity = null;
            httpGet = new HttpGet(apiUrl);
            httpGet.setConfig(requestConfig);
            httpGet = setHeadersToGet(httpGet, headers);
            response = httpclient.execute(httpGet);
            if (response != null) {
                int statusCode = response.getStatusLine().getStatusCode();
                entity = response.getEntity();
            }
            String httpStr = EntityUtils.toString(entity, "utf-8");
            result = JSONObject.toJavaObject(JSONObject.parseObject(httpStr), Result.class);

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            result.setCode(520);
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }


    /**
     * 发送 PUT 请求（HTTP），无参数，无请求头参数
     *
     * @param url API接口URL
     * @return
     */
    public static Result<JSONObject> put(String url) {
        return put(url, null, null);
    }

    /**
     * 发送 PUT 请求（HTTP），K-V形式，无请求头参数
     *
     * @param url    API接口URL
     * @param params 参数map
     * @return
     */
    public static Result<JSONObject> put(String url, Map<String, String> params) {
        return put(url, params, null);
    }

    /**
     * 发送 PUT 请求（HTTP），K-V形式，有请求头参数
     *
     * @param url     API接口URL
     * @param params  参数map
     * @param headers 请求头参数
     * @return
     */
    public static Result<JSONObject> put(String url, Map<String, String> params, Map<String, String> headers) {
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        String responseText = "";
        HttpEntity entity = null;
        RequestResult requestResult = null;
        CloseableHttpResponse response = null;
        String httpStr = null;
        Result<JSONObject> result = new Result<>();
        try {
            HttpPut httpPut = new HttpPut(url);
            if (headers != null) {
                Set<String> set = headers.keySet();
                for (String item : set) {
                    String value = headers.get(item);
                    httpPut.addHeader(item, value);
                }
            }
            if (params != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> param : params.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
                    paramList.add(pair);
                }
                httpPut.setEntity(new UrlEncodedFormEntity(paramList, CODE));
            }
            response = client.execute(httpPut);
            if (response != null) {
                int statusCode = response.getStatusLine().getStatusCode();
                entity = response.getEntity();
            }
            httpStr = EntityUtils.toString(entity, "utf-8");
            result = JSONObject.toJavaObject(JSONObject.parseObject(httpStr), Result.class);

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            result.setCode(520);
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }


    /**
     * 发送 SSL PUT 请求（HTTP），JSON形式，无请求头参数
     *
     * @param url  API接口URL
     * @param json JSON对象
     * @return
     */
    public static Result<JSONObject> doPut(String url, Object json) {
        return doPut(url, json, null);
    }

    /**
     * 发送 PUT 请求（HTTP），JSON形式，有请求头参数
     *
     * @param url     API接口URL
     * @param json    JSON对象
     * @param headers 请求头参数
     * @return
     */
    public static Result<JSONObject> doPut(String url, Object json, Map<String, String> headers) {
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        RequestResult requestResult = new RequestResult();
        HttpPut httpPut = new HttpPut(url);
        CloseableHttpResponse response = null;
        String httpStr = null;
        Result<JSONObject> result = new Result<>();
        try {
            HttpEntity entity = null;
            httpPut.setConfig(requestConfig);
            httpPut = setHeadersToPut(httpPut, headers);
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");//解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPut.setEntity(stringEntity);
            response = httpClient.execute(httpPut);
            if (response != null) {
                int statusCode = response.getStatusLine().getStatusCode();
                entity = response.getEntity();
            }
            httpStr = EntityUtils.toString(entity, "utf-8");
            result = JSONObject.toJavaObject(JSONObject.parseObject(httpStr), Result.class);

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            result.setCode(520);
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    /**
     * 发送 DELETE 请求（HTTP）,无参数
     *
     * @param url API接口URL
     * @return
     */
    public static Result<JSONObject> doDelete(String url) {
        return doDelete(url, null, null);
    }

    /**
     * 发送 DELETE 请求（HTTP），K-V形式，无请求头参数
     *
     * @param url    API接口URL
     * @param params 参数map
     * @return
     */
    public static Result<JSONObject> doDelete(String url, Map<String, String> params) {
        return doDelete(url, params, null);
    }

    /**
     * 发送 DELETE 请求（HTTP），K-V形式，有请求头参数
     *
     * @param url     API接口URL
     * @param params  参数map
     * @param headers 请求头参数
     * @return
     */
    public static Result<JSONObject> doDelete(String url, Map<String, String> params, Map<String, String> headers) {
        CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        String responseText = "";
        HttpEntity entity = null;
        RequestResult requestResult = null;
        CloseableHttpResponse response = null;
        String httpStr = null;
        Result<JSONObject> result = new Result<>();
        try {
            HttpDelete httpDelete = new HttpDelete(url);
            if (headers != null) {
                Set<String> set = headers.keySet();
                for (String item : set) {
                    String value = headers.get(item);
                    httpDelete.addHeader(item, value);
                }
            }
            if (params != null) {
                URIBuilder uriBuilder = new URIBuilder(url);
                if (params != null) {
                    for (String key : params.keySet()) {
                        uriBuilder.setParameter(key, params.get(key));
                    }
                }
                URI uri = uriBuilder.build();
                httpDelete.setURI(uri);
            }
            response = client.execute(httpDelete);
            if (response != null) {
                int statusCode = response.getStatusLine().getStatusCode();
                entity = response.getEntity();
            }
            httpStr = EntityUtils.toString(entity, "utf-8");
            result = JSONObject.toJavaObject(JSONObject.parseObject(httpStr), Result.class);

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            result.setCode(520);
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    /**
     * 创建SSL安全连接
     *
     * @return
     */
    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }

                @Override
                public void verify(String host, SSLSocket ssl) throws IOException {
                }

                @Override
                public void verify(String host, X509Certificate cert) throws SSLException {
                }

                @Override
                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
                }
            });
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return sslsf;
    }

    public static String encodeAesRsa(JSONObject json, String publicKey) {
        JSONObject result = new JSONObject();
        try {

            // 生成aes秘钥
            String aseKey = getRandomString(16);
            // 用非对称加密对aes秘钥进行加密
            String encrypted = TrustedEncryptUtil.dissymmetricEncrypt(publicKey, aseKey);
            // aes加密
            String requestData = AesEncryptUtil.encrypt(json.toJSONString(), aseKey);

            result.put("encrypted", encrypted);
            result.put("requestData", requestData);

        } catch (Exception e) {
            e.printStackTrace();
//            log.error("对方法method :【" + methodParameter.getMethod().getName() + "】返回数据进行解密出现异常：" + e.getMessage());
        }
        return result.toString();
    }

    private static Result<JSONObject> decodeAesRsa(String requestData,String privateKey) {
        if (requestData != null && !requestData.equals("")) {
            Map<String, String> requestMap = new Gson().fromJson(requestData, new TypeToken<Map<String, String>>() {
            }.getType());
            // 密文
            String data = requestMap.get("requestData");
            // 加密的ase秘钥
            String encrypted = requestMap.get("encrypted");
            // 通过header获取accessKeyId
            if (StringUtils.isEmpty(data) || StringUtils.isEmpty(encrypted)) {
                throw new RuntimeException("发起签署繁忙，请稍后重试");
            } else {
                String content = null;
                String aseKey = null;
                try {
                    aseKey = new String(TrustedEncryptUtil.dissymmetricDecrypt(encrypted,privateKey));
                } catch (Exception e) {
                    throw new RuntimeException("参数【aseKey】解析异常！");
                }
                try {
                    content = AesEncryptUtil.decrypt(data, aseKey);
                } catch (Exception e) {
                    throw new RuntimeException("参数【content】解析异常！");
                }
                if (StringUtils.isEmpty(content) || StringUtils.isEmpty(aseKey)) {
                    throw new RuntimeException("参数【requestData】解析参数空指针异常!");
                }
                return JSONObject.toJavaObject(JSONObject.parseObject(content), Result.class);
            }
        }
        throw new RuntimeException("参数【requestData】不合法异常！");
    }



    /**
     * //     * 创建指定位数的随机字符串
     * //     * @param length 表示生成字符串的长度
     * //     * @return 字符串
     * //
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String url = "http://127.0.0.1:8088/trusted-data/sys/app/login";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("username", "admin");
        params.put("password", "123456");
        params.put("remember_me", "");
        params.put("checkKey", "");
//        params.put("token","eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MTE3Mzc0OTMsInVzZXJuYW1lIjoiYWRtaW4ifQ.cKyVE2uMVB99AgcIeOqH7bYHPl0qlDjC-RTXYblIHgg");
//        ;
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json, text/plain, */*");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("_t", "1611734447");
        headers.put("Connection", "keep-alive");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
//        headers.put("X-Access-Token","eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2MTE3Mzc0OTMsInVzZXJuYW1lIjoiYWRtaW4ifQ.cKyVE2uMVB99AgcIeOqH7bYHPl0qlDjC-RTXYblIHgg");
//        RequestResult result = SendHttpRequestUtil.doGet(url,params,headers);
//        String result = SendHttpRequestUtil.doPost(url, params, headers);
//        System.out.print(result);
    }
}
