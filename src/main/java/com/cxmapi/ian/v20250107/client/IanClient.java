package com.cxmapi.ian.v20250107.client;

import com.cxmapi.common.AbstractClient;
import com.cxmapi.common.AbstractModel;
import com.cxmapi.common.exception.YuanapiSdkException;
import com.cxmapi.common.model.Config;

public class IanClient extends AbstractClient {

    public IanClient(Config credential) {
        super(credential);
    }

    public String getIan(AbstractModel request) throws YuanapiSdkException {
        try {
            return internalRequest(request);
        } catch (Exception e) {
            throw new YuanapiSdkException(e.getMessage());
        }

    }
}
