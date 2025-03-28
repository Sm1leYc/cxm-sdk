package com.cxmapi.cityweather.v20240826.request;

import com.cxmapi.common.AbstractModel;
import com.cxmapi.common.HttpRequestParams;

public class CityWeatherRequest extends AbstractModel {


    @Override
    public String getMethod() {
        return HttpRequestParams.GET;
    }

    @Override
    public String getPath() {
        return "/api/v1/city/weather";
    }
}
