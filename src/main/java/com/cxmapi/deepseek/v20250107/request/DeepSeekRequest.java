package com.cxmapi.deepseek.v20250107.request;

import com.cxmapi.common.AbstractModel;
import com.cxmapi.common.HttpRequestParams;

public class DeepSeekRequest extends AbstractModel {


    @Override
    public String getMethod() {
        return HttpRequestParams.POST;
    }

    @Override
    public String getPath() {
        return "/api/v1/deepSeek";
    }
}
