package com.cxmapi.chickensoup.v20240303.client;

import com.cxmapi.common.AbstractClient;
import com.cxmapi.common.AbstractModel;
import com.cxmapi.common.exception.YuanapiSdkException;
import com.cxmapi.common.model.Config;

public class chickenSoupClient extends AbstractClient {

    public chickenSoupClient(Config credential) {
        super(credential);
    }

    public String getToxicChickenSoup(AbstractModel request) throws YuanapiSdkException {
        try {
            return internalRequest(request);
        } catch (Exception e) {
            throw new YuanapiSdkException(e.getMessage());
        }
    }

}
