package com.cxmapi.api.v20231124.client;

import com.cxmapi.common.AbstractModel;
import com.cxmapi.common.AbstractClient;
import com.cxmapi.common.exception.YuanapiSdkException;
import com.cxmapi.common.model.Config;



/**
 * 调用第三方接口的客户端(根据通用请求调用)
 *
 *
 */
public class YuanApiClient extends AbstractClient {

    public YuanApiClient(Config credential) {
        super(credential);
    }

    public YuanApiClient(){

    }

    public String invokeApi(AbstractModel request) throws YuanapiSdkException{
        try {
            return internalRequest(request);
        } catch (Exception e) {
            throw new YuanapiSdkException(e.getMessage());
        }
    }

}
