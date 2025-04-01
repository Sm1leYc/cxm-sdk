package com.cxmapi.common.model;

import java.util.Collections;
import java.util.Map;

public class ApiResponse {
    private final String body;
    private final Map<String, String> reqHeaders;
    private final Map<String, String> resHeaders;

    public ApiResponse(String body, Map<String, String> reqHeaders, Map<String, String> resHeaders) {
        this.body = body;
        this.reqHeaders = Collections.unmodifiableMap(reqHeaders);
        this.resHeaders = Collections.unmodifiableMap(resHeaders);
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getReqHeaders() {
        return reqHeaders;
    }

    public Map<String, String> getResHeaders() {
        return resHeaders;
    }

    public String getReqHeader(String name) {
        return reqHeaders.get(name);
    }

    public String getResHeader(String name) {
        return resHeaders.get(name);
    }
}