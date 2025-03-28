package com.cxmapi.api.v20231124.model.request;

import com.cxmapi.common.AbstractModel;

public class ApiRequest extends AbstractModel {

    private String method;
    private String path;

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    public void setMethod(String method){
        this.method = method;
    }

    public void setPath(String path){
        this.path = path;
    }
}
