package com.cxmapi.common;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.cxmapi.common.exception.YuanapiSdkException;
import com.cxmapi.api.v20231124.utils.HttpUtils;
import com.cxmapi.common.model.ApiResponse;
import com.cxmapi.common.model.Config;
import com.cxmapi.common.retry.RetryStrategy;
import com.cxmapi.common.retry.RetryStrategyLoader;
import com.github.rholder.retry.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


public abstract class AbstractClient {


    public Config config;

    public AbstractClient() {
    }

    public AbstractClient(Config credential) {
        this.config = Objects.requireNonNull(credential, "Config cannot be null");
    }

    // 安全允许的请求头白名单
    private static final List<String> SAFE_REQUEST_HEADERS = Arrays.asList(
            "user-agent",
            "content-type"
    );

    // 安全允许的响应头白名单
    private static final List<String> SAFE_HEADERS = Arrays.asList(
            "date",
            "content-type",
            "content-length",
            "connection",
            "keep-alive",
            "vary",
            "x-trace-id"
    );


    private static final Set<String> SUPPORTED_METHODS = new HashSet<>(Arrays.asList("GET", "POST"));

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

    protected ApiResponse internalRequest(AbstractModel request) throws Exception {
        // 校验参数
        validateRequest(request);

        String path = request.getPath().trim();
        String method = request.getMethod().trim().toUpperCase();

        boolean autoRetry = this.config.isAutoRetry();

        Map<String, Object> requestParams = request.getRequestParams();
        String signParam = HttpUtils.getParam(method, requestParams);

        if (autoRetry) {
            // 获取用户配置的重试策略
            RetryStrategy retryStrategy = RetryStrategyLoader.getStrategy(this.config.getRetryStrategyName());

            // 保存最后一次响应，用于在重试全部失败后返回
            final AtomicReference<ApiResponse> lastResponseRef = new AtomicReference<>();

            // 使用策略创建 Retryer
            Retryer<ApiResponse> retryer = retryStrategy.createRetryer();

            // 定义请求任务 - 每次都创建新的请求（包含新的nonce）
            String finalParam = signParam;
            Callable<ApiResponse> httpRequestTask = () -> {
                // 执行请求并获取响应
                ApiResponse response = executeRequest(request, path, method, finalParam);

                // 保存最新响应
                lastResponseRef.set(response);

                return response;
            };

            try {
                // 执行带有重试策略的 HTTP 请求
                return retryer.call(httpRequestTask);
            } catch (RetryException e) {
                // 重试耗尽，返回最后一次响应而不是抛出异常
                ApiResponse lastResponse = lastResponseRef.get();
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
           return executeRequest(request, path, method, signParam);
        }
    }

    // 执行request请求
    private ApiResponse executeRequest(AbstractModel request, String path, String method, String param)
            throws YuanapiSdkException {

        // 获取请求头
        Map<String, String> reqHeaders = HttpUtils.getHeader(
                param, config.getAccessKey(), config.getSecretKey());

        if (HttpRequestParams.GET.equals(request.getMethod())){
            Map<String, Object> requestParams = request.getRequestParams();
            // url使用编码后参数请求
            param = HttpUtils.buildQueryStringForUrl(requestParams);
        }

        // 获取httpRequest对象
        HttpRequest httpRequest = createHttpRequest(method, path, param)
                .addHeaders(reqHeaders);

        // 执行请求
        HttpResponse httpResponse = httpRequest.execute();

        return new ApiResponse(
                httpResponse.body(),
                filterHeaders(httpRequest.headers(), SAFE_REQUEST_HEADERS),
                filterHeaders(httpResponse.headers(), SAFE_HEADERS)
        );
    }

    // 根据请求方法创建httpRequest对象
    private HttpRequest createHttpRequest(String method, String path, String params)
            throws YuanapiSdkException {

        switch (method) {
            case "POST":
                return HttpUtils.postRequest(config.getEndpoint() + path,
                        config.getConnectTimeout(), config.getReadTimeout()).body(params);
            case "GET":
                StringBuilder urlBuilder = new StringBuilder(this.config.getEndpoint() + path);
                if (StringUtils.isNotBlank(params)){
                    urlBuilder.append("?").append(params);
                }
                return HttpUtils.getRequest(urlBuilder.toString(),
                        config.getConnectTimeout(), config.getReadTimeout());
            default:
                throw new YuanapiSdkException("Unsupported method: " + method);
        }
    }

    /**
     * 过滤头信息方法
     */
    private Map<String, String> filterHeaders(Map<String, ? extends List<String>> headers, List<String> allowedHeaders) {
        return headers.entrySet().stream()
                .filter(entry -> entry.getKey() != null)
                .filter(entry -> allowedHeaders.contains(entry.getKey().toLowerCase()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().isEmpty() ? "" : entry.getValue().get(0) // 只取第一个值
                ));
    }


}
