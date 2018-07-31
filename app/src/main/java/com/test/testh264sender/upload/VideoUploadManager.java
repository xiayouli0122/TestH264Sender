package com.test.testh264sender.upload;

import android.content.Context;
import android.text.TextUtils;

import com.harsom.baselib.net2.ApiException;
import com.harsom.baselib.net2.ApiResponseFunc;
import com.harsom.baselib.net2.BaseResponse;
import com.test.testh264sender.http.BaseRequest;
import com.test.testh264sender.http.RetrofitClient;
import com.test.testh264sender.http.UploadParamResponse;
import com.test.testh264sender.http.VideoUploadResponse;
import com.yuri.xlog.XLog;

import java.net.UnknownHostException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 视频上传Manager <br>
 * 视频上传包含预览图和视频文件的上传 <br>
 * 1. 获取OSS上传参数 <br>
 * 2. 上传预览图到OSS服务器 <br>
 * 2. 上传视频到OSS服务器 <br>
 * 3. 上传到app服务器 <br>
 * 视频只允许单一上传
 * Created by Yuri on 2016/7/5.
 */
public class VideoUploadManager extends BaseUpload implements OssFileManager.OnOSSResultListener {

    /**
     * 视频预览图上传
     */
    private static final int TYPE_PREVIEW = 0;
    /**
     * 视频上传
     */
    private static final int TYPE_VIDEO = 1;

    private int mType;

    /**
     * 要上传的视频信息
     */
    private VideoInfo mVideoInfo;

    /**
     * 上传预览图文件成功后 OSS返回的ObjectKey
     */
    private String mPreviewObjectKey;
    /**
     * 上传视频文件成功后 OSS返回的ObjectKey
     */
    private String mVideoObjectKey;

    private OnVideoUploadListener mVideoUploadListener;
    interface OnVideoUploadListener {
        void onUploadPrepare();
        void onUploadStart();
        void onUploadProgress(long progressBytes, long totalBytes);
        void onUploadSuccess(long timelineId);
        void onUploadFail(String message);
    }

    void setOnVideoUploadListener(OnVideoUploadListener listener) {
        mVideoUploadListener = listener;
    }

    VideoUploadManager(Context context) {
        super(context);
        mOssFileManager.setOnOssResultListener(this);
    }

    /**
     * 上传视频时光轴
     */
    void uploadVideo(UploadCommonInfo commonInfo, final VideoInfo videoInfo) {
        mCommonInfo = commonInfo;
        mVideoInfo = videoInfo;

        XLog.object(mVideoInfo);

        mSuccessObjectKeyList.clear();

        startUpload();
    }

    @Override
    protected void startUpload() {
        //视频上传单独处理
        mIsCancel = false;

        XLog.d("mFailType=" + mFailType);
        Observable.just("")
                .flatMap(new Function<String, ObservableSource<UploadParamResponse>>() {
                    @Override
                    public ObservableSource<UploadParamResponse> apply(String s) {
                        //第一步：获取上传参数
                        if (mFailType != -1 && mFailType != FAIL_GET_PARAM) {
                            return Observable.just(null);
                        }
                        return getUploadParam();
                    }
                })
                .flatMap(new Function<UploadParamResponse, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(UploadParamResponse response) {
                        XLog.i("获取OSS上传参数成功");
                        if (response != null) {
                            initOssManager(response);
                        }

                        if (mFailType != -1 && mFailType != FAIL_UPLOAD_OSS_VIDEO_PREVIEW) {
                            return Observable.just(null);
                        }
                        //第二步：上传预览图
                        return ossPreviewUpload();
                    }
                })
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String objectKey) {
                        XLog.i("上传预览图成功，objectKey：" + objectKey);

//                        if (!TextUtils.isEmpty(objectKey)) {
//                            onUploadOssSuccess(objectKey);
//                            mPreviewObjectKey = objectKey;
//                        }

                        if (mFailType != -1 && mFailType != FAIL_UPLOAD_OSS_VIDEO) {
                            return Observable.just(null);
                        }

                        //第三步：上传视频文件
                        return ossVideoUpload();
                    }
                })
                .flatMap(new Function<String, ObservableSource<VideoUploadResponse>>() {
                    @Override
                    public ObservableSource<VideoUploadResponse> apply(String objectKey) {
                        XLog.i("视频文件上传成功.objectKey：" +objectKey);

                        if (!TextUtils.isEmpty(objectKey)) {
                            mVideoObjectKey = objectKey;
                        }

                        //第四步：上传服务器
                        return uploadToAppServer();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<VideoUploadResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(VideoUploadResponse videoUploadResponse) {
                        mFailType = -1;

                        if (videoUploadResponse != null) {
//                            Log.d("id=" + videoUploadResponse.id);
                            if (mVideoUploadListener != null) {
                                mVideoUploadListener.onUploadSuccess(videoUploadResponse.id);
                            }
                            return;
                        }

                        if (mVideoUploadListener != null) {
                            mVideoUploadListener.onUploadFail("上传失败，请重试!");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
//                        Log.d(e.getMessage());
                        mFailType = -1;
                        String message;
                        if (e instanceof ApiException) {
                            ApiException exception = (ApiException) e;
                            if (exception.tag == TAG_PARAM) {
                                mFailType = FAIL_GET_PARAM;
                            } else if (exception.tag == TAG_PREVIEW) {
                                mFailType = FAIL_UPLOAD_OSS_VIDEO_PREVIEW;
                            } else if (exception.tag == TAG_VIDEO) {
                                mFailType = FAIL_UPLOAD_OSS_VIDEO;
                            } else {
                                mFailType = FAIL_UPLOAD_APP_SERVER;
                            }
                            message = e.getMessage();
                        } else if (e instanceof UnknownHostException) {
                            message = "请检查网络连接";
                        } else {
                            message = e.getMessage();
                        }

//                        Log.d("mFailType=" + mFailType);
//
                        if (mVideoUploadListener != null) {
                            mVideoUploadListener.onUploadFail(message);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected Observable<UploadParamResponse> getUploadParam() {
//        Log.i("开始获取OSS上传参数");
        mVideoUploadListener.onUploadPrepare();
        return RetrofitClient.getInstance().createDefault()
                .timelinVideoUploadParam(new BaseRequest())
                .map(new ApiResponseFunc<UploadParamResponse>(TAG_PARAM));
    }

    @Override
    protected void initObjectKeys() {
        //do nothing
    }

    @Override
    protected Observable<String> uploadPhotoToOSS() {
        //do nothing
        return null;
    }

    @Override
    protected Observable<VideoUploadResponse> uploadToAppServer() {
//        Log.d("previewObjectKey:%s,videoObjectKey:%s", mPreviewObjectKey, mVideoObjectKey);
        return Observable.just(null);
    }

    @Override
    protected void handleSuccess(BaseResponse response) {
        //donothing
    }

    /**
     * 预览图上传
     */
    private Observable<String> ossPreviewUpload() {
        XLog.i("开始上传预览图" );
        if (mIsCancel) {
            return Observable.error(new ApiException("用户已取消上传", TAG_PREVIEW));
        }

        mType = TYPE_PREVIEW;
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) {
                XLog.d("no need upload preview ,continue!!!");
                emitter.onNext("sssssssssss");
            }
        });
    }

    /**
     * 视频文件上传
     */
    private Observable<String> ossVideoUpload() {
        XLog.i("开始上传视频文件" );
        if (mIsCancel) {
            return Observable.error(new ApiException("用户已取消上传", TAG_PREVIEW));
        }

        mType = TYPE_VIDEO;
        mVideoUploadListener.onUploadStart();
        if (mOssFileManager == null) {
            XLog.e("ossfile manager is null");
            return Observable.error(new ApiException("OssFileManager is null"));
        }
        return mOssFileManager.startFileUpload(mVideoInfo.tmpPath, getVideoObjectKey(), TAG_VIDEO);
    }

    private String getPreviewObjectKey() {
        if (mVideoInfo.bitmap != null) {
            return mUploadObjectKey + System.currentTimeMillis() + "_" + mVideoInfo.bitmap.getWidth() + "x" + mVideoInfo.bitmap.getHeight() + ".jpg";
        }
        return mUploadObjectKey + System.currentTimeMillis() + ".jpg";
    }

    private String getVideoObjectKey() {
        return mUploadObjectKey + System.currentTimeMillis() + "_" + mVideoInfo.width + "x" + mVideoInfo.height + ".mp4";
    }

    @Override
    public void onOssUploadProgress(long progressBytes, long totalBytes) {
//        Log.d("progress:" + progressBytes);
        if (mType == TYPE_VIDEO) {
            mVideoUploadListener.onUploadProgress(progressBytes, totalBytes);
        }
    }

}
