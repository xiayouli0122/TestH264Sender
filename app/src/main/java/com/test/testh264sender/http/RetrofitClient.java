package com.test.testh264sender.http;

import com.harsom.baselib.net2.ApiRetrofit;


public class RetrofitClient extends ApiRetrofit {

    private static final String URL = "http://dev.delightmom.com/app/";

    private static RetrofitClient mInstance;

    public static RetrofitClient getInstance() {
        return getInstance(URL);
    }

    public static RetrofitClient getInstance(String baseUrl) {
        if (mInstance == null) {
            synchronized (ApiRetrofit.class) {
                if (mInstance == null) {
                    mInstance = new RetrofitClient(baseUrl);
                }
            }
        }
        return mInstance;
    }

    private RetrofitClient(String baseUrl) {
        super(baseUrl);
    }

    public ApiService createDefault() {
        return getInstance().create(ApiService.class);
    }

    @Override
    public boolean debug() {
        return true;
    }
}
