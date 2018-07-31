package com.harsom.baselib.net2;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiRetrofit implements IApiRetrofit{

    private Interceptor mInterceptor;
    private Retrofit mRetrofit;

    protected ApiRetrofit(String baseUrl) {
        ////通用拦截
        mInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .addHeader("Content_Type", "application/json")
                        .addHeader("charset", "UTF-8")
                        .build();
                return chain.proceed(request);
            }
        };

        //Retrofit实例化
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl == null ? "" : baseUrl)//retroift要求baseurl总是以“/”结尾
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(initClient())
                .build();
    }

    /*
    OKHttp创建
 */
    private OkHttpClient initClient(){
        return new OkHttpClient.Builder()
                .addInterceptor(initLogInterceptor())
                .addInterceptor(mInterceptor)
                .connectTimeout(getConnectionTimeOut(),TimeUnit.SECONDS)
                .readTimeout(getReadTimeOut(),TimeUnit.SECONDS)
                .build();
    }


    /*
        日志拦截器
     */
    private HttpLoggingInterceptor initLogInterceptor(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(debug() ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return interceptor;
    }


    /**
     * 具体服务实例化
     */
    @Override
    public <T> T create(Class<T> service) {
        return mRetrofit.create(service);
    }

    @Override
    public boolean debug() {
        return false;
    }

    /**
     * 单位 s
     */
    @Override
    public int getConnectionTimeOut() {
        return 10;
    }

    /**
     * 单位 s
     */
    @Override
    public int getReadTimeOut() {
        return 10;
    }
}
