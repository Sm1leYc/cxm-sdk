package com.cxmapi.cityWeather.v20240826.client;

import com.cxmapi.common.AbstractClient;
import com.cxmapi.common.AbstractModel;
import com.cxmapi.common.exception.YuanapiSdkException;
import com.cxmapi.common.model.ApiResponse;
import com.cxmapi.common.model.Config;

public class CityWeatherClient extends AbstractClient {



    public CityWeatherClient(Config credential) {
        super(credential);
    }

    public ApiResponse getCityWeather(AbstractModel request) throws YuanapiSdkException {

        try {
            return internalRequest(request);
        } catch (Exception e) {
            throw new YuanapiSdkException(e.getMessage());
        }

    }
}
