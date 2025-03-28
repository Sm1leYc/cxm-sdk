package com.cxmapi.common.retry;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 重试策略加载器
 */
public class RetryStrategyLoader {
    
    private static final Map<String, RetryStrategy> STRATEGIES = new ConcurrentHashMap<>();
    private static volatile boolean loaded = false;
    
    /**
     * 使用SPI机制加载所有可用的重试策略
     */
    public static synchronized void loadStrategies() {
        if (loaded) {
            return;
        }
        
        ServiceLoader<RetryStrategy> loader = ServiceLoader.load(RetryStrategy.class);
        Iterator<RetryStrategy> iterator = loader.iterator();
        
        while (iterator.hasNext()) {
            RetryStrategy strategy = iterator.next();
            STRATEGIES.put(strategy.getStrategyName(), strategy);
        }
        
        // 如果没有加载到任何策略，添加默认策略
        if (STRATEGIES.isEmpty()) {
            RetryStrategy defaultStrategy = new com.cxmapi.common.retry.impl.DefaultRetryStrategy();
            STRATEGIES.put(defaultStrategy.getStrategyName(), defaultStrategy);
        }
        
        loaded = true;
    }
    
    /**
     * 获取指定名称的重试策略
     */
    public static RetryStrategy getStrategy(String strategyName) {
        loadStrategies();
        
        RetryStrategy strategy = STRATEGIES.get(strategyName);
        if (strategy == null) {
            // 如果找不到指定名称的策略，返回默认策略
            strategy = STRATEGIES.get("default");
            if (strategy == null) {
                // 如果依然没有，创建一个默认策略
                strategy = new com.cxmapi.common.retry.impl.DefaultRetryStrategy();
                STRATEGIES.put("default", strategy);
            }
        }
        
        return strategy;
    }
    
    /**
     * 获取所有可用的重试策略名称
     */
    public static String[] getAvailableStrategyNames() {
        loadStrategies();
        return STRATEGIES.keySet().toArray(new String[0]);
    }
    
    /**
     * 注册自定义策略 (非SPI方式)
     */
    public static void registerStrategy(RetryStrategy strategy) {
        loadStrategies();
        STRATEGIES.put(strategy.getStrategyName(), strategy);
    }
}