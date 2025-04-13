package com.cxmapi.common.retry;

import com.cxmapi.common.model.ApiResponse;
import com.github.rholder.retry.Retryer;

public interface RetryStrategy {

    /**
     * 创建Retryer实例
     */
    <T> Retryer<T> createRetryer();

    /**
     * 判断响应是否需要重试
     */
    boolean shouldRetry(ApiResponse response);

    /**
     * 获取策略名称
     */
    String getStrategyName();
}
