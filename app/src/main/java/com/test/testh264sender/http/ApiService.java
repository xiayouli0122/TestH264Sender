package com.test.testh264sender.http;


import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiService {

    /**
     * 下载文件
     *
     * @param url 文件url
     */
    @GET
    Observable<ResponseBody> downloadFile(@Url String url);

    /**
     * 用户视频上传参数接口
     */
    @POST("timeline/uservideo/uploadparam")
    Observable<UploadParamResponse> timelinVideoUploadParam(@Body BaseRequest request);


}
