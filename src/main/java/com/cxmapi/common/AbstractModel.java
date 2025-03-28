package com.cxmapi.common;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractModel {

    private Map<String, Object> param = new HashMap<>();

    private String traceId;

    public abstract String getMethod();

    public abstract String getPath();

    @JsonAnyGetter
    public Map<String, Object> getRequestParams() {
        return param;
    }

    public void setRequestParams(Map<String, Object> params) {
        this.param = params;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
