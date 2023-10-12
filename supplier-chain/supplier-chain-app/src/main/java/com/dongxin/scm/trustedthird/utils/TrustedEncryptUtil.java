package com.dongxin.scm.trustedthird.utils;


import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.*;

@Component
@Slf4j
public class TrustedEncryptUtil {

    private static String privateKey;

    private static Integer keyType;

    private static String[] signHead = { "content-type","to-client-name","from-client-name","method","timestamp","request-id"};

    @Value("${trusted-third.key.privateKey}")
    public void setPrivateKey(String privateKey){
        TrustedEncryptUtil.privateKey = privateKey;
    }

    public static Map<String, String> genarateKeyPair(Integer keyType) throws Exception {
        return EncryptFactory.getEncrypt(keyType).genKeyPair();
    }

    public static Map<String, String> getHeaders(HttpServletRequest request){
        Map<String, String> headers = new HashMap<>();
        //获取请求头信息
        Enumeration headerNames = request.getHeaderNames();
        //使用循环遍历请求头，并通过getHeader()方法获取一个指定名称的头字段
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            if (headerName.equals("content-length")) {
                continue;
            }
            String headervalue = request.getHeader(headerName);
            headers.put(headerName, headervalue);
            log.info("解析请求头："+headerName+":"+headervalue);
        }
        return headers;
    }

    public static String sign(String url, HttpServletRequest request,String privateKey) throws Exception {

        return TrustedEncryptUtil.sign(url, TrustedEncryptUtil.getHeaders(request),privateKey);
    }

    public static String sign(String url, Map<String, String> headers,String privateKey) throws Exception {
        String data = TrustedEncryptUtil.canonicalRequest(url,headers);
        ITrustedEncryptService encrypt = EncryptFactory.getEncrypt(1);
        return encrypt.sign(privateKey,data.toLowerCase());
    }

    public static String dissymmetricEncrypt(String publicKey,String data) throws Exception {
        ITrustedEncryptService encrypt = EncryptFactory.getEncrypt(1);
        return encrypt.encryptByPublicKey(publicKey,data.getBytes());
    }

    public static byte[] dissymmetricDecrypt(String encryptedData,String privateKey) throws Exception {
        ITrustedEncryptService encrypt = EncryptFactory.getEncrypt(1);
        return encrypt.decryptByPrivateKey(privateKey,encryptedData);
    }

    //step2:创建规范请求(canonicalRequest)
    public static String canonicalRequest( String url, Map<String, String> headers) throws Exception {

        String canonicalQueryString = "";
        //1:获取请求方法：参数传递的httpmethod
        //2:取canonicalURI

        URL u = new URL(url);

        String canonicalURI = u.getPath();

        String query = u.getQuery();

        if(query != null && !query.isEmpty())
            canonicalURI = canonicalURI + "?" + query.replaceAll("amp;","");

        if (!canonicalURI.startsWith("/")) {
            canonicalURI = "/" + canonicalURI;
        }
        if(!StringUtils.isEmpty(query)) {
            //3:获取canonicalQueryString
            String params[] = query.split("&");
            //对数组进行排序
            Arrays.sort(params);
            for (String item : params) {
                canonicalQueryString = canonicalQueryString + "&" + item;
            }
            canonicalQueryString = canonicalQueryString.substring(1, canonicalQueryString.length());
        }
        //4：获取CanonicalHeaders，以下Header进行编码：
        //Host、Content-Length、Content-Type、Content-MD5、所有以 x-bce- 开头的Header
        StringBuffer canonicalHeaders = new StringBuffer();
        List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(headers.entrySet());
        // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {

            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });
//        canonicalHeaders = headers.toString();
        Map<String,String> map = new HashMap<>(signHead.length);
        for (Map.Entry<String, String> item : infoIds) {
            if (StrUtil.isNotBlank(item.getKey())) {
                String key = StrUtil.trim(item.getKey()).toLowerCase();
                if (key.toLowerCase().equals("authorization"))
                    continue;
                String value = item.getValue();
//                if (key.startsWith("x-bce-") || Arrays.asList(signHead).contains(key)) {
                if ( Arrays.asList(signHead).contains(key)) {
                    map.put(key,item.getValue());
                    canonicalHeaders.append(key + ":" + value + ",");
                }
            }
        }
        return canonicalURI + "\n" + canonicalHeaders.toString().substring(0,canonicalHeaders.length()-1);
    }

    public static String[] arraySort(String[] input) {
        for (int i = 0; i < input.length - 1; i++) {
            for (int j = 0; j < input.length - i - 1; j++) {
                if (compareByName(input[j], input[j + 1]) == 1) {
                    String temp = input[j];
                    input[j] = input[j + 1];
                    input[j + 1] = temp;
                }
            }
        }
        return input;
    }

    private static int compareByName(String name1, String name2) {
        final byte[] bytes1 = name1.getBytes();
        final byte[] bytes2 = name2.getBytes();
        int i = 0;
        byte b1, b2;
        int numCompare = 0;//如果都是数字, 那么需要比较连续数字的大小, 只要高位大, 这个数字就大
        for (; i < Math.min(bytes1.length, bytes2.length); i++) {
            b1 = bytes1[i];
            b2 = bytes2[i];
            if (b1 != b2) {//只有ascii不相等时才比较
                if (numCompare != 0
                        && !(b1 >= 48 && b1 <= 57)
                        && !(b2 >= 48 && b2 <= 57)) {//已经出现过不等的数字，并且这个循环都是字符的情况
                    return numCompare;
                }
                if (b1 >= 48 && b1 <= 57
                        && b2 >= 48 && b2 <= 57) {//只有都是数字才会进入
                    if (numCompare == 0)
                        numCompare = Byte.compare(b1, b2);
                } else {//其中一个是数字，或者都是字符
                    if (numCompare != 0) {//已经出现过数字，那么本次循环哪个是数字，说明哪个数字位数多，那么这个数字就大
                        if (b1 >= 48 && b1 <= 57)
                            return 1;
                        if (b2 >= 48 && b2 <= 57)
                            return -1;
                    }
                    return Byte.compare(b1, b2);
                }
            }
        }
        if (numCompare == 0)//说明长度较小的部分完全一样，比较哪个长度大
            return Integer.compare(bytes1.length, bytes2.length);
        else {
            if (bytes1.length > bytes2.length) {
                if (bytes1[i] >= 48 && bytes1[i] <= 57)
                    return 1;
                else
                    return numCompare;
            }
            if (bytes1.length < bytes2.length) {
                if (bytes2[i] >= 48 && bytes2[i] <= 57)
                    return -1;
                else
                    return numCompare;
            }
            return numCompare;//出现过数字且不相同，并且最后一位不是字符
        }
    }

    public static void main(String[] args){
        String[] a = {"z:1","a:2","y:3"};
        Arrays.sort(a);
        for (String s:a
        ) {
            System.out.println(s);
        }
        System.out.println(Arrays.toString(a));

        Map<String,String> headers = new HashMap<>(10);
        headers.put("to-client-name","1");
        headers.put("from-client-name","2");
        headers.put("method","3");
        headers.put("timestamp","4");
        headers.put("content-type","5");
        StringBuffer canonicalHeaders = new StringBuffer();
        List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(headers.entrySet());
        // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {

            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });
        headers.forEach((key,value) ->{
            System.out.println(key + ":" + value);
        });
        System.out.println(headers.toString());
    }
}
