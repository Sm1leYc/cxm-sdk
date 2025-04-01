package com.cxmapi.api.v20231124.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.cxmapi.common.HttpRequestParams;
import com.cxmapi.common.SignUtil;
import com.cxmapi.common.exception.YuanapiSdkException;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.stream.Collectors;

public class HttpUtils {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    /**
     * 生成一个随机数
     * @param length 随机数长度
     * @return
     */
    private static String generateNonce(int length) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    /**
     * 获取请求头
     * @param
     * @param secretKey
     * @return
     */
    public static Map<String, String> getHeader(String param, String accessKey, String secretKey){
        String traceId = UUID.randomUUID().toString();

        HashMap<String, String> headMap = new HashMap<>();
        headMap.put("X-AccessKey",accessKey);
        headMap.put("X-Trace-Id", traceId);

        String nonce = generateNonce(16);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        headMap.put("X-Nonce", nonce);
        headMap.put("X-Timestamp", timestamp);
        headMap.put("X-Sign", SignUtil.getSign(param, secretKey, nonce, timestamp));
        return headMap;
    }

    // 获取请求参数
    public static String getParam(String method, Map<String, Object> requestParams){
        String param = "";
        if (HttpRequestParams.POST.equals(method)){
            param = HttpUtils.toJson(requestParams);
        }else {
            param = HttpUtils.buildQueryStringForSign(requestParams);
        }

        return param;
    }

    /**
     * 构建已编码的查询字符串（用于实际URL请求）
     */
    public static String buildQueryStringForUrl(Map<String, Object> params) {
        return params.entrySet().stream()
                .filter(entry -> entry.getValue() != null) // 过滤null值
                .sorted(Map.Entry.comparingByKey()) // 按Key排序
                .map(entry ->
                        encodeParam(entry.getKey()) + "=" + encodeParam(convertToString(entry.getValue())))
                                .collect(Collectors.joining("&"));
    }

    /**
     * 构建未编码的查询字符串（用于签名）
     */
    private static String buildQueryStringForSign(Map<String, Object> params) {
        return params.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + convertToString(entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    // 将任意对象转为String
    private static String convertToString(Object value) {
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Number || value instanceof Boolean) {
            return value.toString(); // 数字和布尔值直接转字符串
        } else {
            return value != null ? value.toString() : "";
        }
    }

    // URL编码工具方法
    private static String encodeParam(String param) {
        try {
            return URLEncoder.encode(param, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return param;
        }
    }

    // 将Map转为json字符串
    private static String toJson(Map<String, Object> params) {
        return JSONUtil.toJsonStr(params);
    }


    // 提取URL中 ? 后面的所有参数
    public static String getParamsAfterAmpersand(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }

        try {
            URI uri = new URI(url);
            String query = uri.getQuery(); // 获取?后的部分
            return query != null ? query : "";
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }



    public static void main(String[] args) {
        HashMap<String, Object> map = new HashMap<>();

        map.put("name", "hello");
        map.put("age", 1);

        System.err.println(getParamsAfterAmpersand("https://api.btstu.cn/sjtx/api.php?lx=c1&format=json&key=value"));
    }

    public static HttpRequest getRequest(String url, int connectTimeout, int readTimeout) throws YuanapiSdkException {
        HttpRequest request = null;
        request = HttpRequest.get(url)
                .setConnectionTimeout(connectTimeout)
                .setReadTimeout(readTimeout);

        return request;
    }

    public static HttpRequest postRequest(String url, int connectTimeout, int readTimeout) throws YuanapiSdkException {

        HttpRequest request = null;

        request = HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .setConnectionTimeout(connectTimeout)
                .setReadTimeout(readTimeout);

        return request;
    }
}
