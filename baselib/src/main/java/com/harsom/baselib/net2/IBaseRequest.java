package com.harsom.baselib.net2;

public interface IBaseRequest {
    /**
     * 添加公共请求部分header
     */
    RequestHeader initRequestHeaders();

    /**获取服务器版本号，现在默认配置为1.0*/
    String getServiceVersion();

    String getClientVersion();

    /**获取app渠道号，android为1000 */
    String getSourceId();

    long getRequestTime();

}
