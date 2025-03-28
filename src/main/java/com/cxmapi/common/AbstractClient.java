package com.cxmapi.common;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.cxmapi.common.exception.YuanapiSdkException;
import com.cxmapi.api.v20231124.utils.HttpUtils;
import com.cxmapi.common.model.Config;
import com.cxmapi.common.retry.RetryStrategy;
import com.cxmapi.common.retry.RetryStrategyLoader;
import com.github.rholder.retry.*;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;


public abstract class AbstractClient {


    public Config config;

    public AbstractClient() {
    }

    public AbstractClient(Config credential) {
        this.config = Objects.requireNonNull(credential, "Config cannot be null");
    }

    private static final Set<String> SUPPORTED_METHODS = new HashSet<>(Arrays.asList("GET", "POST"));


    private String formatRequestData(Map<String, Object> param, String path) throws YuanapiSdkException{
        StringBuilder getUrl = new StringBuilder(this.config.getEndpoint());
        getUrl.append(path);
        if (!param.isEmpty()){
            getUrl.append("?");
            // get请求将数据拼接到url中
            try {
                for (Map.Entry<String, Object> entry : param.entrySet()) {
                    String key = URLEncoder.encode(entry.getKey(), "utf-8");
                    String value = URLEncoder.encode(entry.getValue().toString(), "utf-8");

                    // 忽略空参数
                    if (!value.isEmpty()) {
                        getUrl.append(key).append("=").append(value).append("&");
                    }
                }
                // 移除最后一个多余的 "&"
                if (getUrl.length() > 0) {
                    getUrl.setLength(getUrl.length() - 1);
                }
            } catch (UnsupportedEncodingException e) {
                throw new YuanapiSdkException(e.getClass().getName() + "-" + e.getMessage());
            }
        }

        return getUrl.toString();
    }

    private void validateRequest(AbstractModel request) throws YuanapiSdkException {
        if (request == null) {
            throw new YuanapiSdkException("请求参数不能为空！");
        }
        if (this.config == null) {
            throw new YuanapiSdkException("请求配置不能为空！");
        }
        if (StringUtils.isBlank(request.getPath())) {
            throw new YuanapiSdkException("请求路径不能为空!");
        }
        if (StringUtils.isBlank(request.getMethod())) {
            throw new YuanapiSdkException("请求方法不能为空!");
        }
        if (!SUPPORTED_METHODS.contains(request.getMethod().trim().toUpperCase())) {
            throw new YuanapiSdkException("Only GET/POST methods are supported");
        }
    }

    protected String internalRequest(AbstractModel request) throws Exception {
        // 校验参数
        validateRequest(request);

        String path = request.getPath().trim();
        String method = request.getMethod().trim().toUpperCase();

        String endpoint = this.config.getEndpoint();
        int connectTimeout = this.config.getConnectTimeout();
        int readTimeout = this.config.getReadTimeout();
        boolean autoRetry = this.config.isAutoRetry();

        Map<String, Object> requestParams = request.getRequestParams();
        String jsonStr = JSONUtil.toJsonStr(request.getRequestParams());

        if (autoRetry) {
            // 获取用户配置的重试策略
            RetryStrategy retryStrategy = RetryStrategyLoader.getStrategy(this.config.getRetryStrategyName());

            // 保存最后一次响应，用于在重试全部失败后返回
            final AtomicReference<String> lastResponseRef = new AtomicReference<>();

            // 使用策略创建 Retryer
            Retryer<String> retryer = retryStrategy.createRetryer();

            // 定义请求任务 - 每次都创建新的请求（包含新的nonce）
            Callable<String> httpRequestTask = () -> {
                // 每次重试时生成新的 traceId
                String traceId = UUID.randomUUID().toString();

                // 每次重试时生成新的请求头（包含新的nonce）
                Map<String, String> headers = HttpUtils.getHeader(
                        jsonStr, this.config.getAccessKey(), this.config.getSecretKey(), traceId
                );

                // 获取新的http request对象
                HttpRequest httpRequest;
                try {
                    if ("POST".equals(method)) {
                        httpRequest = HttpUtils.postRequest(endpoint + path, connectTimeout, readTimeout);
                    } else if ("GET".equals(method)) {
                        httpRequest = HttpUtils.getRequest(formatRequestData(requestParams, path), connectTimeout, readTimeout);
                    } else {
                        throw new YuanapiSdkException("仅支持GET/POST请求！");
                    }
                } catch (Exception e) {
                    throw new YuanapiSdkException(e.getMessage());
                }

                // 执行请求并获取响应
                String response = execute(httpRequest.addHeaders(headers).body(jsonStr));

                // 保存最新响应
                lastResponseRef.set(response);

                return response;
            };

            try {
                // 执行带有重试策略的 HTTP 请求
                return retryer.call(httpRequestTask);
            } catch (RetryException e) {
                // 重试耗尽，返回最后一次响应而不是抛出异常
                String lastResponse = lastResponseRef.get();
                if (lastResponse != null) {
                    return lastResponse;
                }

                // 如果没有任何响应，则抛出原始异常
                if (e.getCause() != null) {
                    throw (Exception) e.getCause();
                } else {
                    throw new YuanapiSdkException("网关请求失败");
                }
            } catch (ExecutionException e) {
                // 执行过程中出现异常
                if (e.getCause() != null && e.getCause() instanceof Exception) {
                    throw (Exception) e.getCause();
                } else {
                    throw new YuanapiSdkException("网关请求异常: " + e.getMessage());
                }
            }
        } else {
            // 不启用重试，直接执行一次请求
           return executeRequest(request, path, method, jsonStr);
        }
    }

    private String executeRequest(AbstractModel request, String path, String method, String jsonStr)
            throws YuanapiSdkException {

        String traceId = StringUtils.defaultIfBlank(request.getTraceId(), UUID.randomUUID().toString());
        Map<String, String> headers = HttpUtils.getHeader(
                jsonStr, config.getAccessKey(), config.getSecretKey(), traceId);

        HttpRequest httpRequest = createHttpRequest(method, path, request.getRequestParams());
        return execute(httpRequest.addHeaders(headers).body(jsonStr));
    }

    private HttpRequest createHttpRequest(String method, String path, Map<String, Object> params)
            throws YuanapiSdkException {

        switch (method) {
            case "POST":
                return HttpUtils.postRequest(config.getEndpoint() + path,
                        config.getConnectTimeout(), config.getReadTimeout());
            case "GET":
                return HttpUtils.getRequest(formatRequestData(params, path),
                        config.getConnectTimeout(), config.getReadTimeout());
            default:
                throw new YuanapiSdkException("Unsupported method: " + method);
        }
    }

    // 执行HTTP请求并返回响应
    private String execute(HttpRequest httpRequest) {
        HttpResponse response = httpRequest.execute();
        return response.body(); // 直接返回响应体，无论成功或失败
    }


}
