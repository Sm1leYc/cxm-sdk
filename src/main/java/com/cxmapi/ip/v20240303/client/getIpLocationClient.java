package com.cxmapi.ip.v20240303.client;

import com.cxmapi.common.AbstractClient;
import com.cxmapi.common.AbstractModel;
import com.cxmapi.common.exception.YuanapiSdkException;
import com.cxmapi.common.model.ApiResponse;
import com.cxmapi.common.model.Config;

public class getIpLocationClient extends AbstractClient {

    public getIpLocationClient (Config credential) {
        super(credential);
    }


    public ApiResponse getIpLocation(AbstractModel request) throws YuanapiSdkException {
        try {
            return internalRequest(request);
        } catch (Exception e) {
            throw new YuanapiSdkException(e.getMessage());
        }
    }
}
