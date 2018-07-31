package com.harsom.baselib.net;


import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiRetrofit {

    private static final int DEFAULT_TIMEOUT = 5;

//    private File httpCacheDirectory;
//    private Cache mCache = null;

    private BaseApiService mBaseApiService;

    private static OkHttpClient mOkHttpClient;
    private static Retrofit mRetrofit;

    private static ApiRetrofit mInstance;

    public static ApiRetrofit getInstance() {
        return getInstance(null);
    }

    public static ApiRetrofit getInstance(String baseUrl) {
        return getInstance(baseUrl, null);
    }

    public static ApiRetrofit getInstance(String baseUrl, Map<String, String> headers) {
        if (mInstance == null) {
            synchronized (ApiRetrofit.class) {
                if (mInstance == null) {
                    mInstance = new ApiRetrofit(baseUrl, headers);
                }
            }
        }
        return mInstance;
    }

    private ApiRetrofit() {
        this("", null);
    }

    private ApiRetrofit(String baseUrl) {
        this(baseUrl, null);
    }

    private ApiRetrofit(String baseUrl, Map<String, String> headers) {
//        if (httpCacheDirectory == null) {
//            httpCacheDirectory = new File(context.getCacheDir(), "net_cache");
//        }
//        if (mCache == null) {
//            //10M 缓存
//            mCache = new Cache(httpCacheDirectory, 10*1024*1024);
//        }

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(debug() ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        mOkHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(loggingInterceptor)
//                .cache(mCache)
//                .addInterceptor(new CacheInterceptor(context))
                .addInterceptor(new BaseInterceptor(headers))
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(8, 10, TimeUnit.SECONDS))
                // 这里你可以根据自己的机型设置同时连接的个数和时间，我这里8个，和每个保持时间为10s
                .build();


        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
    }

    public ApiRetrofit createBaseApi() {
        mBaseApiService = create(BaseApiService.class);
        return this;
    }

    public <T> T create(final Class<T> service) {
        if (service == null) {
            throw new RuntimeException("Api Service is null");
        }
        return mRetrofit.create(service);
    }

    /**
     * write超时时间，单位s，默认5s
     */
    protected int writeTimeOut() {
        return 5;
    }

    protected boolean debug() {
        return false;
    }

    /**
     * 总是以"/"结尾
     */
    protected String baseUrl() {
        return null;
    }

    BaseApiService getApi() {
        return mBaseApiService;
    }
}
