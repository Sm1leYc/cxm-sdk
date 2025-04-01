package com.cxmapi.dailyenglish.v20250107.client;

import com.cxmapi.common.AbstractClient;
import com.cxmapi.common.AbstractModel;
import com.cxmapi.common.exception.YuanapiSdkException;
import com.cxmapi.common.model.ApiResponse;
import com.cxmapi.common.model.Config;

public class DailyEnglishClient extends AbstractClient {

    public DailyEnglishClient(Config credential) {
        super(credential);
    }

    public ApiResponse getDailyEnglish(AbstractModel request) throws YuanapiSdkException {

        try {
            return internalRequest(request);
        } catch (Exception e) {
            throw new YuanapiSdkException(e.getMessage());
        }

    }
}
