package com.cxmapi.common.retry.impl;

import com.cxmapi.common.retry.RetryStrategy;
import com.github.rholder.retry.*;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.concurrent.TimeUnit;

/**
 * 默认重试策略实现
 */
public class DefaultRetryStrategy implements RetryStrategy {
    
    private int maxRetryTimes = 3;
    private long initialWaitTime = 2;
    
    public DefaultRetryStrategy() {}
    
    public DefaultRetryStrategy(int maxRetryTimes, long initialWaitTime) {
        this.maxRetryTimes = maxRetryTimes;
        this.initialWaitTime = initialWaitTime;
    }
    
    @Override
    public <T> Retryer<T> createRetryer() {
        return RetryerBuilder.<T>newBuilder()
            .retryIfExceptionOfType(Exception.class)
            .retryIfResult(response -> {
                if (response instanceof String) {
                    return shouldRetry((String) response);
                }
                return false;
            })
            .withWaitStrategy(WaitStrategies.fixedWait(initialWaitTime, TimeUnit.SECONDS))
            .withStopStrategy(StopStrategies.stopAfterAttempt(maxRetryTimes))
            .withRetryListener(new RetryListener() {
                @Override
                public <V> void onRetry(Attempt<V> attempt) {
                    // 可以加入日志记录
                }
            })
            .build();
    }
    
    @Override
    public boolean shouldRetry(String response) {
        if (response == null || response.isEmpty()) {
            return true;
        }
        
        try {
            JSONObject jsonResponse = JSONUtil.parseObj(response);
            int code = jsonResponse.getInt("code", 200);
            
            // 判断网关错误码
            return (code >= 400);
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public String getStrategyName() {
        return "default";
    }
}