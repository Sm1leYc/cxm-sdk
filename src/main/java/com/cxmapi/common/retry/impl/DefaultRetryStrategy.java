package com.cxmapi.common.retry.impl;

import com.cxmapi.common.model.ApiResponse;
import com.cxmapi.common.retry.RetryStrategy;
import com.github.rholder.retry.*;

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
                if (response instanceof ApiResponse) {
                    return shouldRetry((ApiResponse) response);
                }
                return false;
            })
            .withWaitStrategy(WaitStrategies.fixedWait(initialWaitTime, TimeUnit.SECONDS))
            .withStopStrategy(StopStrategies.stopAfterAttempt(maxRetryTimes))
            .build();
    }
    
    @Override
    public boolean shouldRetry(ApiResponse response) {
        if (response == null) {
            return true;
        }
        
        try {
            int code = response.getResponseCode();
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