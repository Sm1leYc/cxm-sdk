package com.cxmapi.dailyenglish.v20250107.request;

import com.cxmapi.common.AbstractModel;
import com.cxmapi.common.HttpRequestParams;

public class DailyEnglishRequest extends AbstractModel {


    @Override
    public String getMethod() {
        return HttpRequestParams.GET;
    }

    @Override
    public String getPath() {
        return "/api/v1/dailyEnglish";
    }
}
