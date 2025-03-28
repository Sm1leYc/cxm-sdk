package com.cxmapi.city.v20240825.request;

import com.cxmapi.common.AbstractModel;
import com.cxmapi.common.HttpRequestParams;

public class CityRequest extends AbstractModel {


    @Override
    public String getMethod() {
        return HttpRequestParams.GET;
    }

    @Override
    public String getPath() {
        return "/api/v1/city";
    }
}
