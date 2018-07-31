package com.harsom.baselib.net2;

public interface IApiRetrofit {

    /**
     * 具体服务实例化
     */
    <T> T create(Class<T> service);

    boolean debug();

    /**
     * 单位 s
     */
    int getConnectionTimeOut();

    /**
     * 单位 s
     */
    int getReadTimeOut();

}
