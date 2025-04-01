package com.cxmapi.city.v20240825.client;

import com.cxmapi.common.AbstractClient;
import com.cxmapi.common.AbstractModel;
import com.cxmapi.common.exception.YuanapiSdkException;
import com.cxmapi.common.model.ApiResponse;
import com.cxmapi.common.model.Config;

public class CityClient extends AbstractClient {

    public CityClient(Config credential) {
        super(credential);
    }

    public ApiResponse getCityInfo(AbstractModel request) throws YuanapiSdkException{
        try {
            return internalRequest(request);
        } catch (Exception e) {
            throw new YuanapiSdkException(e.getMessage());
        }
    }
}
