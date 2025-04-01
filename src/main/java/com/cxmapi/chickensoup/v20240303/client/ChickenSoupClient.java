package com.cxmapi.chickensoup.v20240303.client;

import com.cxmapi.common.AbstractClient;
import com.cxmapi.common.AbstractModel;
import com.cxmapi.common.exception.YuanapiSdkException;
import com.cxmapi.common.model.ApiResponse;
import com.cxmapi.common.model.Config;

public class ChickenSoupClient extends AbstractClient {

    public ChickenSoupClient(Config credential) {
        super(credential);
    }

    public ApiResponse getToxicChickenSoup(AbstractModel request) throws YuanapiSdkException {
        try {
            return internalRequest(request);
        } catch (Exception e) {
            throw new YuanapiSdkException(e.getMessage());
        }
    }

}
