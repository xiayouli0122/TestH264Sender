package com.test.testh264sender.upload;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.DeleteObjectRequest;
import com.alibaba.sdk.android.oss.model.DeleteObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.harsom.baselib.net2.ApiException;
import com.test.testh264sender.http.UploadParamResponse;
import com.yuri.xlog.XLog;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * 阿里云OSS上传文件管理 <br>
 * Created by Yuri on 2016/6/17.
 */
public class OssFileManager {

    private Context mContext;

    /**
     * OSS Client
     */
    private OSS mOSS;
    private String mBucketName;

    private int mTag;

    /**
     * 上传人物，通过这个对象可取消上传任务
     */
    private OSSAsyncTask mUploadTask;

    private OnOSSResultListener mListener;

    public void setOnOssResultListener(OnOSSResultListener listener) {
        mListener = listener;
    }

    public interface OnOSSResultListener {
        void onOssUploadProgress(long progress, long totalBytes);
    }

    public OssFileManager(Context context) {
        mContext = context;
    }

    /**
     * 初始化参数
     * @param param 阿里云上传参数
     */
    public void init(UploadParamResponse param) {
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(
                param.accessKey, param.secretKey, param.securityToken);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次

        mOSS = new OSSClient(mContext, param.endpoint, credentialProvider, conf);

        mBucketName = param.bucketName;
    }


    /**
     * 开始上传
     * @param imageBytes 上传的图片字节流
     * @param objectKey objectKey
     */
    public void start(byte[] imageBytes, String objectKey) {
        XLog.d("start bytes upload objectkey:%s", objectKey);
        PutObjectRequest request = new PutObjectRequest(mBucketName, objectKey, imageBytes);
        doOSSUpload(request);
    }

    /**
     * 开始上传
     * @param imageBytes 上传的图片字节流
     * @param objectKey objectKey
     */
    public Observable<String> start(final byte[] imageBytes, final String objectKey, int tag) {
        mTag = tag;
        XLog.d("start bytes upload objectkey:%s", objectKey);
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) {
                PutObjectRequest request = new PutObjectRequest(mBucketName, objectKey, imageBytes);
                doOSSUpload(request, emitter);
            }
        });
    }

    /**
     * 开上上传
     *
     * @param filePath 文件本地路径
     * @param objectKey objectKey
     *
     */
    public Observable<String> startFileUpload(final String filePath, final String objectKey, int tag) {
        mTag = tag;
        XLog.d("start path upload path:%s,objectkey:%s", filePath, objectKey);
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) {
                PutObjectRequest request = new PutObjectRequest(mBucketName, objectKey, filePath);
                doOSSUpload(request, emitter);
            }
        });
    }

    /**
     * 开上上传
     *
     * @param filePath 文件本地路径
     * @param objectKey objectKey
     *
     */
    public void start(String filePath, String objectKey) {
        XLog.d("start path upload path:%s,objectkey:%s", filePath, objectKey);
        PutObjectRequest request = new PutObjectRequest(mBucketName, objectKey, filePath);
        doOSSUpload(request);
    }

    public Observable<String> start(final String filePath, final String objectKey, int tag) {
        mTag = tag;
        XLog.d("start path upload path:%s,objectkey:%s", filePath, objectKey);
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) {
                PutObjectRequest request = new PutObjectRequest(mBucketName, objectKey, filePath);
                doOSSUpload(request, emitter);
            }
        });
    }


    /**
     * 调用阿里api上传文件到阿里云服务器
     * @param request request
     */
    private void doOSSUpload(PutObjectRequest request, final ObservableEmitter<? super String> subscriber) {
        //设置进度监听
        request.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long progress, long totalBytes) {
//                Log.d("progress:" + progress + "->totalBytes:" + totalBytes);
                if (mListener != null) {
                    mListener.onOssUploadProgress(progress, totalBytes);
                }
            }
        });

        mUploadTask = mOSS.asyncPutObject(request, new OSSCompletedCallback<PutObjectRequest,
                PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult
                    putObjectResult) {
                //在子线程中回调的
                XLog.i("oss upload success:%s", putObjectRequest.getObjectKey());
//                mListener.onOSSUploadSuccess(putObjectRequest.getObjectKey());)
                subscriber.onNext(putObjectRequest.getObjectKey());
//                subscriber.onCompleted();
            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException
                    clientException,
                                  ServiceException serviceException) {
                String errorMessage;
                if (clientException != null) {
                    XLog.e(clientException.getMessage());
                    XLog.e("iscancled:" + clientException.isCanceledException());
                    errorMessage = clientException.getMessage();
//                    clientException.printStackTrace();
                    if (clientException.isCanceledException()) {
                        subscriber.onError(new ApiException("已取消上传", mTag));
                    } else {
                        subscriber.onError(new ApiException(errorMessage, mTag));
                    }
                }

                if (serviceException != null) {
                    XLog.e("Upload.ErrorCode:%s,RequestId:%s,HostId:%s,RawMessage:%s",
                            serviceException.getErrorCode(),
                            serviceException.getRequestId(),
                            serviceException.getHostId(),
                            serviceException.getRawMessage());
                    errorMessage = serviceException.getMessage();
                    subscriber.onError(new ApiException(errorMessage, mTag));
                }
//                XLog.e("oss upload Fail:%s", errorMessage);
            }
        });
    }

    /**
     * 调用阿里api上传文件到阿里云服务器
     * @param request request
     */
    private void doOSSUpload(PutObjectRequest request) {
        //设置进度监听
        request.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long progress, long totalBytes) {
//                XLog.d("progress:" + progress + "->totalBytes:" + totalBytes);
                if (mListener != null) {
                    mListener.onOssUploadProgress(progress, totalBytes);
                }
            }
        });

        mUploadTask = mOSS.asyncPutObject(request, new OSSCompletedCallback<PutObjectRequest,
                PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult
                    putObjectResult) {
                //在子线程中回调的
                XLog.i("oss upload success:%s", putObjectRequest.getObjectKey());
            }

            @Override
            public void onFailure(PutObjectRequest putObjectRequest, ClientException
                    clientException,
                                  ServiceException serviceException) {
                String errorMessage = null;
                if (clientException != null) {
                    XLog.e(clientException.getMessage());
                    errorMessage = clientException.getMessage();
                    clientException.printStackTrace();
                }

                putObjectRequest.getObjectKey();
                if (serviceException != null) {
                    XLog.e("Upload.ErrorCode:%s,RequestId:%s,HostId:%s,RawMessage:%s",
                            serviceException.getErrorCode(),
                            serviceException.getRequestId(),
                            serviceException.getHostId(),
                            serviceException.getRawMessage());
                    errorMessage = serviceException.getMessage();
                }
                XLog.e("oss upload Fail:%s", errorMessage);
            }
        });
    }

    /**
     * 删除文件，当你需要删除掉你之前上传的文件时调用
     * @param objectKey 文件上传成功之后返回的ObjectKey
     */
    public void ossDelete(String objectKey) {
        XLog.d("objectKey:%s", objectKey);
        // 创建删除请求
        DeleteObjectRequest delete = new DeleteObjectRequest(mBucketName, objectKey);
        // 异步删除
        mOSS.asyncDeleteObject(delete, new OSSCompletedCallback<DeleteObjectRequest,
                DeleteObjectResult>() {
            @Override
            public void onSuccess(DeleteObjectRequest request, DeleteObjectResult result) {
                XLog.d("delete success:%s", request.getObjectKey());
            }

            @Override
            public void onFailure(DeleteObjectRequest request, ClientException clientExcepion,
                                  ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }

                if (serviceException != null) {
                    // 服务异常
                    XLog.e("Delete.ErrorCode:%s,RequestId:%s,HostId:%s,RawMessage:%s",
                            serviceException.getErrorCode(),
                            serviceException.getRequestId(),
                            serviceException.getHostId(),
                            serviceException.getRawMessage());
                }
            }

        });
    }

    /**
     * 取消上传任务
     */
    public boolean cancel() {
        if (mUploadTask == null || mUploadTask.isCompleted()) {
            return false;
        }
        mUploadTask.cancel();
        mUploadTask = null;
        return true;
    }
}
