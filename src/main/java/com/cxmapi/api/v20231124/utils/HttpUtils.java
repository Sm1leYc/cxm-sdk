package com.cxmapi.api.v20231124.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.cxmapi.common.SignUtil;
import com.cxmapi.common.exception.YuanapiSdkException;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.net.URLEncoder;

public class HttpUtils {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    /**
     * 生成一个随机数
     * @param length 随机数长度
     * @return
     */
    public static String generateNonce(int length) {
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
     * @param body
     * @param secretKey
     * @return
     */
    public static Map<String, String> getHeader(String body, String accessKey, String secretKey, String traceId)  throws YuanapiSdkException{
        HashMap<String, String> headMap = new HashMap<>();
        headMap.put("x-AccessKey",accessKey);
        headMap.put("X-Trace-Id", traceId);

        // 解决参数中文乱码
        String encode ="";
        try {
            encode = URLEncoder.encode(body, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new YuanapiSdkException(e.getClass().getName() + "-" + e.getMessage());
        }
        headMap.put("x-Body",encode);

        String nonce = generateNonce(16);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

        headMap.put("x-Nonce", nonce);
        headMap.put("x-Timestamp", timestamp);
        headMap.put("x-Sign", SignUtil.getSign(body, secretKey, nonce, timestamp));
        return headMap;
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
