package com.test.testh264sender.http;

import com.harsom.baselib.net2.IBaseRequest;
import com.harsom.baselib.net2.RequestHeader;
import com.test.testh264sender.BuildConfig;

/**
 * 请求基类，包含请求的header数据
 * Created by Yuri on 2016/4/21.
 */
public class BaseRequest implements IBaseRequest {
    protected RequestHeader header;

    public BaseRequest() {
        this.header = initRequestHeaders();
    }

    @Override
    public RequestHeader initRequestHeaders() {
        RequestHeader header = new RequestHeader();
        header.userToken = "6f69366a-251d-4dcc-9f6c-0b1f32c14799";
        header.serviceVersion = getServiceVersion();
        header.clientVersion = getClientVersion();
        header.sourceID = getSourceId();
        header.requestTime = getRequestTime();
        return header;
    }

    @Override
    public String getServiceVersion() {
        return "1.0";
    }

    @Override
    public String getClientVersion() {
        return BuildConfig.VERSION_NAME;
    }

    @Override
    public String getSourceId() {
        return "1000";
    }

    @Override
    public long getRequestTime() {
        return System.currentTimeMillis();
    }
}
