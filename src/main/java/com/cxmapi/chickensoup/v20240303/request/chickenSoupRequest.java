package com.cxmapi.chickensoup.v20240303.request;

import com.cxmapi.common.AbstractModel;
import com.cxmapi.common.HttpRequestParams;

public class chickenSoupRequest extends AbstractModel {


    @Override
    public String getMethod() {
        return HttpRequestParams.GET;
    }

    @Override
    public String getPath() {
        return "/api/v1/soup/random";
    }
}
