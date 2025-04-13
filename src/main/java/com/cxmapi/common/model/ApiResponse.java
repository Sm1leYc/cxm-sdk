package com.cxmapi.common.model;

import java.util.Collections;
import java.util.Map;

public class ApiResponse {
    private final int responseCode;
    private final String body;
    private final Map<String, String> requestHeaders;
    private final Map<String, String> responseHeaders;

    public ApiResponse(int responseCode, String body, Map<String, String> requestHeaders, Map<String, String> responseHeaders) {
        this.responseCode = responseCode;
        this.body = body;
        this.requestHeaders = Collections.unmodifiableMap(requestHeaders);
        this.responseHeaders = Collections.unmodifiableMap(responseHeaders);
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public String getRequestHeader(String header){
        return requestHeaders.getOrDefault(header, null);
    }

    public String getResponseHeader(String header){
        return responseHeaders.getOrDefault(header, null);
    }
}