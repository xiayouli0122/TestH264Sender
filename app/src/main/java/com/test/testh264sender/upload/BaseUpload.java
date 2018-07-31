package com.test.testh264sender.upload;

import android.content.Context;
import android.util.Log;

import com.harsom.baselib.mvp.BaseNetModel;
import com.harsom.baselib.net2.ApiException;
import com.harsom.baselib.net2.BaseResponse;
import com.test.testh264sender.http.UploadParamResponse;
import com.yuri.xlog.XLog;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * 图片或者视频上传的基类，定义上传的步骤 <br>
 * 第一步：获取上传参数 <br>
 * 第二部：上传到阿里云 <br>
 * 第三步：上传到app服务器 <br>
 * Created by Yuri on 2016/7/11.
 */
public abstract class BaseUpload extends BaseNetModel {
    /**
     * 获取上传参数时失败
     */
    protected static final int FAIL_GET_PARAM = 0;
    /**
     * 上传图片到OSS时失败
     */
    protected static final int FAIL_UPLOAD_OSS = 1;
    /**
     * 上传到自己服务器时失败
     */
    protected static final int FAIL_UPLOAD_APP_SERVER = 2;
    protected static final int FAIL_UPLOAD_OSS_VIDEO_PREVIEW = 3;
    protected static final int FAIL_UPLOAD_OSS_VIDEO = 4;
    protected int mFailType = -1;

    protected OssFileManager mOssFileManager;

    /**
     * 上传参数中的ObjectKey
     */
    protected String mUploadObjectKey;

    protected boolean mIsCancel = false;

    protected List<String> mSuccessObjectKeyList = new ArrayList<>();

    protected int mTotalCount = 0;

    protected UploadCommonInfo mCommonInfo;

    //任务标记
    protected static final int TAG_PARAM =  1;
    protected static final int TAG_PHOTO =  2;
    protected static final int TAG_PREVIEW = 3;
    protected static final int TAG_VIDEO =  4;
    protected static final int TAG_SERVER = 5;

    public BaseUpload(Context context) {
        mOssFileManager = new OssFileManager(context.getApplicationContext());
        mSuccessObjectKeyList.clear();
    }


    protected void startUpload() {
        XLog.d("mFailType:" + mFailType);
        mIsCancel = false;

        Observable.just(mTotalCount)
                .flatMap(new Function<Integer, Observable<UploadParamResponse>>() {
                    @Override
                    public Observable<UploadParamResponse> apply(Integer totalCount) {
                        XLog.d("totalCount=" + totalCount);
                        if (totalCount == 0) {
                            mFailType = FAIL_UPLOAD_APP_SERVER;
                            return Observable.just(null);
                        }

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
                        if (response != null) {
                            XLog.i("获取OSS上传参数成功");
                            initOssManager(response);
                            initObjectKeys();
                        }

                        if (mFailType != -1 && mFailType != FAIL_UPLOAD_OSS) {
                            return Observable.just(null);
                        }

                        //第二步：上传图片
                        return uploadPhotoToOSS();
                    }
                })
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        XLog.d("图片上传成功.size:" + mSuccessObjectKeyList.size() + ",count=" + mTotalCount);
                        return mSuccessObjectKeyList.size() == mTotalCount;
                    }
                })
                .flatMap(new Function<String, ObservableSource<? extends BaseResponse>>() {
                    @Override
                    public ObservableSource<? extends BaseResponse> apply(String s) {
                        //第三步：上传服务器
                        return uploadToAppServer();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseResponse response) {
                        handleSuccess(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        XLog.d(e.getMessage());
                        mFailType = -1;
                        String message;
                        if (e instanceof ApiException) {
                            ApiException exception = (ApiException) e;
                            if (exception.tag == TAG_PARAM) {
                                mFailType = FAIL_GET_PARAM;
                            } else if (exception.tag == TAG_PHOTO) {
                                mFailType = FAIL_UPLOAD_OSS;
                            } else {
                                mFailType = FAIL_UPLOAD_APP_SERVER;
                            }
                            message = e.getMessage();
                        } else if (e instanceof UnknownHostException) {
                            message = "请检查网络连接";
                        } else {
                            message = e.getMessage();
                        }

                        if (mIsCancel) {
                            //用户手动取消，就需要全部重新上传
                            mFailType = -1;
                        }

                        XLog.d("mFailType=" + mFailType);
                        handleFail(message);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    protected void initOssManager(UploadParamResponse uploadParam) {
        mUploadObjectKey = uploadParam.objectKey;
        mOssFileManager.init(uploadParam);
    }

    protected abstract Observable<UploadParamResponse> getUploadParam();

    protected abstract void initObjectKeys();

    protected abstract Observable<String> uploadPhotoToOSS() ;

    protected abstract Observable<? extends BaseResponse> uploadToAppServer();

    protected abstract void handleSuccess(BaseResponse response);

    private void handleFail(String errorMsg) {
        XLog.d(errorMsg);
        if (mListener != null) {
            mListener.onUploadFail(errorMsg);
        }
    }

    protected void onUploadOssSuccess(String objectKey) {
        mSuccessObjectKeyList.add(objectKey);
    }

    public interface OnUploadResultListener<T> {
        void onPreStart();
        void onUploadStart();
        void onUploadProgress(int progress);
        void onUploadSuccess(T result);
        void onUploadFail(String errorMsg);
    }

    public OnUploadResultListener mListener;


    public void setResultListener(OnUploadResultListener listener) {
        mListener = listener;
    }

    public void cancel() {
        mIsCancel = true;
        if (mOssFileManager != null) {
            mOssFileManager.cancel();
        }
    }

    public void release() {
        delete();
    }

    public void delete() {
        XLog.d("mFailType:" + mFailType);
        if (mFailType != -1) {
            XLog.d("mSuccessObjectKeyList.size:%d", mSuccessObjectKeyList.size());
            for (String objectKey : mSuccessObjectKeyList) {
                if (mOssFileManager != null) {
                    mOssFileManager.ossDelete(objectKey);
                }
            }
        } else {
            if (mOssFileManager != null) {
                mOssFileManager.cancel();
                mOssFileManager = null;
            }
        }
    }
}
