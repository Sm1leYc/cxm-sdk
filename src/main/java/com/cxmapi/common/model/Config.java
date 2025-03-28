package com.cxmapi.common.model;

public class Config {
    private String baseurl;
    private String accessKey;
    private String secretKey;

    private boolean autoRetry; // 自动重试（可选）

    private String retryStrategyName; // 策略类名称（可选）

    private int connectTimeout; // 连接超时时间（可选）
    private int readTimeout; // 读取超时时间（可选）


    private Config(Builder builder) {
        this.baseurl = builder.baseurl;
        this.accessKey = builder.accessKey;
        this.secretKey = builder.secretKey;
        this.retryStrategyName = builder.retryStrategyName == null ? "default" : builder.retryStrategyName;
        this.connectTimeout = builder.connectTimeout == 0 ? 5000 : builder.connectTimeout;
        this.readTimeout = builder.readTimeout == 0 ? 50000 : builder.readTimeout;
        this.autoRetry = builder.autoRetry;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getEndpoint() {
        return baseurl;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public String getRetryStrategyName() {
        return retryStrategyName;
    }

    public boolean isAutoRetry() {
        return autoRetry;
    }

    public static class Builder {
        private String baseurl;
        private String accessKey;
        private String secretKey;

        private String retryStrategyName;

        private int connectTimeout;
        private int readTimeout;

        private boolean autoRetry;


        // 构造函数
        public Builder setBaseurl(String baseurl) {
            this.baseurl = baseurl;
            return this;
        }

        public Builder setAccessKey(String accessKey){
            this.accessKey = accessKey;
            return this;
        }

        public Builder setSecretKey(String secretKey){
            this.secretKey = secretKey;
            return this;
        }

        public Builder setConnectTimeOut(int connectTimeout){
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setReadTimeOut(int readTimeout){
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder setAutoRetry(boolean autoRetry){
            this.autoRetry = autoRetry;
            return this;
        }

        public Builder setRetryStrategyName(String retryStrategyName) {
            this.retryStrategyName = retryStrategyName;
            return this;
        }

        public Config build(){
            return new Config(this);
        }
    }
}
