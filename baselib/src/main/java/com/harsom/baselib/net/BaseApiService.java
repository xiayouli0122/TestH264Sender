package com.harsom.baselib.net;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Yuri on 2018/5/21.
 */

public interface BaseApiService {

    @GET("{url}")
    Observable<HttpResponse<Object>> excuteGet(@Path("url") String url, @QueryMap Map<String , String> map);


    @POST("{url}")
    Observable<HttpResponse<Object>> excutePost(@Path("url") String url, @Body  RequestBody json);

    Observable<HttpResponse> uploadFile(@Path("url") String url, @Part("image\"; filename=\"image.jpg") RequestBody body);

    @Streaming
    @GET
    Observable<HttpResponse> downloadFile(@Url String fileUrl);
}
