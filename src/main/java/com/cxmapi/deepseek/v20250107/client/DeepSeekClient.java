package com.cxmapi.deepseek.v20250107.client;

import com.cxmapi.common.AbstractClient;
import com.cxmapi.common.AbstractModel;
import com.cxmapi.common.exception.YuanapiSdkException;
import com.cxmapi.common.model.Config;

public class DeepSeekClient extends AbstractClient {

    public DeepSeekClient(Config credential) {
        super(credential);
    }

    public String CallDeepSeekAPI(AbstractModel request) throws YuanapiSdkException {

        try {
            return internalRequest(request);
        } catch (Exception e) {
            throw new YuanapiSdkException(e.getMessage());
        }

    }
}
